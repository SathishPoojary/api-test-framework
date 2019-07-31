/**
 *
 */
package com.shc.automation.api.test.framework.client.process;

import com.shc.automation.api.test.framework.model.request.APIRequest;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * This class is used to generate the API URL Parameters, PayLoads and Request
 * Headers which cannot be directly configured in the test configuration file
 *
 * @author sathish_poojary
 *
 */
public class APIBeforeRequestProcess {
    protected APIRequest request = null;

    public APIBeforeRequestProcess(APIRequest request) {
        this.request = request;
    }

    /**
     * Method to generate Service URL Params. API will be called for each scenario
     * in the generated Map
     *
     * @return Map with Key : Scenario Name Value : Map of Param Name and Param
     *         Value.
     */
    public Map<String, Map<String, Object>> generateUrlParams() {
        return null;
    }

    /**
     * Method to generate service payloads. API will be called for each scenario in
     * the generated Map
     *
     * @return Map with Key : Scenario Name Value : Payload String
     */
    public Map<String, String> generatePayloads() {
        return null;
    }

    /**
     * Method to generate dynamic Header Parameters
     *
     * @return Map with Key : Header Parameter Name Value : Header Parameter Value
     */
    public Map<String, String> generateRequestHeaders() {
        return null;
    }

    /**
     * Method to set the URL. Used to generate URL dynamically from test cases
     * config
     *
     * @return
     */
    public String generateServiceUrl() {
        return null;
    }

    /**
     * Method to set the API Path. Used to generate path dynamically from test cases
     *
     * @return
     */
    public String generateServicePath() {
        return null;
    }

    /**
     * @return
     */
    public Map<String, Map<String, Object>> generateScenarioContext() {
        return null;
    }

    public void process() {
        String externalUrl = generateServiceUrl();
        if (StringUtils.isNotBlank(externalUrl)) {
            request.setBaseUrl(externalUrl);
        }
        String externalPath = generateServicePath();
        if (StringUtils.isNotBlank(externalPath)) {
            if (StringUtils.isNoneBlank(request.getRequestPath())) {
                externalPath = request.getRequestPath() + externalPath;
            }
            request.setRequestPath(externalPath);
        }

        Map<String, Map<String, Object>> urlParams = generateUrlParams();
        if (MapUtils.isNotEmpty(urlParams)) {
            if (MapUtils.isNotEmpty(request.getExternalUrlParams()))
                request.getExternalUrlParams().putAll(urlParams);
            else
                request.setExternalUrlParams(urlParams);
        }

        Map<String, String> headerParams = generateRequestHeaders();
        if (MapUtils.isNotEmpty(headerParams)) {
            if (MapUtils.isNotEmpty(request.getHeaderParameters()))
                request.getHeaderParameters().putAll(headerParams);
            else
                request.setHeaderParameters(headerParams);
        }

        Map<String, String> payloads = generatePayloads();
        if (MapUtils.isNotEmpty(payloads)) {
            if (MapUtils.isNotEmpty(request.getExternalPayload()))
                request.getExternalPayload().putAll(payloads);
            else
                request.setExternalPayload(payloads);
        }
        Map<String, Map<String, Object>> scenarioContextValues = generateScenarioContext();
        if (MapUtils.isNotEmpty(scenarioContextValues)) {
            request.setContextValues(scenarioContextValues);
        }
    }

}
