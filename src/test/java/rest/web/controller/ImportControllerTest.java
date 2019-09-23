package rest.web.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.namespace.CIDOC;
import model.namespace.VISMO;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;

import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
	public void testLinkedExcelFiles() throws Exception {
		Anno4j anno4j = this.importRepository.getAnno4j();
		ObjectConnection connection = anno4j.getObjectRepository().getConnection();
		fillDatabase();
		
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
		
		// find marriage date for BezTest2
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?date ?freedating ?freedatingout \n";
		queryString += "WHERE { \n";
		queryString += "    <" + marriagePartner2 + "> erlangen:P92i_was_brought_into_existence_by ?existence .\n";
		queryString += "    ?existence erlangen:P160_has_temporal_projection ?temporal .\n";
		queryString += "    ?temporal erlangen:P3_has_note ?date .\n";
		queryString += "    ?temporal erlangen:P82_at_some_time_within ?freedating .\n";
		queryString += "    <" + marriagePartner2 + "> erlangen:P93i_was_taken_out_of_existence_by ?outexistence .\n";
		queryString += "    ?outexistence erlangen:P160_has_temporal_projection ?temporalout .\n";
		queryString += "    ?temporalout erlangen:P82_at_some_time_within ?freedatingout .\n";
		queryString += "\n }";

		try {
			TupleQuery temp = connection.prepareTupleQuery(queryString);
			TupleQueryResult result = temp.evaluate();

			while (result.hasNext()) {
				BindingSet solution = result.next();
				Value marriageDate = solution.getValue("date");
				Value freeMarriageDate = solution.getValue("freedating");
				Value freeMarriageDateOut = solution.getValue("freedatingout");
				assertEquals("5/2/93", marriageDate.stringValue());
				assertEquals("Ende", freeMarriageDateOut.stringValue());
				assertEquals("frei", freeMarriageDate.stringValue());
				System.out.println("13");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		// find date of production of Objekttitel
				queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
				queryString += "SELECT ?century ?technique \n";
				queryString += "WHERE { \n";
				queryString += "    <" + objecttitel_inventnr + "> erlangen:P108i_was_produced_by ?produced .\n";
				queryString += "    ?produced erlangen:P160_has_temporal_projection ?temporal .\n";
				queryString += "    ?temporal erlangen:P81_ongoing_throughout ?century .\n";
				queryString += "    ?produced erlangen:P32_used_general_technique ?generaltechnique .\n";
				queryString += "    ?generaltechnique erlangen:P3_has_note ?technique .\n";
				queryString += "\n }";

				try {
					TupleQuery temp = connection.prepareTupleQuery(queryString);
					TupleQueryResult result = temp.evaluate();

					while (result.hasNext()) {
						BindingSet solution = result.next();
						Value century = solution.getValue("century");
						assertEquals("18", century.stringValue());
						Value technique = solution.getValue("technique");
						assertEquals("Technik", technique.stringValue());
						System.out.println("14");
					}
				} catch (MalformedQueryException e) {
					e.printStackTrace();
				}

	}

	@SuppressWarnings("unused")
	private void fillDatabase() throws Exception {
		// Activity & Place
        File originalFile = new File("src/test/resources/visitExcelActivityWithLinksTest.xlsx");
        InputStream is = new FileInputStream(originalFile);
        MockMultipartFile file = new MockMultipartFile("file", "visitExcelActivityWithLinksTest.xlsx", "text/plain", is);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);
        MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "")).andDo(print()).andExpect(status().isNoContent()).andReturn();
        
        //Activity
		File original = new File("src/test/resources/visitExcelActivityTest.xlsx");
		InputStream isActivity = new FileInputStream(original);
		MockMultipartFile fileActivity = new MockMultipartFile("file", "visitExcelActivityTest.xlsx", "text/plain",
				isActivity);
		MockHttpServletRequestBuilder builderActivity = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileActivity);
		MvcResult mvcResultActivity = this.mockMvc.perform(builderActivity.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

		// Person
		File twoPerson = new File("src/test/resources/visitExcelTwoPersonTest.xlsx");
		InputStream isPerson = new FileInputStream(twoPerson);
		MockMultipartFile filePerson = new MockMultipartFile("file", "visitExcelTwoPersonTest.xlsx", "text/plain",
				isPerson);
		MockHttpServletRequestBuilder builderPerson = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(filePerson);
		MvcResult mvcResultPerson = this.mockMvc.perform(builderPerson.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

		// Institution
		File institutionPlaceFile = new File("src/test/resources/visitExcelInstitutionAndPlaceTest.xlsx");
		InputStream isInstitutionPlace = new FileInputStream(institutionPlaceFile);
		MockMultipartFile fileInstitutionPlace = new MockMultipartFile("file", "visitExcelInstitutionAndPlaceTest.xlsx", "text/plain",
				isInstitutionPlace);
		MockHttpServletRequestBuilder builderInstitutionPlace = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileInstitutionPlace);
		MvcResult mvcResultInstitutionPlace = this.mockMvc.perform(builderInstitutionPlace.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

		// Object
		File objectFile = new File("src/test/resources/visitExcelObjectTest.xlsx");
		InputStream isFile = new FileInputStream(objectFile);
		MockMultipartFile fileObject = new MockMultipartFile("file", "visitExcelObjectTest.xlsx", "text/plain",
				isFile);
		MockHttpServletRequestBuilder builderObject = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileObject);
		MvcResult mvcResultObject = this.mockMvc.perform(builderObject.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

		// Reference
		File referenceFile = new File("src/test/resources/visitExcelReferenceTest.xlsx");
		InputStream isReference = new FileInputStream(referenceFile);
		MockMultipartFile fileReference = new MockMultipartFile("file", "visitExcelReferenceTest.xlsx", "text/plain",
				isReference);
		MockHttpServletRequestBuilder builderReference = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileReference);
		MvcResult mvcResultReference = this.mockMvc.perform(builderReference.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

		// Group
		File groupFile = new File("src/test/resources/visitExcelGroupTest.xlsx");
		InputStream isGroup = new FileInputStream(groupFile);
		MockMultipartFile fileGroup = new MockMultipartFile("file", "visitExcelGroupTest.xlsx", "text/plain",
				isGroup);
		MockHttpServletRequestBuilder builderGroup = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileGroup);
		MvcResult mvcResultGroup = this.mockMvc.perform(builderGroup.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();
		
		//Architecture
		File originalArchitecture = new File("src/test/resources/visitExcelArchitectureTest.xlsx");
        InputStream isArchitecture = new FileInputStream(originalArchitecture);
        MockMultipartFile fileArchitecture = new MockMultipartFile("file", "visitExcelArchitectureTest.xlsx", "text/plain", isArchitecture);
        MockHttpServletRequestBuilder builderArchitecture = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(fileArchitecture);
        MvcResult mvcResultArchitecture = this.mockMvc.perform(builderArchitecture.param("context", "")).andDo(print()).andExpect(status().isNoContent()).andReturn();

		// Links
		File linkFile = new File("src/test/resources/visitLinkTest.xlsx");
		InputStream isLink = new FileInputStream(linkFile);
		MockMultipartFile fileLink = new MockMultipartFile("file", "visitLinkTest.xlsx", "text/plain",
				isLink);
		MockHttpServletRequestBuilder builderLink = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(fileLink);
		MvcResult mvcResultLink = this.mockMvc.perform(builderLink.param("context", "")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();

	}
   

	@SuppressWarnings("unused")
	@Test
	public void testExcelUploadWithContext() throws Exception {
		Anno4j anno4j = this.importRepository.getAnno4j();
		ObjectConnection connection = anno4j.getObjectRepository().getConnection();
		
		// Person
		File twoPerson = new File("src/test/resources/visitExcelTwoPersonContextTest.xlsx");
		InputStream isPerson = new FileInputStream(twoPerson);
		MockMultipartFile filePerson = new MockMultipartFile("file", "visitExcelTwoPersonContextTest.xlsx", "text/plain",
				isPerson);
		MockHttpServletRequestBuilder builderPerson = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel")
				.file(filePerson);
		MvcResult mvcResultPerson = this.mockMvc.perform(builderPerson.param("context", "http://context.de/")).andDo(print())
				.andExpect(status().isNoContent()).andReturn();
		
		File originalFile = new File("src/test/resources/visitExcelArchitectureContextTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MockMultipartFile file = new MockMultipartFile("file", "visitExcelArchitectureContextTest.xlsx", "text/plain", is);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);
		MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "http://context.de/")).andDo(print())
				.andExpect(status().isNoContent()).andReturn(); 
		String queryString = "";
		// Find Person "Bez2"
		String bez2Id = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "FROM <http://context.de/> \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Person2" + "'^^<http://www.w3.org/2001/XMLSchema#string> .";
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
				
		// Find Architecture "architectureName"
		String architectureNameId = "";
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?s \n";
		queryString += "FROM <http://context.de/> \n";
		queryString += "WHERE { \n";
		queryString += "    ?s erlangen:P1_is_identified_by ?o . \n";
		queryString += "       ?o erlangen:P3_has_note '" + "Architektur1"
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
		Value person = null;
		queryString = "PREFIX erlangen: <http://erlangen-crm.org/170309/> \n";
		queryString += "SELECT ?person \n";
		queryString += "FROM <http://context.de/> \n";
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
				person = solution.getValue("person");
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		
		assertEquals(bez2Id, person.stringValue());
	}

	@Test
	public void testExcelUploadDuplicateId() throws Exception {
		File originalFile = new File("src/test/resources/visitExcelDuplicateIdTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MockMultipartFile file = new MockMultipartFile("file", "visitExcelDuplicateIdTest.xlsx", "text/plain", is);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);

		@SuppressWarnings("unused")
		MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "")).andDo(print())
				.andExpect(status().isConflict()).andReturn();
	}
	
	@Test
	public void testExcelUploadMissingId() throws Exception {
		File originalFile = new File("src/test/resources/visitExcelMissingIdTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MockMultipartFile file = new MockMultipartFile("file", "visitExcelMissingIdTest.xlsx", "text/plain", is);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(STANDARD_URL + "/excel").file(file);

		@SuppressWarnings("unused")
		MvcResult mvcResult = this.mockMvc.perform(builder.param("context", "")).andDo(print())
				.andExpect(status().isNotAcceptable()).andReturn();
	}
	

    @SuppressWarnings("unused")
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