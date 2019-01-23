package rest.web.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.anno4j.Anno4j;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import model.vismo.Group;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;
import rest.Exception.ObjectClassNotFoundException;

import javax.validation.constraints.AssertFalse;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ObjectControllerTest extends BaseWebTest {

    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private String urlLikeID = "http://visit.de/model/id1";
    private final String standardUrl = "https://database.visit.uni-passau.de/";
    private Anno4j anno4j;
    private String groupId;

    @Test
    public void getRepresentationOfObjectSuccess() throws Exception {
        createTestModelForGroup();
        String requestURL = standardUrl + "object?id=" + this.groupId;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.get("type".substring(0,"type".length()-1)).toString().contains("Group"));
        JSONObject jsonObject1 = jsonObject.getJSONObject("group_refentry");
        assertTrue(jsonObject1.get("type".substring(0,"type".length()-1)).toString().contains("Group"));

    }

    @Test
    public void getRepresentationOfObjectFileNotFound() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound());

    }

    public void createTestModelForGroup() throws RepositoryException, IllegalAccessException, InstantiationException {
        Group group = anno4j.createObject(Group.class);
        group.setIconography("Iconography");
        //group.setKeyword("Keyword");

        this.groupId = group.getResourceAsString();
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