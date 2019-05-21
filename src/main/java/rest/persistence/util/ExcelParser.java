package rest.persistence.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import model.namespace.JSONVISMO;
import model.namespace.VISMO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;
import util.excel.ExcelTemplateContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

public class ExcelParser {

    /*
     Map contains the mapping between Excel labels and JSON IDs.
     If the ID is not contained in a subgroup, the entry just contains the ID.
     If the ID is contained in a subgroup, the entry contains the ID which is preceded by the ID of the subgroup,
     separated by a " | " String.
      */
    private HashMap<String, HashMap<String, String>> idMap;

    private HashMap<String, String> subGroupMap;

    private DataFormatter formatter;

    public ExcelParser() {
        this.idMap = new HashMap<String, HashMap<String, String>>();
        this.initializeLabelIDMap();
        this.subGroupMap = new HashMap<String, String>();

        this.formatter = new DataFormatter();
    }

    public String createJSONFromParsedExcelFile(MultipartFile file) throws ExcelParserException {

        // Read in Excel file
        // Check if input file is an Excel file with ending .xlsx

        InputStream fis = null;
        Workbook workbook = null;
        try {
            fis = file.getInputStream();
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new ExcelParserException("Reading the file was unsuccessful or the file is not an Excel file (ending .xlsx).");
        }

        // Create JSON output
        JsonObject json = new JsonObject();

        // Iterate through every sheet (sheet names are persisted in ExcelTemplateContent)
        for (String entity : ExcelTemplateContent.ENTITIES) {
            JsonArray jsonArray = new JsonArray();

            Sheet sheet = workbook.getSheet(entity);

            boolean emptyCell = false;

            int cellIterator = 1;

            while (!emptyCell) {

                JsonObject entityJson = new JsonObject();

                // This list is later used to "revisit" the created JsonObjects for subgroups and check if these are empty
                LinkedList<LinkedList<String>> subgroupTracker = new LinkedList<LinkedList<String>>();

                if (sheet.getRow(0).getCell(cellIterator) == null) {
                    emptyCell = true;
                    break;
                } else {
                    entityJson.addProperty(JSONVISMO.ID, sheet.getRow(0).getCell(cellIterator).getStringCellValue());

                    // Associated the type for the respective entity
                    entityJson.addProperty(JSONVISMO.TYPE, VISMO.typeAssociation(entity));
                }

                for (int i = 1; i <= sheet.getLastRowNum(); ++i) {

                    Row row = sheet.getRow(i);

                    Cell cell = row.getCell(cellIterator);

                    String label = sheet.getRow(i).getCell(0).getStringCellValue();

                    String value = "";

                    if (!label.endsWith("Untergruppe") && !label.endsWith("Unteruntergruppe")) {
//                        value = cell.getStringCellValue();
                        value = this.formatter.formatCellValue(cell);
                    }

                    String id = this.getIdWithoutSubGroup(this.idMap.get(entity).get(label));

                    if (label.endsWith("Untergruppe")) {
                        if (!entityJson.has(id)) {
                            JsonArray subgroupArray = new JsonArray();

                            // Subgroup tracking
                            LinkedList<String> subgroupList = new LinkedList<String>();
                            subgroupList.add(id);
                            subgroupTracker.add(subgroupList);

                            entityJson.add(id, subgroupArray);

                            JsonObject emptyObject = new JsonObject();
                            subgroupArray.add(emptyObject);
                        } else {
                            // Array is existing, so create a new empty JsonObject to fill
                            JsonArray subgroupArray = entityJson.getAsJsonArray(id);

                            JsonObject emptyObject = new JsonObject();
                            subgroupArray.add(emptyObject);
                        }
                    } else if (label.endsWith("Unteruntergruppe")) {
                        String subgroupName = this.getSubGroupOfId(this.idMap.get(entity).get(label));

                        JsonArray subgroupJsonArray = entityJson.getAsJsonArray(subgroupName);

                        JsonObject lastSubgroupObject = (JsonObject) subgroupJsonArray.get(subgroupJsonArray.size() - 1);

                        if (!lastSubgroupObject.has(id)) {

                            JsonArray subsubgroupArray = new JsonArray();

                            // Subgroup tracking
                            // First look for the correct list of the tracker
                            for (LinkedList<String> list : subgroupTracker) {
                                if (list.contains(id)) {
                                    list.add(id);
                                }
                            }

                            JsonObject emptyObject = new JsonObject();

                            subsubgroupArray.add(emptyObject);

                            lastSubgroupObject.add(id, subsubgroupArray);

                            // Add the combination of subgroup and its subsubgroup to a map to use this information later
                            this.subGroupMap.put(id, subgroupName);
                        } else {
                            JsonObject emptyObject = new JsonObject();

                            JsonArray subsubgroupArray = lastSubgroupObject.getAsJsonArray(id);

                            subsubgroupArray.add(emptyObject);
                        }
                    } else {
                        if (!value.isEmpty()) {

                            // Find the respective JsonObject (which is the overall object or a sub-part of it),
                            // to which the current property is to be added
                            JsonObject objectToAddProperty = null;

                            if (this.idContainedInSubgroup(this.idMap.get(entity).get(label))) {
                                // Id is part of a subgroup or subsubgroup

                                // Check if we have a subsubgroup
                                String subgroup = this.getSubGroupOfId(this.idMap.get(entity).get(label));

                                if (this.subGroupMap.containsKey(subgroup)) {
                                    // Subsubgroup affiliation
                                    JsonArray subgroupJsonArray = entityJson.getAsJsonArray(this.subGroupMap.get(subgroup));

                                    JsonObject subgroupLastObject = (JsonObject) subgroupJsonArray.get(subgroupJsonArray.size() - 1);

                                    JsonArray subsubgroupJsonArray = (JsonArray) subgroupLastObject.get(subgroup);

                                    objectToAddProperty = (JsonObject) subsubgroupJsonArray.get(subgroupJsonArray.size() - 1);
                                } else {
                                    // Normal subgroup affiliation
                                    JsonArray subgroupArray = entityJson.getAsJsonArray(subgroup);

                                    objectToAddProperty = (JsonObject) subgroupArray.get(subgroupArray.size() - 1);
                                }
                            } else {
                                // Id is not part of subgroup
                                objectToAddProperty = entityJson;
                            }

                            if (value.contains("|")) {
                                String[] split = value.split("\\|");

                                String updatedValue = "";

                                for (int j = 0; j < split.length; ++j) {
                                    String part = split[j];

                                    updatedValue += part.trim();

                                    if (j < split.length - 1) {
                                        updatedValue += ", ";
                                    }
                                }

                                value = updatedValue;
                            }

                            // Add the actual property
                            if (objectToAddProperty.has(id)) {
                                // Property already existing, so read out old value and add new value
                                JsonPrimitive jsonPrimitive = objectToAddProperty.getAsJsonPrimitive(id);

                                String oldValue = jsonPrimitive.getAsString();

                                objectToAddProperty.addProperty(id, oldValue + ", " + value);
                            } else {
                                objectToAddProperty.addProperty(id, value);
                            }
                        }
                    }
                }

                this.removeEmptySubgroups(entityJson, subgroupTracker);

                jsonArray.add(entityJson);
                ++cellIterator;
            }

            if(jsonArray.size() > 0) {
                json.add(entity, jsonArray);
            }
        }

        return json.toString();
    }

