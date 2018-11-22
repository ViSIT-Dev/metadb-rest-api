package rest.persistence.repository;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.Resource;
import model.VISMO;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class DigitalRepresentationRepository {

    @Autowired
    private Anno4j anno4j;

    public String getSingleTechnicalMetadataByMediaID(String id) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();

        qs.addCriteria(".", id);

        List<DigitalRepresentation> result = qs.execute(DigitalRepresentation.class);

        if (!result.isEmpty()) {
            return result.get(0).getTechnicalMetadata();
        } else {
            // Exchange this to own exception
            throw new QueryEvaluationException("ID not existent!");
        }
    }

    // TODO (Manu) Rewrite this to Anno4j? No direct SPARQL needed

    /**
     * Private method that queries the technicalMetadata Strings given an ID of a vismo:Resource entity.
     *
     * @param id The ID of the vismo:Resource from which the technicalMetadata is to be queried.
     * @return A list of Strings that represent the technicalMetadata for the searched vismo:Resource entity.
     */
    public List<String> getAllTechnicalMetadataStringsByObjectID(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        String queryString = "SELECT ?d\n" +
                "   WHERE { <" + id + "> a <" + VISMO.RESOURCE + "> .\n" +
                "   <" + id + "> <" + VISMO.HAS_DIGITAL_REPRESENTATION + "> ?d }";

        ObjectQuery query = connection.prepareObjectQuery(queryString);

        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<RDFObject> list = result.asList();

        List<String> technicalMetadatas = new LinkedList<>();

        for (RDFObject object : list) {
            technicalMetadatas.add(((DigitalRepresentation) object).getTechnicalMetadata());
        }

        return technicalMetadatas;
    }

    /**
     * Public method to create a new node of a DigitialRepresentation on a givebn resource
     *
     * @param resourceId objectID to create the new DigitalRepresentaion on
     * @return return the id of the new DigitalRepresentation
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ParseException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public String createNewDigitalRepresentationNode(String resourceId) throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Anno4j anno4j = getAnno4j();
        //Resource resource = anno4j.createObject(Resource.class);
        QueryService qs = anno4j.createQueryService();
        qs.addCriteria(".", resourceId);
        List<Resource> resources = qs.execute(Resource.class);
        if (!resources.isEmpty()) {
            DigitalRepresentation digitalRepresentation = anno4j.createObject(DigitalRepresentation.class);
            Resource resource = resources.get(0);
            resource.addDigitalRepresentation(digitalRepresentation);
            return digitalRepresentation.getResourceAsString();
        } else {
            throw new QueryEvaluationException("ID is not existent!");
        }
    }

    /**
     * @param resourceID
     * @param newData
     */
    public void updateDigitalRepresentationNode(String resourceID, JsonObject newData) {
        Anno4j anno4j = getAnno4j();
        try{

        }catch(Exception e){

        }




    }


    /**
     * ATM mainly here for test purposes. Do not like this, change possibility?
     */
    public Anno4j getAnno4j() {
        return this.anno4j;
    }
}
