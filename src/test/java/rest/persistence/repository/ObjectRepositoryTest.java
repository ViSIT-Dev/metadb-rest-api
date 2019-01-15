package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.google.gson.JsonParser;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import rest.BaseWebTest;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertFalse;

/**
 * Test Class for Testing the Object Repository
 */
public class ObjectRepositoryTest extends BaseWebTest {

    @Autowired
    private ObjectRepository objectRepository;

    private String groupId;

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
     * Test to get a Json Represented as a String of the Object with a given Object ID expected to be successfull
     * @throws Exception
     */
    @Test
    public void getRepresenatationofObjectSuccess() throws Exception {
        String testClass= "Group";
        String testID = this.groupId;
        String result = objectRepository.getRepresentationOfObject(testID,testClass);
        assertFalse(result.isEmpty());
        JsonParser jsonParser = new JsonParser();
        jsonParser.parse(result);
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
        while(result.hasNext()) {
            // Die haben dann jeweils ein BindingSet, welches die key/value Paare beinhaltet
            BindingSet next = result.next();

            // Und die printen wir dann einfach :)
            System.out.println("Entity found with the following relationships and properties:");
            for(String binding : next.getBindingNames()) {
                System.out.println("Key: " + binding + " - With value: " + next.getValue(binding));

            }
        }
    }


    // TODO (Christian) Habe Dir jetzt mal das Modell und die erzeugten Daten hier erweitert, damit müsstest Du die rekursive Funktion testen können
    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        Anno4j anno4j = this.objectRepository.getAnno4j();

        Group group = anno4j.createObject(Group.class);
        group.setIconography("Iconography");
//        group.addKeyword("Keyword");

        ReferenceEntry entry = anno4j.createObject(ReferenceEntry.class);
        entry.setPages(5);

        Reference reference = anno4j.createObject(Reference.class);

        group.setEntry(entry);
        entry.setEntryIn(reference);

        this.groupId = group.getResourceAsString();
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