    /**
     * In the idMap of this class every label of the input Excel file is mapped to an ID.
     * This ID can be placed inside a subgroup, which is implemented as follows: "subgroupID | ID" (
     * otherwise only the ID itself is persisted in the HashMap).
     * This method separates the String from the pipe and the subgroupID to get the actual ID.
     *
     * @param input The String persisted in the idMap.
     * @return Only the ID of the respective value mapped to the Excel label.
     */
    private String getIdWithoutSubGroup(String input) {
        if (!input.contains("|")) {
            // ID is not part of a subgroup and therefore only the ID is persisted.
            return input;
        } else {
            return input.substring(input.indexOf("|") + 2, input.length());
        }
    }

    private String getSubGroupOfId(String input) {
        return input.substring(0, input.indexOf("|") - 1);
    }

    private boolean idContainedInSubgroup(String input) {
        return input.contains("|");
    }

    /**
     * Method receives an JsonObject to check and a tracker which contains the possible subgroups in the JsonObject.
     * Then it will be checked, if the respective subgroups are empty, and if yes, they are removed from the JsonObject.
     *
     * @param jsonObject The JSON to check.
     * @param tracker    A list of IDs that represent the possible subgroups of the JsonObject.
     */
    private void removeEmptySubgroups(JsonObject jsonObject, LinkedList<LinkedList<String>> tracker) {
        for (LinkedList<String> list : tracker) {
            if (list.size() == 1) {
                JsonArray subgroup = jsonObject.getAsJsonArray(list.get(0));

                if (this.isJsonArrayEmpty(subgroup)) {
                    jsonObject.remove(list.get(0));
                }

            } else {
                for (int i = list.size() - 1; i >= 1; ++i) {

                    JsonArray jsonArray = jsonObject.getAsJsonArray(list.get(0));

                    if (this.isJsonArrayEmpty(jsonArray)) {
                        jsonObject.remove(list.get(0));
                    }
                }
            }
        }
    }

    /**
     * Private method to check if the supported JsonArray is empty.
     * This is done by checking if the array has elements altogether, and if it does, if these are just empty JsonObjects.
     *
     * @param jsonArray The JsonArray to check.
     * @return True, if the JsonArray does not have any elements or only empty elements.
     */
    private boolean isJsonArrayEmpty(JsonArray jsonArray) {
        if (jsonArray.size() == 0) {
            return true;
        } else {
            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement.isJsonObject()) {
                    if (!((JsonObject) jsonElement).keySet().isEmpty()) {
                        return false;
                    }
                } else {
                    boolean subArrayEmpty = this.isJsonArrayEmpty((JsonArray) jsonElement);

                    if (!subArrayEmpty) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void initializeLabelIDMap() {
        this.idMap.put("Activity", ExcelTemplateContent.ACTIVITY_LABEL_ID_MAP);
        this.idMap.put("Group", ExcelTemplateContent.GROUP_LABEL_ID_MAP);
        this.idMap.put("Person", ExcelTemplateContent.PERSON_LABEL_ID_MAP);
        this.idMap.put("Place", ExcelTemplateContent.PLACE_LABEL_ID_MAP);
        this.idMap.put("Object", ExcelTemplateContent.OBJECT_LABEL_ID_MAP);
        this.idMap.put("Reference", ExcelTemplateContent.REFERENCE_LABEL_ID_MAP);
        this.idMap.put("Institution", ExcelTemplateContent.INSTITUTION_LABEL_ID_MAP);
        this.idMap.put("Architecture", ExcelTemplateContent.ARCHITECTURE_LABEL_ID_MAP);
    }
}
