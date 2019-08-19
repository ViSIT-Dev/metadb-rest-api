package rest.persistence.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.github.anno4j.Anno4j;
import rest.BaseWebTest;
import rest.application.exception.ExcelParserException;

public class ExcelParserLinksTest extends BaseWebTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testPersonLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelTwoPersonTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelPersonTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Person");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Person");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testArchitectureLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelArchitectureTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelArchitectureTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Architecture");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Architecture");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPlaceLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelInstitutionAndPlaceTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelInstitutionAndPlaceTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Place");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Place");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInstitutionLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelInstitutionAndPlaceTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelInstitutionTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Institution");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Institution");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testActivityLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelActivityTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelActivityTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Activity");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Activity");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testReferenceLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelReferenceTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelReferenceTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Reference");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Reference");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testObjectLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelObjectTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelObjectTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Object");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Object");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGroupLinking()
			throws IOException, ExcelParserException, JSONException, RepositoryException, QueryEvaluationException {
		File originalFile = new File("src/test/resources/visitExcelGroupTest.xlsx");
		InputStream is = new FileInputStream(originalFile);
		MultipartFile file = new MockMultipartFile("visitExcelGroupTest.xlsx", is);
		ExcelParser parser = new ExcelParser();
		String jsonNormal = parser.createJSONFromParsedExcelFile(file);
		String jsonTransformed = updateJSON(jsonNormal, "");

		JSONObject jsonObjectNormal = new JSONObject(jsonNormal);
		JSONObject jsonObjectTransformed = new JSONObject(jsonTransformed);

		JSONArray architectureArrayNormal = jsonObjectNormal.getJSONArray("Group");
		JSONArray architectureArrayTransformed = jsonObjectTransformed.getJSONArray("Group");

		JSONObject jsonInnerNormal = (JSONObject) architectureArrayNormal.get(0);
		JSONObject jsonInnerTransformed = (JSONObject) architectureArrayTransformed.get(0);

		assertEquals(jsonInnerNormal.length(), jsonInnerTransformed.length());

		Iterator<String> iterator = jsonInnerNormal.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = jsonInnerNormal.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray arrayTransformed = (JSONArray) jsonInnerTransformed.get(key);

				assertEquals(array.length(), arrayTransformed.length());

				JSONObject subgroup = (JSONObject) array.get(0);
				JSONObject subgroupTransformed = (JSONObject) arrayTransformed.get(0);
				Iterator<String> subgroupIterator = subgroup.keys();
				while (subgroupIterator.hasNext()) {
					String subgroupKey = subgroupIterator.next();
					Object subgroupValues = subgroup.get(subgroupKey);
				
					if (subgroupValues instanceof JSONArray) {
						JSONArray subgroupValueArray = (JSONArray) subgroupValues;
						JSONArray subgroupValueTransformedArray = (JSONArray) subgroupTransformed.get(subgroupKey);

						for (int i = 0; i < subgroupValueArray.length(); i++) {
							JSONObject subgroupValue = (JSONObject) subgroupValueArray.get(i);
							JSONObject subgroupValueTransformed = (JSONObject) subgroupValueTransformedArray.get(i);
							Iterator<String> subgroupIteratorObjects = subgroupValue.keys();

							while (subgroupIteratorObjects.hasNext()) {
								String subgroupValueKey = subgroupIteratorObjects.next();
								assertEquals(subgroupValue.get(subgroupValueKey).toString(),
										subgroupValueTransformed.get(subgroupValueKey).toString());
							}

						}
					} else {
						String valueTransformed = subgroupTransformed.getString(subgroupKey);
						assertEquals(subgroupValues.toString(), valueTransformed.toString());
					}

				}
			} else {
				Object valueTransformed = jsonInnerTransformed.get(key);
				assertEquals(value.toString(), valueTransformed.toString());
			}

		}

	}

	
	@SuppressWarnings("unchecked")
	private String updateJSON(String json, String context)
			throws RepositoryException, JSONException, QueryEvaluationException {
		Anno4j anno4j = this.importRepository.getAnno4j();
		ObjectConnection connection = anno4j.getObjectRepository().getConnection();
		JSONObject jsonObject = new JSONObject(json);

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
											if (!context.isEmpty()) {
												queryString += "FROM <" + context + "> \n";
											}
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
								if (!context.isEmpty()) {
									queryString += "FROM <" + context + "> \n";
								}
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

		connection.close();
		return jsonObject.toString();

	}

	@Override
	public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException,
			RepositoryConfigException, MalformedQueryException, UpdateExecutionException {

	}

}
