package util.excel;

import model.namespace.JSONVISMO;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.*;
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

    private static final boolean COLOR_CODED = true;

    public static void main(String[] args) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (String[][] sheetInput : ExcelTemplateContent.SHEETS) {
            XSSFSheet sheet = workbook.createSheet(sheetInput[0][0]);
            sheet.createFreezePane(1,1,1,1);

            Row idRow = sheet.createRow(0);

            CellStyle idRowStyle = workbook.createCellStyle();
            idRowStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
            idRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            idRowStyle.setBorderLeft(BorderStyle.THIN);
            idRowStyle.setBorderRight(BorderStyle.THIN);
            idRowStyle.setBorderTop(BorderStyle.THIN);
            idRowStyle.setBorderBottom(BorderStyle.THIN);

            XSSFFont idRowFont = workbook.createFont();
            idRowFont.setBold(true);
            idRowStyle.setFont(idRowFont);

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

                    // Add validation if the cell represents a reference
                    if(ExcelTemplateContent.ENTITIES.contains(getDataType(sheetInput[i][0]))) {
                        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);

                        CellRangeAddressList addressList = new CellRangeAddressList(cell.getAddress().getRow(), cell.getAddress().getRow(), cell.getAddress().getColumn() + 1, cell.getAddress().getColumn() + 500);

                        String constraint = "=" + getDataType(sheetInput[i][0]) + "!$B$1:$XFD$1";

                        XSSFDataValidationConstraint formulaListConstraint = (XSSFDataValidationConstraint)
                                dvHelper.createFormulaListConstraint(constraint);

                        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(formulaListConstraint, addressList);

                        sheet.addValidationData(validation);
                    }

                    // Colorcoding
//                    if(COLOR_CODED) {
//                        CellStyle colorStyle = workbook.createCellStyle();
//                        colorStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
//                        colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                        colorStyle.setBorderLeft(BorderStyle.THIN);
//                        colorStyle.setBorderRight(BorderStyle.THIN);
//                        colorStyle.setBorderTop(BorderStyle.THIN);
//                        colorStyle.setBorderBottom(BorderStyle.THIN);
//
//                        row.setRowStyle(colorStyle);
//                        cell.setCellStyle(colorStyle);
//                    }

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
                    headerStyle.setBorderTop(BorderStyle.MEDIUM);

                    XSSFFont headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);

                    headerRow.setRowStyle(headerStyle);
                    headerCell.setCellStyle(headerStyle);

                    int subsubgroup = 0;

                    for (int j = 1; j < sheetInput[i].length; ++j) {
                        Row row = sheet.createRow(rows);
                        rows += 1;

                        Cell cell = row.createCell(0);
                        cell.setCellValue(stripDataType(sheetInput[i][j]));

                        // Add validation if the cell represents a reference
                        if(sheetInput[i][j].endsWith(")") && ExcelTemplateContent.ENTITIES.contains(getDataType(sheetInput[i][j]))) {
                            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);

                            CellRangeAddressList addressList = new CellRangeAddressList(cell.getAddress().getRow(), cell.getAddress().getRow(), cell.getAddress().getColumn() + 1, cell.getAddress().getColumn() + 500);

                            String constraint = "=" + getDataType(sheetInput[i][j]) + "!$B$1:$XFD$1";

                            XSSFDataValidationConstraint formulaListConstraint = (XSSFDataValidationConstraint)
                                    dvHelper.createFormulaListConstraint(constraint);

                            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(formulaListConstraint, addressList);

                            sheet.addValidationData(validation);
                        }

                        if(COLOR_CODED) {
                            CellStyle colorStyle = workbook.createCellStyle();

                            if(subsubgroup > 1) {
                                colorStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                            } else {
                                colorStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                            }

                            colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            colorStyle.setBorderLeft(BorderStyle.THIN);
                            colorStyle.setBorderRight(BorderStyle.THIN);
                            colorStyle.setBorderTop(BorderStyle.THIN);
                            colorStyle.setBorderBottom(BorderStyle.THIN);

                            row.setRowStyle(colorStyle);
                            cell.setCellStyle(colorStyle);
                        }

                        if(sheetInput[i][j].endsWith("]")) {
                            // Sub-subgroup, start counter for fields associated with the subsubgroup
                            subsubgroup = stripSubSubGroupLength(sheetInput[i][j]);

                            CellStyle topGroupStyle = workbook.createCellStyle();
                            if(COLOR_CODED) {
                                topGroupStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                                topGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                topGroupStyle.setBorderLeft(BorderStyle.THIN);
                                topGroupStyle.setBorderRight(BorderStyle.THIN);
                                topGroupStyle.setBorderBottom(BorderStyle.THIN);
                            }
                            topGroupStyle.setBorderTop(BorderStyle.DASHED);


                            XSSFFont topGroupFont = workbook.createFont();
                            topGroupFont.setBold(true);
                            topGroupStyle.setFont(topGroupFont);

                            row.setRowStyle(topGroupStyle);
                            cell.setCellStyle(topGroupStyle);
                        } else if(subsubgroup == 1) {
                            CellStyle bottomSubGroupStyle = workbook.createCellStyle();
                            if(COLOR_CODED) {
                                bottomSubGroupStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                                bottomSubGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                bottomSubGroupStyle.setBorderLeft(BorderStyle.THIN);
                                bottomSubGroupStyle.setBorderRight(BorderStyle.THIN);
                                bottomSubGroupStyle.setBorderTop(BorderStyle.THIN);
                            }
                            bottomSubGroupStyle.setBorderBottom(BorderStyle.DASHED);

                            row.setRowStyle(bottomSubGroupStyle);
                            cell.setCellStyle(bottomSubGroupStyle);
                        } else if (j == sheetInput[i].length - 1) {
                            // End of subgroup reached, so add closing style
                            CellStyle bottomGroupStyle = workbook.createCellStyle();

                            if(COLOR_CODED) {
                                bottomGroupStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                                bottomGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                bottomGroupStyle.setBorderLeft(BorderStyle.THIN);
                                bottomGroupStyle.setBorderRight(BorderStyle.THIN);
                                bottomGroupStyle.setBorderTop(BorderStyle.THIN);
                            }

                            bottomGroupStyle.setBorderBottom(BorderStyle.MEDIUM);

                            row.setRowStyle(bottomGroupStyle);
                            cell.setCellStyle(bottomGroupStyle);
                        }

                        if(subsubgroup != 0) {
                            subsubgroup -= 1;
                        }
                    }
                }
            }

            sheet.autoSizeColumn(0);

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
        if(input.endsWith(")")) {
            return input.substring(0, input.indexOf("("));
        } else {
            return input.substring(0, input.indexOf("["));
        }
    }

    private static int stripSubSubGroupLength(String input) { return Integer.parseInt(input.substring(input.indexOf("[") + 1, input.length() - 1));}

}
