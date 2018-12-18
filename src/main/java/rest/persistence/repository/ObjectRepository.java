package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.apache.jena.atlas.json.JsonObject;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ServerSocket;

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

    public void getRepresentationOfObject(@NonNull String id, @NonNull String className) throws FileNotFoundException {
        String directory = "templates";
        String fileName = directory + "\\" + className + ".txt";
        File file = new File(fileName);
        System.out.println("Checking, if " + file.getAbsolutePath() + " does exist...");
        if (new File(directory).isDirectory()) {
            System.out.println("Directory does exist! ");
        } else {
            throw new FileNotFoundException("Directory "+new File(directory).getAbsolutePath()+" does not exist!");
        }
        if (!file.canRead()) {
            throw new FileNotFoundException("File with Query Template for Class " + className + " has not been found!");
        } else {
            System.out.println("File does exist!");
        }

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
