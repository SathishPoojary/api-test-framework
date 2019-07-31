package com.shc.automation.api.test.framework.internal.executors;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.connect.APIHttpConnection;
import com.shc.automation.api.test.framework.internal.connect.APIHttpConnectionManager;
import com.shc.automation.api.test.framework.internal.request.readers.APIRequestReader;
import com.shc.automation.api.test.framework.internal.response.APIScenarioResponseProcessor;
import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import com.shc.automation.api.test.framework.model.response.APIResponse;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class APITestExecutor implements APITestExecutorBase {
    private final Logger logger = Logger.getLogger("APITestExecutor");
    private static final int PARALLELISM_LEVEL = getCores();

    private final APIRequestReader requestReader;
    private final APIScenarioExecutor scenarioExecutor;
    private final APIScenarioResponseProcessor scenarioResponseProcessor;

    @Inject
    public APITestExecutor(APIRequestReader requestReader,
                           APIScenarioExecutor scenarioExecutor,
                           APIScenarioResponseProcessor scenarioResponseProcessor) {
        this.requestReader = requestReader;
        this.scenarioExecutor = scenarioExecutor;
        this.scenarioResponseProcessor = scenarioResponseProcessor;
    }

    /**
     * @param apiBaseRequest
     */
    @Override
    public APIBaseResponse execute(APIBaseRequest apiBaseRequest) {
        System.out.println("+++++++++++++ Starting the execution of TEST : " + apiBaseRequest.getTestName());
        APIRequest request = (APIRequest) apiBaseRequest;
        APIResponse response = new APIResponse();
        response.setTestName(request.getTestName());
        response.setServiceUrl(request.getServiceUrl());
        response.setReportFormat(request.getReportFormat());

        List<APIScenarioRequest> scenarioRequests = requestReader.read(request);
        if (CollectionUtils.isEmpty(scenarioRequests)) {
            logger.error("No Scenario Requests found for Test :" + request.getTestName());
            return response;
        }
        System.out.println("Executing " + scenarioRequests.size() + " Scenarios ");

        Map<String, APIScenarioResponse> scenarioResponses = null;
        try {
            scenarioResponses = new ForkJoinPool(PARALLELISM_LEVEL).submit(
                    () -> executeScenarios(request, scenarioRequests)
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.setTotalRequests(scenarioRequests.size());
        response.setResponseItems(scenarioResponses);
        System.out.println("+++++++++++++ Completed execution of TEST: " + request.getTestName());

        return response;

    }

    private Map<String, APIScenarioResponse> executeScenarios(APIRequest request, List<APIScenarioRequest> requestItems) {
        APIHttpConnection httpConnection = getHttpConnection(request);
        try {
            return requestItems.parallelStream()
                    .map(scenarioRequest -> executeScenario(httpConnection, scenarioRequest))
                    .map(scenarioResponse -> processScenarioResponse(request, scenarioResponse))
                    .collect(Collectors.toMap(APIScenarioResponse::getScenarioName, scenarioResponse -> scenarioResponse));
        } finally {
            httpConnection.close();
        }
    }

    private APIScenarioResponse executeScenario(APIHttpConnection httpConnection, APIScenarioRequest scenarioRequest) {
        throttle(httpConnection);
        return scenarioExecutor.execute(scenarioRequest, httpConnection);
    }

    private APIScenarioResponse processScenarioResponse(APIRequest request, APIScenarioResponse scenarioResponse) {
        return scenarioResponseProcessor.process(scenarioResponse, request);
    }

    private void throttle(final APIHttpConnection httpConnection) {
        if (httpConnection.getRateLimiter() != null) {
            httpConnection.getRateLimiter().acquire();
        }
    }

    private APIHttpConnection getHttpConnection(APIRequest apiRequest) {
        return APIHttpConnectionManager.create(apiRequest.getThreadPoolSize(), apiRequest.getRequestsPerSecond());
    }

    private static int getCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
