package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Resource;
import model.VISMO;
import model.technicalMetadata.DigitalRepresentation;
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
            throw new QueryEvaluationException("ID " + id + "  not existent!");
        }
    }

    // TODO (Manu) Rewrite this to Anno4j? No direct SPARQL needed

    /**
     * Private method that queries the technicalMetadata Strings given an ID of a vismo:Resource entity.
     *
     * @param id The ID of the vismo:Resource from which the technicalMetadata is to be queried.
     * @return A list of Strings that represent the technicalMetadata for the searched vismo:Resource entity.
     */
    public String getAllTechnicalMetadataStringsByObjectID(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        String queryString = "SELECT ?d\n" +
                "   WHERE { <" + id + "> a <" + VISMO.RESOURCE + "> .\n" +
                "   <" + id + "> <" + VISMO.HAS_DIGITAL_REPRESENTATION + "> ?d }";

        ObjectQuery query = connection.prepareObjectQuery(queryString);

        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<RDFObject> list = result.asList();
        JsonArray jsonElements = new JsonArray();
        for (RDFObject object : list) {
            jsonElements.add(((DigitalRepresentation) object).getTechnicalMetadata());
        }

        // TODO (Christian) Noch falsch aufgedröselt. Bei "ObjectId" sollte die ID des Objekts stehen (im Parameter "id" drin), dann eine Liste als zweites anhängen die z.B. als key "DigitalRepresentations" und als values dann Deine "jsonElements" hat
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("ObjectId",jsonElements);

        return jsonObject.toString();
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
            throw new QueryEvaluationException("ID " + resourceId + " is not existent!");
        }
    }

    /**
     * Method to update the new created/exisitng  media representation node given the JSON String and the mediaID.
     *
     * @param mediaID
     * @param newDataString
     * @throws RepositoryException
     * @throws ParseException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public void updateDigitalRepresentationNode(String mediaID, String newDataString) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        Anno4j anno4j = getAnno4j();
        QueryService qs = anno4j.createQueryService();
        qs.addCriteria(".", mediaID);
        List<DigitalRepresentation> digitalRepresentations = qs.execute(DigitalRepresentation.class);
        if (!digitalRepresentations.isEmpty()) {
            DigitalRepresentation digitalRepresentation = digitalRepresentations.get(0);
            digitalRepresentation.setTechnicalMetadata(newDataString);
        } else {
            throw new QueryEvaluationException("ID " + mediaID + " is not existent!");
        }
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media and Object id.
     *
     * @param mediaID
     * @param objectID
     * @throws RepositoryException
     * @throws ParseException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public void deleteDigitalRepresentationMediaAndObject(String objectID, String mediaID) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        Anno4j anno4j = getAnno4j();
        QueryService qs = anno4j.createQueryService();
        qs.addCriteria(".", objectID);
        List<Resource> resources = qs.execute(Resource.class);
        if (!resources.isEmpty()) {
            mediaQueryService(mediaID, anno4j);
        } else {
            throw new QueryEvaluationException("ObjectID " + objectID + " is not existent!");
        }
    }

    /**
     * Wrapper Method to Delete a DigitalRepresentation by Media ID returns void or Exception.
     *
     * @param mediaID
     * @param anno4j
     * @throws RepositoryException
     * @throws ParseException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private void mediaQueryService(String mediaID, Anno4j anno4j) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs2 = anno4j.createQueryService();
        qs2.addCriteria(".", mediaID);
        List<DigitalRepresentation> result = qs2.execute(DigitalRepresentation.class);
        if (!result.isEmpty()) {
            DigitalRepresentation representationToDelete = result.get(0);
            representationToDelete.delete();
        } else {
            throw new QueryEvaluationException("MediaID " + mediaID + " is not existent!");
        }
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media id.
     *
     * @param mediaID
     * @throws RepositoryException
     * @throws ParseException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public void deleteDigitalRepresentationMedia(String mediaID) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        Anno4j anno4j = getAnno4j();
        mediaQueryService(mediaID, anno4j);
    }

    /**
     * ATM mainly here for test purposes. Do not like this, change possibility?
     */
    public Anno4j getAnno4j() {
        return this.anno4j;
    }
}
