package rest.web.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rest.application.exception.MetadataNotFoundException;
import rest.application.exception.MetadataQueryException;
import rest.application.exception.ObjectClassNotFoundException;
import rest.service.DigitalRepresentationService;
import rest.service.ObjectService;

/**
 * Controller Class for the Object Repository
 */
@CrossOrigin
@RestController
@RequestMapping//(value = "/api/")
public class ObjectController {

    @Autowired
    private ObjectService objectService;

    @Autowired
    private DigitalRepresentationService digitalRepresentationService;

    /**
     * Method to return a JSON Representation of an Object with the given wisskiId
     *
     * @param wisskipath WissKI Id of the represented OBJECT
     * @return returns a JSON represented in a String with all the Information belonging to the Object
     */
    @ApiOperation(value = "Get JSON Representation of given object by WissKI View Path",
            notes = "Method to request a JSON representation of a given RDF Visit Resource defined by the given WissKI View Path.")
    @GetMapping(value = "wisskiobject")
    public String getRepresentationOfObjectByWisskiPath(
            @ApiParam(required = true, value = "The wisskiId of the object to request")
            @RequestParam("wisskipath") String wisskipath) throws MetadataQueryException, MetadataNotFoundException {
        return this.objectService.getObjectIdByWisskiPath(wisskipath);
    }

    /**
     * Method to return a Json Representation of an Object with a given Id
     *
     * @param id ID of the represented OBJECT
     * @return returns a JSON represented in a String with all the Information belonging to the Object
     */
    @ApiOperation(value = "Get JSON Representation of given object",
            notes = "Method to request a JSON representation of a given RDF Visit Resource defined by the given objectId.")
    @GetMapping(value = "object")
    public String getRepresentationOfObject(
            @ApiParam(required = true, value = "The ID of the object to request")
            @RequestParam("id") String id) throws ObjectClassNotFoundException {
       return this.objectService.getRepresentationOfObject(id);
    }

    @ApiOperation(value = "Get number of DigRep nodes by objectID",
            notes = "Method to request the number of associated DigitalRepresentations nodes of a given entity with the supported id.")
    @GetMapping(value = "object/amountById")
    public int getDigitalRepresentationsByObjectId(
            @ApiParam(required = true, value = "The ID of the entity node.")
            @RequestParam("id") String id) {
        return this.digitalRepresentationService.getNumberOfDigitalRepresentationsByObjectId(id);
    }

    @ApiOperation(value = "Get number of DigRep nodes by WisskiViewPath",
            notes = "Method to request the number of associated DigitalRepresentations nodes of a given entity with the supported Wisski view path.")
    @GetMapping(value = "object/amountByPath")
    public int getDigitalRepresentationsByWisskiPath(
            @ApiParam(required = true, value = "The Wisski view path of the entity node.")
            @RequestParam("wisskiPath") String wisskipath) {
        return this.digitalRepresentationService.getNumberOfDigitalRepresentationsByWisskiPath(wisskipath);
    }
}
