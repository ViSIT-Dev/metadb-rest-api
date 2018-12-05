package rest.web.controller;

import org.apache.jena.atlas.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.Exception.ObjectRepositoryException;
import rest.service.ObjectRepositoryService;

/**
 * Controller Class for the Object Repository
 */
@RestController
@RequestMapping(value = "/api/")
public class ObjectRepositoryController {
    @Autowired
    ObjectRepositoryService objectRepositoryService;

    /**
     * Method to return a Json Representation of an Object with a given ID
     * @param id
     * @return
     */
    @GetMapping(value = "object")
    public JsonObject getRepresentationOfObject(@RequestParam("id") String id) throws ObjectRepositoryException {
        return this.objectRepositoryService.getRepresentationOfObject(id);
    }
}
