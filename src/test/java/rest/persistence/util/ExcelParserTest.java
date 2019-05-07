package rest.persistence.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.vismo.Activity;
import model.vismo.Place;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.query.Update;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite to test the ExcelParser Class.
 */
public class ExcelParserTest {

   @Test
    public void testExcelParserWithActivityWithLinksAndQuery() throws Exception {
       File originalFile = new File("src/test/resources/visitExcelActivityWithLinksTest.xlsx");

       InputStream is = new FileInputStream(originalFile);

       MultipartFile file = new MockMultipartFile("visitExcelActivityWithLinksTest.xlsx", is);

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

       List<Activity> activities = qs.execute(Activity.class);

       Activity activity = activities.get(0);

       QueryService qs2 = anno4j.createQueryService();

       List<Place> places = qs2.execute(Place.class);

       Place place = places.get(0);

       assertEquals(activity.getP7TookPlaceAt().getResourceAsString(), place.getResourceAsString());
   }

    @Test
    public void testExcelParserWithActivityAndGroup() throws IOException, ExcelParserException, JSONException {
        File originalFile = new File("src/test/resources/visitExcelActivityAndGroupTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("visitExcelActivityAndGroupTest.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray groupArray = jsonObject.getJSONArray("Group");

        assertEquals(1, groupArray.length());

        JSONObject group = groupArray.getJSONObject(0);

        assertEquals("name", group.getString("group_idby_actorappel"));
        assertEquals("auftrag", group.getString("group_motiv_structevol"));
        assertEquals("formerobject", group.getString("group_lostcustodyof_object"));

        JSONObject reference = group.getJSONObject("group_refentry");

        assertEquals("titel", reference.getString("group_refentry_in_reference"));
        assertEquals("seiten", reference.getString("group_refentry_pages"));
    }

    @Test
    public void testExcelParserWithSingleGroup() throws IOException, ExcelParserException, JSONException {
        File originalFile = new File("src/test/resources/visitExcelGroupTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("visitExcelGroupTest.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray groupArray = jsonObject.getJSONArray("Group");

        assertEquals(1, groupArray.length());

        JSONObject group = groupArray.getJSONObject(0);

        assertEquals("name", group.getString("group_idby_actorappel"));
        assertEquals("auftrag", group.getString("group_motiv_structevol"));
        assertEquals("formerobject", group.getString("group_lostcustodyof_object"));

        JSONObject reference = group.getJSONObject("group_refentry");

        assertEquals("titel", reference.getString("group_refentry_in_reference"));
        assertEquals("seiten", reference.getString("group_refentry_pages"));
    }

    @Test
    public void testExcelParserWithActivities() throws IOException, ExcelParserException, JSONException {
        File originalFile = new File("src/test/resources/visitExcelActivityTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray activityArray = jsonObject.getJSONArray("Activity");

        assertEquals(1, activityArray.length());

        JSONObject activity = activityArray.getJSONObject(0);

        assertEquals("titel", activity.getString("activity_idby_title"));
        assertEquals("beschr", activity.getString("activity_description"));
        assertEquals("object", activity.getString("activity_used_object"));

        JSONObject dating = activity.getJSONObject("activity_dating");

        assertEquals("genau", dating.getString("activity_dating_exact"));
        assertEquals("1/1/01", dating.getString("activity_dating_start"));
    }

    @Test
    public void testExcelParserWithTwoActivities() throws IOException, ExcelParserException, JSONException {
        File originalFile = new File("src/test/resources/visitExcelTwoActivityTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);

        ExcelParser parser = new ExcelParser();

        String json = parser.createJSONFromParsedExcelFile(file);

        System.out.println(json);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray activityArray = jsonObject.getJSONArray("Activity");

        assertEquals(2, activityArray.length());

        JSONObject activity = activityArray.getJSONObject(0);

        assertEquals("titel", activity.getString("activity_idby_title"));
        assertEquals("beschr", activity.getString("activity_description"));
        assertEquals("object", activity.getString("activity_used_object"));

        JSONObject dating = activity.getJSONObject("activity_dating");

        assertEquals("genau", dating.getString("activity_dating_exact"));
        assertEquals("1/1/01", dating.getString("activity_dating_start"));

        JSONObject activity2 = activityArray.getJSONObject(1);

        assertEquals("titel2", activity2.getString("activity_idby_title"));
        assertEquals("beschr2", activity2.getString("activity_description"));
        assertEquals("object2", activity2.getString("activity_used_object"));

        JSONObject dating2 = activity2.getJSONObject("activity_dating");

        assertEquals("genau2", dating2.getString("activity_dating_exact"));
        assertEquals("1/1/01", dating2.getString("activity_dating_start"));

        JSONObject refentry2 = activity2.getJSONObject("activity_refentry");

        assertEquals("titelz2", refentry2.getString("activity_refentry_in_reference"));
        assertEquals("pages2", refentry2.getString("activity_refentry_pages"));
    }
}