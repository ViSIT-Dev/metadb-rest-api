package rest.web.controller;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/download/")
public class DownloadController {

    private final static String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @GetMapping
    public @ResponseBody Resource download(HttpServletResponse response) {


        return null;
    }
}
