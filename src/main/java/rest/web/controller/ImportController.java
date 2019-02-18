package rest.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.service.ImportService;

//@RestController
//@RequestMapping(value = "/import/")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping
    public void importJSON(
            @RequestBody String json) {
        this.importService.importJSON(json);


    }
}
