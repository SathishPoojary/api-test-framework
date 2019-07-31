package com.shc.automation.api.test.framework.internal.report;

import com.google.inject.Inject;
import com.shc.automation.TestHarnessProperties;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.testng.ITestResult;
import org.testng.Reporter;

public class APITestReportManager {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Inject
    private APINoSQLResponseLogger noSQLResponseLogger;

    public String report(ITestResult result, APIBaseResponse response, String reportName, String retryCount) {
        if (response == null) {
            return "<b><FONT COLOR=\"RED\">Empty or Invalid Test Response. No log available</FONT></b>";
        }
        return reportResponse(result, response, reportName, retryCount);
    }

    private String reportResponse(ITestResult result, APIBaseResponse response, String reportName, String retryCount) {
        ObjectId responseObjectId = null;
        if (TestHarnessProperties.WRITE_TO_DB) {
            System.out.println("Saving the API Test Results to NoSQL :" + response.getTestName());
            try {
                responseObjectId = noSQLResponseLogger.saveToNoSQL(response);
                System.out.println(response.getTestName() + " Test response inserted successfully to MongoDB with Id :" + responseObjectId);
            } catch (Exception e) {
                log.error("Error in Saving the response to Mongo :" + response.getTestName(), e);
            }
        }
        Reporter.log(APITestReportUtils.getPrintableReport(response, result.getMethod().getMethodName(), reportName));

        return "Successfully Saved with Id :" + responseObjectId;
    }
}