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
import java.util.ArrayList;
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

    /**
     * Parses an excel file and returns a JSON representation as a String
     * 
     * @param file a {@link MultipartFile}
     * @return a JSON String representation 
     * @throws ExcelParserException if the file is invalid
     */
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

				String identifierValue = "";

				//check for possible identifiers within the entities
				switch (entity) {
				case "Activity":
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Architecture":
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Group":
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Institution":
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Object":
					// object can be identified in two ways
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					} else if (sheet.getRow(32).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(32).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Person":
					if (sheet.getRow(4).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(4).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Place":
					if (sheet.getRow(0).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(0).getCell(cellIterator).getStringCellValue();
					}
					break;
				case "Reference":
					if (sheet.getRow(2).getCell(cellIterator) != null) {
						identifierValue = sheet.getRow(2).getCell(cellIterator).getStringCellValue();
					}
					break;
				}

				//only read entities with a valid identifier
				if (identifierValue.equals("")) {
					emptyCell = true;
					break;
				} else {
					// Associated the type for the respective entity
					String type = VISMO.typeAssociation(entity);
					entityJson.addProperty(JSONVISMO.TYPE, type);

					// add ID for respective type
					switch (type) {
					case "http://visit.de/ontologies/vismo/Activity":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Architecture":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Group":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Institution":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Object":
						if (sheet.getRow(0).getCell(cellIterator) != null) {
							if (!sheet.getRow(0).getCell(cellIterator).getStringCellValue().equals("")) {
								entityJson.addProperty(JSONVISMO.ID,
										sheet.getRow(0).getCell(cellIterator).getStringCellValue());
							} else {
								if (sheet.getRow(32).getCell(cellIterator) != null) {
									if (!sheet.getRow(32).getCell(cellIterator).getStringCellValue().equals("")) {
										entityJson.addProperty(JSONVISMO.SECONDARY_IDENTIFIER,
												sheet.getRow(32).getCell(cellIterator).getStringCellValue());
									}
								}
							}
						}
						break;
					case "http://visit.de/ontologies/vismo/Person":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Place":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					case "http://visit.de/ontologies/vismo/Reference":
						entityJson.addProperty(JSONVISMO.ID, identifierValue);
						break;
					}

				}

				for (int i = 0; i <= sheet.getLastRowNum(); ++i) {

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

						JsonObject lastSubgroupObject = (JsonObject) subgroupJsonArray
								.get(subgroupJsonArray.size() - 1);

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

							// Add the combination of subgroup and its subsubgroup to a map to use this
							// information later
							this.subGroupMap.put(id, subgroupName);
						} else {
							JsonObject emptyObject = new JsonObject();

							JsonArray subsubgroupArray = lastSubgroupObject.getAsJsonArray(id);

							subsubgroupArray.add(emptyObject);
						}
					} else {
						if (!value.isEmpty()) {

							value = this.adaptInputValue(value);

							// Find the respective JsonObject (which is the overall object or a sub-part of
							// it),
							// to which the current property is to be added
							JsonObject objectToAddProperty = null;

							if (this.idContainedInSubgroup(this.idMap.get(entity).get(label))) {
								// Id is part of a subgroup or subsubgroup

								// Check if we have a subsubgroup
								String subgroup = this.getSubGroupOfId(this.idMap.get(entity).get(label));

								if (this.subGroupMap.containsKey(subgroup)) {
									// Subsubgroup affiliation
									JsonArray subgroupJsonArray = entityJson
											.getAsJsonArray(this.subGroupMap.get(subgroup));

									JsonObject subgroupLastObject = (JsonObject) subgroupJsonArray
											.get(subgroupJsonArray.size() - 1);

									JsonArray subsubgroupJsonArray = (JsonArray) subgroupLastObject.get(subgroup);

									objectToAddProperty = (JsonObject) subsubgroupJsonArray
											.get(subsubgroupJsonArray.size() - 1);
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

			if (jsonArray.size() > 0) {
				json.add(entity, jsonArray);
			}
		}

		if (json.size() == 0) {
			throw new ExcelParserException("File is empty or has missing ids.");
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

                if(this.isEmptyJsonArray(subgroup)) {
                    jsonObject.remove(list.get(0));
                }

            } else {
                for (int i = list.size() - 1; i >= 1; ++i) {

                    JsonArray jsonArray = jsonObject.getAsJsonArray(list.get(0));

                    if (this.isEmptyJsonArray(jsonArray)) {
                        jsonObject.remove(list.get(0));
                    }
                }
            }
        }
    }

    private boolean isEmptyJsonObject(JsonObject jsonObject) {
        if(jsonObject.keySet().size() == 0) {
            return true;
        } else {
            boolean empty = true;

            ArrayList<String> keysToRemove = new ArrayList<String>();
            for(String key : jsonObject.keySet()) {
                JsonElement jsonElement = jsonObject.get(key);

                // Only consider further JsonObjects and Arrays
                if(!jsonElement.isJsonPrimitive()) {
                    if(jsonElement.isJsonObject()) {
                        boolean intermediateEmpty = this.isEmptyJsonObject((JsonObject) jsonElement);

                        if(intermediateEmpty) {
                            keysToRemove.add(key);
                        } else {
                            empty = false;
                        }

                    } else {
                        boolean intermediateEmpty = this.isEmptyJsonArray((JsonArray) jsonElement);

                        if(intermediateEmpty) {
                        	keysToRemove.add(key);
                        } else {
                            empty = false;
                        }

                    }
                } else {
                    empty = false;
                }
            }

            //remove all unneeded keys
            for (String key : keysToRemove) {
				jsonObject.remove(key);
			}
            
            return empty;
        }
    }

    private boolean isEmptyJsonArray(JsonArray jsonArray) {
        if(jsonArray.size() == 0) {
            return true;
        } else {
            boolean empty = true;

            for(int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);

                // Only consider further JsonObjects and Arrays
                if(!jsonElement.isJsonPrimitive()) {
                    if(jsonElement.isJsonObject()) {
                        boolean intermediateEmpty = this.isEmptyJsonObject((JsonObject) jsonElement);

                        if(intermediateEmpty) {
                            jsonArray.remove(i);

                            // Ugly, but necessary: As we delete one object from the array, we have to lower the for-variable
                            --i;
                        } else {
                            empty = false;
                        }
                    } else {
                        boolean intermediateEmpty = this.isEmptyJsonArray((JsonArray) jsonElement);

                        if(intermediateEmpty) {
                            jsonArray.remove(i);
                        } else {
                            empty = false;
                        }

                    }
                }
            }

            return empty;
        }
    }

    private String adaptInputValue(String value) {
        String adaptedValue = value;

        adaptedValue = adaptedValue.trim();
        adaptedValue = adaptedValue.replaceAll("\n", " ");

        // Create a protected Komma
        adaptedValue = adaptedValue.replaceAll(",", "[,]");

        adaptedValue = adaptedValue.replaceAll("\"", "\\\\\"");

        return adaptedValue;
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
