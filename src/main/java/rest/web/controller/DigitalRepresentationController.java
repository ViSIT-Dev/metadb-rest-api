package rest.web.controller;

import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rest.service.DigitalRepresentationService;

import java.util.List;

/**
 * Created by Manu on 31.10.18.
 */
@Controller
@RequestMapping(value="")
public class DigitalRepresentationController {
    @Autowired
    DigitalRepresentationService digitalRepresentationService;
    // TODO (Christian) Controller für die DigitalRepresentation Requirements. Nimmt alle HTTP Anfragen entgegen und gibt dementsprechend Aufrufe an den Service weiter

    /**
     *
     * @param id Gives the id for the Access on the Digital Representation Object Repository.
     * @return returns a List of Strings of the Metadata found.
     * @throws OpenRDFException
     */
    @GetMapping(value="/{id}")
    public List<String> getTechnicalMetaDataStrings(@PathVariable("id") String id) throws OpenRDFException {
        return digitalRepresentationService.getTechnicalMetadataStrings(id);
    }
}
