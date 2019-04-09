package rest.persistence.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
}