package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.openrdf.OpenRDFException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    // TODO (Christian) Kein Konstruktor nötig (hab ich bereits rausgenommen)

    // TODO (Christian) Methode hier: Erhält ID und Klassennamen. Mit dem Klassennamen kann das richtige Query-Template ausgewählt werden

    // TODO (Christian) Methode hier: Auslesen der Templates.txt files. Wird vmtl. über ein java File und den richtigen Pfad geschehen. Bitte Funktionalität dafür suchen

    // TODO (Christian) In den Templates: Query idR fast fertig, der String (im Template) "ADD_ID_HERE" muss gegen die richtige ID ausgetauscht werden - vmtl. ein String.replace

    // TODO (Christian) Diese Query, die Du dann als String brauchst, kannst Du genau wie im Anno4jRepo als Query an das hier autogewirete Anno4j Object weiter geben

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id
     * @return
     */

    public String getRepresentationOfObject(@NonNull String id, @NonNull String className) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        String directory = "templates";
        String fileName = directory + "\\" + className + ".txt";
        File file = new File(fileName);
        System.out.println("Checking, if " + file.getAbsolutePath() + " does exist...");
        if (new File(directory).isDirectory()) {
            System.out.println("Directory does exist! ");
        } else {
            throw new FileNotFoundException("Directory " + new File(directory).getAbsolutePath() + " does not exist!");
        }
        if (!file.canRead()) {
            throw new FileNotFoundException("File with Query Template for Class " + className + " has not been found!");
        } else {
            System.out.println("File does exist!");
        }
            String fileContent = this.readFile(fileName);
            System.out.println("\nFile content is: ");
            System.out.println(fileContent);
            String sparqlQuery = this.replaceString(fileContent, "^ADD_ID_HERE$", id);
            System.out.println("\nNew SparqlQuery: ");
            System.out.println(sparqlQuery);
            ObjectConnection objectConnection = this.anno4j.getObjectRepository().getConnection();
            ObjectQuery objectQuery = objectConnection.prepareObjectQuery(sparqlQuery);
            Result<RDFObject> rdfObjectResult = objectQuery.evaluate(RDFObject.class);
            List<RDFObject> rdfObjectList = rdfObjectResult.asList();
            StringBuilder result = new StringBuilder();
            for (RDFObject rdfObject : rdfObjectList) {
                result.append("\n").append(rdfObject.getResource().toString());
            }
            return result.toString();

        }
        /**
         * Wrapper Method to read Content of Files
         *
         * @param path
         * @return
         * @throws IOException
         */
        private String readFile (String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded);
        }

        /**
         * Wrapper Method to replace content of String with another Substring
         *
         * @param base
         * @param remove
         * @param replace
         * @return
         */
        private String replaceString (String base, String remove, String replace){
            return Pattern.compile(Pattern.quote(remove), Pattern.CASE_INSENSITIVE).matcher(base).replaceAll(replace);
        }


        /**
         * ATM mainly here for test purposes. Do not like this, change possibility?
         */
        private Anno4jRepository getAnno4jRepository () {
            return this.anno4jRepository;
        }

        /**
         * @return
         */
        public Anno4j getAnno4j () {
            return anno4j;
        }
    }
