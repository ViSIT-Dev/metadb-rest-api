package rest.web.controller;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DownloadController {

    private final static String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response) {
    	 try {
    		 response.setContentType(EXCEL_MIME);
    		 File downloadFile = new File("templates/visitExcel.xlsx");
    	      // get your file as InputStream
    	      InputStream is = new FileInputStream(downloadFile);
    	      
    	      response.setContentLength((int) downloadFile.length());     
    	      response.setHeader("Content-Disposition", String.format("inline; filename=\"" + downloadFile.getName() +"\""));
    	      
    	    //Copy bytes from source to destination(outputstream in this example), closes both streams.
    	     FileCopyUtils.copy(is, response.getOutputStream());
    	    } catch (IOException ex) {
    	      throw new RuntimeException("IOError writing file to output stream");
    	    }
    }
}
