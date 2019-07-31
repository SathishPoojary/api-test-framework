package com.shc.automation.api.test.framework.internal.response;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.response.APIResponse;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

public class APIResponseProcessor {
    private final APIScenarioResponseProcessor scenarioResponseProcessor;

    @Inject
    public APIResponseProcessor(APIScenarioResponseProcessor scenarioResponseProcessor) {
        this.scenarioResponseProcessor = scenarioResponseProcessor;
    }

    public APIResponse process(APIRequest apiRequest, Map<String, APIScenarioResponse> scenarioResponses) {
        APIResponse response = new APIResponse();
        response.setTestName(apiRequest.getTestName());
        response.setServiceUrl(apiRequest.getServiceUrl());
        response.setReportFormat(apiRequest.getReportFormat());

        if(MapUtils.isEmpty(scenarioResponses)){
            return response;
        }
        scenarioResponses.values().parallelStream().forEach(scenarioResponse -> scenarioResponseProcessor.process(scenarioResponse, apiRequest));
        response.setResponseItems(scenarioResponses);

        return response;
    }
}
