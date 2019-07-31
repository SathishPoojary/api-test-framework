package com.shc.automation.api.test.framework.internal.process.source;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.internal.config.QueryProperty;
import com.shc.automation.api.test.framework.internal.process.APITestScenarioProcessor;

public class APIExcelSourceProcessor implements APIDataSourceMarker {
	private static final Logger log = Logger.getLogger("APIExcelSourceProcessor");

	public Map<String, Map<String, Object>> processRequestSource(APITestInputSource requestSource, Map<String, Object> contextRecords) {
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
		String requestFileName = QueryProperty.INSTANCE.getFileName(excelFileName);
		if (StringUtils.isEmpty(requestFileName)) {
			if (excelFileName.endsWith(".xls") || excelFileName.endsWith(".xlsx")) {
				requestFileName = excelFileName;
			} else {
				log.error("Please check the CONFIG : Property not found in the Config for Excel Param Source :" + excelFileName);
				return null;
			}
		}
		System.out.println("Starting processing of EXCEL file : " + requestFileName + " ....");

		String fileXtn = requestFileName.substring(requestFileName.lastIndexOf(".") + 1);
		if ("xlsx".equalsIgnoreCase(fileXtn)) {
			return readFromXLSX(requestSource, requestFileName, sheetName);
		} else if ("xls".equalsIgnoreCase(fileXtn)) {
			return readFromXLS(requestSource, requestFileName, sheetName);
		} else {
			log.error("Invalid EXCEL source. File extension not valid :" + fileXtn);
			return null;
		}
	}

	/**
	 * @param requestFileName
	 * @param sheetName
	 * @throws APIException
	 */
	private Map<String, Map<String, Object>> readFromXLS(APITestInputSource requestSource, String requestFileName, String sheetName) {
		InputStream excelStream = getExcelStream(requestFileName);
		if (excelStream == null) {
			return null;
		}

		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new POIFSFileSystem(excelStream));
			HSSFSheet sheet = null;
			if (StringUtils.isEmpty(sheetName)) {
				sheet = workbook.getSheetAt(0);
			} else {
				sheet = workbook.getSheet(sheetName);
			}
			return getRequestValuesFromExcelSheet(sheet, requestSource);

		} catch (Exception e) {
			log.error("Error in Excel Read of File : " + requestFileName + " -> " + e);
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

	private Map<String, Map<String, Object>> readFromXLSX(APITestInputSource requestSource, String requestFileName, String sheetName) {
		InputStream excelStream = getExcelStream(requestFileName);
		if (excelStream == null) {
			return null;
		}

		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(excelStream);
			XSSFSheet sheet = null;
			if (StringUtils.isEmpty(sheetName)) {
				sheet = workbook.getSheetAt(0);
			} else {
				sheet = workbook.getSheet(sheetName);
			}
			return getRequestValuesFromExcelSheet(sheet, requestSource);

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

	private Map<String, Map<String, Object>> getRequestValuesFromExcelSheet(Sheet sheet, APITestInputSource requestSource) {

		int endIndex = sheet.getPhysicalNumberOfRows();
		int beginIndex = 1;
		beginIndex = requestSource.getFromIndex() <= 0 ? 1 : requestSource.getFromIndex();
		if (beginIndex > endIndex) {
			log.error("No rows picked up from excel file. No. of rows in excel is less than begin index");
			return null;
		}
		if (requestSource.getToIndex() > 0 && requestSource.getToIndex() < endIndex) {
			endIndex = requestSource.getToIndex();
		}
		System.out.println("Getting the Records from Excel File : " + requestSource.getSourceName() + " [ " + beginIndex + " - " + endIndex + " ] ");

		Map<String, Integer> columnsNameIndexMap = getColumnNames(requestSource, sheet.getRow(0));
		if (MapUtils.isEmpty(columnsNameIndexMap)) {
			log.error("No Columns marked for value retrieval in Request Source. Returning NULL");
			return null;
		}

		Row row = null;
		Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>(endIndex - beginIndex + 1);
		Map<String, Object> record = null;
		Iterator<String> columnNames = columnsNameIndexMap.keySet().iterator();
		List<String> scenarioNames = requestSource.getScenarioFields();

		for (int i = beginIndex; i < endIndex; i++) {
			row = sheet.getRow(i);
			if (row == null || row.getPhysicalNumberOfCells() == 0) {
				continue;
			}
			record = new HashMap<String, Object>(columnsNameIndexMap.size());
			while (columnNames.hasNext()) {
				String column = columnNames.next();
				Integer columnIndex = columnsNameIndexMap.get(column);
				if (columnIndex >= 0) {
					String columnValue = getCellValue(row.getCell(columnIndex));
					record.put(column, columnValue);
				}
			}
			String scenarioName = APITestScenarioProcessor.generateScenarioNameFromURLInputRecord(scenarioNames, record, i);
			records.put(scenarioName, record);
			columnNames = columnsNameIndexMap.keySet().iterator();
		}
		if (MapUtils.isEmpty(records)) {
			log.error("!!!! No records found in EXCEL :" + requestSource.getSourceName() + " [ " + beginIndex + " - " + endIndex + " ] ");
		}
		return records;

	}

	private Map<String, Integer> getColumnNames(APITestInputSource requestSource, Row excelHeaderRow) {
		Map<String, Integer> columnNames = new HashMap<String, Integer>();

		Cell cell = null;
		if (excelHeaderRow != null) {
			Iterator<Cell> cells = excelHeaderRow.cellIterator();
			String columnName = null;
			int columnIndex = 0;
			while (cells.hasNext()) {
				cell = cells.next();
				columnName = cell.getRichStringCellValue() == null ? "" : cell.getRichStringCellValue().getString().trim();
				columnIndex = cell.getColumnIndex();
				columnNames.put(columnName, columnIndex);
			}
		}
		return columnNames;
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
				return String.valueOf(cell.getDateCellValue().getTime());
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
