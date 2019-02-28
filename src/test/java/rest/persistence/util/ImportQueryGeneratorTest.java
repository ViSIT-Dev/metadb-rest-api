package rest.persistence.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.namespace.VISMO;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import rest.application.exception.IdMapperException;
import rest.application.exception.QueryGenerationException;
import rest.configuration.VisitIDGenerator;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite for the ImportQueryGenerator Class.
 */
public class ImportQueryGeneratorTest {

    private final static String QUERY_OBJECT_IDENTIFIEDBY_TITLE = "?x <http://erlangen-crm.org/170309/P1_is_identified_by> ?y1 . ?y1 rdf:type <http://erlangen-crm.org/170309/E35_Title> . ?y1 <http://erlangen-crm.org/170309/P3_has_note> ?object_identifiedby_title";
    private final static String QUERY_PERSON_HASTYPE_PROFESSION = "?x <http://erlangen-crm.org/170309/P2_has_type> ?y2 . ?y2 rdf:type <http://visit.de/ontologies/vismo/Profession> . ?y2 <http://erlangen-crm.org/170309/P3_has_note> ?person_hastype_profession";
    private final static String QUERY_PERSON_MOTHER = "?x <http://erlangen-crm.org/170309/P92i_was_brought_into_existence_by> ?y1 . ?y1 rdf:type <http://erlangen-crm.org/170309/E67_Birth> . ?y1 <http://erlangen-crm.org/170309/P96_by_mother> ?person_mother . ?person_mother rdf:type <http://visit.de/ontologies/vismo/Person>";
    private final static String PERSON_BIRTH_DATING_EXACT = "?x <http://erlangen-crm.org/170309/P92i_was_brought_into_existence_by> ?y1 . ?y1 rdf:type <http://erlangen-crm.org/170309/E67_Birth> . ?y1 <http://erlangen-crm.org/170309/P160_has_temporal_projection> ?y2 . ?y2 rdf:type <http://visit.de/ontologies/vismo/Dating> . ?y2 <http://erlangen-crm.org/170309/P3_has_note> ?person_birth_dating_exact";

    private final static String SIMPLE_GENERATION = "{\n" +
            "  \"Group\" : {\n" +
            "    \"type\": \"http://visit.de/ontologies/vismo/Group\",\n" +
            "    \"id\": \"1\",\n" +
            "    \"group_iconography\": \"Iconography\",\n" +
            "    \"group_keyword\": \"Keyword\"\n" +
            "  }\n" +
            "}";

    private final static String MORE_COMPLEX_GENERATION = "{\n" +
            "  \"Group\": {\n" +
            "    \"type\": \"http://visit.de/ontologies/vismo/Group\",\n" +
            "    \"group_refentry\": [\n" +
            "      {\n" +
            "        \"group_refentry_pages\": \"11\",\n" +
            "        \"group_refentry_in_reference\": \"1\",\n" +
            "        \"type\": \"http://visit.de/ontologies/vismo/ReferenceEntry\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"group_refentry_pages\": \"22\",\n" +
            "        \"group_refentry_in_reference\": \"1\",\n" +
            "        \"type\": \"http://visit.de/ontologies/vismo/ReferenceEntry\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"id\": \"2\"\n" +
            "  }\n" +
            "}";

    @Test
    public void testSimpleQueryGeneration() throws IdMapperException, QueryGenerationException, RepositoryConfigException, RepositoryException, MalformedQueryException, UpdateExecutionException, ParseException, QueryEvaluationException {
        ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

        String updateQueryFromJSON = generator.createUpdateQueryFromJSON(SIMPLE_GENERATION);

        System.out.println(updateQueryFromJSON);

        Anno4j anno4j = new Anno4j(false);

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(updateQueryFromJSON);
        update.execute();

        QueryService qs = anno4j.createQueryService();
        List<Group> result = qs.execute(Group.class);

        assertEquals(1, result.size());
        Group resultGroup = result.get(0);
        assertEquals("Iconography", resultGroup.getIconography());
        assertTrue(resultGroup.getKeywords().contains("Keyword"));
    }

