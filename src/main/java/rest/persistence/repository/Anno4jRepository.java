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

    // TODO (Manu) Exchange this, when Anno4j version is updated to 2.4.x, Matthias already implemented this
    public String getLowestClassGivenId(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        ObjectQuery query = connection.prepareObjectQuery(
                "SELECT ?c1 {" +
                        "   <" + id + "> a ?c1 . " +
                        "   MINUS {" +
                        "       <" + id + "> a ?c2 . " +
                        "       ?c2 rdfs:subClassOf+ ?c1 . " +
                        "       FILTER(?c1 != ?c2)" +
                        "   }" +
                        "}"
        );

        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<RDFObject> list = result.asList();

        return list.get(0).getResource().toString();
    }

    public String getPersonJSON(String id) {
        

        return null;

    }
}
















