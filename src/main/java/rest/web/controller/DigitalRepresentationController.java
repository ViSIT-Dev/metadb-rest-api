package rest.web.controller;

import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rest.Exception.DigitalRepositoryException;
import rest.service.DigitalRepresentationService;

import java.util.List;

@Controller
@RequestMapping(value="https://database.visit.uni-passau.de/api/")
public class DigitalRepresentationController {
    @Autowired private DigitalRepresentationService digitalRepresentationService;
    // TODO (Christian) Controller für die DigitalRepresentation Requirements. Nimmt alle HTTP Anfragen entgegen und gibt dementsprechend Aufrufe an den Service weiter

    /**
     *
     * @param id Gives the Media id for Access on the Repository
     * @return returns a String of the Metadata found.
     * @throws DigitalRepositoryException
     */
    @GetMapping(value="media/{id}")
    public String getSingleTechnicalMetadataByMediaID(@PathVariable("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @GetMapping(value="object/{id}")
    public List<String> getAllTechnicalMetadataStringsByObjectID(@PathVariable("id") String id) throws DigitalRepositoryException{
        return digitalRepresentationService.getAllTechnicalMetadataStringsByObjectID(id);
    }



    /*
    HTTP Requirements (neu, Stand 8.11.18):
    Standard-Pfad: https://database.visit.uni-passau.de/api/
    - GET:  Gegeben objectID, liefere alle technischen Metadaten    Pfad: standard/object
    - GET:  Gegeben medienID, liefere entsprechende (einzelne) technische Metadata  Pfad: standard/media
    - POST: Gegeben objectID, erzeuge neuen DigitalRepresentation Knoten und liefere medienID (ID des Knotens) zurück   Pfad: standard/object
    - PUT:  Gegeben medienID und neues JSON (technische Metadaten), update entsprechende technische Metadaten   Pfad: standard/media
     */
}
