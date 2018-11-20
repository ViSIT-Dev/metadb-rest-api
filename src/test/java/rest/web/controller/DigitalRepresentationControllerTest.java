package rest.web.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;
import rest.Exception.DigitalRepositoryException;
import rest.persistence.repository.DigitalRepresentationRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

/**
 * Test Class with methods in order to test the DigitalRepresentationController.
 */
public class DigitalRepresentationControllerTest extends BaseWebTest {

    // TODO (Christian) Bitte hier tests entwickeln, die die Funktionalitäten um die DigitalRepresentation Requirements abdecken
    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private String urlLikeID = "http://visit.de/model/id1";
    private final String standardUrl = "https://database.visit.uni-passau.de/api/";

    /**
     * Testmethod, expects a DigitalRepositoryException to come with a false given MediaID
     *
     * @throws Exception
     */
    @Test
    public void getSingleTechnicalMetadataByFalseMediaIDTest() throws Exception {
        /*Erstelle einen zufälligen Alphanumerischen String mit Länge 47*/
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
        MvcResult mvcResult = this.mockMvc.perform(get(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertTrue(mvcResultString.contains("test1"));
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
        MvcResult mvcResult = this.mockMvc.perform(get(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertTrue(mvcResultString.contains("urlLikeIDtechMetadata"));
    }

    /**
     * Testmethod, expects a List to come with a ObjectID given
     */
    @Test
    public void getAllTechnicalMetadataStringsByObjectIDTest() throws Exception {
        String requestURL = standardUrl + "object?id=" + objectID;
        MvcResult mvcResult = this.mockMvc.perform(get(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertTrue(mvcResultString.contains("urlLikeIDtechMetadata"));
        assertTrue(mvcResultString.contains("test1"));
        assertTrue(mvcResultString.contains("test2"));
    }

    /**
     * Testmethod to look for a, expects NotFoundResponse
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
     * @throws Exception
     */
    @Test
    public void createNewDigitalRepresentationSuccess() throws Exception {
        String requestURL = standardUrl + "object?id=" + objectID;
        MvcResult mvcResult = this.mockMvc.perform(post(requestURL))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertFalse(mvcResultString.isEmpty());
    }

    /**
     * TestMethod to create a new node with a given ObjectID expects a NotFoundResponse
     * @throws Exception
     */
    @Test
    public void createNewDigitalRepresentationFailure() throws Exception {
        String random =  RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        MvcResult mvcResult = this.mockMvc.perform(post(requestURL))
                .andDo(print()).andExpect(status().isNotFound()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertThat(mvcResultString, Matchers.isEmptyOrNullString());
    }

    /*@Test
    private void checkReferencesOnObject(String objectID) throws Exception {
        String requestURL = standardUrl + "object?id="+ objectID;
        MvcResult mvcResult = this.mockMvc.perform((get(requestURL)))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertTur

    }*/

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        /*
         Alle Repositories sind in der "BaseWebTest" Klasse definiert. Auf diese kannst Du also von überall aus drauf
         zugreifen, um entsprechende Testdaten einzufügen. Hier haben wir also eine Resource (ID: objectID) mit zwei
         DigitalRepresentation Objekten (IDs: mediaID1 und mediaID2).
         Auf diese Daten solltest Du dann Deine Tests aufbauen.
          */
        Anno4j anno4j = this.digitalRepresentationRepository.getAnno4j();
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