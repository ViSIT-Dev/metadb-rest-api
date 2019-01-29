package rest.service;

import com.google.gson.JsonObject;
import model.namespace.JSONVISMO;
import org.apache.jena.atlas.json.JSON;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import rest.Exception.CreateNewDigtialRepresentationNodeException;
import rest.Exception.DeleteDigitalRepresentationException;
import rest.Exception.DigitalRepositoryException;
import rest.Exception.UpdateDigitalRepositoryException;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.DigitalRepresentationRepository;

import java.util.List;

/**
 * Service which does the access on the DigitalRepresentationRepository.
 */
@Service
public class DigitalRepresentationService {

    @Autowired
    private DigitalRepresentationRepository digitalRepresentationRepository;

    @Autowired
    private Anno4jRepository anno4jRepository;

    /**
     * Service Method to get a media representation with given media id.
     *
     * @param id Gives the Media id for Access on the Repository.
     * @return returns a String of the Metadata found.
     */
    public String getSingleTechnicalMetadataByMediaID(@NonNull String id) {
        try {
            return digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(id);
        } catch (Exception e) {
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

    /**
     * Servce Method to get a List of media representaions by given object id.
     *
     * @param id Gives the Object id for Access on the repository.
     * @return returns a List of Strings of the Metadata found.
     */
    public String getAllTechnicalMetadataStringsByObjectID(@NonNull String id) {
        try {
            return digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(id);
        } catch (Exception e) {
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

    /**
     * Service Method to create a new Media Representation node by given Object ID.
     *
     * @param id
     * @return
     */
    public String createNewDigitalRepresentationNode(@NonNull String id) {
        try {
            return digitalRepresentationRepository.createNewDigitalRepresentationNode(id);
        } catch (Exception e) {
            throw new CreateNewDigtialRepresentationNodeException(e.getMessage());
        }
    }

    /**
     * Service Method to update a Media Representation by given media ID and the new Dataset.
     *
     * @param mediaId
     * @param newData
     */
    public String updateDigitalRepresentationNode(@NonNull String mediaId, @NonNull String newData) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        try {
            this.digitalRepresentationRepository.updateDigitalRepresentationNode(mediaId, newData);
            String resourceId = this.anno4jRepository.getResourceIdByDigitalRepresentation(mediaId);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(JSONVISMO.OBJECT_ID, resourceId);
            jsonObject.addProperty(JSONVISMO.MEDIA_ID, mediaId);
            jsonObject.addProperty(JSONVISMO.TECHNICAL_METADATA_STRING, newData);
            return jsonObject.toString();
        } catch (Exception e) {
            throw new UpdateDigitalRepositoryException(e.getMessage());
        }
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media and Object id.
     *
     * @param mediaID
     * @param objectID
     */
    public void deleteDigitalRepresentationMediaAndObject(@NonNull String objectID, @NonNull String mediaID) {
        try {
            this.digitalRepresentationRepository.deleteDigitalRepresentationMediaAndObject(objectID, mediaID);
        } catch (Exception e) {
            throw new DeleteDigitalRepresentationException(e.getMessage());
        }
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media id.
     *
     * @param mediaID
     */
    public void deleteDigitalRepresentationMedia(String mediaID) {
        try {
            this.digitalRepresentationRepository.deleteDigitalRepresentationMedia(mediaID);
        } catch (Exception e) {
            throw new DeleteDigitalRepresentationException(e.getMessage());
        }
    }

}
