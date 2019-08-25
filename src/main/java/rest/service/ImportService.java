package rest.service;

import org.apache.marmotta.ldpath.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.anno4j.Anno4j;
import rest.application.exception.ExcelParserException;
import rest.application.exception.IdMapperException;
import rest.application.exception.ImportException;
import rest.application.exception.QueryGenerationException;
import rest.persistence.repository.ImportRepository;
import rest.persistence.util.ExcelParser;
import rest.persistence.util.ImportQueryGenerator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Service class for the import functionality.
 */
@Service
public class ImportService {

	@Autowired
	private ImportRepository importRepository;

	@Autowired
	private ImportQueryGenerator importQueryGenerator;

	@Autowired
	private ExcelParser excelParser;

	public void importJSON(String json) throws ImportException {
		try {
			try {
				json = updateJSON(json, "");
				json = checkForDuplicateIds(json);
			} catch (QueryEvaluationException e) {
				throw new ImportException(e.getMessage());
			} catch (JSONException e) {
				throw new ImportException(e.getMessage());
			} catch (ParseException e) {
				throw new ImportException(e.getMessage());
			}
			String updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(json);

			this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
		} catch (QueryGenerationException | IdMapperException | UpdateExecutionException | MalformedQueryException
				| RepositoryException e) {
			throw new ImportException(e.getMessage());
		}
	}

	public void importExcelUpload(MultipartFile file, String context) throws ExcelParserException {
		try {
			String jsonFromParsedExcelFile = this.excelParser.createJSONFromParsedExcelFile(file);

			jsonFromParsedExcelFile = updateJSON(jsonFromParsedExcelFile, context);

			// check for duplicate ids needs to be done here as we need to check
			// what values are actually in the database instead of
			// possibly wrong values in system
			jsonFromParsedExcelFile = checkForDuplicateIds(jsonFromParsedExcelFile);

			String updateQuery = "";

			if (context.isEmpty()) {
				updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(jsonFromParsedExcelFile);
			} else {
				updateQuery = this.importQueryGenerator.createUpdateQueryFromJSONIntoContext(jsonFromParsedExcelFile,
						context);
			}

			this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
		} catch (Exception e) {
			throw new ExcelParserException(e.getMessage());
		}
	}

	private String checkForDuplicateIds(String jsonFromParsedExcelFile)
			throws QueryEvaluationException, JSONException, RepositoryException, IdMapperException {
		ArrayList<String> idList = new ArrayList<String>();
		Anno4j anno4j = this.importRepository.getAnno4j();
		ObjectConnection connection = anno4j.getObjectRepository().getConnection();
		JSONObject jsonObject = new JSONObject(jsonFromParsedExcelFile);

		Iterator<String> iterator = jsonObject.keys();
		String key;

		Object object;

		while (iterator.hasNext()) {
			key = iterator.next();
			object = jsonObject.get(key);

			if (object instanceof JSONArray) {
				JSONArray array = (JSONArray) object;
				String currentKey = "";
				for (int i = 0; i < array.length(); ++i) {
					JSONObject jsonObjectFromArray = array.getJSONObject(i);
					Iterator<String> iteratorValues = jsonObjectFromArray.keys();
					while (iteratorValues.hasNext()) {
						currentKey = iteratorValues.next();
						if ((currentKey.equals("id"))) {
							final String value = jsonObjectFromArray.getString(currentKey);

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
								if (!result.hasNext()) {
									if (idList.contains(value)) {
										throw new IdMapperException("The read baseID <" + value
												+ "> clashes and is thereby used more than once, which is not allowed!");
									} else {
										idList.add(value);
									}
								} else {
									throw new IdMapperException("The read baseID <" + value
											+ "> clashes and is thereby used more than once, which is not allowed!");
								}

							} catch (MalformedQueryException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		connection.close();
		return jsonFromParsedExcelFile;
	}

	private String updateJSON(String json, String context) throws RepositoryException, QueryEvaluationException,
			JSONException, MalformedQueryException, ParseException {
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
						if (!(currentKey.equals("id") || currentKey.contains("idby")
								|| currentKey.contains("identifiedby"))) {
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

											if (subKey.equals("reference_title_title")) {
												// ignore field as it is the id
												updatedJSONObject.put(subKey, subValue);
											} else if (subValue.contains("http:") && subValue.contains("visit")) {
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
						} else {
							jsonObjectFromArray.put(currentKey, jsonObjectFromArray.getString(currentKey));
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
}
