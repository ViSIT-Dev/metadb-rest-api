package rest.service;

import com.github.anno4j.model.impl.ResourceObject;
import com.google.gson.JsonObject;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import rest.application.exception.*;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.DigitalRepresentationRepository;

/**
 * Service which does the access on the DigitalRepresentationRepository.
 */
@Service
public class DigitalRepresentationService {

    @Autowired
    private DigitalRepresentationRepository digitalRepresentationRepository;

    @Autowired
    private Anno4jRepository anno4jRepository;

    public int getNumberOfDigitalRepresentationsByObjectId(String id) {
        try {
            return this.digitalRepresentationRepository.getNumberOfDigitalRepresentationsByObjectId(id);
        } catch (Exception e) {
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

    public int getNumberOfDigitalRepresentationsByWisskiPath(String wisskiPath) {
        try {
            String objectIdByWisskiPath = this.anno4jRepository.getObjectIdByWisskiPath(wisskiPath);

            return this.digitalRepresentationRepository.getNumberOfDigitalRepresentationsByObjectId(objectIdByWisskiPath);
        } catch (Exception e) {
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

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
            Class<? extends ResourceObject> clazz = this.anno4jRepository.getLowestClassGivenId(id);

            return digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(id, clazz);
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
    public String createNewDigitalRepresentationNode(@NonNull String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String resourceType = this.anno4jRepository.getLowestClassGivenIdAsString(id);

        if (resourceType.equals(VISMO.RESOURCE) ||
                resourceType.equals(VISMO.ACTIVITY) ||
                resourceType.equals(VISMO.ARCHITECTURE) ||
                resourceType.equals(VISMO.GROUP) ||
                resourceType.equals(VISMO.INSTITUTION) ||
                resourceType.equals(VISMO.OBJECT) ||
                resourceType.equals(VISMO.PERSON) ||
                resourceType.equals(VISMO.PLACE)) {
            try {
                return digitalRepresentationRepository.createNewDigitalRepresentationNode(id, resourceType);
            } catch (RepositoryException | InstantiationException | MalformedQueryException | UpdateExecutionException | IllegalAccessException e) {
                throw new CreateNewDigtialRepresentationNodeException("Something went wrong with the creation of a DigitalRepresentation object.");
            }

        } else {
            throw new CreateNewDigtialRepresentationNodeException("Entity with id " + id + " is not a known vismo:Resource Class.");
        }

    }

    /**
     * Service Method to update a Media Representation by given media ID and the new Dataset.
     *
     * @param mediaId
     * @param newData
     */
    public String updateDigitalRepresentationNode(@NonNull String mediaId, @NonNull String newData) throws MetadataQueryException {
        try {
            this.digitalRepresentationRepository.updateDigitalRepresentationNode(mediaId, newData);
            String resourceId = this.anno4jRepository.getResourceIdByDigitalRepresentationSPARQL(mediaId);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(JSONVISMO.OBJECT_ID, resourceId);
            jsonObject.addProperty(JSONVISMO.MEDIA_ID, mediaId);
            jsonObject.addProperty(JSONVISMO.TECHNICAL_METADATA_STRING, newData);
            return jsonObject.toString();
        } catch (RepositoryException | MalformedQueryException | ParseException | QueryEvaluationException e) {
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
