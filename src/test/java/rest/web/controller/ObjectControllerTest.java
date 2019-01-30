package rest.web.controller;

import com.github.anno4j.Anno4j;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ObjectControllerTest extends BaseWebTest {

    private final String standardUrl = "https://database.visit.uni-passau.de/";
    private String groupId;

    @Test
    public void getRepresentationOfObjectSuccess() throws Exception {
        String requestURL = standardUrl + "object?id=" + this.groupId;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        assertFalse(mvcResultString.isEmpty());
        JSONObject jsonObject = new JSONObject(mvcResultString);
        assertTrue(jsonObject.get("type").toString().contains("Group"));
//        JSONObject jsonObject1 = jsonObject.getJSONObject("group_refentry");
//        assertTrue(jsonObject1.get("type").toString().contains("ReferenceEntry"));
    }

    @Test
    public void getRepresentationOfObjectFileNotFound() throws Exception {
        String random = RandomStringUtils.randomAlphanumeric(47);
        String requestURL = standardUrl + "object?id=" + random;
        mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isNotFound());

    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException {
        Anno4j anno4j = this.objectRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.setIconography("Iconography");
        group.addKeyword("Keyword");
        group.addKeyword("Keyword 2");

        ReferenceEntry entry = anno4j.createObject(ReferenceEntry.class);
        entry.setPages(11);

        entry.setIsAbout(group);
        group.addEntry(entry);

        ReferenceEntry entry2 = anno4j.createObject(ReferenceEntry.class);
        entry2.setPages(22);

        entry2.setIsAbout(group);
        group.addEntry(entry2);

        Reference reference = anno4j.createObject(Reference.class);
        reference.setKeyword("ReferenceKeyword");

        reference.addEntry(entry);
        entry.setEntryIn(reference);

        reference.addEntry(entry2);
        entry2.setEntryIn(reference);

        this.groupId = group.getResourceAsString();

//        this.anno4jRepository.getAnno4j().setRepository(this.objectRepository.getAnno4j().getObjectRepository());
    }
}