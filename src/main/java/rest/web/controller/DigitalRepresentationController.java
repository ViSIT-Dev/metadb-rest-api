package rest.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rest.Exception.DigitalRepositoryException;
import rest.service.DigitalRepresentationService;

import java.util.List;

@RestController
@RequestMapping(value="/api/")//produces = "application/json; charset=utf-8")
public class DigitalRepresentationController {
    @Autowired private DigitalRepresentationService digitalRepresentationService;
    // TODO (Christian) Controller für die DigitalRepresentation Requirements. Nimmt alle HTTP Anfragen entgegen und gibt dementsprechend Aufrufe an den Service weiter

    // TODO Bitte die IDs als Parameter des Requests, nicht als Pfad-Variable übergeben
    /*
     Bezug IDs unserer Metadatenobjekte:
     Ich befürchte, dass wir die IDs nicht direkt als PathVariable übergeben können. Die IDs, mit denen wir arbeiten,
     sehen in der Regel irgendwie so aus: http://visit.de/model/a3k4jdnapwiej.
     Das heißt, dass wir hier ja teils URI Fragmente darin haben. Wenn wir das jetzt in ner Pfad-Variable verwursteln
     könnte das zu Probleme führen, weil die eigentlich URL dann ja nicht mehr zu erkennen ist.
      */

    /**
     * @param id Gives the Media id for Access on the Repository
     * @return returns a String of the Metadata found.
     * @throws DigitalRepositoryException
     */

    // TODO Christian: Controller Methoden auf ID als RequestParameter umschreiben, da wir diese nicht wie bisher als Pfadvariablen benutzen können
    // TODO Hier ist ein Beispiel dafür: https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/spring-mvc-request-param.html
    @RequestMapping(value="media")
    public String getSingleTechnicalMetadataByMediaID(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @GetMapping(value="object")
    public List<String> getAllTechnicalMetadataStringsByObjectID(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getAllTechnicalMetadataStringsByObjectID(id);
    }
    @PostMapping(value="object")
    public String createNewDigitalRepresentationNode(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.createNewDigitalRepresentationNode(id);
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
