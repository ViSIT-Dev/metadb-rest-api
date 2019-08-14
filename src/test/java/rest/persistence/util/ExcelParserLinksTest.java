package rest.persistence.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.github.anno4j.Anno4j;
import rest.BaseWebTest;

public class ExcelParserLinksTest extends BaseWebTest {
	private Anno4j anno4j;
	private ObjectConnection connection;
	
	@Before
	public void setUp() throws Exception {
		// create one mock database, one query service, and one connection that is used
		this.anno4j = this.importRepository.getAnno4j();
		this.connection = anno4j.getObjectRepository().getConnection();

		fillDatabase();
	}

	@Test
	public void testLinkedExcelFiles() throws Exception {
		// Query nach Activity
		String activity1Id = "";
		// Find Activity "title"
		String queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "titel"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> . \n";
		queryString += "    ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Activity> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				activity1Id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		// Query nach Activity
		String activity2Id = "";
		// Find Activity "activitytitel"
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "activitytitel"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> . \n";
		queryString += "    ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Activity> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				activity2Id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		// Query nach Place
		String placeId = "";
		// Find Place "place"
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "place"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> . \n";
		queryString += "    ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Place> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				placeId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		// Find activity 1 place
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?o \n";
		queryString += "WHERE { \n";
		queryString += "    <" + activity1Id + "> erlangen:P7_took_place_at ?o .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value place = solution.getValue("o");

				assertEquals(placeId, place.stringValue());
				System.out.println("0");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		// Find activity 2 place
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?o \n";
		queryString += "WHERE { \n";
		queryString += "    <" + activity2Id + "> erlangen:P7_took_place_at ?o .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value place = solution.getValue("o");

				assertEquals(placeId, place.stringValue());
				System.out.println("1");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		
		// Find Person "BezTest"
		String bezTestId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "BezTest"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				bezTestId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find birthplace
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?birthplace \n";
		queryString += "WHERE { \n";
		queryString += "    <" + bezTestId + "> erlangen:P92i_was_brought_into_existence_by ?o .\n";
		queryString += "    ?o erlangen:P7_took_place_at ?birthplace .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value birthplace = solution.getValue("birthplace");

				assertEquals(birthplace.stringValue(), placeId);
				System.out.println("2");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Person "Bez2"
		String bez2Id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Bez2" + "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				bez2Id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Person "BezTest2"
		String bezTest2Id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "BezTest2"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				bezTest2Id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find marriage partner (middle object) of BezTest2
		String marriagePartner2 = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?partner \n";
		queryString += "WHERE { \n";
		queryString += "    <" + bezTest2Id + "> erlangen:P107i_is_current_or_former_member_of ?partner .\n";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value partner = solution.getValue("partner");
				marriagePartner2 = partner.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find marriage partner of BezTest2
		String marriagePartner = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?partner \n";
		queryString += "WHERE { \n";
		queryString += "    <" + marriagePartner2 + "> erlangen:P107_has_current_or_former_member ?partner .\n";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value partner = solution.getValue("partner");
				marriagePartner = partner.stringValue();
				assertEquals(marriagePartner, bez2Id);
				System.out.println("3");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Architecture "architectureName"
		String architectureNameId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "architecturename"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				architectureNameId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find carried out by
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?person \n";
		queryString += "WHERE { \n";
		queryString += "    <" + architectureNameId + "> erlangen:P108i_was_produced_by ?object . \n";
		queryString += "    ?object erlangen:P14_carried_out_by ?person . \n";
		queryString += "    ?person <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Person> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value person = solution.getValue("person");
				assertEquals(bez2Id, person.stringValue()); 
				System.out.println("4");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Architecture "Architecture1"
		String architecture1Id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Architecture1"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				architecture1Id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find architecture place
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?place \n";
		queryString += "WHERE { \n";
		queryString += "    <" + architecture1Id + "> erlangen:P55_has_current_location ?place .\n";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value placeValue = solution.getValue("place");
				assertEquals(placeId, placeValue.stringValue());
				System.out.println("5");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find Architecture1 contains Architecturename
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?architecture \n";
		queryString += "WHERE { \n";
		queryString += "    <" + architecture1Id + "> erlangen:P89i_contains ?architecture .\n";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value value = solution.getValue("architecture");
				assertEquals(architectureNameId, value.stringValue());
				System.out.println("6");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Institution "instiname"
		String institutionId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "instiname"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				institutionId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Group "groupname"
		String groupId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "groupname"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				groupId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find owner group
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?group \n";
		queryString += "WHERE { \n";
		queryString += "    <" + architecture1Id + "> erlangen:P52_has_current_owner ?group .\n";
		queryString += "    ?group <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Group> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value group = solution.getValue("group");
				assertEquals(groupId, group.stringValue());
				System.out.println("7");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find owner institution
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?institution \n";
		queryString += "WHERE { \n";
		queryString += "    <" + architecture1Id + "> erlangen:P52_has_current_owner ?institution .\n";
		queryString += "    ?institution <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Institution> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value institution = solution.getValue("institution");
				assertEquals(institutionId, institution.stringValue());
				System.out.println("8");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Reference "TitelReferenz"
		String referenceId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "TitelReferenz"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				referenceId = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find reference in person (middle object)
		String referencedID = "";
		queryString = "SELECT ?reference ?pred \n";
		queryString += "WHERE { \n";
		queryString += "    <" + bezTestId + "> <http://visit.de/ontologies/vismo/referencedByEntry> ?reference .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value reference = solution.getValue("reference");
				referencedID = reference.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find reference in person
		queryString = "SELECT ?o \n";
		queryString += "WHERE { \n";
		queryString += "    <" + referencedID + "> <http://visit.de/ontologies/vismo/isEntryIn> ?o .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value reference = solution.getValue("o");
				assertEquals(referenceId, reference.stringValue());
				System.out.println("9");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Object "Object1"
		String object1_id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Object1"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				object1_id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Object "Object2"
		String object2_id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Object2"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				object2_id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Object "Objekttitel"
		String objecttitel_id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Objekttitel"
				+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				objecttitel_id = updatedValue.stringValue();
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// Find Object "Objekttitel"
		String objecttitel_inventnr = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P48_has_preferred_identifier ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "ivnr" + "'^^<http://www.w3.org/2001/XMLSchema#string> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value updatedValue = solution.getValue("s");
				objecttitel_inventnr = updatedValue.stringValue();
				assertEquals(objecttitel_id, objecttitel_inventnr);
				System.out.println("10");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find Object 1 consists of Objekttitel
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?object \n";
		queryString += "WHERE { \n";
		queryString += "    <" + object1_id + "> erlangen:P46_is_composed_of ?object .\n";
		queryString += "    ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Object> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value object = solution.getValue("object");
				assertEquals(objecttitel_id, object.stringValue());
				System.out.println("11");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		// find Object 2 consists of Objekttitel
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?object \n";
		queryString += "WHERE { \n";
		queryString += "    <" + object2_id + "> erlangen:P46_is_composed_of ?object .\n";
		queryString += "    ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://visit.de/ontologies/vismo/Object> .";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value object = solution.getValue("object");
				assertEquals(objecttitel_id, object.stringValue());
				System.out.println("12");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

	}

