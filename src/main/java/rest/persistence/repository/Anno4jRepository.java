package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
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

    public String getPersonJSON(String id) {
        

        return null;

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
















