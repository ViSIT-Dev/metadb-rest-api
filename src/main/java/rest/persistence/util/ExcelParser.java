package rest.persistence.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;
import util.excel.ExcelTemplateContent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

    public String createJSONFromParsedExcelFile(MultipartFile file) throws ExcelParserException, IOException {

        // Read in Excel file
        // Check if input file is an Excel file with ending .xlsx
        if(!file.getName().endsWith(".xlsx")) {
            throw new ExcelParserException("Input file is not an Excel file.");
        }

        InputStream fis = file.getInputStream();

        Workbook workbook = new XSSFWorkbook(fis);

        // Create JSON output
        JsonObject json = new JsonObject();

        // Iterate through every sheet (sheet names are persisted in ExcelTemplateContent)
        for(String entity : ExcelTemplateContent.ENTITIES) {
            JsonArray jsonArray = new JsonArray();

            Sheet sheet = workbook.getSheet(entity);

            boolean emptyCell = false;

            int cellIterator = 1;

            while(!emptyCell) {

                if(sheet.getRow(0).getCell(cellIterator) == null) {
                    emptyCell = true;
                    break;
                }

                JsonObject entityJson = new JsonObject();

                for(int i = 1; i <= sheet.getLastRowNum(); ++i) {

                    Row row = sheet.getRow(i);

                    Cell cell = row.getCell(cellIterator);

                    String label = sheet.getRow(i).getCell(0).getStringCellValue();

                    String value = "";

                    if(!label.endsWith("Untergruppe") && !label.endsWith("Unteruntergruppe")) {
//                        value = cell.getStringCellValue();
                        value = this.formatter.formatCellValue(cell);
                    }

                    String id = this.getIdWithoutSubGroup(this.idMap.get(entity).get(label));;

                    if(label.endsWith("Untergruppe")) {
                        JsonObject subgroup = new JsonObject();

                        entityJson.add(id, subgroup);
                    } else if(label.endsWith("Unteruntergruppe")) {
                        JsonObject subsubgroup = new JsonObject();

                        String subgroup = this.getSubGroupOfId(this.idMap.get(entity).get(label));

                        JsonObject subgroupJsonObject = entityJson.getAsJsonObject(subgroup);

                        subgroupJsonObject.add(id, subsubgroup);

                        // Add the combination of subgroup and its subsubgroup to a map to use this information later
                        this.subGroupMap.put(id, subgroup);
                    } else {
                        if(this.idContainedInSubgroup(this.idMap.get(entity).get(label))) {
                            // Id is part of a subgroup or subsubgroup

                            // Check if we have a subsubgroup
                            String subgroup = this.getSubGroupOfId(this.idMap.get(entity).get(label));

                            if(this.subGroupMap.containsKey(subgroup)) {
                                JsonObject subgroupJsonObject = entityJson.getAsJsonObject(this.subGroupMap.get(subgroup));

                                JsonObject subsubGroupJsonObject = subgroupJsonObject.getAsJsonObject(subgroup);

                                subsubGroupJsonObject.addProperty(id, value);
                            } else {
                                // Normal subgroup affiliation

                                JsonObject subgroupJsonObject = entityJson.getAsJsonObject(subgroup);

                                subgroupJsonObject.addProperty(id, value);
                            }
                        } else {
                            // Id is not part of subgroup
                            entityJson.addProperty(id, value);
                        }
                    }
                }

                jsonArray.add(entityJson);
                ++cellIterator;

            }

            json.add(entity, jsonArray);
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
     * @return      Only the ID of the respective value mapped to the Excel label.
     */
    private String getIdWithoutSubGroup(String input) {
        if(!input.contains("|")) {
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

    private void initializeLabelIDMap() {
        this.idMap.put("Activity", ExcelTemplateContent.ACTIVITY_LABEL_ID_MAP);
        this.idMap.put("Group", ExcelTemplateContent.GROUP_LABEL_ID_MAP);
//        this.idMap.put("Person", ExcelTemplateContent.PERSON_LABEL_ID_MAP);
        this.idMap.put("Place", ExcelTemplateContent.PLACE_LABEL_ID_MAP);
    }
}
