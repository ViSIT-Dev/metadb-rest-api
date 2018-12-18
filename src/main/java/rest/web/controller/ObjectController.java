package rest.web.controller;

import org.apache.jena.atlas.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.Exception.ObjectClassNotFoundException;
import rest.service.ObjectService;

/**
 * Controller Class for the Object Repository
 */
@RestController
@RequestMapping(value = "/api/")
public class ObjectController {

    // TODO (Christian) In "ObjectController" umbenennen

    @Autowired
    ObjectService objectService;

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id
     * @return
     */
    @GetMapping(value = "object")
    public void getRepresentationOfObject(@RequestParam("id") String id) throws ObjectClassNotFoundException {
        this.objectService.getRepresentationOfObject(id);
    }
}
