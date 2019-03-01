package rest.web.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.application.exception.ImportException;
import rest.service.ImportService;

@RestController
@RequestMapping(value = "/import/")
public class ImportController {

    @Autowired
    private ImportService importService;

    @ApiOperation(value = "Import data represented as JSON",
                notes = "Method to import data chungs that are represented in a JSON format. Make sure that the JSON format does correspond to the VisMo JSON Schema!")
    @PostMapping
    public ResponseEntity importJSON(
            @RequestBody String json) throws ImportException {
        this.importService.importJSON(json);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }




//    @ApiOperation(value = "Update DigitalRepresentation node",
//            notes = "Method to update a given DigitalRepresentation node, defined by the supported mediaID, by exchanging its Technical Metadata String with the one supported by newData.")
//    @PutMapping(value = "media")
//    public String updateDigitalRepresentationNode(
//            @ApiParam(required = true, value = "The ID of the DigitalRepresentation node")
//            @RequestParam("id") String id,
//            @ApiParam(value = "The new Technical Metadata String")
//            @RequestBody String newData) throws DigitalRepositoryException, MetadataQueryException {
//        return digitalRepresentationService.updateDigitalRepresentationNode(id, newData);
//    }
}
