package rest.web.controller;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test Class with methods in order to test the DigitalRepresentationController.
 */
public class DigitalRepresentationControllerTest extends BaseWebTest {

    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private String urlLikeID = "http://visit.de/model/id1";
    private final String standardUrl = "https://database.visit.uni-passau.de/digrep/";
    private Anno4j anno4j;

    /**
     * Testmethod, expects a DigitalRepositoryException to come with a false given MediaID
     *
     * @throws Exception
     */
    @Test
    public void getSingleTechnicalMetadataByFalseMediaIDTest() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "media?id=" + random;
        mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound());
    }

    /**
     * Test if mediaID in Collection exists if requested by controller
     *
     * @throws Exception
     */
    @Test
    public void getSingleTechnicalMetadataByExistingIDTest() throws Exception {
        String requestURL = standardUrl + "media?id=" + mediaID1;
        performCorrectGetResultTest(requestURL, "test1");
    }

    /**
     * Tests on error if two URL are concatenated
     *
     * @throws Exception
     */
    @Test
    public void testGetSingleTechnicalMetadataByExistingIDWithURLlikeID() throws Exception {
        /*
        Wie vermutet: Mit Anno4j-auto-generierten IDs funktioniert die Pfad-Variable.
        Wenn wir aber eine andere ID setzen, so wie die urlLikeID = "http://visit.de/model/id1", dann klappt die Anfrage nicht
        weil er den Pfad nicht kennt.
        */
        String requestURL = standardUrl + "media?id=" + urlLikeID;
        String result1 = "urlLikeIDtechMetadata";
        performCorrectGetResultTest(requestURL, result1);
    }

    /**
     * Wrapper Method for expected correct Get Request Tests.
     *
     * @param requestURL
     * @param expectedResult
     * @throws Exception
     */
    private void performCorrectGetResultTest(String requestURL, String expectedResult) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertTrue(mvcResultString.contains(expectedResult));
    }

    /**
     * Testmethod, expects a List to come with a ObjectID given.
     */
    @Test
    public void getAllTechnicalMetadataStringsByObjectIDTest() throws Exception {
        String requestURL = standardUrl + "object?id=" + objectID;
        String result1 = "test1";
        String result2 = "test2";
        String result3 = "urlLikeIDtechMetadata";
        this.performCorrectGetResultTest(requestURL, result1);
        this.performCorrectGetResultTest(requestURL, result2);
        this.performCorrectGetResultTest(requestURL, result3);
    }

    /**
     * Testmethod to look for a Object with a random String, expects NotFoundResponse
     */
    @Test
    public void getAllTechnicalMetadataStringsByFalseObjectIDTest() throws Exception {
        /*Erstelle einen zufälligen Alphanumerischen String mit Länge 47*/
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        MvcResult mvcResult = this.mockMvc.perform(get(requestURL))
                .andDo(print()).andExpect(status().isNotFound()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertThat(mvcResultString, Matchers.isEmptyOrNullString());
    }

    /**
     * TestMethod to create a new node with a given ObjectID expects a String with the new DigitalRepresentationID
     *
     * @throws Exception
     */
    @Test
    public void createNewDigitalRepresentationSuccess() throws Exception {
        String requestURL = standardUrl + "object?id=" + objectID;
        MvcResult mvcResult = this.mockMvc.perform(post(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertFalse(mvcResultString.isEmpty());
        String newMediaID = mvcResultString;
        //check in the result if an empty media object is created when requesting the object
        String requestURL1 = standardUrl + "object?id=" + objectID;
        performCorrectGetResultTest(requestURL1, "null");
        //check if with the given new Media id a OK response form the Server comes with an empty body
        String requestURL2 = standardUrl + "media?id=" + newMediaID;
        this.performCorrectGetResultTest(requestURL2, "");
    }

    /**
     * TestMethod to create a new node with a given ObjectID expects a NotFoundResponse
     *
     * @throws Exception
     */
    @Test
    public void createNewDigitalRepresentationFailure() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        MvcResult mvcResult = this.mockMvc.perform(post(requestURL))
                .andDo(print()).andExpect(status().isConflict()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertThat(mvcResultString, Matchers.isEmptyOrNullString());
        //check in the result if an empty media object is not created when requesting the object
        String requestURL1 = standardUrl + "object?id=" + objectID;
        MvcResult mvcResult1 = this.mockMvc.perform(get(requestURL1))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString1 = mvcResult1.getResponse().getContentAsString();
        assertFalse(mvcResultString1.contains("null"));
    }

    /**
     * TestMethod to Update an existing Digital REpresentation with a given DigitalRepresentation String with the new Metadata String Expects the new String t
     * be in the response when same DigitalRepresentation is requested
     *
     * @throws Exception
     */
    @Test
    public void updateDigitalRepresentationSuccess() throws Exception {
        String requestURL = standardUrl + "media?id=" + this.mediaID1;

        String newData = "newDataInput";
        //Hole das existierende DigitalRepresentation Objekt und schreibe Metadaten neu um  späte diese als JSON Bean überzugeben.
        /*this.anno4j = this.digitalRepresentationRepository.getAnno4j();
        QueryService qs = anno4j.createQueryService();
        qs.addCriteria(".", this.mediaID1);
        DigitalRepresentation digitalRepresentation = qs.execute(DigitalRepresentation.class).get(0);
        digitalRepresentation.setTechnicalMetadata(newData);
        Gson gson = new Gson();
        String digitalRepresenation  =  gson.toJson(digitalRepresentation);*/
        MvcResult mvcResult = this.mockMvc.perform(put(requestURL)
                .content(newData))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.contains(this.mediaID1));
        this.performCorrectGetResultTest(requestURL, newData);
    }

    /**
     * TestMethod to Update an existing Digital REpresentation with a given DigitalRepresentation JSON with the new Metadata String Expects the new String t
     * be in the response when same DigitalRepresentation is requested
     *
     * @throws Exception
     */
    @Test
    public void updateDigitalRepresentationFailure() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "media?id=" + random;
        String newData = "newDataInput";
        this.mockMvc.perform(put(requestURL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(newData))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn();
        this.mockMvc.perform(get(requestURL))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDigitalRepresentationNodeMediaSuccess() throws Exception {
        String requestURL = standardUrl + "media?id=" + this.mediaID1;
        MvcResult mvcResult = mockMvc.perform(delete(requestURL)).andDo(print()).andExpect(status().isNoContent()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.isEmpty());
        mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void deleteDigitalRepresentationNodeMediaFailure() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "media?id=" + random;
        mockMvc.perform(delete(requestURL)).andDo(print()).andExpect(status().isConflict());
    }

    @Test
    public void deleteDigitalRepresentationNodeObjectMediaSuccess() throws Exception {
        String requestURL = standardUrl + "object?objectid=" + this.objectID + "&mediaid=" + this.mediaID1;
        String requestURL2 = standardUrl + "media?id=" + this.mediaID1;
        MvcResult mvcResult = mockMvc.perform(delete(requestURL)).andDo(print()).andExpect(status().isNoContent()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.isEmpty());
        mockMvc.perform(get(requestURL2)).andDo(print()).andExpect(status().isNotFound());
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        /*
         Alle Repositories sind in der "BaseWebTest" Klasse definiert. Auf diese kannst Du also von überall aus drauf
         zugreifen, um entsprechende Testdaten einzufügen. Hier haben wir also eine Resource (ID: objectID) mit zwei
         DigitalRepresentation Objekten (IDs: mediaID1 und mediaID2).
         Auf diese Daten solltest Du dann Deine Tests aufbauen.
          */
        this.anno4j = this.digitalRepresentationRepository.getAnno4j();
        Resource resource = anno4j.createObject(Resource.class);
        this.objectID = resource.getResourceAsString();
        DigitalRepresentation rep1 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID1 = rep1.getResourceAsString();
        String test = "test1";
        rep1.setTechnicalMetadata(test);
        DigitalRepresentation rep2 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID2 = rep2.getResourceAsString();
        String test2 = "test2";
        rep2.setTechnicalMetadata(test2);
        /*
        So erstellst Du ein Objekt mit eigens gewählter ID (hinten per "urlLikeID", kann aber auch beliebiger String sein).
        Da es eine Semantic Web URI sein muss, brauchen wir gewisse URL Requirements, http, slashes, etc.
         */
        DigitalRepresentation repWithURLlikeID = anno4j.createObject(DigitalRepresentation.class, (org.openrdf.model.Resource) new URIImpl(urlLikeID));
        repWithURLlikeID.setTechnicalMetadata("urlLikeIDtechMetadata");
        resource.addDigitalRepresentation(rep1);
        resource.addDigitalRepresentation(rep2);
        resource.addDigitalRepresentation(repWithURLlikeID);
    }
}