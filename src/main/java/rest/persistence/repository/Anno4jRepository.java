package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.Resource;
import model.namespace.VISMO;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Anno4jRepository {

   @Autowired
    private Anno4j anno4j;

    public Anno4jRepository() {}

    /**
     * ATM mainly here for test purposes..
     */
    public Anno4j getAnno4j() {
        return this.anno4j;
    }

    public String getLowestClassGivenId(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String annotation = this.anno4j.getConcept(id).getAnnotations()[0].toString();

        return this.selectValue(annotation);
    }

    /**
     * Method queries for the ID of the superordinate vismo:Resource of the DigitalRepresentation defined by the supported digRepId.
     *
     * @param digRepId  The Id of the DigitalRepresentation node.
     * @return          The Id of the superordinate vismo:Resource.
     */
    public String getResourceIdByDigitalRepresentation (String digRepId) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService queryService = this.anno4j.createQueryService();

        queryService.addPrefix(VISMO.PREFIX, VISMO.NS);

        queryService.addCriteria("<" + VISMO.HAS_DIGITAL_REPRESENTATION + ">", digRepId);

        List<Resource> result = queryService.execute(Resource.class);

        return result.get(0).getResourceAsString();
    }

    /**
     * Method get a whole Java-Annotation as input and retrieves and returns its set value.
     *
     * @param annotation    The Annotation-String to read the value from.
     * @return              The value set for the input Annotation-String.
     */
    private String selectValue(String annotation) {
        int value = annotation.indexOf("value=");

        return annotation.substring(value + 6, annotation.length() - 1);
    }
}
















