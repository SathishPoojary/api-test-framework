/**
 *
 */
package com.shc.automation.testng.extensions;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.APITestReportManager;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.dao.entity.DatacaptureTestVariant;
import com.shc.automation.utils.DataReader;
import com.shc.automation.utils.TestHarnessContext;

public class APITestListener extends TestListener {
	private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass().getName());

	public APITestListener() {

	}

	@Override
	public void onStart(ITestContext context) {

		log.info("==============================================================");
		log.info("*******************Starting Automated API Testing*************");
		log.info("==============================================================");
		logger = Logger.getLogger(APITestListener.class.getSimpleName());
		logFilePath = System.getProperty(CONFIG_PROPERTY_LOG_FILE_PATH, "build" + File.separatorChar + "APITestListener.txt");
		super.onStart(context);

	}

	@Override
	public void onFinish(ITestContext context) {
		try {
			if (!DataReader.verifyShutDownRequested()) {
				super.onFinish(context);

				if (m_passed == m_count)
					log.info("*************** API Testing Successful ********************");
				else
					log.error("*************** API Testing Failed ********************");
				APIConfigManager.closeAllConnections();
			}
		} catch (ParseException e) {
			log.error("Error On Finish : " + e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error On Finish : " + e);
			e.printStackTrace();
		}
	}

	public APITestContext getApiContext() {
		return APITestContext.get();
	}

	@Override
	public void onTestStart(ITestResult result) {
		super.onTestStart(result);
		getApiContext().put("TestCaseName", result.getMethod().getMethodName());
		getApiContext().put("TestClassName", result.getTestClass().getName());

	}

	@Override
	public void onTestSuccess(ITestResult result) {
		List<APIResponse> responses = getAPIResponses();
		setAPITestStatus(responses, result);

		setTestParamters(result);
		super.onTestSuccess(result);
		removeAPISession(result);

	}

	public void onTestSkipped(ITestResult result) {
		super.onTestSkipped(result);
		removeAPISession(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		try {

			if (!DataReader.verifyShutDownRequested()) {
				try {

					TestHarnessContext.get().put("failedFunction", getFailedFunction());					
					setTestParamters(result);
					super.onTestFailure(result);

				} catch (Exception e) {
					log.error("Printing stacktrace ..");
					e.printStackTrace();
					log.error("Error occured in OnTestFailure for method " + result.getMethod().getMethodName() + ":" + result.getTestClass().getName() + e);
				} finally {
					removeAPISession(result);
				}
			}

		} catch (ParseException e) {
			log.error("Error On Test Failure : " + e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error On Test Failure : " + e);
			e.printStackTrace();
		}
	}

	private String getTestLog(ITestResult result) {

		String reportName = (String) TestHarnessContext.get().get("reportName");

		APITestReportManager reportManager = new APITestReportManager();
		List<APIResponse> responses = getAPIResponses();

		return reportManager.report(result, responses, reportName, retryCount);

	}

	private String getFailedFunction() {
		return "";
	}

	/**
	 *
	 * @param result
	 */
	public void removeAPISession(ITestResult result) {
		APITestContext.thread.remove();
	}

	@SuppressWarnings("unchecked")
	private void setTestParamters(ITestResult result) {
		TestHarnessContext.get().put("screenshotFileName", APITestConstants.NO_SCREENSHOT);
		TestHarnessContext.get().put("cookieURL", APITestConstants.NO_COOKIE_URL);
		TestHarnessContext.get().put("datacaptureTestVariants", (Set<DatacaptureTestVariant>) APITestContext.get().get("dataCaptureTestVariant"));
		TestHarnessContext.get().put("compareAndUpdateTestStatus",
				APITestContext.get().get("compareAndUpdateTestStatus") != null ? (Boolean) APITestContext.get().get("compareAndUpdateTestStatus") : false);
		TestHarnessContext.get().put("user", "API");
		Object clone = TestHarnessContext.get().get("cloneID");
		String cloneId = "-";
		if (clone != null && !"NO_CLONE_ID".equalsIgnoreCase(clone.toString())) {
			cloneId = "-" + clone.toString() + "-";
		}
		TestHarnessContext.get().put("reportName", "api-testng-results-" + result.getMethod().getMethodName() + cloneId + retryCount + ".xls");
		TestHarnessContext.get().put("log", getTestLog(result));
	}

	private List<APIResponse> getAPIResponses() {
		List<APIResponse> responses = APITestContext.get().getTestResponses();
		APIResponse testResponse = APITestContext.get().getTestResponse();
		if (responses == null) {
			responses = new ArrayList<APIResponse>(1);
		}
		if (testResponse != null) {
			responses.add(testResponse);
		}
		return responses;
	}

	private void setAPITestStatus(List<APIResponse> responses, ITestResult result) {
		if (CollectionUtils.isEmpty(responses)) {
			log.error("No Responses found in the Context. Please verify the test");
			TestHarnessContext.get().put("status", APITestConstants.TEST_FAIL);
			return;
		}
		APIResponse response = null;
		for (int i = 0; i < responses.size(); i++) {
			response = responses.get(i);
			if (response == null || !response.isTestSuccessful()) {
				TestHarnessContext.get().put("status", APITestConstants.TEST_FAIL);
				String stackTrace = APITestConstants.NO_STACKTRACE;
				if (response.getTestError() == null) {
					result.setAttribute("errorMessage", "API Test Failed");
					result.setAttribute("errorMessageForHardFailure", "API Test Failed");
				} else {
					stackTrace = APITestUtils.getThrowableStackTrace(response.getTestError());
				}
				TestHarnessContext.get().put("stackTrace", stackTrace);
				result.setStatus(ITestResult.FAILURE);
				break;
			}
		}
	}

}