	private void fillDatabase() throws Exception {
		// Activity & Place
		File originalFile = new File("src/test/resources/visitExcelActivityWithLinksTest.xlsx");
		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivityWithLinksTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		json = updateJSON(json);

		ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSON = generator.createUpdateQueryFromJSON(json);

		Update update = connection.prepareUpdate(updateQueryFromJSON);
		update.execute();

		File original = new File("src/test/resources/visitExcelActivityTest.xlsx");
		InputStream isActivity = new FileInputStream(original);

		MultipartFile fileMock = new MockMultipartFile("visitExcelActivityTest.xlsx", isActivity);

		ExcelParser parserActivity = new ExcelParser();

		String jsonActivity = parserActivity.createJSONFromParsedExcelFile(fileMock);

		jsonActivity = updateJSON(jsonActivity);

		ImportQueryGenerator generatorActivity = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONActivity = generatorActivity.createUpdateQueryFromJSON(jsonActivity);

		Update updateActivity = connection.prepareUpdate(updateQueryFromJSONActivity);
		updateActivity.execute();

		// Person
		File twoPerson = new File("src/test/resources/visitExcelTwoPersonTest.xlsx");
		InputStream isPerson = new FileInputStream(twoPerson);

		MultipartFile fileMockPerson = new MockMultipartFile("visitExcelTwoPersonTest.xlsx", isPerson);

		ExcelParser parserPerson = new ExcelParser();

		String jsonPerson = parserPerson.createJSONFromParsedExcelFile(fileMockPerson);

		jsonPerson = updateJSON(jsonPerson);

		ImportQueryGenerator generatorPerson = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONPerson = generatorPerson.createUpdateQueryFromJSON(jsonPerson);

		Update updatePerson = connection.prepareUpdate(updateQueryFromJSONPerson);
		updatePerson.execute();

		// Institution
		File institutionPlaceFile = new File("src/test/resources/visitExcelInstitutionAndPlaceTest.xlsx");
		InputStream isInstitutionPlace = new FileInputStream(institutionPlaceFile);

		MultipartFile fileMockInstitutionPlace = new MockMultipartFile("visitExcelInstitutionAndPlaceTest.xlsx",
				isInstitutionPlace);

		ExcelParser parserInstitutionPlace = new ExcelParser();

		String jsonInstitutionPlace = parserInstitutionPlace.createJSONFromParsedExcelFile(fileMockInstitutionPlace);

		jsonInstitutionPlace = updateJSON(jsonInstitutionPlace);

		ImportQueryGenerator generatorInstitutionPlace = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONInstitutionPlace = generatorInstitutionPlace
				.createUpdateQueryFromJSON(jsonInstitutionPlace);

		Update updateInstitutionPlace = connection.prepareUpdate(updateQueryFromJSONInstitutionPlace);
		updateInstitutionPlace.execute();

		// Object
		File objectFile = new File("src/test/resources/visitExcelObjectTest.xlsx");
		InputStream isFile = new FileInputStream(objectFile);
		MultipartFile fileObject = new MockMultipartFile("visitExcelObjectTest.xlsx", isFile);
		ExcelParser parserFile = new ExcelParser();
		String jsonObject = parserFile.createJSONFromParsedExcelFile(fileObject);

		jsonObject = updateJSON(jsonObject);

		ImportQueryGenerator generatorObject = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONObject = generatorObject.createUpdateQueryFromJSON(jsonObject);

		Update updateObject = connection.prepareUpdate(updateQueryFromJSONObject);
		updateObject.execute();

		// Architecture
		File architectureFile = new File("src/test/resources/visitExcelArchitectureTest.xlsx");
		InputStream isArchitecture = new FileInputStream(architectureFile);

		MultipartFile fileMockArchitecture = new MockMultipartFile("visitExcelArchitectureTest.xlsx", isArchitecture);

		ExcelParser parserArchitecture = new ExcelParser();

		String jsonArchitecture = parserArchitecture.createJSONFromParsedExcelFile(fileMockArchitecture);

		jsonArchitecture = updateJSON(jsonArchitecture);

		ImportQueryGenerator generatorArchitecture = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONArchitecture = generatorArchitecture.createUpdateQueryFromJSON(jsonArchitecture);

		Update updateArchitecture = connection.prepareUpdate(updateQueryFromJSONArchitecture);
		updateArchitecture.execute();

		// Reference
		File referenceFile = new File("src/test/resources/visitExcelReferenceTest.xlsx");
		InputStream isReference = new FileInputStream(referenceFile);

		MultipartFile fileMockReference = new MockMultipartFile("visitExcelReferenceTest.xlsx", isReference);

		ExcelParser parserReference = new ExcelParser();

		String jsonReference = parserReference.createJSONFromParsedExcelFile(fileMockReference);

		jsonReference = updateJSON(jsonReference);

		ImportQueryGenerator generatorReference = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONReference = generatorReference.createUpdateQueryFromJSON(jsonReference);

		Update updateReference = connection.prepareUpdate(updateQueryFromJSONReference);
		updateReference.execute();

		// Group
		File groupFile = new File("src/test/resources/visitExcelGroupTest.xlsx");
		InputStream isGroup = new FileInputStream(groupFile);

		MultipartFile fileMockGroup = new MockMultipartFile("visitExcelGroupTest.xlsx", isGroup);

		ExcelParser parserGroup = new ExcelParser();

		String jsonGroup = parserGroup.createJSONFromParsedExcelFile(fileMockGroup);

		jsonGroup = updateJSON(jsonGroup);

		ImportQueryGenerator generatorGroup = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONGroup = generatorGroup.createUpdateQueryFromJSON(jsonGroup);

		Update updateGroup = connection.prepareUpdate(updateQueryFromJSONGroup);
		updateGroup.execute();

		// Links
		File linkFile = new File("src/test/resources/visitLinkTest.xlsx");
		InputStream isLinked = new FileInputStream(linkFile);

		MultipartFile fileMockLink = new MockMultipartFile("visitLinkTest.xlsx", isLinked);

		ExcelParser parserLink = new ExcelParser();

		String jsonLink = parserLink.createJSONFromParsedExcelFile(fileMockLink);

		jsonLink = updateJSON(jsonLink);

		ImportQueryGenerator generatorLink = new ImportQueryGenerator("none", "none", "somePath");
		String updateQueryFromJSONLink = generatorLink.createUpdateQueryFromJSON(jsonLink);

		Update updateLink = connection.prepareUpdate(updateQueryFromJSONLink);
		updateLink.execute();

	}

