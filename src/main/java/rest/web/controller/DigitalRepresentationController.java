package rest.web.controller;

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

    /**
     * Controller Method to get a single media representation based on a media id
     *
     * @param id Gives the Media id for Access on the Repository
     * @return returns a String of the Metadata found.
     * @throws DigitalRepositoryException
     */

    // TODO (Christian): Hier fehlt noch die HTTP Methode
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

/*    @DeleteMapping(value = "media")
    public void deleteDigitalRepresentationMediaAndObject(@RequestParam("id") String mediaID, @RequestParam("id") String objectID) {
        digitalRepresentationService.deleteDigitalRepresentationMediaAndObject(mediaID, objectID);
    }
    */

    @DeleteMapping(value = "media")
    public void deleteDigitalRepresentationMedia(@RequestParam("id") String mediaID) {
        digitalRepresentationService.deleteDigitalRepresentationMedia(mediaID);
    }
}
