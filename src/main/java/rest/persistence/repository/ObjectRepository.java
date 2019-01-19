package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.google.gson.JsonObject;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

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

    @Autowired
    private Anno4jRepository anno4jRepository;

    @Autowired
    private Anno4j anno4j;
    // Alte TODOs oben
    // TODO (Christian) Template-Methode um Sub-Queries erweitern
    // TODO (Christian) Dazu: Am Anfang der Methode über alle Templates laufen und Dir eine Liste anlegen, die sich die Namen der Templates als String merkt
    // TODO (Christian) Beim durchgehen des Bindings-Set: Testen, ob der name eines keys einem Template entspricht und damit in der obigen String-Liste enthalten ist
    // TODO (Christian) Falls ja: Rekursiver Aufruf Deiner Methode

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
        String directory = "templates";
        String fileName = directory + "/" + className + ".txt";
        File file = new File(fileName);
        System.out.println("Checking, if " + file.getAbsolutePath() + " does exist...");
        List<String> listClasses = this.getTemplateNames(directory);
        if (!file.canRead()) {
            throw new FileNotFoundException("File with Query Template for Class " + className + " has not been found!");
        } else {
            System.out.println("File does exist!");
        }

        String fileContent = this.readFile(fileName);
        System.out.println("\nFile content is: ");
        System.out.println(fileContent);

        // Hab hier einen kleinen "Fehler" gefunden: Man darf die beiden Sonderzeichen vor "ADD_ID_HERE" nicht entfernen - Hab's ausgebessert
        String sparqlQuery = this.replaceString(fileContent, "ADD_ID_HERE", id);
        System.out.println("\nNew SparqlQuery: ");
        System.out.println(sparqlQuery);
        ObjectConnection objectConnection = this.anno4j.getObjectRepository().getConnection();
        TupleQuery tupleQuery = objectConnection.prepareTupleQuery(sparqlQuery);
        TupleQueryResult evaluateTupleQuery = tupleQuery.evaluate();
        JsonObject allBindings = new JsonObject();
        while (evaluateTupleQuery.hasNext()) {
            BindingSet currentResult = evaluateTupleQuery.next();
            System.out.println("Binding sets with Values have been found:");
            for (String binding : currentResult.getBindingNames()) {
                if (containsClass(binding, listClasses)) {
                    System.out.println("Inner Class: " + binding);
                    allBindings.addProperty(binding, getRepresentationOfObject(id, binding));
                } else {
                    System.out.println("Key: " + binding + " With Value: " + currentResult.getValue(binding).stringValue());
                    allBindings.addProperty(binding, currentResult.getValue(binding).stringValue());
                }
            }
        }
        System.out.println(allBindings.toString());
        return allBindings.toString();


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
     * ATM mainly here for test purposes. Do not like this, change possibility?
     */
    private Anno4jRepository getAnno4jRepository() {
        return this.anno4jRepository;
    }

    /**
     * @return
     */
    public Anno4j getAnno4j() {
        return anno4j;
    }
}
