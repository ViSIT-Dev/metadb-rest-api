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

    // TODO (Christian) In "ObjectService" umbenennen

    // TODO (Christian) Anno4jRepository autowiren

    @Autowired
    ObjectRepository objectRepository;

    // TODO (Christian) Zu implementierende Methode: Zuerst im Anno4jRepo die Klasse anfragen (wird als String zurück gegeben), dann beide Infos (ID + Klassennamen) ins ObjectRepo weiter geben (-> Dort auch Methode anpassen dafür)

    // TODO (Christian) Rückgabewert der Methode kann ein String sein, der das JSON repräsentieren wird

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
