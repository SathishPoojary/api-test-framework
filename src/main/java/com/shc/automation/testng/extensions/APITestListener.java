/**
 *
 */
package com.shc.automation.testng.extensions;


import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.internal.config.injector.APIDependencyInjector;
import com.shc.automation.api.test.framework.internal.report.APITestReportManager;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.DataReader;
import com.shc.automation.utils.TestHarnessContext;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

public class APITestListener extends TestListener {
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass().getName());

    private static final Integer TEST_FAIL = -1;
    private static final String NO_STACKTRACE = "No StackTrace Available";

    @Override
    public void onStart(ITestContext context) {

        log.info("==============================================================");
        log.info("*******************Starting Automated API Testing*************");
        log.info("==============================================================");
        logger = Logger.getLogger(APITestListener.class.getSimpleName());
        logFilePath = System.getProperty(CONFIG_PROPERTY_LOG_FILE_PATH, "read" + File.separatorChar + "APITestListener.txt");
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
                APIDependencyInjector.INSTANCE.getInstance(APIConfigManager.class).closeAllConnections();
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
        setAPITestStatus(result);

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
        APIBaseResponse response = APITestContext.get().getTestResponse();
        String testDesc = result.getMethod().getDescription();
        testDesc = testDesc == null ? "None" : testDesc;
        response.setDescription(testDesc);
        return APIDependencyInjector.INSTANCE.getInstance(APITestReportManager.class).report(result, response, reportName, retryCount);

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
        TestHarnessContext.get().put("reportName", "api-testng-results-" + result.getMethod().getMethodName() + retryCount + ".xls");
        TestHarnessContext.get().put("log", getTestLog(result));
    }

    private void setAPITestStatus(ITestResult result) {
        APIBaseResponse response = APITestContext.get().getTestResponse();
        if (response == null || !response.isTestSuccessful()) {
            TestHarnessContext.get().put("status", TEST_FAIL);
            String stackTrace = NO_STACKTRACE;
            if (response.getTestError() == null) {
                result.setAttribute("errorMessage", "API Test Failed");
                result.setAttribute("errorMessageForHardFailure", "API Test Failed");
            } else {
                stackTrace = APITestUtils.getThrowableStackTrace(response.getTestError());
            }
            TestHarnessContext.get().put("stackTrace", stackTrace);
            result.setStatus(ITestResult.FAILURE);
        }
    }


}
