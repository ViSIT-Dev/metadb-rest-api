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

    @GetMapping(value = "media")
    public String getSingleTechnicalMetadataByMediaID(@RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    // TODO (Christian) Bitte den return der Methode umschreiben: Soll zu jedem TechMetadata-String auch die ID des DigRep-Entity zurückgeben (in einer JSON Liste)
    /**
     * Controller Method to get all the media represenatation based on a Object ID
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @GetMapping(value = "object")
    public String getAllTechnicalMetadataStringsByObjectID(@RequestParam("id") String id) throws DigitalRepositoryException {
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

    // TODO (christian) Bitte den return ändern auf: ID des DigRep Knotens + (neuen) TechMetadata-String
    //TODO nochmal überprüfen wegen der Übertragung im Body als JSON(für das erfolgreiche Updaten  der Metadaten reicht schon ein einfacher String im Body...)
    @PutMapping(value = "media")
    public String updateDigitalRepresentationNode(@RequestParam("id") String id, @RequestBody String newData) {
      return digitalRepresentationService.updateDigitalRepresentationNode(id, newData);
    }

    @DeleteMapping(value = "object")
    public String deleteDigitalRepresentationMediaAndObject(@RequestParam("objectid") String objectID,@RequestParam("mediaid") String mediaID) {
       return digitalRepresentationService.deleteDigitalRepresentationMediaAndObject(objectID, mediaID);
    }

    @DeleteMapping(value = "media")
    public String deleteDigitalRepresentationMedia(@RequestParam("id") String mediaID) {
      return  digitalRepresentationService.deleteDigitalRepresentationMedia(mediaID);
    }
}
