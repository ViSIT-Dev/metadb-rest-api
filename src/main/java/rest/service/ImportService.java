package rest.service;

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

	/**
	 * Imports a JSON file represented in a String into the database.
	 * 
	 * @param json the JSON to be imported
	 * @throws ImportException if the import failed
	 */
	public void importJSON(String json) throws ImportException {
		try {
			try {
				json = updateJSON(json, "");
				json = checkForDuplicateIds(json);
			} catch (QueryEvaluationException e) {
				throw new ImportException(e.getMessage());
			} catch (JSONException e) {
				throw new ImportException(e.getMessage());
			}
			String updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(json);

			this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
		} catch (QueryGenerationException | IdMapperException | UpdateExecutionException | MalformedQueryException
				| RepositoryException e) {
			throw new ImportException(e.getMessage());
		}
	}

	/**
	 * Imports an excel file into the database.
	 * 
	 * @param file    the excel file to be imported
	 * @param context the context within the graph. If the default graph should be
	 *                used, use an empty string.
	 * 
	 * @throws ExcelParserException if the file can't be imported or parsed
	 */
	public void importExcelUpload(MultipartFile file, String context) throws ExcelParserException {
		try {
			String jsonFromParsedExcelFile = this.excelParser.createJSONFromParsedExcelFile(file);

			//check, if elements reference others by id
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

	/**
	 * Checks all ids for duplicates within the database as IDs have to be unique.
	 * 
	 * @param jsonFromParsedExcelFile the JSON to be checked
	 * @return the JSON
	 * @throws RepositoryException      if the database has an error
	 * @throws QueryEvaluationException if the query result was faulty
	 * @throws JSONException            if the JSON couldn't be read
	 * @throws IdMapperException        if there was a duplicate ID
	 */
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

	/**
	 * Checks the databased for possible references in the JSON (via ID).
	 * 
	 * @param json    the json to be checked
	 * @param context the context of the JSON
	 * @return an updated JSON String
	 * @throws RepositoryException      if the database has an error
	 * @throws QueryEvaluationException if the query result was faulty
	 * @throws JSONException            if the JSON couldn't be read
	 * @throws MalformedQueryException  if the query was malformed
	 */
	private String updateJSON(String json, String context) throws RepositoryException, QueryEvaluationException,
			JSONException, MalformedQueryException {
		String[] result = json.split(",\"");
		boolean change = false;

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
				String currentKey = "";
				Object subObject;
				for (int i = 0; i < array.length(); ++i) {
					JSONObject jsonObjectFromArray = array.getJSONObject(i);
					Iterator<String> iteratorValues = jsonObjectFromArray.keys();
					while (iteratorValues.hasNext()) {
						currentKey = iteratorValues.next();
						//ids are checked for duplicates later
						if (!(currentKey.equals("id") || currentKey.contains("idby")
								|| currentKey.contains("identifiedby"))) {
							subObject = jsonObjectFromArray.get(currentKey);
							String subKey = "";

							if (subObject instanceof JSONArray) {
								JSONArray subObjectArray = (JSONArray) subObject;
								JSONObject subJSONObject = new JSONObject();
								for (int j = 0; j < subObjectArray.length(); j++) {
									subJSONObject = subObjectArray.getJSONObject(j);
									Iterator<String> subKeys = subJSONObject.keys();
									while (subKeys.hasNext()) {
										subKey = subKeys.next();
										// there are no reference fields in subsubparts
										if (!(subJSONObject.get(subKey) instanceof JSONArray)) {
											final String subValue = subJSONObject.getString(subKey);

											if (subKey.equals("reference_title_title")) {
												// ignore field as it is the id
											} else if (subValue.contains("http:") && subValue.contains("visit")) {
												// ignore field as it has already an id
											} else if (subKey.contains("dating")) {
												// ignore field as it is a date
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
													TupleQueryResult resultQuery = temp.evaluate();

													while (resultQuery.hasNext()) {
														BindingSet solution = resultQuery.next();
														Value updatedValue = solution.getValue("s");
														for (int k = 0; k < result.length; k++) {
															if (result[k].contains(subKey)
																	&& result[k].contains(subValue)) {
																change = true;
																String[] split = result[k].split("\":\"");
																String[] subSplit = split[1].split("\"");
																subSplit[0] = updatedValue.stringValue();

																split[1] = "";

																for (int l = 0; l < subSplit.length; l++) {
																	if (subSplit.length == 1
																			|| l < (subSplit.length - 1)) {
																		split[1] = split[1] + subSplit[l] + "\"";
																	} else {
																		split[1] = split[1] + subSplit[l];
																	}
																}

																result[k] = "";

																for (int j1 = 0; j1 < split.length; j1++) {
																	if (j1 < (split.length - 1)) {
																		result[k] = result[k] + split[j1] + "\":\"";
																	} else {
																		result[k] = result[k] + split[j1];
																	}
																}

																k = result.length + 1;

															}
														}
													}
												} catch (MalformedQueryException e) {
													e.printStackTrace();
												}
											}
										}
									}
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
										TupleQueryResult resultQuery = temp.evaluate();
										while (resultQuery.hasNext()) {
											BindingSet solution = resultQuery.next();
											Value updatedValue = solution.getValue("s");

											for (int k = 0; k < result.length; k++) {
												if (result[k].contains(currentKey) && result[k].contains(value)) {
													change = true;
													String[] split = result[k].split("\":\"");
													String[] subSplit = split[1].split("\"");
													subSplit[0] = updatedValue.stringValue();

													split[1] = "";

													for (int l = 0; l < subSplit.length; l++) {
														if (subSplit.length == 1 || l < (subSplit.length - 1)) {
															split[1] = split[1] + subSplit[l] + "\"";
														} else {
															split[1] = split[1] + subSplit[l];
														}
													}

													result[k] = "";

													for (int j = 0; j < split.length; j++) {
														if (j < (split.length - 1)) {
															result[k] = result[k] + split[j] + "\":\"";
														} else {
															result[k] = result[k] + split[j];
														}
													}

													k = result.length + 1;

												}
											}

										}

									} catch (MalformedQueryException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

				}
			}
		}

		connection.close();

		if (change) {
			String resultString = "";

			for (int i = 0; i < result.length; i++) {
				if (i < result.length - 1) {
					resultString = resultString + result[i] + ",\"";
				} else {
					resultString = resultString + result[i];
				}
			}

			return resultString;
		} else {
			return json;
		}

	}
}
