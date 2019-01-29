package rest.web.controller;

import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.Exception.DigitalRepositoryException;
import rest.service.DigitalRepresentationService;

import java.util.List;

@RestController
@RequestMapping(value = "/digrep/")//produces = "application/json; charset=utf-8")
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
    @ApiOperation(value = "Get single Technical Metadata by mediaID",
            notes = "Method to request a single Technical Metadata String by supporting the ID of the respective DigitalRepresentation node.")
    @GetMapping(value = "media")
    public String getSingleTechnicalMetadataByMediaID(
            @ApiParam(required = true, value = "The ID of the DigitalRepresentation node.")
            @RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     * Controller Method to get all the media represenatation based on a Object ID
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @ApiOperation(value = "Get all Technical Metadata String by objectID",
            notes = "Method to request all Technical Metadata Strings that are associated with a given object node.")
    @GetMapping(value = "object")
    public String getAllTechnicalMetadataStringsByObjectID(
            @ApiParam(required = true, value = "The ID of the object node.")
            @RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.getAllTechnicalMetadataStringsByObjectID(id);
    }

    /**
     * Controller Method to create a new media representation node based on a object id
     *
     * @param id
     * @return
     * @throws DigitalRepositoryException
     */
    @ApiOperation(value = "Create new DigitalRepresentation node",
            notes = "Method to instantiate a new DigitalRepresentation node for a given object node defined by the supported objectID.")
    @PostMapping(value = "object")
    public String createNewDigitalRepresentationNode(
            @ApiParam(required = true, value = "The ID for the object node.")
            @RequestParam("id") String id) throws DigitalRepositoryException {
        return digitalRepresentationService.createNewDigitalRepresentationNode(id);
    }

    // TODO (christian) Bitte den return ändern auf: ID des DigRep Knotens + (neuen) TechMetadata-String
    //TODO nochmal überprüfen wegen der Übertragung im Body als JSON(für das erfolgreiche Updaten  der Metadaten reicht schon ein einfacher String im Body...)
    @ApiOperation(value = "Update DigitalRepresentation node",
            notes = "Method to update a given DigitalRepresentation node, defined by the supported mediaID, by exchanging its Technical Metadata String with the one supported by newData.")
    @PutMapping(value = "media")
    public String updateDigitalRepresentationNode(
            @ApiParam(required = true, value = "The ID of the DigitalRepresentation node")
            @RequestParam("id") String id,
            @ApiParam(value = "The new Technical Metadata String")
            @RequestBody String newData) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return digitalRepresentationService.updateDigitalRepresentationNode(id, newData);
    }

    @ApiOperation(value = "Delete DigitalRepresentation given object and media ID",
            notes = "Method to delete a DigitalRepresentation node, given both the object and media ID.")
    @DeleteMapping(value = "object")
    public ResponseEntity deleteDigitalRepresentationMediaAndObject(
            @ApiParam(required = true, value = "The ID of the object node")
            @RequestParam("objectid") String objectID,
            @ApiParam(required = true, value = "The ID of the DigitalRepresentation node")
            @RequestParam("mediaid") String mediaID) {
        this.digitalRepresentationService.deleteDigitalRepresentationMediaAndObject(objectID, mediaID);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Delete DigitalRepresentation given its media ID",
            notes = "Method to delete a DigitalRepresentation node, given the media ID.")
    @DeleteMapping(value = "media")
    public ResponseEntity deleteDigitalRepresentationMedia (
            @ApiParam(required = true, value = "The ID of the DigitalRepresentation node")
            @RequestParam("id") String mediaID) {
        this.digitalRepresentationService.deleteDigitalRepresentationMedia(mediaID);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
