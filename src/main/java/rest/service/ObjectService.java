package rest.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.json.JsonObject;
import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.Exception.ObjectClassNotFoundException;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.ObjectRepository;

import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

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
    public String getRepresentationOfObject(String id) {
        String result = null;
        try {
            String className = anno4jRepository.getLowestClassGivenId(id);
            String toRemoveChar = "http://visit.de/ontologies/vismo/";
            String classGetFile = this.withoutString(className, toRemoveChar);
            result = objectRepository.getRepresentationOfObject(id, classGetFile);
        } catch (OpenRDFException e) {
            throw new ObjectClassNotFoundException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Wrapper Method to remove Content of String
     * @param base
     * @param remove
     * @return
     */
    private String withoutString(String base, String remove) {
        return Pattern.compile(Pattern.quote(remove), Pattern.CASE_INSENSITIVE).matcher(base).replaceAll("");
    }
}