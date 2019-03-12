package util.excel;

import model.namespace.JSONVISMO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import javax.swing.border.Border;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * This Class comprises the functionality to generate a Excel Sheet, which can be used as input for the import
 * functionality of the Visit REST API.
 * <p>
 * Start the main method to create the Excel template in the templates folder of this project.
 * <p>
 * Note: In order to run, this process needs the json.txt file, which is created by the python script in the templates folder!
 */
public class ExcelTemplateProducer {

    private static final String EXCEL_FILE_NAME = "templates/visitExcel.xlsx";
    private static final String JSON_FILE_NAME = "templates/json.txt";

    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();

//        String jsonContent = "";
//
//        // Read in the json file containing all relevant information
//        try {
//            byte[] encoded = Files.readAllBytes(Paths.get(JSON_FILE_NAME));
//            jsonContent = new String(encoded);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject outerObject = new JSONObject(jsonContent);
//
//        // Create a Sheet for every main Class
//        for (String outerKey : outerObject.keySet()) {
//            Sheet sheet = workbook.createSheet(outerKey);
//
//            JSONObject innerObject = outerObject.getJSONObject(outerKey);
//
//            processInnerEntries(sheet, innerObject);
//        }

        for (String[][] sheetInput : ExcelTemplateContent.SHEETS) {
            Sheet sheet = workbook.createSheet(sheetInput[0][0]);

            Row idRow = sheet.createRow(0);

            CellStyle idRowStyle = workbook.createCellStyle();
            idRowStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
            idRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            idRowStyle.setBorderBottom(BorderStyle.THIN);
            idRow.setRowStyle(idRowStyle);

            Cell idCell = idRow.createCell(0);
            idCell.setCellValue("ID");
            idCell.setCellStyle(idRowStyle);

            int rows = 1;
            for (int i = 1; i < sheetInput.length; ++i) {

                if (sheetInput[i].length == 1) {
                    // Normal entry
                    Row row = sheet.createRow(rows);
                    rows += 1;
                    Cell cell = row.createCell(0);

                    cell.setCellValue(stripDataType(sheetInput[i][0]));
//                  Do datatype check  getDataType(sheetInput[i][0])
                } else {
                    // Subgroup
                    // Create "header" for subgroup
                    Row headerRow = sheet.createRow(rows);
                    rows += 1;

                    Cell headerCell = headerRow.createCell(0);
                    headerCell.setCellValue(sheetInput[i][0]);

                    CellStyle headerStyle = workbook.createCellStyle();
                    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setBorderTop(BorderStyle.THIN);

                    headerRow.setRowStyle(headerStyle);
                    headerCell.setCellStyle(headerStyle);

                    for (int j = 1; j < sheetInput[i].length; ++j) {
                        Row row = sheet.createRow(rows);
                        rows += 1;

                        Cell cell = row.createCell(0);
                        cell.setCellValue(stripDataType(sheetInput[i][j]));

                        if (j == sheetInput[i].length - 1) {
                            CellStyle bottomGroupStyle = workbook.createCellStyle();
                            bottomGroupStyle.setBorderBottom(BorderStyle.THIN);

                            row.setRowStyle(bottomGroupStyle);
                            cell.setCellStyle(bottomGroupStyle);
                        }
                    }


                }
            }

        }


        try {
            FileOutputStream os = new FileOutputStream(EXCEL_FILE_NAME);
            workbook.write(os);
            workbook.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDataType(String input) {
        return input.substring(input.indexOf("(") + 1, input.length() - 1);
    }

    private static String stripDataType(String input) {
        return input.substring(0, input.indexOf("("));
    }

    private static int processInnerEntries(Sheet sheet, JSONObject currentObject) {
        int rowNumber = sheet.getLastRowNum() + 1;

        for (String innerObjectKey : currentObject.keySet()) {
            Object innerObjectEntry = currentObject.get(innerObjectKey);

            if (innerObjectEntry instanceof JSONObject) {
                // Subgroup
                rowNumber = processInnerEntries(sheet, (JSONObject) innerObjectEntry);
            } else {
                // Normal data entry
                if (!innerObjectKey.equals(JSONVISMO.TYPE)) {
                    Row row = sheet.createRow(rowNumber);
                    rowNumber += 1;

                    Cell cell = row.createCell(0);
                    cell.setCellValue(innerObjectKey);
                }
            }

        }


        return rowNumber;
    }
}
