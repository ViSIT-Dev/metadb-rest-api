package rest.persistence.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.namespace.VISMO;
import model.vismo.Activity;
import model.vismo.Place;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.query.Update;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;
import rest.application.exception.IdMapperException;
import rest.application.exception.QueryGenerationException;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite to test the ExcelParser Class.
 */
public class ExcelParserTest {

	@Test
	public void testExcelParserWithActivityWithLinksAndQuery() throws Exception {

		// Excel File wird eingelesen und InputStream erstellt
		File originalFile = new File("src/test/resources/visitExcelActivityWithLinksTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		// Ein MockMultipartFile wird benötigt, da diese MultipartFiles später als
		// FileUpload für die REST API dienen
		MultipartFile file = new MockMultipartFile("visitExcelActivityWithLinksTest.xlsx", is);

		// Eigene Klasse ExcelParser wird inizialisiert
		ExcelParser parser = new ExcelParser();

		// ExcelParser benötigt (zwecks späterer API Anbindung) ebenfalls ein
		// MultipartFile
		// Der Parser nimmt ein Excel entgegen und bastelt daraus eine JSON
		// Repräsentation. Diese kann von der Import-Funktionalität der REST API
		// verstanden werden
		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		// Der ImportQueryGenerator nimmt nun das JSON entgegen, und kann daraus eine
		// UPDATE SPARQL Query erstellen, die an einen RDF Triplestore gegeben werden
		// kann
		// Dies funktioniert ungefähr so: jede ID im JSON hat einen RDF Pfad in Wisski,
		// d.h. aus jeder ID werden mehrere RDF Triples erstellt, die den Pfad
		// widerspiegeln.
		// Die Klasse TripleMerger ist dann dafür da, alle diese Triples an Stellen
		// zusammenzuführen, wo sich die Pfade überlappen.
		ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

		// Update Query wird erstellt
		String updateQueryFromJSON = generator.createUpdateQueryFromJSON(json);

		System.out.println(updateQueryFromJSON);

		// Die Anno4j Klasse kennst Du ja schon etwas. Diese "gaukelt" uns hier einen
		// Triplestore vor.
		// Für einen Test reicht es uns, die Daten in-memory zu halten, wir brauchen
		// also keinen wirklichen Triplestore.
		Anno4j anno4j = new Anno4j(false);

		ObjectConnection connection = anno4j.getObjectRepository().getConnection();

		// Das Update Objekt ist eine Kapselung der UPDATE SPARQL Query, welche im
		// nächsten Schritt ausgeführt wird.
		// Durch die Verbindung der ObjectConnection zum Anno4j Objekt, wird die Query
		// im Anno4j Speicher ausgeführt.
		Update update = connection.prepareUpdate(updateQueryFromJSON);
		update.execute();

		// Nun um noch zu leichten testen, dass alles auch geklappt hat.
		// Der QueryService ist eine eigene Implementierung für Anno4j, um Queries zu
		// basteln und abzuschicken.
		QueryService qs = anno4j.createQueryService();

		// Query nach allen Activity Objekten
		List<Activity> activities = qs.execute(Activity.class);

		Activity activity = activities.get(0);

		QueryService qs2 = anno4j.createQueryService();

		// Query nach allen Place Objekten
		List<Place> places = qs2.execute(Place.class);

		Place place = places.get(0);

		// Vergleich, ob die Verbindung von Activity zu Place besteht und ob die
		// entsprechenden IDs passen
		assertEquals(activity.getP7TookPlaceAt().getResourceAsString(), place.getResourceAsString());
	}

	@Test
	public void testExcelParserWithArchitecture() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelArchitectureTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelArchitectureTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray architectureArray = jsonObject.getJSONArray("Architecture");

		assertEquals(1, architectureArray.length());

		JSONObject architecture = architectureArray.getJSONObject(0);

		assertEquals(VISMO.ARCHITECTURE, architecture.getString("type"));
		assertEquals("sakral", architecture.getString("arch_sacraltype"));
		assertEquals("profan", architecture.getString("arch_has_seculartype"));

		assertEquals("objekt", architecture.getString("arch_currentlyholds_object"));

		JSONObject production = architecture.getJSONArray("arch_producedby_production").getJSONObject(0);

		assertEquals("auftraggeber", production.getString("production_motivatedby_person"));
		assertEquals("Bez2", production.getString("production_carriedoutby_person"));
		assertEquals("beteiligter", production.getString("production_inflby_person"));

		JSONObject productionDating = production.getJSONArray("arch_production_dating").getJSONObject(0);

		assertEquals("beginn", productionDating.getString("arch_prod_dating_start"));
		assertEquals("ende", productionDating.getString("arch_prod_dating_end"));
		assertEquals("frei", productionDating.getString("archproduction_sometime"));
		assertEquals("jahrhundert", productionDating.getString("arch_prod_dating_century"));

		JSONObject modification = architecture.getJSONArray("arch_modifiedby_structevolution").getJSONObject(0);

		assertEquals("titel", modification.getString("structuralevolution_idby_title"));
		assertEquals("beschreibung", modification.getString("structuralevolution_description"));

		JSONObject modificationDating = modification.getJSONArray("arch_structevol_dating").getJSONObject(0);

		assertEquals("beginn", modificationDating.getString("arch_structevol_dating_start"));
		assertEquals("ende", modificationDating.getString("arch_structevol_dating_end"));
		assertEquals("frei", modificationDating.getString("arch_evol_dat_sometime"));
		assertEquals("jahrhundert", modificationDating.getString("arch_structevol_dating_century"));
	}

