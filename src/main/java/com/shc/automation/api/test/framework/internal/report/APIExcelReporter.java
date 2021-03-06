/**
 * 
 */
package com.shc.automation.api.test.framework.internal.report;

import com.shc.automation.adapter.ExcelAdapter;
import com.shc.automation.adapter.ExcelAdapterImpl;
import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponse;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponse;
import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.model.response.APIResponse;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.entity.*;
import com.shc.automation.excelservice.ExcelProcessor;
import com.shc.automation.utils.json.JsonMismatchField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import testNG.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APIExcelReporter {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public HSSFWorkbook generateExcel(APIResponse apiTestResponse) {
		String testName = apiTestResponse.getTestName();
		Map<String, APIScenarioResponse> testResponseItemList = apiTestResponse.getResponseItems();

		List<ExcelTestResponseItem> excelResponseItemList = getExcelEntityList(testName, testResponseItemList);
		if (CollectionUtils.isNotEmpty(excelResponseItemList)) {
			try {
				ExcelProcessor excelProcessor = new ExcelProcessor();
				ExcelAdapter ex = new ExcelAdapterImpl();
				return excelProcessor.createWorkbook(ex.populateSimpleExcelEntity(excelResponseItemList));
			} catch (Exception e) {
				log.error("Failed to read EXCEL file for test :" + testName, e);

			}
		}

		return null;
	}

	public HSSFWorkbook generateExcel(APICompareTestsResponse apiTestResponse) {
		String testName = apiTestResponse.getTestName();
		Map<String, APICompareTestsResponseItem> testResponseItemList = apiTestResponse.getResponseItems();

		List<ExcelComparisonResponseItem> excelResponseItemList = getExcelCompareEntityList(testName, testResponseItemList);
		if (CollectionUtils.isNotEmpty(excelResponseItemList)) {
			try {
				ExcelProcessor excelProcessor = new ExcelProcessor();
				ExcelAdapter ex = new ExcelAdapterImpl();
				return excelProcessor.createWorkbook(ex.populateComparisonExcelEntity(excelResponseItemList));
			} catch (Exception e) {
				log.error("Failed to read EXCEL file for test :" + testName, e);

			}
		}

		return null;
	}

	public String report(APIResponse apiTestResponse, String testName, String reportName) {
		Map<String, APIScenarioResponse> testResponseItemList = apiTestResponse.getResponseItems();

		List<ExcelTestResponseItem> excelResponseItemList = getExcelEntityList(testName, testResponseItemList);
		if (CollectionUtils.isNotEmpty(excelResponseItemList)) {
			try {
				ExcelProcessor excelProcessor = new ExcelProcessor();
				ExcelAdapter ex = new ExcelAdapterImpl();
				excelProcessor.startProcessing(getLogFilePath(reportName), ex.populateSimpleExcelEntity(excelResponseItemList));

			} catch (Exception e) {
				log.error("Failed to read EXCEL file for test :" + testName, e);

			}
		}

		return getReportLink(reportName, false).toString();
	}

	public String report(APICompareTestsResponse apiCompareTestsResponse, String testName, String reportName) {
		Map<String, APICompareTestsResponseItem> compareResponseItems = apiCompareTestsResponse.getResponseItems();

		List<ExcelComparisonResponseItem> excelComparisonResponseItemList = getExcelCompareEntityList(testName, compareResponseItems);

		if (CollectionUtils.isNotEmpty(excelComparisonResponseItemList)) {
			ExcelProcessor excelProcessor = new ExcelProcessor();
			ExcelAdapter ex = new ExcelAdapterImpl();
			try {
				excelProcessor.startProcessing(getLogFilePath(reportName), ex.populateComparisonExcelEntity(excelComparisonResponseItemList));
			} catch (Exception e) {
				log.error("Failed to read EXCEL file for test :" + testName, e);

			}
		}

		return getReportLink(reportName, false).toString();
	}

	private List<ExcelTestResponseItem> getExcelEntityList(String testName, Map<String, APIScenarioResponse> testResponseItemList) {
		List<ExcelTestResponseItem> excelResponseItemList = new ArrayList<ExcelTestResponseItem>();
		ExcelTestResponseItem excelTestResponseItem = null;

		Map<String, ExcelValidationField> excelValidationFieldMap = null;
		Map<String, ExcelPrintField> excelPrintFieldMap = null;
		String responseContent = null;
		String responseCode = null;
		String mongoRecordId = null;
		String result = null;

		for (APIScenarioResponse testResponseItem : testResponseItemList.values()) {

			excelValidationFieldMap = getExcelValidatorMap(testResponseItem.getValidators());
			excelPrintFieldMap = getExcelPrinterMap(testResponseItem.getPrinters());
			responseContent = testResponseItem.getResponseContent() != null ? testResponseItem.getResponseContent().toString() : null;
			responseCode = testResponseItem.getResponseCode() != null ? testResponseItem.getResponseCode().toString() : null;
			result = testResponseItem.getResult() != null ? testResponseItem.getResult().toString() : null;
			String payload = testResponseItem.getPayLoad() == null ? null : testResponseItem.getPayLoad().toString();

			excelTestResponseItem = new ExcelTestResponseItem(testName, testResponseItem.getRequestUrl(), payload, responseContent, responseCode, testResponseItem.getResponseType(), mongoRecordId,
					String.valueOf(testResponseItem.getExecutionTime()), testResponseItem.getScenarioName(), result, excelValidationFieldMap, excelPrintFieldMap);
			excelResponseItemList.add(excelTestResponseItem);

		}
		return excelResponseItemList;
	}

	public String report(APIChainTestsResponse apiTestResponse, String testName, String filePath) {
		return "<b>Excel Report Format is not supported for CHAIN TESTS </b><br/>";
	}

	private List<ExcelComparisonResponseItem> getExcelCompareEntityList(String testName, Map<String, APICompareTestsResponseItem> compareResponseItems) {
		Map<String, ExcelValidationField> excelValidationFieldMap1 = null;
		Map<String, ExcelValidationField> excelValidationFieldMap2 = null;
		Map<String, ExcelPrintField> excelPrintFieldMap1 = null;
		Map<String, ExcelPrintField> excelPrintFieldMap2 = null;

		ExcelComparisonResponseItem excelComparisonResponseItem = null;
		List<ExcelComparisonResponseItem> excelComparisonResponseItemList = new ArrayList<ExcelComparisonResponseItem>();
		String compareResult = null;

		for (APICompareTestsResponseItem apiCompareTestsResponseItem : compareResponseItems.values()) {
			excelValidationFieldMap1 = getExcelValidatorMap(apiCompareTestsResponseItem.getResponse1().getValidators());
			excelValidationFieldMap2 = getExcelValidatorMap(apiCompareTestsResponseItem.getResponse2().getValidators());
			excelPrintFieldMap1 = getExcelPrinterMap(apiCompareTestsResponseItem.getResponse1().getPrinters());
			excelPrintFieldMap2 = getExcelPrinterMap(apiCompareTestsResponseItem.getResponse2().getPrinters());
			compareResult = apiCompareTestsResponseItem.getResult() != null ? apiCompareTestsResponseItem.getResult().toString() : null;
			List<ExcelMismatchField> excelMismatchFields = null;

			if (CollectionUtils.isNotEmpty(apiCompareTestsResponseItem.getMismatches())) {
				excelMismatchFields = new ArrayList<ExcelMismatchField>();
				ExcelMismatchField excelMismatchField = null;
				for (JsonMismatchField mismatchField : apiCompareTestsResponseItem.getMismatches()) {
					excelMismatchField = new ExcelMismatchField(mismatchField.getMismatchPath(), mismatchField.getLeftValue() != null ? mismatchField.getLeftValue().toString() : null,
							mismatchField.getRightValue() != null ? mismatchField.getRightValue().toString() : null);
					excelMismatchFields.add(excelMismatchField);
				}
			}

			String payload1 = apiCompareTestsResponseItem.getResponse1().getPayLoad() == null ? null : apiCompareTestsResponseItem.getResponse1().getPayLoad().toString();
			String payload2 = apiCompareTestsResponseItem.getResponse2().getPayLoad() == null ? null : apiCompareTestsResponseItem.getResponse2().getPayLoad().toString();
			excelComparisonResponseItem = new ExcelComparisonResponseItem(testName, apiCompareTestsResponseItem.getScenarioName(), apiCompareTestsResponseItem.getResponse1().getRequestUrl(),
					apiCompareTestsResponseItem.getResponse2().getRequestUrl(), payload1, payload2, excelMismatchFields, excelValidationFieldMap1, excelValidationFieldMap2, excelPrintFieldMap1,
					excelPrintFieldMap2, compareResult);
			excelComparisonResponseItemList.add(excelComparisonResponseItem);
		}
		return excelComparisonResponseItemList;
	}

	private Map<String, ExcelValidationField> getExcelValidatorMap(List<APIValidation> validations) {
		if (CollectionUtils.isEmpty(validations)) {
			return null;
		}

		Map<String, ExcelValidationField> excelValidationFieldMap = new HashMap<String, ExcelValidationField>();
		for (APIValidation entry : validations) {
			excelValidationFieldMap.put(entry.getResponsePath(), getExcelValidationField(entry));
		}
		return excelValidationFieldMap;
	}

	private ExcelValidationField getExcelValidationField(APIValidation apiValidation) {
		if (apiValidation == null) {
			return null;
		}
		String expected = apiValidation.getExpectedResponseValue() == null ? null : apiValidation.getExpectedResponseValue().toString();
		String actual = apiValidation.getActualResponseValue() == null ? null : apiValidation.getActualResponseValue().toString();
		String validationType = apiValidation.getValidationType() == null ? null : apiValidation.getValidationType().toString();
		String result = apiValidation.getValidationResult() == null ? null : apiValidation.getValidationResult().toString();
		return new ExcelValidationField(apiValidation.getValidationName(), apiValidation.getResponsePath(), expected, actual, validationType, result, apiValidation.getValidationMessage());
	}

	private Map<String, ExcelPrintField> getExcelPrinterMap(List<APIPrint> printers) {
		if (CollectionUtils.isEmpty(printers)) {
			return null;
		}

		Map<String, ExcelPrintField> excelPrintFieldMap = new HashMap<String, ExcelPrintField>();
		for (APIPrint entry : printers) {
			excelPrintFieldMap.put(entry.getPrintName(), getExcelPrintField(entry));
		}
		return excelPrintFieldMap;
	}

	private ExcelPrintField getExcelPrintField(APIPrint apiPrint) {
		if (apiPrint == null)
			return null;
		return new ExcelPrintField(apiPrint.getPrintName(), apiPrint.getResponsePath(), (apiPrint.getResponseValue() == null ? null : apiPrint.getResponseValue().toString()));
	}

	public StringBuffer getReportLink(String reportName, boolean isChain) {
		StringBuffer reportLink = new StringBuffer();
		StringBuilder reportDir = Config.getLinkToReportDirectory(Config.getModule());
		String linkName = "";
		if (isChain) {
			reportName = "api-testng-results-0.xml";
			linkName = "XML Report";
		} else {
			linkName = "Excel Report";
		}
		if (reportDir == null || StringUtils.isBlank(reportDir.toString())) {
			reportLink = reportLink.append("<table id=\"reportId\" width=\"50%\" ><tr><td><a href=").append(reportName).append(">").append(linkName).append("</a></td></tr></table>");
		} else {
			reportLink = reportLink.append("<table id=\"reportId\" width=\"50%\" ><tr><td><a href=").append(reportDir).append("archive/test-report/").append(reportName).append(">").append(linkName)
					.append("</a></td></tr></table>");
		}

		return reportLink;
	}

	public String getLogFilePath(String reportName) {
		String filePath = Config.Test_Output;
		if (StringUtils.isBlank(filePath))
			filePath = "test-output";

		return new StringBuffer(filePath).append("/").append(reportName).toString();
	}

}
