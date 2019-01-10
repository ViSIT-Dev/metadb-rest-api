package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import model.Resource;
import org.apache.jena.atlas.lib.Tuple;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.openrdf.query.*;
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

    // TODO (Christian) Kein Konstruktor nötig (hab ich bereits rausgenommen)

    // TODO (Christian) Methode hier: Erhält ID und Klassennamen. Mit dem Klassennamen kann das richtige Query-Template ausgewählt werden

    // TODO (Christian) Methode hier: Auslesen der Templates.txt files. Wird vmtl. über ein java File und den richtigen Pfad geschehen. Bitte Funktionalität dafür suchen

    // TODO (Christian) In den Templates: Query idR fast fertig, der String (im Template) "ADD_ID_HERE" muss gegen die richtige ID ausgetauscht werden - vmtl. ein String.replace

    // TODO (Christian) Diese Query, die Du dann als String brauchst, kannst Du genau wie im Anno4jRepo als Query an das hier autogewirete Anno4j Object weiter geben
    // Alte TODOs oben

    // TODO (Christian) Erweiterungen der Methode:
    // TODO (Christian) 1: "Unterobjekte" der Queries rausfinden: Wenn ein value eines key/value pairs auf eine weitere ID zeigt, dann kann dies in eine weitere Query an die DB aufgelöst werden.
    //  Hier ist dann der key der Name des Query-Templates, der value beinhaltet die ID, die dann mit der selben Funktionalität angefragt werden kann
    // TODO (Christian) 2: Schreiben der Ergebnisse als JSON. Dieses kann simpel aussehen und einfach alle key/value Paare als JSON Einträge beinhalten. Bei "Unterobjekten" können diese als Array eingetragen werden. Dieses JSON ist das Ergebnise der Anfrage, und kann dementsprechend an den Controller zurück gegeben werden.

    // TODO (Christian) Checken, welcher Query-Typ der Objectconnection für uns am besten geeignet ist: objectConnection.prepareObjectQuery/.prepareTupleQuery/.prepareQuery

    /**
     *
     * @param id ID of the represented OBJECT
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

        // TODO (Christian) Bitte allgemeinen generischen Seperator einfügen, damit dies auf anderen Betriebssystemen funktioniert (kann ich für dich testen, wenn fertig)

        String fileName = directory + "/" + className + ".txt";
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
        /*ObjectQuery objectQuery = objectConnection.(QueryLanguage.SPARQL,sparqlQuery);*/
        ObjectQuery objectQuery = objectConnection.prepareObjectQuery(QueryLanguage.SPARQL, sparqlQuery);
        Result<RDFObject> rdfObjectResult = objectQuery.evaluate(RDFObject.class);
        List<RDFObject> rdfObjectList = rdfObjectResult.asList();
        List<String> resultIDs = new LinkedList<>();
        StringBuilder result = new StringBuilder();
        for (RDFObject rdfObject : rdfObjectList) {
            resultIDs.add(rdfObject.getResource().toString());
        }
        for (String resultId : resultIDs) {
            result.append("\n\n\n").append(resultId);
        }


        System.out.println(result.toString());
        return result.toString();

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
     * @param base String on which the Substring should be removed
     * @param remove Substring which should be removed
     * @param replace Replacement for the Substring
     * @return
     */
    private String replaceString(String base, String remove, String replace) {
        return Pattern.compile(Pattern.quote(remove), Pattern.CASE_INSENSITIVE).matcher(base).replaceAll(replace);
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
