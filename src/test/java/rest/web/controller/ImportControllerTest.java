package rest.web.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.namespace.CIDOC;
import model.namespace.VISMO;
import model.vismo.Architecture;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import rest.BaseWebTest;

import java.io.*;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test suite for the ImportController Class.
 */
public class ImportControllerTest extends BaseWebTest {

    private final static String STANDARD_URL = "https://database.visit.uni-passau.de/import/";

    @Test
    public void testExcelUpload() throws Exception {
        File originalFile = new File("src/test/resources/visitExcelArchitectureTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MockMultipartFile file = new MockMultipartFile("file", "visitExcelArchitectureTest.xlsx", "text/plain", is);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);

        MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "")).andDo(print()).andExpect(status().isNoContent()).andReturn();

        Anno4j anno4j = this.importRepository.getAnno4j();

        QueryService qs = anno4j.createQueryService();

        List<Architecture> result = qs.execute(Architecture.class);

        assertEquals(1, result.size());
    }

    @Test
    public void testExcelUploadWithContext() throws Exception {
        File originalFile = new File("src/test/resources/visitExcelArchitectureTest.xlsx");

        InputStream is = new FileInputStream(originalFile);

        MockMultipartFile file = new MockMultipartFile("file", "visitExcelArchitectureTest.xlsx", "text/plain", is);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);

        MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "http://context.de/")).andDo(print()).andExpect(status().isNoContent()).andReturn();

        Anno4j anno4j = this.importRepository.getAnno4j();

        QueryService qs = anno4j.createQueryService(new URIImpl("http://context.de/"));

        List<Architecture> result = qs.execute(Architecture.class);

        assertEquals(1, result.size());
    }

    @Test
    public void testImportWithJSON() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(STANDARD_URL).content(COMPLEX_WITH_TWO_ENTITIES))
                .andDo(print()).andExpect(status().isNoContent()).andReturn();

        QueryService qs = this.importRepository.getAnno4j().createQueryService();
        qs.addPrefix(VISMO.PREFIX, VISMO.NS);
        qs.addCriteria("vismo:iconography", "GroupIconography");

        List<Group> result = qs.execute(Group.class);

        assertEquals(1, result.size());
        Group resultGroup = result.get(0);
        assertEquals(2, resultGroup.getEntries().size());

        ReferenceEntry referenceEntry = (ReferenceEntry) resultGroup.getEntries().toArray()[0];

        assertTrue(referenceEntry.getPages().equals(BigInteger.valueOf(11)) || referenceEntry.getPages().equals(BigInteger.valueOf(22)));
        assertTrue(referenceEntry.getEntryIn() != null);

        QueryService qs2 = this.importRepository.getAnno4j().createQueryService();
        qs2.addPrefix(VISMO.PREFIX, VISMO.NS);
        qs2.addCriteria("<" + CIDOC.P3_HAS_NOTE + ">", "ImportReferenceKeyword");

        List<Reference> result2 = qs2.execute(Reference.class);

        assertEquals(1, result2.size());

        Reference reference = result2.get(0);

        assertEquals(reference.getResourceAsString(), referenceEntry.getEntryIn().getResourceAsString());
        assertTrue(reference.getKeywords().contains("ImportReferenceKeyword"));
        assertEquals("ImportReferenceTitle", reference.getTitle().getTitle());
        assertEquals("ImportReferenceSuperordinateTitle", reference.getTitle().getSuperordinateTitle());
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, MalformedQueryException, UpdateExecutionException {

    }

    private final static String COMPLEX_WITH_TWO_ENTITIES = "{\n" +
            "  \"Reference\": {\n" +
            "    \"reference_title\": {\n" +
            "      \"reference_title_title\": \"ImportReferenceTitle\",\n" +
            "      \"reference_title_superordinate\": \"ImportReferenceSuperordinateTitle\",\n" +
            "      \"id\": \"1.1\",\n" +
            "      \"type\": \"http://visit.de/ontologies/vismo/Title\"\n" +
            "    },\n" +
            "    \"type\": \"http://visit.de/ontologies/vismo/Reference\",\n" +
            "    \"reference_keyword\": \"ImportReferenceKeyword\",\n" +
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
            "    \"group_iconography\": \"GroupIconography\",\n" +
            "    \"group_keyword\": \"Keyword,Keyword2\"\n" +
            "  }\n" +
            "}";
}