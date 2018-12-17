package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.RDFObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

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
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */

    public JsonObject getRepresentationOfObject(@NonNull String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException, ClassNotFoundException {
        Anno4jRepository anno4jRepository = this.getAnno4jRepository();
        String className = anno4jRepository.getLowestClassGivenId(id);
        System.out.println(className);

        return null;

    }


    /**
     * ATM mainly here for test purposes. Do not like this, change possibility?
     */
    private Anno4jRepository getAnno4jRepository() {
        return this.anno4jRepository;
    }

    /**
     *
     * @return
     */
    public Anno4j getAnno4j() {
        return anno4j;
    }
}