    @Test
    public void testMoreComplexQueryGeneration() throws IdMapperException, QueryGenerationException, RepositoryConfigException, RepositoryException, MalformedQueryException, UpdateExecutionException, ParseException, QueryEvaluationException {
        ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

        generator.getMapper().addBaseID("1", VISMO.REFERENCE);

        String updateQueryFromJSON = generator.createUpdateQueryFromJSON(MORE_COMPLEX_GENERATION);

        System.out.println(updateQueryFromJSON);

        Anno4j anno4j = new Anno4j(false);

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(updateQueryFromJSON);
        update.execute();

        QueryService qs = anno4j.createQueryService();
        List<Group> result = qs.execute(Group.class);

        assertEquals(1, result.size());
        Group resultGroup = result.get(0);
        assertEquals(2, resultGroup.getEntries().size());

        ReferenceEntry referenceEntry = (ReferenceEntry) resultGroup.getEntries().toArray()[0];

        System.out.println(String.valueOf(referenceEntry.getPages()));

        assertTrue(referenceEntry.getEntryIn() != null);
    }

    @Test
    public void testInitialisation() {
        ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

        assertEquals(8, generator.getBasicGroups().size());

        assertTrue(generator.getBasicGroups().containsKey("Group"));
        assertTrue(generator.getBasicGroups().containsKey("Architecture"));
        assertTrue(generator.getBasicGroups().containsKey("Person"));

        assertTrue(generator.getBasicGroups().get("Object").containsKey("object_inventory_number"));
        assertTrue(generator.getBasicGroups().get("Person").containsKey("person_helpfullinks"));
        assertTrue(generator.getBasicGroups().get("Reference").containsKey("reference_series"));

        assertTrue(generator.getSubGroups().containsKey("object_prefidentifier_inscriptio"));
        assertTrue(generator.getSubGroups().containsKey("person_birth"));
        assertTrue(generator.getSubGroups().containsKey("arch_producedby_production"));

        assertTrue(generator.getSubGroups().get("place_refentry").containsKey("place_refentry_pages"));
        assertTrue(generator.getSubGroups().get("person_refentry").containsKey("person_refentry_in_reference"));
        assertTrue(generator.getSubGroups().get("marriage_end_dating").containsKey("marriage_end_dating_exact"));

        assertEquals(QUERY_OBJECT_IDENTIFIEDBY_TITLE, generator.getBasicGroups().get("Object").get("object_identifiedby_title"));
        assertEquals(QUERY_PERSON_HASTYPE_PROFESSION, generator.getBasicGroups().get("Person").get("person_hastype_profession"));
        assertEquals(QUERY_PERSON_MOTHER, generator.getSubGroups().get("person_birth").get("person_mother"));

        assertEquals(8, generator.getBasicGroupNames().size());
        assertTrue(generator.getBasicGroups().containsKey("Reference"));
        assertTrue(generator.getBasicGroups().containsKey("Activity"));
        assertTrue(generator.getBasicGroups().containsKey("Institution"));

        assertTrue(generator.getIdnames().contains("object_iconography"));
        assertTrue(generator.getIdnames().contains("person_death_dating_start"));
        assertTrue(generator.getIdnames().contains("arch_orderaffiliation"));

        assertTrue(generator.getDatatypes().containsKey("object_prod_dating_start"));
        assertEquals("string", generator.getDatatypes().get("object_prod_dating_start"));

        assertTrue(generator.getDatatypes().containsKey("object_refentry_in_reference"));
        assertEquals("entity_reference (http://visit.de/ontologies/vismo/Reference)", generator.getDatatypes().get("object_refentry_in_reference"));

        assertTrue(generator.getDatatypes().containsKey("person_comment"));
        assertEquals("string_long", generator.getDatatypes().get("person_comment"));
    }

    @Test
    public void testExchangeRDFVariables() {
        String result = this.exchangeRDFVariables(PERSON_BIRTH_DATING_EXACT, "http://someid.com");

        assertFalse(result.contains("?x"));
        assertFalse(result.contains("?y1"));
        assertFalse(result.contains("?y2"));

        System.out.println(result);
    }

    private String exchangeRDFVariables(String input, String objectID) {
        String result = input.replace("?x", "<" + objectID + ">");

        // Find all occurences of intermediary variables (written as ?y + a number) and replace them with same URIs
        int index = input.indexOf("?y");
        while(index > 0) {
            int start = index;
            int end = input.indexOf(" ", index);

            String intermediary = input.substring(start, end);
            String uri = VisitIDGenerator.generateVisitDBID();

            result = result.replace(intermediary, "<" + uri + ">");

            index = input.indexOf("?y", start + 1);
        }

        return result;
    }
}