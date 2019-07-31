package com.shc.automation.api.test.framework.internal.request.readers.source;


import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.config.APIPropertyQueryConfig;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIExcelReader implements APITestDataReader {
    private static final Logger log = Logger.getLogger("APIExcelReader");
    private final APIPropertyQueryConfig propertyQueryConfig;

    @Inject
    public APIExcelReader(APIPropertyQueryConfig propertyQueryConfig) {
        this.propertyQueryConfig = propertyQueryConfig;
    }

    @Override
    public Map<String, Map<String, Object>> processRequestSource(APITestDataSource requestSource, Map<String, Object> parameters) {
        if (requestSource == null) {
            log.error("Please check the CONFIG : EXCEL Source is NULL");
            return null;
        }
        String excelFileName = requestSource.getSourceName();
        if (StringUtils.isEmpty(excelFileName)) {
            log.error("Please check the CONFIG : URL Excel Param Source is NULL");
            return null;
        }

        String sheetName = requestSource.getSourcePath();
        String requestFileName = propertyQueryConfig.getFileName(excelFileName);
        if (StringUtils.isEmpty(requestFileName)) {
            if (excelFileName.endsWith(".xls") || excelFileName.endsWith(".xlsx")) {
                requestFileName = excelFileName;
            } else {
                log.error("Please check the CONFIG : Property not found in the Config for Excel Param Source :" + excelFileName);
                return null;
            }
        }
        System.out.println("Starting processing of EXCEL file : " + requestFileName + " ....");

        return getRequestValuesFromExcelSheet(getDataSheet(requestFileName, sheetName), requestSource);
    }

    private Sheet getDataSheet(String requestFileName, String sheetName) {
        InputStream excelStream = getExcelStream(requestFileName);
        if (excelStream == null) {
            return null;
        }
        String fileXtn = requestFileName.substring(requestFileName.lastIndexOf(".") + 1);
        Workbook workbook = null;
        try {
            if ("xlsx".equalsIgnoreCase(fileXtn)) {
                workbook = new XSSFWorkbook(excelStream);
            } else {
                workbook = new HSSFWorkbook(new POIFSFileSystem(excelStream));
            }
            if (StringUtils.isEmpty(sheetName)) {
                return workbook.getSheetAt(0);
            }
            return workbook.getSheet(sheetName);


        } catch (Exception e) {
            log.error("Error in Excel Read of File : " + requestFileName + " -> " + e);
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
                excelStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Map<String, Object>> getRequestValuesFromExcelSheet(Sheet sheet, APITestDataSource requestSource) {

        int endIndex = sheet.getPhysicalNumberOfRows();
        int beginIndex = requestSource.getFromIndex() <= 0 ? 1 : requestSource.getFromIndex();
        if (beginIndex > endIndex) {
            log.error("No rows picked up from excel file. No. of rows in excel is less than begin index");
            return null;
        }
        if (requestSource.getToIndex() > 0 && requestSource.getToIndex() < endIndex) {
            endIndex = requestSource.getToIndex();
        }
        System.out.println("Getting the Records from Excel File : " + requestSource.getSourceName() + " [ " + beginIndex + " - " + endIndex + " ] ");

        Map<String, Integer> columnsNameIndexMap = getColumnNames(sheet.getRow(0));
        if (MapUtils.isEmpty(columnsNameIndexMap)) {
            log.error("No Columns marked for value retrieval in Request Source. Returning NULL");
            return null;
        }


        Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>(endIndex - beginIndex + 1);
        List<String> scenarioNames = requestSource.getScenarioFields();

        for (int i = beginIndex; i < endIndex; i++) {
            Row row = sheet.getRow(i);
            if (isEmptyRow(row)) {
                continue;
            }
            Map<String, Object> record = new HashMap<>(columnsNameIndexMap.size());
            columnsNameIndexMap.keySet().forEach(column -> {
                Integer columnIndex = columnsNameIndexMap.get(column);
                if (columnIndex >= 0) {
                    String columnValue = getCellValue(row.getCell(columnIndex));
                    record.put(column, columnValue);
                }
            });

            String scenarioName = processScenarioName(scenarioNames, record, i);
            records.put(scenarioName, record);
        }

        if (MapUtils.isEmpty(records)) {
            log.error("!!!! No records found in EXCEL :" + requestSource.getSourceName() + " [ " + beginIndex + " - " + endIndex + " ] ");
        }
        return records;

    }

    private boolean isEmptyRow(Row row) {
        return row == null || row.getPhysicalNumberOfCells() == 0;
    }

    private Map<String, Integer> getColumnNames(Row excelHeaderRow) {
        if (excelHeaderRow == null) {
            return null;
        }
        Map<String, Integer> columnNames = new HashMap<String, Integer>();
        excelHeaderRow.cellIterator().forEachRemaining(cell -> columnNames.put(getColumnName(cell), cell.getColumnIndex()));

        return columnNames;
    }

    private String getColumnName(Cell cell) {
        return cell.getRichStringCellValue() == null ? "" : cell.getRichStringCellValue().getString().trim();
    }

    private InputStream getExcelStream(String requestFileName) {
        InputStream excelStream = this.getClass().getClassLoader().getResourceAsStream(requestFileName);
        if (excelStream == null) {
            log.error("File not found in Resources folder:" + requestFileName);
            log.error("Checking for absolute path");
            Path path = Paths.get(requestFileName);
            if (path.isAbsolute()) {
                try {
                    excelStream = new FileInputStream(requestFileName);
                } catch (FileNotFoundException e) {
                    log.error("File not found :" + requestFileName);
                }
            }
        }
        if (excelStream == null) {
            log.error("Please check the CONFIG : Excel File not found :" + requestFileName);
            return null;
        }
        return excelStream;
    }

    @SuppressWarnings("deprecation")
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        Object value = null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = String.valueOf(cell.getDateCellValue().getTime());
                } else {
                    value = NumberToTextConverter.toText(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = Boolean.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value == null ? "" : value.toString().trim();
    }
}
