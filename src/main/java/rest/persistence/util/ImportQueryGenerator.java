package rest.persistence.util;

import com.opencsv.CSVReader;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rest.VisitRestApplication;
import rest.application.exception.IdMapperException;
import rest.application.exception.MetadataQueryException;
import rest.application.exception.QueryGenerationException;
import rest.configuration.VisitIDGenerator;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static javafx.scene.input.KeyCode.T;

public class ImportQueryGenerator {

    private String sparqlEndpointQuery;

    private String sparqlEndpointUpdate;

    private String pathToTemplates;

    private HashMap<String, HashMap<String, String>> basicGroups;
    private HashMap<String, HashMap<String, String>> subGroups;

    private HashSet<String> basicGroupNames;
    private HashSet<String> idnames;

    private HashMap<String, String> datatypes;

    private IdMapper mapper;

    private String errors;

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    public ImportQueryGenerator(String sparqlEndpointQuery, String sparqlEndpointUpdate, String pathToTemplates) {
        this.errors = "The following errors are contained in the supported JSON input:\n";

        this.sparqlEndpointQuery = sparqlEndpointQuery;
        this.sparqlEndpointUpdate = sparqlEndpointUpdate;
        this.pathToTemplates = pathToTemplates;

        this.initialiseQueryWrappers();

        this.mapper = new IdMapper();
    }

    public String createUpdateQueryFromJSON(String json) throws QueryGenerationException, IdMapperException {
        JSONObject jsonObject = new JSONObject(json);
        LinkedList<String> queries = new LinkedList<String>();

        Iterator<String> iterator = jsonObject.keys();

        while(iterator.hasNext()) {
            String rootObjectName = iterator.next();

            if (this.basicGroupNames.contains(rootObjectName)) {
                Object rootObject = jsonObject.get(rootObjectName);

                if (rootObject instanceof JSONArray) {
                    JSONArray array = (JSONArray) rootObject;

                    for(int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);

                        LinkedList<String> queryParts = this.processJSONObject(object, rootObjectName);
                        String mergedTriples = TripleMerger.mergeTriples(queryParts);

                        String objectID = this.mapper.addBaseID(object.getString(JSONVISMO.ID), object.getString(JSONVISMO.TYPE));
                        queries.add(this.exchangeRDFVariables(mergedTriples, objectID));
                    }
                } else if (rootObject instanceof JSONObject) {
                    JSONObject currentObject = (JSONObject) rootObject;
                    LinkedList<String> queryParts = this.processJSONObject(currentObject, rootObjectName);
                    String mergedTriples = TripleMerger.mergeTriples(queryParts);

                    String objectID = this.mapper.addBaseID(currentObject.getString(JSONVISMO.ID), currentObject.getString(JSONVISMO.TYPE));
                    queries.add(this.exchangeRDFVariables(mergedTriples, objectID));
                } else {
                    // TODO Can this happen?
                    throw new QueryGenerationException("Input JSON String contained an incorrect object.");
                }
            } else {
                throw new QueryGenerationException("Root name " + rootObjectName + " is not an accepted entity.");
            }
        }

        String overallQuery = "INSERT DATA {\n";
        for(String query : queries) {
            overallQuery += query;
        }
        overallQuery += "}";

        return overallQuery;
    }

    private LinkedList<String> processJSONObject(JSONObject jsonObject, String groupName) throws IdMapperException {

        LinkedList<String> queryParts = new LinkedList<String>();
        if(this.basicGroups.keySet().contains(groupName)) {
            queryParts.add(this.typeAssociation(groupName));
        }

        String objectID = "";

        Iterator<String> iterator = jsonObject.keys();

        while(iterator.hasNext()) {
            String id = iterator.next();

            if (id.equals(JSONVISMO.TYPE)) {

            } else if(id.equals(JSONVISMO.ID)) {
                // Supposedly not needed here, done in a step before
//                objectID = this.mapper.addBaseID(jsonObject.getString(JSONVISMO.ID), jsonObject.getString(JSONVISMO.TYPE));
            } else {

                // Check if id is given in the supported model
                if (this.idnames.contains(id)) {
                    // Check if id stands for a property/relationship or for a subgroup
                    if (this.subGroups.keySet().contains(id)) {
                        // Subgroup
                        Object subgroup = jsonObject.get(id);

                        if(subgroup instanceof JSONArray) {
                            JSONArray subGroupArray = (JSONArray) subgroup;

                            for(int i = 0; i < subGroupArray.length(); ++i) {
                                LinkedList<String> intermediateQueryParts = processJSONObject(subGroupArray.getJSONObject(i), id);

                                String[] split = TripleMerger.mergeTriples(intermediateQueryParts).split(" .\n");

                                queryParts.addAll(this.exchangeRDFVariablesInList(Arrays.asList(split)));


//                                queryParts.add(TripleMerger.mergeTriples(processJSONObject(subGroupArray.getJSONObject(i), id)));
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
                            for (String split : value.split(",")) {
//                                query += this.createQueryAddition(groupName, split, id);
                                queryParts.add(this.createQueryAddition(groupName, split, id));
                            }
                        }
                    }
                } else {
                    this.errors += "The given id " + id + " is not supported in the underlying model and thereby ignored.\n";
                }
            }
        }

        return queryParts;
    }

    private String typeAssociation(String groupName) {
        String groupID = "";

        switch(groupName) {
            case "Activity":
                groupID = VISMO.ACTIVITY;
                break;
            case "Architecture":
                groupID = VISMO.ARCHITECTURE;
                break;
            case "Group":
                groupID = VISMO.GROUP;
                break;
            case "Institution":
                groupID = VISMO.INSTITUTION;
                break;
            case "Object":
                groupID = VISMO.PERSON;
                break;
            case "Person":
                groupID = VISMO.PERSON;
                break;
            case "Place":
                groupID = VISMO.PLACE;
                break;
            case "Reference":
                groupID = VISMO.REFERENCE;
                break;
        }

        return "?x rdf:type <" + groupID + ">";
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

    private LinkedList<String> exchangeRDFVariablesInList(List<String> input) {
        LinkedList<String> result = new LinkedList<String>();
        HashMap<String, String> substitutions = new HashMap<String, String>();
//        substitutions.put("?x", VisitIDGenerator.generateVisitDBID());

        for(String triple : input) {
            String change = triple;

            for(String subKey : substitutions.keySet()) {
                if(change.contains(subKey)) {
                    change = change.replace(subKey, "<" + substitutions.get(subKey) + ">");
                }
            }

            int index = change.indexOf("?y");
            while(index > 0) {
                int start = index;
                int end = change.indexOf(" ", index);
                if(end == -1) {
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

        if(this.basicGroups.containsKey(groupName)) {
            queryAddition = this.basicGroups.get(groupName).get(id);
        } else {
            queryAddition = this.subGroups.get(groupName).get(id);
        }

        String queryValue = value;

        if(this.datatypes.get(id).startsWith("entity_reference")) {
            queryValue = "<" + this.mapper.addReferenceID(value) + ">";
        } else if(this.datatypes.get(id).startsWith("string")) {
            queryValue = "\"" + value + "\"";
            if(StringUtils.isNumeric(value)) {
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

        if(this.sparqlEndpointUpdate.equals("none") && this.sparqlEndpointQuery.equals("none")) {
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

                this.datatypes.put(line[1], line[4]);
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
}
