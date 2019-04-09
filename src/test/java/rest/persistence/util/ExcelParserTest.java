package rest.persistence.util;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Test suite to test the ExcelParser Class.
 */
public class ExcelParserTest {

    @Test
    public void testExcelParserWithActivities() throws IOException, ExcelParserException {
//        File originalFile = new File("src/test/resources/visitExcelActivityTest.xlsx");
//
//        InputStream is = new FileInputStream(originalFile);
//
//        MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);
//
//        ExcelParser parser = new ExcelParser();
//
//        String json = parser.createJSONFromParsedExcelFile(file);
//
//        System.out.println(json);
    }
}