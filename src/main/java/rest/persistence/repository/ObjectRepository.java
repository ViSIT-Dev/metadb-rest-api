package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
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
    private DigitalRepresentationRepository digitalRepresentationRepository;
    private RDFObject rdfObject;
    private DigitalRepresentation digitalRepresentation;

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
        String className = anno4jRepository.getLowestClassGivenId(id);
        System.out.println(className);

        return null;

    }
}
