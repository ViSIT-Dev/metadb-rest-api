package rest.web.controller;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rest.Exception.DigitalRepositoryException;
import rest.service.DigitalRepresentationService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/digrep/")//produces = "application/json; charset=utf-8")
public class DigitalRepresentationController {
    @Autowired
    private DigitalRepresentationService digitalRepresentationService;
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
     * Controller Method to get a single media representation based on a media id
     *
     * @param id Gives the Media id for Access on the Repository
     * @return returns a String of the Metadata found.
     * @throws DigitalRepositoryException
     */

    // TODO Christian: Controller Methoden auf ID als RequestParameter umschreiben, da wir diese nicht wie bisher als Pfadvariablen benutzen können
    // TODO Hier ist ein Beispiel dafür: https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/spring-mvc-request-param.html
    @RequestMapping(value = "media")
    public String getSingleTechnicalMetadataByMediaID(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     * Controller Method to get all the media represenatation based on a Object ID
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @GetMapping(value = "object")
    public List<String> getAllTechnicalMetadataStringsByObjectID(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getAllTechnicalMetadataStringsByObjectID(id);
    }

    /**
     * Controller Method to create a new media representation node based on a object id
     *
     * @param id
     * @return
     * @throws DigitalRepositoryException
     */
    @PostMapping(value = "object")
    public String createNewDigitalRepresentationNode(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.createNewDigitalRepresentationNode(id);
    }

    //TODO nochmal überprüfen wegen der Übertragung im Body als JSON(für das erfolgreiche Updaten  der Metadaten reicht schon ein einfacher String im Body...)
    @PutMapping(value = "media")
    public void updateDigitalRepresentationNode(@RequestParam("id") String id, @RequestBody String newData) {
        digitalRepresentationService.updateDigitalRepresentationNode(id, newData);
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media and Object id.
     *
     * @param mediaID
     * @param objectID
     */
    @DeleteMapping(value = "media")
    public void deleteDigitalRepresentationMediaAndObject(@RequestParam("id") String mediaID, @RequestParam("id") String objectID) {
        digitalRepresentationService.deleteDigitalRepresentationMediaAndObject(mediaID, objectID);
    }

    /**
     * Method to delete a exisitng DigitalRepresentation given the media id.
     *
     * @param mediaID
     */
    @DeleteMapping(value = "media")
    public void deleteDigitalRepresentationMedia(@RequestParam("id") String mediaID) {
        digitalRepresentationService.deleteDigitalRepresentationMedia(mediaID);
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
