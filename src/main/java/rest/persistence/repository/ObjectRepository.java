package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.namespace.JSONVISMO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import rest.VisitRestApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Repository Class for Querying RDFObject Representations
 */
@Repository
public class ObjectRepository {

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.templates.path}")
    private String pathToTemplates;

    @Value("${visit.rest.templates.fullresult}")
    private boolean fullResult;

    @Autowired
    private Anno4j anno4j;

    @Autowired
    private Anno4jRepository anno4jRepository;

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    /**
     * @param id        ID of the represented OBJECT
     * @param className ClassName on which the SPARQL query should be executed on
     * @return Returns a JSON in Form of a String with all the relevant Information belonging to the Object
     * @throws IOException
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     * @throws ParseException
     * @throws ClassNotFoundException
     */
    public String getRepresentationOfObject(@NonNull String id, @NonNull String className) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException, ParseException, ClassNotFoundException {
        // Find the directory of the templates (local vs. productive with a set path to templates)
        String directory;

        if (this.sparqlEndpointQuery.equals("none")) {
            directory = "templates";
        } else {
            directory = this.pathToTemplates;
        }

        String fileName = directory + "/" + className + ".txt";
        File file = new File(fileName);
        logger.debug("Checking, if " + file.getAbsolutePath() + " does exist...");
        if (!file.canRead()) {
            throw new FileNotFoundException("File with Query Template for Class " + className + " has not been found!");
        } else {
            logger.debug("File does exist!");
        }

        String fileContent = this.readFile(fileName);
        logger.debug("\nFile content is: " + fileContent);

        // Create a list of template names
        List<String> listClasses = this.getTemplateNames(directory);

        // Adapt the SPARQL query read from the template
        String sparqlQuery = this.replaceString(fileContent, "ADD_ID_HERE", id);
        logger.debug("\nNew SPARQL Query: " + sparqlQuery);

        // Issue the query
        ObjectConnection objectConnection = this.anno4j.getObjectRepository().getConnection();
        TupleQuery tupleQuery = objectConnection.prepareTupleQuery(sparqlQuery);
        TupleQueryResult evaluateTupleQuery = tupleQuery.evaluate();

        // Create result JSON from the query results
        JsonObject jsonObject = new JsonObject();
        BindingSet currentResult = evaluateTupleQuery.next();

        for (String binding : currentResult.getBindingNames()) {

            if (this.fullResult || !currentResult.getValue(binding).stringValue().isEmpty()) {

                // Check if the current binding represents a subclass
                String bindingSingular = binding.substring(0, binding.length() - 1);
                if (containsClass(bindingSingular, listClasses)) {

                    logger.debug("Inner Class found: " + bindingSingular + ". Recursive call to template queries done.");
                    String subId = currentResult.getValue(binding).stringValue();

                    // Issue recursive call to this method with sub-node
                    JsonParser jsonParser = new JsonParser();
                    JsonObject subObject = (JsonObject) jsonParser.parse(getRepresentationOfObject(id, bindingSingular));

                    // Make two adaptions of values for sub-template query results
                    if (subObject.has(JSONVISMO.ID)) {
                        // Change query id (which is the id of the top-level object) to the id of the sub-template object
                        subObject.remove(JSONVISMO.ID);
                        subObject.addProperty(JSONVISMO.ID, subId);
                    }

                    if (!subId.isEmpty() && subObject.get(JSONVISMO.ID).getAsString().split(",").length == 1 && subObject.has(JSONVISMO.TYPE)) {
                        subObject.remove(JSONVISMO.TYPE);
                        subObject.addProperty(JSONVISMO.TYPE, this.anno4jRepository.getLowestClassGivenId(subId));
                    }

                    JsonElement jsonElement = this.splitMultipleJsonObject(subObject);

                    jsonObject.add(bindingSingular, jsonElement);
                } else {
                    // Query works with placeholder ?x for the current ID, exchange this with id
                    switch (binding) {
                        case "x":
                            jsonObject.addProperty(JSONVISMO.ID, currentResult.getValue(binding).stringValue());
                            break;
                        case "type":
                            jsonObject.addProperty(JSONVISMO.TYPE, this.anno4jRepository.getLowestClassGivenId(id));
                            break;
                        default:
                            jsonObject.addProperty(binding, currentResult.getValue(binding).stringValue());
                            break;
                    }
                }
            }
        }

        String jsonObjectString = jsonObject.toString();
        jsonObjectString = replaceString(jsonObjectString, "\\", "");
        logger.debug("Created output JSON: " + jsonObjectString);

        return jsonObjectString;
    }

    private JsonElement splitMultipleJsonObject(JsonObject jsonObject) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        JsonArray jsonArray = new JsonArray();

        // Find out, if jsonObject represents "more" single objects
        if (jsonObject.get(JSONVISMO.ID).toString().contains(",")) {
            String[] split = jsonObject.get(JSONVISMO.ID).getAsString().split(",");

            for (int i = 0; i < split.length; ++i) {
                JsonObject splitObject = new JsonObject();

                for (String binding : jsonObject.keySet()) {
                    if (!binding.equals(JSONVISMO.TYPE)) {

                        String[] bindingSplit = jsonObject.get(binding).getAsString().split(",");
                        if (bindingSplit.length > 1) {
                            splitObject.addProperty(binding, bindingSplit[i]);
                            if (binding.equals(JSONVISMO.ID)) {
                                splitObject.addProperty(JSONVISMO.TYPE, this.anno4jRepository.getLowestClassGivenId(bindingSplit[i]));
                            }
                        } else {
                            splitObject.addProperty(binding, bindingSplit[0]);
                            if (binding.equals(JSONVISMO.ID)) {
                                splitObject.addProperty(JSONVISMO.TYPE, this.anno4jRepository.getLowestClassGivenId(bindingSplit[0]));
                            }
                        }
                    }
                }

                jsonArray.add(splitObject);
            }
        } else {
            return jsonObject;
        }

        return jsonArray;
    }

    /**
     * Wrapper Method to look for a String in a Collection
     *
     * @param key    String to search for.
     * @param search Collection to look in.
     * @return true or false.
     */
    private boolean containsClass(String key, List<String> search) {
        for (String currVal : search) {
            if (currVal.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wrapper Method to read Content of Files
     *
     * @param path Path for the File.
     * @return returns a String with the content of the File
     * @throws IOException
     */
    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    /**
     * Wrapper Method to replace content of String with another Substring
     *
     * @param base    String on which the Substring should be removed
     * @param remove  Substring which should be removed
     * @param replace Replacement for the Substring
     * @return
     */
    private String replaceString(String base, String remove, String replace) {
        return Pattern.compile(Pattern.quote(remove), Pattern.CASE_INSENSITIVE).matcher(base).replaceAll(replace);
    }

    /**
     * Gets a Collection of Strings withc all the filenames shortened representing a ClassName
     *
     * @return returns a String List with the filenames.
     */
    private List<String> getTemplateNames(String directory) throws FileNotFoundException {
        List<String> templateNameCollection = new LinkedList<>();
        File file = new File(directory);
        if (file.isDirectory()) {
            try {
                String[] list = file.list();
                for (int i = 0; i < list.length; i++) {
                    String currentClass = this.replaceString(list[i], ".txt", "");
                    templateNameCollection.add(currentClass);
                }
            } catch (NullPointerException e) {
                throw new FileNotFoundException("Directory " + new File(directory).getAbsolutePath() + " is empty!");
            }
            System.out.println("Directory does exist!");
            System.out.println("Content of the Directory is:\n" + templateNameCollection.toString());
            return templateNameCollection;
        } else
            throw new FileNotFoundException("Directory " + new File(directory).getAbsolutePath() + " does not exist!");


    }

    /**
     * @return
     */
    public Anno4j getAnno4j() {
        return anno4j;
    }
}
