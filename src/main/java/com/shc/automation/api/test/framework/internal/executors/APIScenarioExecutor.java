package com.shc.automation.api.test.framework.internal.executors;

import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.connect.APIHttpConnection;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import org.apache.log4j.Logger;

import java.io.IOException;

public class APIScenarioExecutor {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    final Integer API_RETRY_COUNT = 3;

    public APIScenarioResponse execute(final APIScenarioRequest scenarioRequest, final APIHttpConnection httpConnection) {
        APIScenarioResponse scenarioResponse = executeWithRetry(scenarioRequest, httpConnection, API_RETRY_COUNT);

        System.out.println("Completed Scenario  ----->   " + scenarioResponse.getScenarioName());

        return scenarioResponse;
    }

    public APIScenarioResponse executeWithRetry(final APIScenarioRequest scenarioRequest, final APIHttpConnection httpConnection, final int maxRetryAllowed) {
        int retryCount = 1;
        APIScenarioResponse scenarioResponse = null;
        while (retryCount <= maxRetryAllowed) {
            try {
                scenarioResponse = httpConnection.execute(scenarioRequest);
                break;
            } catch (IOException e) {
                logger.error("Retry#" + retryCount + " to Connect for Test Scenario" + scenarioRequest.getScenarioName(), e);
                retryCount++;
                if (retryCount > maxRetryAllowed) {
                    e.printStackTrace();
                    logger.error("Execution Failed after maximum retry for Test Scenario :" + scenarioRequest.getScenarioName());
                    scenarioResponse = createErrorResponse(e, "Maximum Retry Reached. API Connection failed with Exception ");
                }
            } catch (Exception e) {
                logger.error("Exception in execution of API Test Scenario > " + scenarioRequest.getScenarioName(), e);
                scenarioResponse = createErrorResponse(e, "Unexpected Error. Error in processing API response");
            }
        }
        scenarioResponse.setRetryCount(retryCount);
        scenarioResponse.updateFromScenarioRequest(scenarioRequest);

        return scenarioResponse;
    }

    private APIScenarioResponse createErrorResponse(Exception error, String errorMessage) {
        APIScenarioResponse scenarioResponse = new APIScenarioResponse();
        scenarioResponse.setApiError(new APITestException(errorMessage, error));
        return scenarioResponse;
    }

}
