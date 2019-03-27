package rest.web.controller;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import model.vismo.Title;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;
import rest.application.exception.MetadataNotFoundException;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ObjectControllerTest extends BaseWebTest {

    private final String standardUrl = "https://database.visit.uni-passau.de/";

    private final static String WISSKI_VIEW_PATH = "http://database.visit.uni-passau.de/drupal/wisski/navigate/178/view";
    private final static String WISSKI_VIEW_PATH2 = "http://database.visit.uni-passau.de/drupal/wisski/navigate/179/view";
    private final static String WISSKI_VIEW_PATH3 = "http://database.visit.uni-passau.de/drupal/wisski/navigate/171/view";
    private final static String WISSKI_VIEW_PATH4 = "http://database.visit.uni-passau.de/drupal/wisski/navigate/172/view";

    private String groupId;
    private String referenceId;
    private String iconographyString;
    private String keywordString1;
    private String keywordString2;
    private String entryId1;
    private String referenceKeyword;
    private String referenceTitle;
    private String referenceSuperordinateTitle;

    @Test
    public void testGetObjectRepresentationByWissKIViewPath() throws Exception {
        // Test data for the getObjectIdByWisskiPath method
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.addKeyword("Keyword");
        group.setIconography("Iconography");

        String updateQuery = "INSERT DATA\n" +
                "{ \n" +
                "  <" + WISSKI_VIEW_PATH2 + "> <http://www.w3.org/2002/07/owl#sameAs> <" + group.getResourceAsString() + "> ." +
                "}";

        Update update = anno4j.getObjectRepository().getConnection().prepareUpdate(updateQuery);

        update.execute();

        String requestURL = standardUrl + "wisskiobject?wisskipath=" + WISSKI_VIEW_PATH2;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();

        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.getString(JSONVISMO.TYPE).equals(VISMO.GROUP));
        assertEquals(group.getResourceAsString(), jsonObject.getString(JSONVISMO.ID));
        assertEquals("Iconography", jsonObject.getString(JSONVISMO.GROUP_ICONOGRAPHY));
        assertTrue(jsonObject.getString(JSONVISMO.GROUP_KEYWORD).contains("Keyword"));
    }

    @Test
    public void testGetObjectRepresentationByWissKIViewPathMixedHttps() throws Exception {
        // Test data for the getObjectIdByWisskiPath method by mixing http and https
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.addKeyword("Keyword");
        group.setIconography("Iconography");

        String updateQuery = "INSERT DATA\n" +
                "{ \n" +
                "  <" + WISSKI_VIEW_PATH3 + "> <http://www.w3.org/2002/07/owl#sameAs> <" + group.getResourceAsString() + "> ." +
                "}";

        Update update = anno4j.getObjectRepository().getConnection().prepareUpdate(updateQuery);

        update.execute();

        String requestPath = WISSKI_VIEW_PATH3.replace("http", "https");

        String requestURL = standardUrl + "wisskiobject?wisskipath=" + requestPath;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();

        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.getString(JSONVISMO.TYPE).equals(VISMO.GROUP));
        assertEquals(group.getResourceAsString(), jsonObject.getString(JSONVISMO.ID));
        assertEquals("Iconography", jsonObject.getString(JSONVISMO.GROUP_ICONOGRAPHY));
        assertTrue(jsonObject.getString(JSONVISMO.GROUP_KEYWORD).contains("Keyword"));
    }

    @Test
    public void testGetObjectRepresentationByWissKIViewPathMixedHttps2() throws Exception {
        // Test data for the getObjectIdByWisskiPath method by mixing http and https
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.addKeyword("Keyword");
        group.setIconography("Iconography");

        String persistanceWisskiPath = WISSKI_VIEW_PATH4.replace("http", "https");

        String updateQuery = "INSERT DATA\n" +
                "{ \n" +
                "  <" + persistanceWisskiPath + "> <http://www.w3.org/2002/07/owl#sameAs> <" + group.getResourceAsString() + "> ." +
                "}";

        Update update = anno4j.getObjectRepository().getConnection().prepareUpdate(updateQuery);

        update.execute();

        String requestURL = standardUrl + "wisskiobject?wisskipath=" + WISSKI_VIEW_PATH4;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();

        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.getString(JSONVISMO.TYPE).equals(VISMO.GROUP));
        assertEquals(group.getResourceAsString(), jsonObject.getString(JSONVISMO.ID));
        assertEquals("Iconography", jsonObject.getString(JSONVISMO.GROUP_ICONOGRAPHY));
        assertTrue(jsonObject.getString(JSONVISMO.GROUP_KEYWORD).contains("Keyword"));
    }

    @Test
    public void testGetObjectRepresentationByWissKIViewPathFailure() throws Exception {
        // Test data for the getObjectIdByWisskiPath method
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.addKeyword("Keyword");
        group.setIconography("Iconography");

        String updateQuery = "INSERT DATA\n" +
                "{ \n" +
                "  <" + WISSKI_VIEW_PATH + "> <http://www.w3.org/2002/07/owl#sameAs> <" + group.getResourceAsString() + "> ." +
                "}";

        Update update = anno4j.getObjectRepository().getConnection().prepareUpdate(updateQuery);

        update.execute();

        String nonExistingWisskiPath = WISSKI_VIEW_PATH.replace("178", "2000");

        String requestURL = standardUrl + "wisskiobject?wisskipath=" + nonExistingWisskiPath;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
    }

    @Test
    public void testGetRepresentationOfReferenceSuccess() throws Exception {
        String requestURL = standardUrl + "object?id=" + this.referenceId;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();

        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);

        assertEquals(VISMO.REFERENCE, jsonObject.getString(JSONVISMO.TYPE));
        assertEquals(this.referenceId, jsonObject.getString(JSONVISMO.ID));
        assertEquals(this.referenceKeyword, jsonObject.getString(JSONVISMO.REFERENCE_KEYWORD));

        JSONObject titleObject = jsonObject.getJSONObject(JSONVISMO.REFERENCE_TITLE);
        assertEquals(this.referenceTitle, titleObject.getString(JSONVISMO.REFERENCE_TITLE_TITLE));
        assertEquals(this.referenceSuperordinateTitle, titleObject.getString(JSONVISMO.REFERENCE_TITLE_SUPERORDINATETITLE));

        JSONArray jsonRefEntries = jsonObject.getJSONArray(JSONVISMO.REFERENCE_ENTRY);
        assertEquals(2, jsonRefEntries.length());

        JSONObject jsonRefEntry = (JSONObject) jsonRefEntries.get(0);
        assertEquals(11, jsonRefEntry.getInt(JSONVISMO.REFERENCE_ENTRY_PAGES));
        assertEquals(VISMO.REFERENCE_ENTRY, jsonRefEntry.getString(JSONVISMO.TYPE));
        assertEquals(this.entryId1, jsonRefEntry.getString(JSONVISMO.ID));
    }

    @Test
    public void getRepresentationOfObjectSuccess() throws Exception {
        String requestURL = standardUrl + "object?id=" + this.groupId;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.getString(JSONVISMO.TYPE).equals(VISMO.GROUP));
        assertEquals(this.groupId, jsonObject.getString(JSONVISMO.ID));
        assertEquals(this.iconographyString, jsonObject.getString(JSONVISMO.GROUP_ICONOGRAPHY));
        assertTrue(jsonObject.getString(JSONVISMO.GROUP_KEYWORD).contains(this.keywordString1));
        assertTrue(jsonObject.getString(JSONVISMO.GROUP_KEYWORD).contains(this.keywordString2));

        JSONArray jsonRefEntries = jsonObject.getJSONArray(JSONVISMO.GROUP_REFENTRY);
        assertEquals(2, jsonRefEntries.length());

        JSONObject jsonRefEntry = (JSONObject) jsonRefEntries.get(0);
        assertEquals(11, jsonRefEntry.getInt(JSONVISMO.GROUP_REFENTRY_PAGES));
        assertEquals(VISMO.REFERENCE_ENTRY, jsonRefEntry.getString(JSONVISMO.TYPE));
    }

    @Test
    public void getRepresentationOfObjectFileNotFound() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound());

    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, MalformedQueryException, UpdateExecutionException {
        Anno4j anno4j = this.objectRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        this.iconographyString = "Iconography";
        group.setIconography(this.iconographyString);
        this.keywordString1 = "Keyword";
        this.keywordString2 = "Keyword2";
        group.addKeyword(this.keywordString1);
        group.addKeyword(this.keywordString2);

        ReferenceEntry entry = anno4j.createObject(ReferenceEntry.class);
        this.entryId1 = entry.getResourceAsString();
        entry.setPages(BigInteger.valueOf(11));

        entry.setIsAbout(group);
        group.addEntry(entry);

        ReferenceEntry entry2 = anno4j.createObject(ReferenceEntry.class);
        entry2.setPages(BigInteger.valueOf(22));

        entry2.setIsAbout(group);
        group.addEntry(entry2);

        Reference reference = anno4j.createObject(Reference.class);
        this.referenceKeyword = "ReferenceKeyword";
        reference.addKeyword(this.referenceKeyword);

        Title title = anno4j.createObject(Title.class);
        this.referenceTitle = "ReferenceTitle";
        title.setTitle(this.referenceTitle);
        this.referenceSuperordinateTitle = "ReferenceSuperordinateTitle";
        title.setSuperordinateTitle(this.referenceSuperordinateTitle);
        reference.setTitle(title);

        reference.addEntry(entry);
        entry.setEntryIn(reference);

        reference.addEntry(entry2);
        entry2.setEntryIn(reference);

        this.groupId = group.getResourceAsString();
        this.referenceId = reference.getResourceAsString();
    }
}