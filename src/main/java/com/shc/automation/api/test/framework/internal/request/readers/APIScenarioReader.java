/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.APIScenarioContextProcessor;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.request.RequestType;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APIScenarioReader {
    protected final static Logger log = Logger.getLogger("APIScenarioReader");
    private APIRequest request;
    private final APIUrlReader urlReader;
    private final APIPayloadReader payloadReader;
    private final APIScenarioContextProcessor contextProcessor;

    @Inject
    public APIScenarioReader(APIUrlReader urlReader, APIPayloadReader payloadReader, APIScenarioContextProcessor contextProcessor) {
        this.urlReader = urlReader;
        this.payloadReader = payloadReader;
        this.contextProcessor = contextProcessor;
    }

    public void setRequest(APIRequest request) {
        this.request = request;
        this.request.setServiceUrl(urlReader.getServiceUrl(request));
    }

    public APIScenarioRequest createRequestItem(String scenario,
                                                Map<String, Object> urlRecord,
                                                Map<String, Object> payloadRecord,
                                                Map<String, Object> validationRecord) {
        APIScenarioRequest requestItem = new APIScenarioRequest();
        requestItem.setScenarioName(scenario);
        requestItem.setRequestType(request.getRequestType());


        requestItem.setUrlParameters(request.getUrlParameters());
        requestItem.setUrl(request.getServiceUrl());
        if (urlRecord != null) {
            requestItem.setUrlParameters(urlReader.create(request, urlRecord));
        }

        if (request.getRequestType() != RequestType.get) {
            requestItem.setPayload(payloadReader.create(request, requestItem.getScenarioContext(), payloadRecord));
        }
        contextProcessor.processScenarioContextFromTestData(request, requestItem, urlRecord, payloadRecord, validationRecord);

        requestItem.setSocketTimeout(request.getApiToTest().getSocketTimeout());
        requestItem.setHeaders(request.getHeaderParameters());
        requestItem.setValidStatusCodes(request.getValidStatusCodes());
        requestItem.setValidators(request.getValidationFields());

        return requestItem;
    }
}
