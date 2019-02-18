package rest.persistence.util;

import com.opencsv.CSVReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import rest.VisitRestApplication;
import rest.application.exception.QueryGenerationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@Component
public class ImportQueryGenerator {

    private HashMap<String, HashMap<String, String>> basicGroups;
    private HashMap<String, HashMap<String, String>> subGroups;

    private HashSet<String> basicGroupNames;
    private HashSet<String> idnames;

    private String errors;

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    public ImportQueryGenerator() {
        this.errors = "The following errors are contained in the supported JSON input:\n";

        this.initialiseQueryWrappers();
    }

    public String createUpdateQueryFromJSON(String json) throws QueryGenerationException {
        JSONObject jsonObject = new JSONObject(json);

        for(String rootObjectName : jsonObject.keySet()) {
            if(this.basicGroupNames.contains(rootObjectName)) {
                Object rootObject = jsonObject.get(rootObjectName);

                if(rootObject instanceof JSONArray) {

                } else if(rootObject instanceof JSONObject) {

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

    private String processJSONObject(JSONObject jsonObject, String groupName) {

        String query = "";

        for(String id : jsonObject.keySet()) {
            String value = jsonObject.getString(id);

            // Check if id is given in the supported model
            if(this.idnames.contains(id)) {
                // Check if id stands for a property/relationship or for a subgroup
                if(this.subGroups.keySet().contains(id)) {
                    // Subgroup


                } else {
                    // Normal id, check if multiple or single value supported
                    if(this.isSingleValue(value)) {
                        query += this.createQueryAddition(groupName, value, id);
                    } else {
                        for(String split : value.split(",")) {
                            query += this.createQueryAddition(groupName, split, id);
                        }
                    }
                }
            } else {
                this.errors += "The given id " + id + " is not supported in the underlying model and thereby ignored.\n";
            }
        }


        return query;
    }

    private String createQueryAddition(String groupName, String value, String id) {
        String queryAddition = this.basicGroups.get(groupName).get(id);

        queryAddition = queryAddition.replace(id, value);

        return queryAddition + " .\n";
    }

    private boolean isSingleValue(String text) {
        return !text.contains(",");
    }

    private void initialiseQueryWrappers() {
        this.basicGroups = new HashMap<String, HashMap<String, String>>();
        this.subGroups = new HashMap<String, HashMap<String, String>>();

        this.basicGroupNames = new HashSet<String>();
        this.idnames = new HashSet<String>();

        // TODO Change this to also productive version, path checking etc.
        String csv = "templates/wrapper.csv";

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csv));

            String line[];

            while ((line = reader.readNext()) != null) {
                String group = line[2];

                if(Character.isUpperCase(group.charAt(0))) {
                    // Basic group
                    if(!this.basicGroups.containsKey(group)) {
                        this.basicGroups.put(group, new HashMap<String, String>());
                        this.basicGroupNames.add(group);
                    }

                    this.basicGroups.get(group).put(line[1], line[3]);
                } else {
                    // Subgroup
                    if(!this.subGroups.containsKey(group)) {
                        this.subGroups.put(group, new HashMap<String, String>());
                    }

                    this.subGroups.get(group).put(line[1], line[3]);
                }

                this.idnames.add(line[1]);
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
}
