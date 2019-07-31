package com.shc.automation.api.test.framework.internal.response;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.APIScenarioContextProcessor;
import com.shc.automation.api.test.framework.internal.response.printers.APIScenarioResponsePrinter;
import com.shc.automation.api.test.framework.internal.response.validators.APIScenarioResponseValidator;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.ResultType;

import java.io.IOException;

public class APIScenarioResponseProcessor {
    private final APIScenarioResponseParser scenarioResponseParser;
    private final APIScenarioResponseValidator responseValidator;
    private final APIScenarioResponsePrinter responsePrinter;
    private final APIScenarioContextProcessor contextProcessor;

    @Inject
    public APIScenarioResponseProcessor(APIScenarioResponseParser scenarioResponseParser,
                                        APIScenarioResponseValidator responseValidator,
                                        APIScenarioResponsePrinter responsePrinter,
                                        APIScenarioContextProcessor contextProcessor) {
        this.scenarioResponseParser = scenarioResponseParser;
        this.responseValidator = responseValidator;
        this.responsePrinter = responsePrinter;
        this.contextProcessor = contextProcessor;
    }

    public APIScenarioResponse process(APIScenarioResponse scenarioResponse, APIRequest request) {
        scenarioResponse.setEnvironment(request.getEnvironment());
        scenarioResponse.setEndpoint(request.getServiceEndPointName());
        Boolean turnOffParsing = APITestContext.get().getExecutionConfig().getTurnOffResponseParsing();
        if (turnOffParsing || scenarioResponse.getResponseContent() == null) {
            scenarioResponse.setResult((scenarioResponse.getValidResponse() ? ResultType.PASSED : ResultType.FAILED));
            return scenarioResponse;
        }

        try {
            processResponseContent(scenarioResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return scenarioResponse;
        }

        if (!scenarioResponse.getValidResponse()) {
            scenarioResponse.setResult(ResultType.FAILED);
            return scenarioResponse;
        }

        contextProcessor.processScenarioContextFromResponse(request, scenarioResponse);
        contextProcessor.updateValidationsFromScenarioContext(scenarioResponse);
        responseValidator.validate(request, scenarioResponse);
        responsePrinter.print(request, scenarioResponse);

        return scenarioResponse;
    }

    private void processResponseContent(APIScenarioResponse responseItem) throws IOException {
        String rawResponse = responseItem.getResponseContent() == null ? null : responseItem.getResponseContent().toString();
        responseItem.setResponseContent(scenarioResponseParser.formatResponseContent(rawResponse, responseItem.getResponseType()));
    }

}
