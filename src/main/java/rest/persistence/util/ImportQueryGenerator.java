package rest.persistence.util;

import com.opencsv.CSVReader;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.application.exception.IdMapperException;
import rest.application.exception.QueryGenerationException;
import rest.configuration.VisitIDGenerator;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImportQueryGenerator {

	private String sparqlEndpointQuery;

	private String sparqlEndpointUpdate;

	private String pathToTemplates;

	private HashMap<String, HashMap<String, String>> basicGroups;
	private HashMap<String, HashMap<String, String>> subGroups;

	private HashSet<String> basicGroupNames;
	private HashSet<String> idnames;

	private HashMap<String, String> datatypes;

	List<String> totalList = new ArrayList<String>();

	private IdMapper mapper;

	private String errors = "";

	public ImportQueryGenerator(String sparqlEndpointQuery, String sparqlEndpointUpdate, String pathToTemplates) {
		this.sparqlEndpointQuery = sparqlEndpointQuery;
		this.sparqlEndpointUpdate = sparqlEndpointUpdate;
		this.pathToTemplates = pathToTemplates;

		this.initialiseQueryWrappers();

		this.mapper = new IdMapper();
	}

	/**
	 * Creates an update query String from a JSON string for the default graph.
	 * 
	 * @param json the JSON, e.g. created by the {@link ExcelParser}
	 * @return the update query as a String
	 * @throws QueryGenerationException if the query couldn't be created, e.g. for syntax reasons
	 * @throws IdMapperException if there was a problem with the identifiers within the JSON
	 */
	public String createUpdateQueryFromJSON(String json) throws QueryGenerationException, IdMapperException {
		JSONObject jsonObject = new JSONObject(json);
		LinkedList<String> queries = new LinkedList<String>();

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String rootObjectName = iterator.next();

			if (this.basicGroupNames.contains(rootObjectName)) {
				Object rootObject = jsonObject.get(rootObjectName);

				if (rootObject instanceof JSONArray) {
					JSONArray array = (JSONArray) rootObject;

					for (int i = 0; i < array.length(); ++i) {
						JSONObject object = array.getJSONObject(i);

						LinkedList<String> queryParts = this.processJSONObject(object, rootObjectName);
						String mergedTriples = TripleMerger.mergeTriples(queryParts);

						String objectID = "";

						if (object.getString(JSONVISMO.ID) == null) {
							objectID = this.mapper.addBaseID(object.getString(JSONVISMO.SECONDARY_IDENTIFIER),
									object.getString(JSONVISMO.TYPE));
						} else {
							objectID = this.mapper.addBaseID(object.getString(JSONVISMO.ID),
									object.getString(JSONVISMO.TYPE));
						}
						queries.add(this.exchangeRDFVariables(mergedTriples, objectID));
					}
				} else if (rootObject instanceof JSONObject) {
					JSONObject currentObject = (JSONObject) rootObject;
					LinkedList<String> queryParts = this.processJSONObject(currentObject, rootObjectName);
					String mergedTriples = TripleMerger.mergeTriples(queryParts);

					String objectID = "";

					if (currentObject.getString(JSONVISMO.ID) == null) {
						objectID = this.mapper.addBaseID(currentObject.getString(JSONVISMO.SECONDARY_IDENTIFIER),
								currentObject.getString(JSONVISMO.TYPE));
					} else {
						objectID = this.mapper.addBaseID(currentObject.getString(JSONVISMO.ID),
								currentObject.getString(JSONVISMO.TYPE));
					}
					queries.add(this.exchangeRDFVariables(mergedTriples, objectID));
				} else {
					throw new QueryGenerationException("Input JSON String contained an incorrect object.");
				}
			} else {
				throw new QueryGenerationException("Root name " + rootObjectName + " is not an accepted entity.");
			}
		}

		String overallQuery = "INSERT DATA {\n";
		for (String query : queries) {
			overallQuery += query;
		}
		overallQuery += "}";

		return overallQuery;
	}

	/**
	 * Creates an update query String from a JSON string for a context
	 * 
	 * @param json the JSON, e.g. created by the {@link ExcelParser}
	 * @param context the context for the graph
	 * @return the update query as a String
	 * @throws QueryGenerationException if the query couldn't be created, e.g. for syntax reasons
	 * @throws IdMapperException if there was a problem with the identifiers within the JSON
	 */
	public String createUpdateQueryFromJSONIntoContext(String json, String context)
			throws IdMapperException, QueryGenerationException {
		String updateQueryFromJSON = this.createUpdateQueryFromJSON(json);

		String updateQueryFromJSONWithContext = "";

		if (!context.isEmpty()) {
			updateQueryFromJSONWithContext = updateQueryFromJSON.replace("INSERT DATA {\n",
					"INSERT DATA { GRAPH <" + context + "> {\n");
			updateQueryFromJSONWithContext = updateQueryFromJSONWithContext.concat("\n}");
		}

		return updateQueryFromJSONWithContext;
	}

	private LinkedList<String> processJSONObject(JSONObject jsonObject, String groupName) throws IdMapperException {
		boolean marriage = false;

		LinkedList<String> queryParts = new LinkedList<String>();
		if (this.basicGroups.keySet().contains(groupName)) {
			queryParts.add(this.typeAssociationTriple(groupName));
		}

		Iterator<String> iterator = jsonObject.keys();

		// special case for dating as the elements within this group need to be sorted
		if (groupName.contains("dating")) {
			ArrayList<String> ids = new ArrayList<String>();

			while (iterator.hasNext()) {
				ids.add(iterator.next());
			}

			// sort ids
			if (groupName.equals("activity_dating")) {
				//there is another order for activity dating
				ids.sort(new IdComparatorDating(true));
			} else {
				ids.sort(new IdComparatorDating(false));
			}

			String id = "";
			String value = "";
			for (int i = 0; i < ids.size(); i++) {
				id = ids.get(i);
				value = jsonObject.getString(id);

				if (this.isSingleValue(value)) {
					queryParts.add(this.createQueryAddition(groupName, value, id));
				} else {
					for (String split : value.split(", ")) {

						split = split.trim();
						queryParts.add(this.createQueryAddition(groupName, split, id));
					}
				}
			}
		}

		while (iterator.hasNext()) {
			String id = iterator.next();

			if (id.equals(JSONVISMO.TYPE)) {

			} else if (id.equals(JSONVISMO.ID)) {
				// Supposedly not needed here, done in a step before
//                objectID = this.mapper.addBaseID(jsonObject.getString(JSONVISMO.ID), jsonObject.getString(JSONVISMO.TYPE));
			} else {

				// Check if id is given in the supported model
				if (this.idnames.contains(id)) {
					// Check if id stands for a property/relationship or for a subgroup
					if (this.subGroups.keySet().contains(id)) {
						// Subgroup
						Object subgroup = jsonObject.get(id);

						if (subgroup instanceof JSONArray) {
							JSONArray subGroupArray = (JSONArray) subgroup;

							for (int i = 0; i < subGroupArray.length(); ++i) {
								LinkedList<String> intermediateQueryParts = processJSONObject(
										subGroupArray.getJSONObject(i), id);

								String[] split = TripleMerger.mergeTriples(intermediateQueryParts).split(" .\n");
								List<String> splitList = Arrays.asList(split);

								//special case for marriage as this includes subsubgroups
								if (splitList.get(0).contains("P107i_is_current_or_former_member_of")) {
									marriage = true; 
									totalList.addAll(splitList);
								} else if (totalList.size() > 0 && totalList.get(0).contains("P107i_is_current_or_former_member_of")) {
									marriage = true; 
								} else {
									queryParts.addAll(this.exchangeRDFVariablesInList(splitList));
								}
							}

							//if the recursion reaches the start again, add all parts to the query only now
							if (marriage && id.equals("person_marriage")) {
								String[] split = TripleMerger.mergeTriples(totalList).split(" .\n");
								queryParts.addAll(this.exchangeRDFVariablesInList(Arrays.asList(split)));
								totalList.clear();
							}
						} else {
							LinkedList<String> intermediateQueryParts = processJSONObject((JSONObject) subgroup, id);

							String[] split = TripleMerger.mergeTriples(intermediateQueryParts).split(" .\n");

							queryParts.addAll(this.exchangeRDFVariablesInList(Arrays.asList(split)));
//                            queryParts.add(TripleMerger.mergeTriples(processJSONObject((JSONObject) subgroup, id)));
						}
					} else {
						String value = jsonObject.getString(id);

						// Normal id, check if multiple or single value supported
						if (this.isSingleValue(value)) {
//                            query += this.createQueryAddition(groupName, value, id);
							queryParts.add(this.createQueryAddition(groupName, value, id));
						} else {
							for (String split : value.split(", ")) {
//                                query += this.createQueryAddition(groupName, split, id);

								split = split.trim();
								queryParts.add(this.createQueryAddition(groupName, split, id));
							}
						}
					}
				} else {
					this.errors  += "The given id " + id
							+ " is not supported in the underlying model and thereby ignored.\n";
				}
			}
		}

		return queryParts;
	}

	private String typeAssociationTriple(String groupName) {
		String id = VISMO.typeAssociation(groupName);

		return "?x rdf:type <" + id + ">";
	}

	private String exchangeRDFVariables(String input, String objectID) {
		String result = input.replace("?x", "<" + objectID + ">");

		// Find all occurences of intermediary variables (written as ?y + a number) and
		// replace them with same URIs
		int index = input.indexOf("?y");
		while (index > 0) {
			int start = index;
			int end = input.indexOf(" ", index);

			String intermediary = input.substring(start, end);
			String uri = VisitIDGenerator.generateVisitDBID();

			result = result.replace(intermediary + " ", "<" + uri + "> ");

			index = input.indexOf("?y", start + 1);
		}

		return result;
	}

	private LinkedList<String> exchangeRDFVariablesInList(List<String> input) {
		LinkedList<String> result = new LinkedList<String>();
		HashMap<String, String> substitutions = new HashMap<String, String>();
//        substitutions.put("?x", VisitIDGenerator.generateVisitDBID());

		for (String triple : input) {
			String change = triple;

			for (String subKey : substitutions.keySet()) {
				String fullSubKey = subKey + " ";
				if (change.contains(fullSubKey)) {
					change = change.replace(subKey, "<" + substitutions.get(subKey) + ">");
				}
			}

			int index = change.indexOf("?y");
			while (index > 0) {
				int start = index;
				int end = change.indexOf(" ", index);
				if (end == -1) {
					end = change.length();
				}

				String intermediary = change.substring(start, end);
				String uri = VisitIDGenerator.generateVisitDBID();

				change = change.replace(intermediary, "<" + uri + ">");
				substitutions.put(intermediary, uri);

				index = change.indexOf("?y", start + 1);
			}

			result.add(change);
		}

		return result;
	}

	private String createQueryAddition(String groupName, String value, String id) {
		String queryAddition = "";

		if (this.basicGroups.containsKey(groupName)) {
			queryAddition = this.basicGroups.get(groupName).get(id);
		} else {
			queryAddition = this.subGroups.get(groupName).get(id);
		}

		String queryValue = value;

		if (this.datatypes.get(id).startsWith("entity_reference")) {
			if (value.contains("http") && value.contains("visit")) {
				queryValue = "<" + value + ">";
			} else {
				queryValue = "<" + this.mapper.addReferenceID(value) + ">";
			}
		} else if (this.datatypes.get(id).startsWith("string")) {
			queryValue = "\"" + value + "\"";

			// Remove protected comma from ExcelParser
			queryValue = queryValue.replaceAll("\\[,\\]", ",");

			if (StringUtils.isNumeric(value)) {
				queryValue += "^^xsd:integer";
			}
		}

		return queryAddition.replace("?" + id, queryValue);
	}

	private boolean isSingleValue(String text) {
		return !text.contains(",");
	}

	private void initialiseQueryWrappers() {
		this.basicGroups = new HashMap<String, HashMap<String, String>>();
		this.subGroups = new HashMap<String, HashMap<String, String>>();

		this.basicGroupNames = new HashSet<String>();
		this.idnames = new HashSet<String>();

		this.datatypes = new HashMap<String, String>();

		String csv = "";

		//loads the parts of the queries from the csv file
		if (this.sparqlEndpointUpdate.equals("none") && this.sparqlEndpointQuery.equals("none")) {
			csv = "templates/wrapper.csv";
		} else {
			csv = this.pathToTemplates + "/wrapper.csv";
		}

		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(csv));

			String line[];

			while ((line = reader.readNext()) != null) {
				String group = line[2];

				if (Character.isUpperCase(group.charAt(0))) {
					// Basic group
					if (!this.basicGroups.containsKey(group)) {
						this.basicGroups.put(group, new HashMap<String, String>());
						this.basicGroupNames.add(group);
					}

					this.basicGroups.get(group).put(line[1], line[3]);
				} else {
					// Subgroup
					if (!this.subGroups.containsKey(group)) {
						this.subGroups.put(group, new HashMap<String, String>());
						this.idnames.add(group);
					}

					this.subGroups.get(group).put(line[1], line[3]);
				}

				this.idnames.add(line[1]);

				String datatype = line[4];
				if (datatype.equals("datetime") || datatype.equals("list_string")) {
					this.datatypes.put(line[1], "string");
				} else {
					this.datatypes.put(line[1], line[4]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets basicGroups.
	 *
	 * @return Value of basicGroups.
	 */
	public HashMap<String, HashMap<String, String>> getBasicGroups() {
		return basicGroups;
	}

	/**
	 * Gets subGroups.
	 *
	 * @return Value of subGroups.
	 */
	public HashMap<String, HashMap<String, String>> getSubGroups() {
		return subGroups;
	}

	/**
	 * Gets basicGroupNames.
	 *
	 * @return Value of basicGroupNames.
	 */
	public HashSet<String> getBasicGroupNames() {
		return basicGroupNames;
	}

	/**
	 * Gets idnames.
	 *
	 * @return Value of idnames.
	 */
	public HashSet<String> getIdnames() {
		return idnames;
	}

	/**
	 * Gets datatypes.
	 *
	 * @return Value of datatypes.
	 */
	public HashMap<String, String> getDatatypes() {
		return datatypes;
	}

	/**
	 * Gets mapper.
	 *
	 * @return Value of mapper.
	 */
	public IdMapper getMapper() {
		return mapper;
	}

	/**
	 * Dating attributes need to be in a special order to be handled in the right
	 * way. This order is messed up, when the string is changed to a JSONObject.
	 * This Comparator is used to bring them into the right order again.
	 *
	 */
	private class IdComparatorDating implements Comparator<String> {
		private boolean activityDating = false;

		/**
		 * Activity dating needs a different order than all other dating attributes.
		 * 
		 * @param activityDating true, if the id is 'activity_dating'
		 */
		public IdComparatorDating(boolean activityDating) {
			this.activityDating = activityDating;
		}

		@Override
		public int compare(String o1, String o2) {
			if (this.activityDating) {
				if (o1.contains("exact")) {
					return -1;
				} else if (o2.contains("exact")) {
					return 1;
				} else if (o1.contains("end")) {
					return -1;
				} else if (o2.contains("end")) {
					return 1;
				} else if (o1.contains("start")) {
					return -1;
				} else if (o2.contains("start")) {
					return 1;
				}
			} else {
				if (o1.contains("exact")) {
					return -1;
				} else if (o2.contains("exact")) {
					return 1;
				} else if (o1.contains("start")) {
					return -1;
				} else if (o2.contains("start")) {
					return 1;
				} else if (o1.contains("end")) {
					return -1;
				} else if (o2.contains("end")) {
					return 1;
				} else if (o1.contains("some")) {
					return -1;
				} else if (o2.contains("some")) {
					return 1;
				}
			}

			return 0;
		}

	}
}
