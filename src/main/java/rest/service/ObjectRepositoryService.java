package rest.service;

import org.apache.jena.atlas.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.Exception.ObjectRepositoryException;
import rest.persistence.repository.ObjectRepository;

/**
 * Service Class for the Object Repository
 */
@Service
public class ObjectRepositoryService {
    @Autowired
    ObjectRepository objectRepository;

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id
     * @return
     */
    public JsonObject getRepresentationOfObject(String id) {
        try {
            return objectRepository.getRepresentationOfObject(id);
        } catch (Exception e) {
            throw new ObjectRepositoryException(e.getMessage());
        }
    }
}