	@Test
	public void testExcelParserWithActivityAndSplitSubgroupValues()
			throws IOException, ExcelParserException, JSONException, IdMapperException, QueryGenerationException {
		File originalFile = new File("src/test/resources/visitExcelActivitySplitSubgroupValuesTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivitySplitSubgroupValuesTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray activityArray = jsonObject.getJSONArray("Activity");

		assertEquals(1, activityArray.length());

		JSONObject activity = activityArray.getJSONObject(0);

		assertEquals("titel, titel2", activity.getString("activity_idby_title"));
		assertEquals("beschr, beschr2", activity.getString("activity_description"));

		JSONArray refArray = activity.getJSONArray("activity_refentry");

		assertEquals(2, refArray.length());

		JSONObject refentry1 = refArray.getJSONObject(0);

		assertEquals("kurztitel1", refentry1.getString("activity_refentry_in_reference"));
		assertEquals("seiten1", refentry1.getString("activity_refentry_pages"));

		JSONObject refentry2 = refArray.getJSONObject(1);

		assertEquals("kurztitel2", refentry2.getString("activity_refentry_in_reference"));
		assertEquals("seiten2", refentry2.getString("activity_refentry_pages"));

		ImportQueryGenerator generator = new ImportQueryGenerator("none", "none", "somePath");

		String updateQueryFromJSON = generator.createUpdateQueryFromJSON(json);

		System.out.println(updateQueryFromJSON);
	}

	@Test
	public void testExcelParserWithActivityAndSplitValues() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelActivitySplitValuesTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivitySplitValuesTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray activityArray = jsonObject.getJSONArray("Activity");

		assertEquals(1, activityArray.length());

		JSONObject activity = activityArray.getJSONObject(0);

		assertEquals("titel, titel2", activity.getString("activity_idby_title"));
		assertEquals("beschr, beschr2", activity.getString("activity_description"));
	}

	@Test
	public void testExcelParserWithActivityAndMultipleValues() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelActivityMultipleValuesTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivityMultipleValuesTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray activityArray = jsonObject.getJSONArray("Activity");

		assertEquals(1, activityArray.length());

		JSONObject activity = activityArray.getJSONObject(0);

