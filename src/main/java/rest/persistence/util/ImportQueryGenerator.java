package rest.persistence.util;

import com.opencsv.CSVReader;
import model.namespace.JSONVISMO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import rest.VisitRestApplication;
import rest.application.exception.QueryGenerationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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

    public String createUpdateQueryFromJSON(String json) throws QueryGenerationException {
        JSONObject jsonObject = new JSONObject(json);
        LinkedList<String> queries = new LinkedList<String>();

        for (String rootObjectName : jsonObject.keySet()) {
            if (this.basicGroupNames.contains(rootObjectName)) {
                Object rootObject = jsonObject.get(rootObjectName);

                if (rootObject instanceof JSONArray) {

                } else if (rootObject instanceof JSONObject) {

                } else {
                    // TODO Can this happen?
                    throw new QueryGenerationException("Input JSON String contained an incorrect object.");
                }
            } else {
                // TODO Throw proper exception or info text
                this.errors += "Root name " + rootObjectName + " is not an accepted entity.\n";
            }
        }



        return null;
    }

    private List<String> processJSONObject(JSONObject jsonObject, String groupName) {

//        String query = "";
        LinkedList<String> queryParts = new LinkedList<String>();

        for (String id : jsonObject.keySet()) {

            if (id.equals(JSONVISMO.TYPE)) {

            } else if(id.equals(JSONVISMO.ID)) {

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
                                queryParts.addAll(processJSONObject(subGroupArray.getJSONObject(0), id));
                            }
                        } else {
                            queryParts.addAll(processJSONObject((JSONObject) subgroup, id));
                        }
//                        HashMap<String, String> subGroupIds = this.subGroups.get(id);
//
//                        LinkedList<String> wrapperQueries = new LinkedList<String>();
//
//                        for(String subGroupId : subGroupIds.keySet()) {
//                            if(subGroupId.equals("type")) {
//
//                            } else {
//                                wrapperQueries.add(subGroupIds.get(subGroupId));
//                            }
//                        }
                    } else {
                        String value = jsonObject.getString(id);

                        // Normal id, check if multiple or single value supported
                        if (this.isSingleValue(value)) {
//                            query += this.createQueryAddition(groupName, value, id);
                            queryParts.add(this.createQueryAddition(groupName, value, id));
                        } else {
                            for (String split : value.split(",")) {
//                                query += this.createQueryAddition(groupName, split, id);
                                queryParts.add(this.createQueryAddition(groupName, value, id));
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

    private String createQueryAddition(String groupName, String value, String id) {
        String queryAddition = this.basicGroups.get(groupName).get(id);

        return queryAddition.replace(id, value);
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
}
