package rest.service;

import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.application.exception.MetadataQueryException;
import rest.application.exception.ObjectClassNotFoundException;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.ObjectRepository;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Service Class for the Object Repository
 */
@Service
public class ObjectService {

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private Anno4jRepository anno4jRepository;

    public String getObjectIdByWisskiPath(String wisskiPath) throws MetadataQueryException {
        try {
            String id = this.anno4jRepository.getObjectIdByWisskiPath(wisskiPath);

            return this.getRepresentationOfObject(id);
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
            throw new MetadataQueryException("Wisski Path: " + wisskiPath + " non-existent or not mapped to a vismo:Resource.");
        }
    }

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id ID of the represented OBJECT
     * @return returns a JSON represented in a String with all the Information belonging to the Object
     */
    public String getRepresentationOfObject(String id) {
        String result = null;
        try {
            String className = anno4jRepository.getLowestClassGivenId(id);
            String toRemoveChar = "http://visit.de/ontologies/vismo/";
            String classGetFile = this.withoutString(className, toRemoveChar);
            result = objectRepository.getRepresentationOfObject(id, classGetFile);
        } catch (OpenRDFException | IOException | ClassNotFoundException | ParseException e ) {
            throw new ObjectClassNotFoundException(e.getMessage());
        }
        return result;
    }

    /**
     * Wrapper Method to replace content of String with another Substring
     *
     * @param base   String on which the Substring should be removed
     * @param remove Substring which should be removed
     * @return String which contains the String without the Substring
     */
    private String withoutString(String base, String remove) {
        return Pattern.compile(Pattern.quote(remove), Pattern.CASE_INSENSITIVE).matcher(base).replaceAll("");
    }
}