		assertEquals("titel, titel2", activity.getString("activity_idby_title"));
		assertEquals("beschr, beschr2", activity.getString("activity_description"));
	}

	@Test
	public void testExcelParserWithActivityAndGroup() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelActivityAndGroupTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivityAndGroupTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray groupArray = jsonObject.getJSONArray("Group");

		assertEquals(1, groupArray.length());

		JSONObject group = groupArray.getJSONObject(0);

		assertEquals("name", group.getString("group_idby_actorappel"));
		assertEquals("auftrag", group.getString("group_motiv_structevol"));
		assertEquals("formerobject", group.getString("group_lostcustodyof_object"));

		JSONArray refArray = group.getJSONArray("group_refentry");

		JSONObject reference = refArray.getJSONObject(0);

		assertEquals("titel", reference.getString("group_refentry_in_reference"));
		assertEquals("seiten", reference.getString("group_refentry_pages"));
	}

	@Test
	public void testExcelParserWithSingleGroup() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelGroupTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelGroupTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray groupArray = jsonObject.getJSONArray("Group");

		assertEquals(1, groupArray.length());

		JSONObject group = groupArray.getJSONObject(0);

		assertEquals("groupname", group.getString("group_idby_actorappel"));
		assertEquals("auftrag", group.getString("group_motiv_structevol"));
		assertEquals("formerobject", group.getString("group_lostcustodyof_object"));

		JSONArray referenceArray = group.getJSONArray("group_refentry");

		JSONObject reference = referenceArray.getJSONObject(0);

		assertEquals("titel", reference.getString("group_refentry_in_reference"));
		assertEquals("seiten", reference.getString("group_refentry_pages"));
	}

	@Test
	public void testExcelParserWithActivities() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelActivityTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray activityArray = jsonObject.getJSONArray("Activity");

		assertEquals(1, activityArray.length());

		JSONObject activity = activityArray.getJSONObject(0);

		assertEquals("activitytitel", activity.getString("activity_idby_title"));
		assertEquals("beschr", activity.getString("activity_description"));
		assertEquals("object", activity.getString("activity_used_object"));

		JSONArray datingArray = activity.getJSONArray("activity_dating");

		JSONObject dating = datingArray.getJSONObject(0);

		assertEquals("genau", dating.getString("activity_dating_exact"));
		assertEquals("1/1/01", dating.getString("activity_dating_start"));
	}

	@Test
	public void testExcelParserWithTwoActivities() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelTwoActivityTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray activityArray = jsonObject.getJSONArray("Activity");

		assertEquals(2, activityArray.length());

		JSONObject activity = activityArray.getJSONObject(0);

		assertEquals("titel", activity.getString("activity_idby_title"));
		assertEquals("beschr", activity.getString("activity_description"));
		assertEquals("object", activity.getString("activity_used_object"));

		JSONArray datingArray = activity.getJSONArray("activity_dating");

		JSONObject dating = datingArray.getJSONObject(0);

		assertEquals("genau", dating.getString("activity_dating_exact"));
		assertEquals("1/1/01", dating.getString("activity_dating_start"));

		JSONObject activity2 = activityArray.getJSONObject(1);

		assertEquals("titel2", activity2.getString("activity_idby_title"));
		assertEquals("beschr2", activity2.getString("activity_description"));
		assertEquals("object2", activity2.getString("activity_used_object"));

		JSONArray datingArray2 = activity2.getJSONArray("activity_dating");

		JSONObject dating2 = datingArray2.getJSONObject(0);

		assertEquals("genau2", dating2.getString("activity_dating_exact"));
		assertEquals("1/1/01", dating2.getString("activity_dating_start"));

		JSONArray refArray2 = activity2.getJSONArray("activity_refentry");

		JSONObject refentry2 = refArray2.getJSONObject(0);

		assertEquals("titelz2", refentry2.getString("activity_refentry_in_reference"));
		assertEquals("pages2", refentry2.getString("activity_refentry_pages"));
	}

	@Test
	public void testExcelParserWithSingleReference() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelReferenceTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelReferenceTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray referenceArray = jsonObject.getJSONArray("Reference");

		assertEquals(1, referenceArray.length());

		JSONObject reference = referenceArray.getJSONObject(0);

		assertEquals("Typ", reference.getString("reference_has_type"));
		assertEquals("23", reference.getString("reference_pages"));
		assertEquals("Test", reference.getString("reference_keyword"));

		JSONArray referenceEntryArray = reference.getJSONArray("reference_entry");

		JSONObject referenceEntry = referenceEntryArray.getJSONObject(0);

		assertEquals("Passau", referenceEntry.getString("reference_entry_about_place"));
		assertEquals("24", referenceEntry.getString("reference_entry_pages"));
	}

	@Test
	public void testExcelParserWithTwoPersons() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelTwoPersonTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelPersonTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray personArray = jsonObject.getJSONArray("Person");

		assertEquals(2, personArray.length());

		JSONObject person = personArray.getJSONObject(0);

		assertEquals("Vorname", person.getString("person_firstname"));
		assertEquals("Beruf", person.getString("person_hastype_profession"));
		assertEquals("Kommentar", person.getString("person_comment"));

		JSONArray datingArray = person.getJSONArray("person_birth");
		JSONObject birthDatingArray = datingArray.getJSONObject(0);

		JSONArray dating = birthDatingArray.getJSONArray("person_birth_dating");

		assertEquals("5/22/74", dating.getJSONObject(0).getString("person_birth_dating_exact"));

		JSONObject person2 = personArray.getJSONObject(1);

		assertEquals("Vorname2", person2.getString("person_firstname"));
		assertEquals("Haus", person2.getString("person_ownerof_architecture"));
		assertEquals("KN2", person2.getString("person_pseudonym"));

		JSONArray datingArray2 = person2.getJSONArray("person_marriage");
		JSONObject birthDatingArray2 = datingArray2.getJSONObject(0);

		JSONArray dating2 = birthDatingArray2.getJSONArray("marriage_begin_dating");

		assertEquals("frei", dating2.getJSONObject(0).getString("person_marriage_dating_sometime"));

		JSONArray refArray2 = person2.getJSONArray("person_refentry");

		JSONObject refentry2 = refArray2.getJSONObject(0);

		assertEquals("4", refentry2.getString("person_refentry_pages"));
	}

	@Test
	public void testExcelParserWithInstitutionAndPlace() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelInstitutionAndPlaceTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelInstitutionAndPlaceTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray InstitutionArray = jsonObject.getJSONArray("Institution");

		assertEquals(1, InstitutionArray.length());

		JSONObject institution = InstitutionArray.getJSONObject(0);

		assertEquals("instiname", institution.getString("institution_idby_appel"));
		assertEquals("place", institution.getString("institution_fallswithin_place"));
		assertEquals("Ausstellung", institution.getString("institution_owns_catalog"));

		JSONArray PlaceArray = jsonObject.getJSONArray("Place");

		assertEquals(1, PlaceArray.length());

		JSONObject place = PlaceArray.getJSONObject(0);

		assertEquals("Regensburg", place.getString("place_idby_placeappel"));
		assertEquals("schl", place.getString("place_keyword"));

		JSONArray referenceEntryArray = place.getJSONArray("place_refentry");

		JSONObject referenceEntry = referenceEntryArray.getJSONObject(0);

		assertEquals("kt", referenceEntry.getString("place_refentry_in_reference"));
		assertEquals("5", referenceEntry.getString("place_refentry_pages"));
	}

	@Test
	public void testExcelParserWithSingleObject() throws IOException, ExcelParserException, JSONException {
		File originalFile = new File("src/test/resources/visitExcelObjectTest.xlsx");

		InputStream is = new FileInputStream(originalFile);

		MultipartFile file = new MockMultipartFile("visitExcelObjectTest.xlsx", is);

		ExcelParser parser = new ExcelParser();

		String json = parser.createJSONFromParsedExcelFile(file);

		System.out.println(json);

		JSONObject jsonObject = new JSONObject(json);

		JSONArray objectArray = jsonObject.getJSONArray("Object");

		assertEquals(1, objectArray.length());

		JSONObject object = objectArray.getJSONObject(0);

		assertEquals("Objekttitel", object.getString("object_identifiedby_title"));
		assertEquals("Funktion", object.getString("object_exemplifies_function"));
		assertEquals("beschr", object.getString("object_description"));
		
		JSONArray objectDating = object.getJSONArray("object_dating");
		JSONObject objectDatingObject = objectDating.getJSONObject(0);
		
		assertEquals("19", objectDatingObject.getString("object_dating_century"));

		JSONArray objectProvDating = object.getJSONArray("object_transferred_custody");
		JSONObject objectProvDatingObject = objectProvDating.getJSONObject(0);
		JSONArray provDating = objectProvDatingObject.getJSONArray("object_toc_dating");

		assertEquals("20", provDating.getJSONObject(0).getString("object_toc_dating_century"));
		
		JSONArray objectProduction = object.getJSONArray("object_producedby_production");
		JSONObject objectProductionObject = objectProduction.getJSONObject(0);
		JSONArray production = objectProductionObject.getJSONArray("production_dating");

		assertEquals("18", production.getJSONObject(0).getString("object_prod_dating_century"));
		assertEquals("pers", objectProduction.getJSONObject(0).getString("production_doneby_person"));

	}

}