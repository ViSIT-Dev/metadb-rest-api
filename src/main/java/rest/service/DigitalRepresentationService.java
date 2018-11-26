package rest.service;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import rest.Exception.DigitalRepositoryException;
import rest.Exception.UpdateDigitalRepositoryException;
import rest.persistence.repository.DigitalRepresentationRepository;

import java.util.List;

/**
 * Service which does the access on the DigitalRepresentationRepository.
 */
@Service
public class DigitalRepresentationService {

    // TODO Ganz wichtig! Autowiring nicht vergessen :)
    @Autowired
    private DigitalRepresentationRepository digitalRepresentationRepository;

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
    public List<String> getAllTechnicalMetadataStringsByObjectID(@NonNull String id) {
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
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

    /**
     * Service Method to update a Media Representation by given media ID and the new Dataset.
     *
     * @param mediaId
     * @param newData
     */
    public void updateDigitalRepresentationNode(@NonNull String mediaId, @NonNull JsonObject newData) {
        String newDataString = newData.toString();
        try {
            digitalRepresentationRepository.updateDigitalRepresentationNode(mediaId, newDataString);
        } catch (Exception e) {
            throw new UpdateDigitalRepositoryException(e.getMessage());
        }
    }

}
