package misc;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rest.persistence.util.ExcelParser;
import rest.persistence.util.ImportQueryGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This test suite does first experiments with the Architecture Excel of Andrea and Nina.
 */
public class ProductiveArchitectureTest {

    @Ignore
    @Test
    public void testProductiveArchitectureExcel() throws Exception {
        File originalFile = new File("src/test/resources/BurgenVisit.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("BurgenVisit.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

        String updateQueryFromJSON = generator.createUpdateQueryFromJSON(json);

        System.out.println(updateQueryFromJSON);
    }
}
