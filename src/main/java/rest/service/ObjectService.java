package rest.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.json.JsonObject;
import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.Exception.ObjectClassNotFoundException;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.ObjectRepository;

import java.io.FileNotFoundException;

/**
 * Service Class for the Object Repository
 */
@Service
public class ObjectService {

    // TODO (Christian) In "ObjectService" umbenennen

    // TODO (Christian) Anno4jRepository autowiren

    @Autowired
    ObjectRepository objectRepository;
    @Autowired
    Anno4jRepository anno4jRepository;

    // TODO (Christian) Zu implementierende Methode: Zuerst im Anno4jRepo die Klasse anfragen (wird als String zurück gegeben), dann beide Infos (ID + Klassennamen) ins ObjectRepo weiter geben (-> Dort auch Methode anpassen dafür)

    // TODO (Christian) Rückgabewert der Methode kann ein String sein, der das JSON repräsentieren wird

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id
     * @return
     */
    public void getRepresentationOfObject(String id) {
        try {
            String className = anno4jRepository.getLowestClassGivenId(id);
            String classNameShortened = className.replaceAll("[a-z]{4}://[a-z]{3}.[a-z]{5}/[a-z]{10}/[a-z]{5}/","");
           // http://www.visit.de/ontologies/vismo/
            objectRepository.getRepresentationOfObject(id, classNameShortened);
        } catch (OpenRDFException e) {
            throw new ObjectClassNotFoundException(e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
