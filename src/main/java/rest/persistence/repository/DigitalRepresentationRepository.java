package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Resource;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
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

    /**
     * Private method that queries the technicalMetadata Strings given an ID of a vismo:Resource entity.
     *
     * @param id The ID of the vismo:Resource from which the technicalMetadata is to be queried.
     * @return A list of Strings that represent the technicalMetadata for the searched vismo:Resource entity.
     */
    public String getAllTechnicalMetadataStringsByObjectID(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException, ParseException {
        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", id);

        List<Resource> result = qs.execute(Resource.class);

        Resource resource = result.get(0);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JSONVISMO.OBJECT_ID, id);

        JsonArray jsonArray = new JsonArray();

        for(DigitalRepresentation digrep : resource.getDigitalRepresentations()) {
            JsonObject digrepObject = new JsonObject();

            digrepObject.addProperty(JSONVISMO.MEDIA_ID, digrep.getResourceAsString());
            digrepObject.addProperty(JSONVISMO.TECHNICAL_METADATA_STRING, digrep.getTechnicalMetadata());

            jsonArray.add(digrepObject);
        }

        jsonObject.add(JSONVISMO.DIGITAL_REPRESENTATIONS, jsonArray);

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
