package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import model.vismo.Title;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import rest.BaseWebTest;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for Testing the Object Repository
 */
public class ObjectRepositoryTest extends BaseWebTest {

    private String groupId;
    private String referenceId;
    private String entryId1;

    private String iconographyString;
    private String keywordString1;
    private String keywordString2;

    private String referenceTitle;
    private String referenceSuperordinateTitle;
    private String referenceKeyword;

    /**
     * Test to get a Json Represented as a String of the Object with a given Object ID expects a FileNotFoudnException to come
     */
    @Test(expected = FileNotFoundException.class)
    public void getRepresentationOfObjectFailure() throws Exception {
        String randClass = RandomStringUtils.randomAlphanumeric(12);
        String randID = RandomStringUtils.randomAlphanumeric(47);
        this.objectRepository.getRepresentationOfObject(randClass, randID);
    }

    /**
     * Test to get a Json Represented as a String of the Object with a given Object ID expected to be successful
     *
     * @throws Exception
     */
    @Test
    public void getRepresenatationofObjectSuccess() throws Exception {
        String testClass = "Group";
        String result = objectRepository.getRepresentationOfObject(this.groupId, testClass);
        assertFalse(result.isEmpty());
        JSONObject jsonObject = new JSONObject(result);
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
    public void getRepresentationsOfReference() throws MalformedQueryException, RepositoryException, IOException, ParseException, QueryEvaluationException, ClassNotFoundException, JSONException {
        String testClass = "Reference";
        String result = this.objectRepository.getRepresentationOfObject(this.referenceId, testClass);

        assertFalse(result.isEmpty());
        JSONObject jsonObject = new JSONObject(result);
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
    public void testConnectionQueries() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        Anno4j anno4j = this.objectRepository.getAnno4j();

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Query wird genau so aufgebaut, wie Du es gewohnt bist
        String preparedQuery = this.queryString.replace("ADD_ID_HERE", this.groupId);

        // Wir brauchen eine TupleQuery, die kann uns die richtigen Sachen aus der DB holen
        TupleQuery tupleQuery = connection.prepareTupleQuery(preparedQuery);

        // Diese an die DB senden...
        TupleQueryResult result = tupleQuery.evaluate();

        // Aus dem "result" kannst Du dann per .next() die gefundenen Treffer rausholen
        while (result.hasNext()) {
            // Die haben dann jeweils ein BindingSet, welches die key/value Paare beinhaltet
            BindingSet next = result.next();

            // Und die printen wir dann einfach :)
            System.out.println("Entity found with the following relationships and properties:");
            for (String binding : next.getBindingNames()) {
                System.out.println("Key: " + binding + " - With value: " + next.getValue(binding));

            }
        }
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
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
        entry.setPages(11);

        entry.setIsAbout(group);
        group.addEntry(entry);

        ReferenceEntry entry2 = anno4j.createObject(ReferenceEntry.class);
        entry2.setPages(22);

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

    private String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX cidoc: <http://erlangen-crm.org/170309/>\n" +
            "PREFIX vismo: <http://visit.de/ontologies/vismo/>\n" +
            "SELECT ?x ?type ?group_keyword ?group_iconography\n" +
            "WHERE { ?x rdf:type ?type .\n" +
            "OPTIONAL {\n" +
            "?x <http://visit.de/ontologies/vismo/keyword> ?group_keyword}\n" +
            "OPTIONAL {\n" +
            "?x <http://visit.de/ontologies/vismo/iconography> ?group_iconography}\n" +

            "FILTER regex(str(?x), \"^ADD_ID_HERE$\", \"\")}\n" +
            "GROUP BY ?x ?type ?group_keyword ?group_iconography\n";
}