	private String updateJSON(String json) throws RepositoryException, QueryEvaluationException, JSONException,
			MalformedQueryException, ParseException {
		JSONObject jsonObject = new JSONObject(json);

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = jsonObject.keys();
		String key;

		Object object;

		while (iterator.hasNext()) {
			key = iterator.next();
			object = jsonObject.get(key);

			if (object instanceof JSONArray) {
				JSONArray array = (JSONArray) object;
				JSONArray changedArray = new JSONArray();
				String currentKey = "";
				Object subObject;
				for (int i = 0; i < array.length(); ++i) {
					JSONObject jsonObjectFromArray = array.getJSONObject(i);
					@SuppressWarnings("unchecked")
					Iterator<String> iteratorValues = jsonObjectFromArray.keys();
					while (iteratorValues.hasNext()) {
						currentKey = iteratorValues.next();
						subObject = jsonObjectFromArray.get(currentKey);
						String subKey = "";

						if (subObject instanceof JSONArray) {
							JSONArray subObjectArray = (JSONArray) subObject;
							JSONArray subArrayChanged = new JSONArray();
							JSONObject subJSONObject = new JSONObject();
							JSONObject updatedJSONObject = new JSONObject();
							for (int j = 0; j < subObjectArray.length(); j++) {
								subJSONObject = subObjectArray.getJSONObject(j);
								@SuppressWarnings("unchecked")
								Iterator<String> subKeys = subJSONObject.keys();
								while (subKeys.hasNext()) {
									subKey = subKeys.next();
									// there are no reference field in subsubparts
									if (!(subJSONObject.get(subKey) instanceof JSONArray)) {
										final String subValue = subJSONObject.getString(subKey);

										if (subValue.contains("http:") && subValue.contains("visit")) {
											// ignore field as it has already an id
											updatedJSONObject.put(subKey, subValue);
										} else if (subKey.contains("dating")) {
											// ignore field as it is a date
											updatedJSONObject.put(subKey, subValue);
										} else {
											String queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
											queryString += "SELECT ?s \n";
											queryString += "WHERE { \n{ \n";
											queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
											queryString += "       ?o erlangen:P3_has_note '" + subValue
													+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
											queryString += "\n } \n UNION \n { \n";
											queryString += "    ?s erlangen:P48_has_preferred_identifier ?o . \n";
											queryString += "       ?o erlangen:P3_has_note '" + subValue
													+ "'^^<http://www.w3.org/2001/XMLSchema#string> . \n } \n }";

											try {
												TupleQuery temp = connection.prepareTupleQuery(queryString);
												TupleQueryResult result = temp.evaluate();

												// no results found
												if (!result.hasNext()) {
													updatedJSONObject.put(subKey, subValue);
												}

												while (result.hasNext()) {
													BindingSet solution = result.next();
													Value updatedValue = solution.getValue("s");
													updatedJSONObject.put(subKey, updatedValue);
												}
											} catch (MalformedQueryException e) {
												e.printStackTrace();
											}
										}
									} else {
										JSONArray secondDimension = (JSONArray) subJSONObject.get(subKey);
										updatedJSONObject.put(subKey, secondDimension);
									}
								}
							}
							if (updatedJSONObject.length() > 0) {
								subArrayChanged.put(updatedJSONObject);
								jsonObjectFromArray.put(currentKey, subArrayChanged);
							}
						} else {

							final String value = jsonObjectFromArray.getString(currentKey);

							if (value.contains("http:") && value.contains("visit")) {
								// ignore field as it has already an id
							} else {
								String queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
								queryString += "SELECT ?s \n";
								queryString += "WHERE { \n{ \n";
								queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
								queryString += "       ?o erlangen:P3_has_note '" + value
										+ "'^^<http://www.w3.org/2001/XMLSchema#string> .";
								queryString += "\n } \n UNION \n { \n";
								queryString += "    ?s erlangen:P48_has_preferred_identifier ?o . \n";
								queryString += "       ?o erlangen:P3_has_note '" + value
										+ "'^^<http://www.w3.org/2001/XMLSchema#string> . \n } \n }";

								try {
									TupleQuery temp = connection.prepareTupleQuery(queryString);
									TupleQueryResult result = temp.evaluate();
									while (result.hasNext()) {
										BindingSet solution = result.next();
										Value updatedValue = solution.getValue("s");
										jsonObjectFromArray.put(currentKey, updatedValue);
									}

								} catch (MalformedQueryException e) {
									e.printStackTrace();
								}
							}
						}
					}

					changedArray.put(jsonObjectFromArray);
					jsonObject.put(key, changedArray);

				}
			}
		}

		return jsonObject.toString();

	}

	@Override
	public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException,
			RepositoryConfigException, MalformedQueryException, UpdateExecutionException {
		
	}

}
