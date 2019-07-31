package com.shc.automation.api.test.framework;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.types.ObjectId;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.shc.automation.TestHarnessProperties;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APIDataSourceType;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.internal.report.APIExcelReporter;
import com.shc.automation.api.test.framework.internal.report.APIHtmlReporter;
import com.shc.automation.api.test.framework.internal.report.APIJsonTransformer;
import com.shc.automation.api.test.framework.internal.report.APINoSQLResponseLogger;
import com.shc.automation.api.test.framework.internal.report.APITestReportUtils;
import com.shc.automation.api.test.framework.utils.APITestUtils;

public class APITestReportManager {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public String report(ITestResult result, List<APIResponse> responses, String reportName, String retryCount) {
		StringBuilder reportLink = new StringBuilder();
		if (CollectionUtils.isEmpty(responses)) {
			reportLink = reportLink.append("<b><FONT COLOR=\"RED\">Empty or Invalid Test Response. No log available</FONT></b>");
		} else {
			reportLink = reportResponses(result, responses, reportName, retryCount);
		}
		return reportLink.toString();
	}

	private StringBuilder reportResponses(ITestResult result, List<APIResponse> responses, String reportName, String retryCount) {
		StringBuilder htmlLog = new StringBuilder();
		StringBuilder mongoObjectLog = new StringBuilder();
		String testDesc = result.getMethod().getDescription();
		testDesc = testDesc == null ? "None" : testDesc;

		APIResponse response = null;
		boolean printeEmailable = APITestContext.get().getTestConfig().getTestPackageConfig().getPrintInEmailableReport();

		for (int i = 0; i < responses.size(); i++) {

			response = responses.get(i);
			if (response != null) {
				response.setDescription(testDesc);
				if (TestHarnessProperties.WRITE_TO_DB) {
					if (response.getTestError() != null) {
						mongoObjectLog.append(
								"<b><FONT COLOR=\"RED\">API Test Error : <br/>" + ExceptionUtils.getStackTrace(response.getTestError()) + "</FONT></b><br/>");
					} else if (response.getTotalRequests() <= 0) {
						mongoObjectLog.append(emptyResponseMessage(response.getTestName()));
					} else {
						ObjectId responseObjectId = null;
						try {
							System.out.println("Saving the API Test Results to NoSQL :" + response.getTestName());
							responseObjectId = new APINoSQLResponseLogger().saveToNoSQL(response);
						} catch (Exception e) {
							log.error("Error in Saving the response to Mongo :" + response.getTestName(), e);
							mongoObjectLog.append(" Saving response to MongoDB failed -> " + e.getMessage());
						}
						if (responseObjectId != null) {
							System.out.println(response.getTestName() + " Test response inserted successfully to MongoDB with Id :" + responseObjectId);
							mongoObjectLog.append(responseObjectId.toString());
						} else {
							log.error("Not able to Save the Results for Test :" + response.getTestName());
						}
					}
				}

				if (printeEmailable || !TestHarnessProperties.WRITE_TO_DB) {
					System.out.println("Generating Printable Report for test :" + response.getTestName());
					htmlLog.append(APITestReportUtils.getSummary(testDesc, response.getTestType(), response.getTotalRequests(), response.getTotalFailed()));
					htmlLog.append(APITestReportUtils.START_TEST_FOLDER);
					if (APIDataSourceType.excel.toString().equals(response.getReportFormat())) {
						htmlLog.append(generateEXCEL(response, result.getMethod().getMethodName(), reportName));
					} else {
						htmlLog.append(generateHTML(response));
					}

					htmlLog.append(APITestReportUtils.endDiv());
					htmlLog.append(APITestReportUtils.getMismatchFrequencyHtml(APITestUtils.getMismatchedFieldFrequencyMap(response)));
					htmlLog.append("<br/>");
				}

			}
		}
		Reporter.log(htmlLog.toString());

		return mongoObjectLog;
	}

	public String generateHTML(APIResponse apiResponse) {

		if (apiResponse.getTestError() != null) {
			return "<b><FONT COLOR=\"RED\">API Test Error :<br/>" + ExceptionUtils.getStackTrace(apiResponse.getTestError()) + "</FONT></b><br/>";
		}
		if (apiResponse.getTotalRequests() <= 0) {
			return emptyResponseMessage(apiResponse.getTestName());
		}

		return new APIHtmlReporter().report(apiResponse).toString();
	}

	public String generateEXCEL(APIResponse apiResponse, String testName, String filePath) {
		if (apiResponse.getTestError() != null) {
			return "<b><FONT COLOR=\"RED\">API Test Error : <br/>" + ExceptionUtils.getStackTrace(apiResponse.getTestError()) + "</FONT></b><br/>";
		}
		if (apiResponse.getTotalRequests() <= 0) {
			return emptyResponseMessage(apiResponse.getTestName());
		}

		if (apiResponse instanceof APITestResponse)
			return new APIExcelReporter().report((APITestResponse) apiResponse, testName, filePath);
		if (apiResponse instanceof APICompareTestsResponse)
			return new APIExcelReporter().report((APICompareTestsResponse) apiResponse, testName, filePath);

		return "OOPS";
	}

	public HSSFWorkbook generateEXCEL(APIResponse apiResponse) {
		if (apiResponse.getTestError() != null) {
			return null;
		}
		if (apiResponse.getTotalRequests() <= 0) {
			return null;
		}

		if (apiResponse instanceof APITestResponse)
			return new APIExcelReporter().generateExcel((APITestResponse) apiResponse);
		if (apiResponse instanceof APICompareTestsResponse)
			return new APIExcelReporter().generateExcel((APICompareTestsResponse) apiResponse);

		return null;
	}

	public String emptyResponseMessage(String testName) {
		return "<div align=\"center\"><b><FONT COLOR=\"RED\">API Test Error : No Responses found for Test :" + testName + "</FONT></b></div><br/>";
	}

	public String getAPIResponseFromStore(String documentId) {
		return new APINoSQLResponseLogger().getAPIResponseFromMongo(documentId);
	}

	public APIResponse getAPIResponseSummery(String documentId) {
		String response = getAPIResponseFromStore(documentId);

		if (StringUtils.isNotBlank(response)) {
			return APIJsonTransformer.getSummary(response);
		} else {
			System.out.println("Not able to retrieve the response from MongoDB :" + documentId);
			return null;
		}
	}

	public APIResponse getAPIResponse(String documentId) {
		String respone = getAPIResponseFromStore(documentId);

		if (StringUtils.isNotBlank(respone)) {
			return APIJsonTransformer.convertToAPIResponse(respone);
		} else {
			System.out.println("Not able to retrieve the response from MongoDB :" + documentId);
			return null;
		}
	}

	public String getResponseJsonContentForScenario(String documentId, String scenario, Integer stepNumber, Integer itemNumber) {
		return new APINoSQLResponseLogger().getContentJsonFromResponseItem(documentId, scenario, stepNumber, itemNumber);
	}
}