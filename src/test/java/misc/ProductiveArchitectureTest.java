package misc;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.vismo.Architecture;
import org.junit.Test;
import org.openrdf.query.Update;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rest.persistence.util.ExcelParser;
import rest.persistence.util.ImportQueryGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test suite does first experiments with the Architecture Excel of Andrea and Nina.
 */
public class ProductiveArchitectureTest {

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

        Anno4j anno4j = new Anno4j(false);

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(updateQueryFromJSON);
        update.execute();

        QueryService qs = anno4j.createQueryService();

        List<Architecture> result = qs.execute(Architecture.class);

        assertEquals(259, result.size());
    }

    @Test
    public void testSmallProductiveArchitectureExcel() throws Exception {
        File originalFile = new File("src/test/resources/BurgenVisit4.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("BurgenVisit4.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

        String updateQueryFromJSON = generator.createUpdateQueryFromJSONIntoContext(json, "http://visit.de/data/");

        System.out.println(updateQueryFromJSON);

        Anno4j anno4j = new Anno4j(false);

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(updateQueryFromJSON);
        update.execute();

        QueryService qs = anno4j.createQueryService();

        List<Architecture> result = qs.execute(Architecture.class);

        assertEquals(64, result.size());
    }
}
