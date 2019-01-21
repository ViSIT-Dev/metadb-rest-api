package rest.web.controller;

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

    @Autowired
    private ObjectService objectService;

    /**
     * Method to return a Json Representation of an Object with a given ID
     *
     * @param id ID of the represented OBJECT
     * @return returns a JSON represented in a String with all the Information belonging to the Object
     */
    @GetMapping(value = "object")
    public String getRepresentationOfObject(@RequestParam("id") String id) throws ObjectClassNotFoundException {
       return this.objectService.getRepresentationOfObject(id);
    }
}
