package com.shc.automation.api.test.framework.model.request;

import com.shc.automation.api.test.framework.model.API;
import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class APIRequest extends APIBaseRequest {
    private final TestType testType = TestType.standalone;
    private API apiToTest = null;
    private String serviceUrl;

    private APITestDataSource urlParamInputSource = null;
    private APITestDataSource payloadInputSource = null;
    private APITestDataSource validationInputSource = null;
    private APITestDataSource headerParamInputSource = null;

    private List<APIValidation> validationFields;
    private List<APIPrint> printFields;

    private Map<String, Map<String, Object>> externalUrlParams = null;
    private Map<String, String> externalPayload = null;

    private String afterResponseProcess = null;
    private String beforeRequestProcess = null;

    private String validStatusCodes = null;

    private List<APIPrint> contextFields = null;
    private Map<String, Map<String, Object>> contextValues = null;
    private String compareResponseDisplayKey = null;

    private APIRequest() {
        validationFields = new ArrayList<APIValidation>();
        printFields = new ArrayList<APIPrint>();
        contextFields = new ArrayList<APIPrint>();
    }
    public TestType getTestType() {
        return testType;
    }
    public static APIRequest getInstance() {
        return new APIRequest();
    }

    public API getApiToTest() {
        return apiToTest;
    }

    public void setAPI(API apiToTest) {
        this.apiToTest = apiToTest;
    }

    public String getRequestPath() {
        return apiToTest.getRequestPath();
    }

    public void setRequestPath(String requestPath) {
        apiToTest.setRequestPath(requestPath);
    }

    public RequestType getRequestType() {
        return apiToTest.getRequestType();
    }

    public void setRequestType(RequestType requestType) {
        apiToTest.setRequestType(requestType);
    }


    /**
     * @return the apiType
     */

    /**
     * @return the headerParameters
     */
    public Map<String, String> getHeaderParameters() {
        return apiToTest.getHeaderParameters();
    }

    /**
     * @param headerParameters the headerParameters to set
     */
    public void setHeaderParameters(Map<String, String> headerParameters) {
        apiToTest.setHeaderParameters(headerParameters);
    }

    /**
     * @return the payLoad
     */
    public String getPayLoad() {
        return apiToTest.getPayLoad();
    }

    /**
     * @param payLoad the payLoad to set
     */
    public void setPayLoad(String payLoad) {
        apiToTest.setPayLoad(payLoad);
    }

    public void addUrlParameter(APIRequestParameter parameter) {
        apiToTest.addUrlParameter(parameter);
    }

    public void addHeaderParameter(String headerName, String headerValue) {
        apiToTest.addHeaderParameter(headerName, headerValue);
    }

    public List<APIValidation> getValidationFields() {
        return validationFields;
    }

    public void setValidationFields(List<APIValidation> validationFields) {
        this.validationFields = validationFields;
    }

    public void addValidationField(String name, APIValidation field) {
        this.validationFields.add(field);
    }

    public ParameterType getUrlParameterType() {
        return apiToTest.getUrlParameterType();
    }

    public void setUrlParameterType(ParameterType urlParameterType) {
        apiToTest.setUrlParameterType(urlParameterType);
    }

    public List<APIPrint> getPrintFields() {
        return printFields;
    }

    public void setPrintFields(List<APIPrint> printFields) {
        this.printFields = printFields;
    }

    public void addPrintField(APIPrint printField) {
        this.printFields.add(printField);
    }

    public String getServiceEndPointName() {
        return apiToTest.getServiceEndPointName();
    }

    public void setServiceEndPointName(String serviceEndPointName) {
        apiToTest.setServiceEndPointName(serviceEndPointName);
    }

    public String getEnvironment() {
        return apiToTest.getTestEnvironment();
    }

    public void setEnvironment(String environment) {
        apiToTest.setTestEnvironment(environment);
    }

    public String getEndpointVersion() {
        return apiToTest.getEndpointVersion();
    }

    public void setEndpointVersion(String endpointVersion) {
        apiToTest.setEndpointVersion(endpointVersion);
    }

    public Double getRequestsPerSecond() {
        return apiToTest.getRequestsPerSecond();
    }

    public void setRequestsPerSecond(Double requestsPerSecond) {
        apiToTest.setRequestsPerSecond(requestsPerSecond);
    }

    public String getPayLoadType() {
        return apiToTest.getPayLoadType();
    }

    public void setPayLoadType(String payLoadType) {
        apiToTest.setPayLoadType(payLoadType);
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public List<APIRequestParameter> getUrlParameters() {
        return apiToTest.getUrlParameters();
    }

    public void setUrlParameters(List<APIRequestParameter> urlParameters) {
        apiToTest.setUrlParameters(urlParameters);
    }

    public String getCompareResponseDisplayKey() {
        return compareResponseDisplayKey;
    }

    public void setCompareResponseDisplayKey(String compareResponseDisplayKey) {
        this.compareResponseDisplayKey = compareResponseDisplayKey;
    }

    public Map<String, Map<String, Object>> getExternalUrlParams() {
        return externalUrlParams;
    }

    public void setExternalUrlParams(Map<String, Map<String, Object>> externalUrlParams) {
        this.externalUrlParams = externalUrlParams;
    }

    public Map<String, String> getExternalPayload() {
        return externalPayload;
    }

    public void setExternalPayload(Map<String, String> externalPayload) {
        this.externalPayload = externalPayload;
    }

    public String getAfterResponseProcess() {
        return afterResponseProcess;
    }

    public void setAfterResponseProcess(String afterResponseProcess) {
        this.afterResponseProcess = afterResponseProcess;
    }

    public String getBeforeRequestProcess() {
        return beforeRequestProcess;
    }

    public void setBeforeRequestProcess(String beforeRequestProcess) {
        this.beforeRequestProcess = beforeRequestProcess;
    }

    public String getValidStatusCodes() {
        return validStatusCodes;
    }

    public void setValidStatusCodes(String validStatusCodes) {
        this.validStatusCodes = validStatusCodes;
    }

    public APITestDataSource getUrlParamInputSource() {
        return urlParamInputSource;
    }

    public void setUrlParamInputSource(APITestDataSource urlParamInputSource) {
        this.urlParamInputSource = urlParamInputSource;
    }

    public APITestDataSource getPayloadInputSource() {
        return payloadInputSource;
    }

    public void setPayloadInputSource(APITestDataSource payloadInputSource) {
        this.payloadInputSource = payloadInputSource;
    }

    public void updateParam(String paramName, String paramValue) {
        updateParam(paramName, paramValue, null);
    }

    public void updateParam(String paramName, String paramValue, ParameterType type) {
        updateParam(paramName, paramValue, null, true);
    }

    public void updateParam(String paramName, String paramValue, ParameterType type, Boolean staticParam) {
        if (apiToTest == null || StringUtils.isBlank(paramName)) {
            System.out.println("UpdateParam - Request Error: Check the config / Param name Input");
            return;
        }

        APIRequestParameter paramToUpdate = null;
        for (int i = 0; i < apiToTest.getUrlParameters().size(); i++) {
            if (apiToTest.getUrlParameters().get(i).getParamName().equalsIgnoreCase(paramName)) {
                paramToUpdate = apiToTest.getUrlParameters().get(i);
                break;
            }
        }
        if (paramToUpdate == null) {
            if (type == null)
                type = this.getUrlParameterType();
            paramToUpdate = new APIRequestParameter(paramName, paramValue, type);
            paramToUpdate.setInputColumnName(paramName);
            paramToUpdate.setOverride(staticParam);
            apiToTest.getUrlParameters().add(paramToUpdate);
        } else {
            paramToUpdate.setOverride(staticParam);
            paramToUpdate.setParamValue(paramValue);
        }
    }

    public void addStaticUrlParameter(APIRequestParameter param) {
        if (apiToTest == null || param == null) {
            System.out.println("addStaticUrlParameter - Request Error: Check the config / Param name Input");
            return;
        }
        updateParam(param.getParamName(), param.getParamValue() == null ? "" : param.getParamValue().toString(), param.getType(), true);
    }

    public void addDynamicUrlParameter(APIRequestParameter param) {
        if (apiToTest == null || param == null) {
            System.out.println("addStaticUrlParameter - Request Error: Check the config / Param name Input");
            return;
        }
        updateParam(param.getParamName(), param.getParamValue() == null ? "" : param.getParamValue().toString(), param.getType(), false);
    }

    public String toString() {
        StringBuilder stringBuff = new StringBuilder();
        stringBuff.append("\n\nTest Name : " + this.testName);
        stringBuff.append(apiToTest.toString());
        if (urlParamInputSource != null)
            stringBuff.append("\t  URL Param Source :" + urlParamInputSource);
        if (payloadInputSource != null)
            stringBuff.append("\n  Payload Source :" + payloadInputSource);
        stringBuff.append("\nThread Pool size :" + threadPoolSize);

        return stringBuff.toString();

    }

    public String getBaseUrl() {
        return apiToTest.getBaseUrl();
    }

    public void setBaseUrl(String requestBaseUrl) {
        apiToTest.setBaseUrl(requestBaseUrl);
    }

    public APITestDataSource getValidationInputSource() {
        return validationInputSource;
    }

    public void setValidationInputSource(APITestDataSource validationInputSource) {
        this.validationInputSource = validationInputSource;
    }

    public String getDataEnvironment() {
        return dataEnvironment;
    }

    public void setDataEnvironment(String dataEnvironment) {
        this.dataEnvironment = dataEnvironment;
    }

    public Integer getInvocationCount() {
        return invocationCount;
    }

    public void setInvocationCount(Integer invocationCount) {
        this.invocationCount = invocationCount;
    }

    public APITestDataSource getHeaderParamInputSource() {
        return headerParamInputSource;
    }

    public void setHeaderParamInputSource(APITestDataSource headerParamInputSource) {
        this.headerParamInputSource = headerParamInputSource;
    }

    public List<APIPrint> getContextFields() {
        return contextFields;
    }

    public void setContextFields(List<APIPrint> context) {
        this.contextFields = context;
    }

    public void addContextField(APIPrint field) {
        contextFields.add(field);
    }

    public Map<String, Map<String, Object>> getContextValues() {
        return contextValues;
    }

    public void setContextValues(Map<String, Map<String, Object>> contextValues) {
        this.contextValues = contextValues;
    }
}
