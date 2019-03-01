package rest.web.controller;

import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test suite for the ImportController Class.
 */
public class ImportControllerTest extends BaseWebTest {

    private final static String STANDARD_URL = "https://database.visit.uni-passau.de/import/";

    private final static String COMPLEX_WITH_TWO_ENTITIES = "{\n" +
            "  \"Reference\": {\n" +
            "    \"reference_title\": {\n" +
            "      \"reference_title_title\": \"ReferenceTitle\",\n" +
            "      \"reference_title_superordinate\": \"ReferenceSuperordinateTitle\",\n" +
            "      \"id\": \"1.1\",\n" +
            "      \"type\": \"http://visit.de/ontologies/vismo/Title\"\n" +
            "    },\n" +
            "    \"type\": \"http://visit.de/ontologies/vismo/Reference\",\n" +
            "    \"reference_keyword\": \"ReferenceKeyword\",\n" +
            "    \"id\": \"1\"\n" +
            "  },\n" +
            "  \"Group\": {\n" +
            "    \"type\": \"http://visit.de/ontologies/vismo/Group\",\n" +
            "    \"group_refentry\": [\n" +
            "      {\n" +
            "        \"group_refentry_pages\": \"11\",\n" +
            "        \"group_refentry_in_reference\": \"1\",\n" +
            "        \"id\": \"2.1\",\n" +
            "        \"type\": \"http://visit.de/ontologies/vismo/ReferenceEntry\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"group_refentry_pages\": \"22\",\n" +
            "        \"group_refentry_in_reference\": \"1\",\n" +
            "        \"id\": \"2.2\",\n" +
            "        \"type\": \"http://visit.de/ontologies/vismo/ReferenceEntry\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"id\": \"2\",\n" +
            "    \"group_iconography\": \"Iconography\",\n" +
            "    \"group_keyword\": \"Keyword,Keyword2\"\n" +
            "  }\n" +
            "}";

    @Test
    public void testImport() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(STANDARD_URL).content(COMPLEX_WITH_TWO_ENTITIES))
                .andDo(print()).andExpect(status().isNoContent()).andReturn();
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, MalformedQueryException, UpdateExecutionException {

    }
}