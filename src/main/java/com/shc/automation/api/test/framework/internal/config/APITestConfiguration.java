package com.shc.automation.api.test.framework.internal.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "api-test")
class APITestConfiguration {
	String testName;
	String apiToTest;
	String apiConfigFile;
	APIConfigRequest apiRequestConfig;
	APIConfigResponse apiResponseConfig;
	Context context;

	public String getTestName() {
		return testName == null ? null : testName.trim();
	}

	@XmlAttribute(name = "test-name")
	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getApiToTest() {
		return apiToTest == null ? null : apiToTest.trim();
	}

	@XmlAttribute(name = "api-to-test")
	public void setApiToTest(String apiToTest) {
		this.apiToTest = apiToTest;
	}

	public String getApiConfigFile() {
		return apiConfigFile == null ? null : apiConfigFile.trim();
	}

	@XmlAttribute(name = "api-config-file")
	public void setApiConfigFile(String apiConfigFile) {
		this.apiConfigFile = apiConfigFile;
	}

	public APIConfigRequest getApiRequestConfig() {
		return apiRequestConfig;
	}

	@XmlElement(name = "api-request")
	public void setApiRequestConfig(APIConfigRequest apiRequestConfig) {
		this.apiRequestConfig = apiRequestConfig;
	}

	public APIConfigResponse getApiResponseConfig() {
		return apiResponseConfig;
	}

	@XmlElement(name = "api-response")
	public void setApiResponseConfig(APIConfigResponse apiResponseConfig) {
		this.apiResponseConfig = apiResponseConfig;
	}

	public Context getContext() {
		return context;
	}

	@XmlElement(name = "scenario-context")
	public void setContext(Context context) {
		this.context = context;
	}

}

@XmlRootElement(name = "api-request")
class APIConfigRequest {
	String path;
	String beforeRequest;
	Integer threadPoolSize;
	APIConfigPayload payLoad;
	APIConfigUrlParam urlParamConfig;
	APIConfigHeaderParam headerParamConfig;
	String defaultEnvironment;

	public String getPath() {
		return path == null ? null : path.trim();
	}

	@XmlElement(name = "path")
	public void setPath(String path) {
		this.path = path;
	}

	public String getBeforeRequest() {
		return beforeRequest == null ? null : beforeRequest.trim();
	}

	@XmlElement(name = "before-request")
	public void setBeforeRequest(String beforeRequest) {
		this.beforeRequest = beforeRequest;
	}

	public Integer getThreadPoolSize() {
		return threadPoolSize;
	}

	@XmlElement(name = "thread-pool-size")
	public void setThreadPoolSize(Integer threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public APIConfigHeaderParam getHeaderParamConfig() {
		return headerParamConfig;
	}

	@XmlElement(name = "header-params")
	public void setHeaderParamConfig(APIConfigHeaderParam headerParamConfig) {
		this.headerParamConfig = headerParamConfig;
	}

	public APIConfigPayload getPayLoad() {
		return payLoad;
	}

	@XmlElement(name = "payload")
	public void setPayLoad(APIConfigPayload payLoad) {
		this.payLoad = payLoad;
	}

	public APIConfigUrlParam getUrlParamConfig() {
		return urlParamConfig;
	}

	@XmlElement(name = "url-params")
	public void setUrlParamConfig(APIConfigUrlParam urlParamConfig) {
		this.urlParamConfig = urlParamConfig;
	}

	public String getDefaultEnvironment() {
		return defaultEnvironment == null ? null : defaultEnvironment.trim();
	}

	@XmlElement(name = "default-environment")
	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}
}

@XmlRootElement(name = "api-response")
class APIConfigResponse {
	String reportFormat;
	String validStatusCodes;
	String afterResponse;
	APIConfigPrints printConfigs;
	APIConfigValidations validateConfigs;

	public String getReportFormat() {
		return reportFormat == null ? null : reportFormat.trim();
	}

	@XmlAttribute(name = "report-format")
	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}

	public String getAfterResponse() {
		return afterResponse == null ? null : afterResponse.trim();
	}

	@XmlElement(name = "after-response")
	public void setAfterResponse(String afterResponse) {
		this.afterResponse = afterResponse;
	}

	public APIConfigPrints getPrintConfigs() {
		return printConfigs;
	}

	@XmlElement(name = "get-fields")
	public void setPrintConfigs(APIConfigPrints printConfigs) {
		this.printConfigs = printConfigs;
	}

	public APIConfigValidations getValidateConfigs() {
		return validateConfigs;
	}

	@XmlElement(name = "validations")
	public void setValidateConfigs(APIConfigValidations validateConfigs) {
		this.validateConfigs = validateConfigs;
	}

	public String getValidStatusCodes() {
		return validStatusCodes;
	}

	@XmlAttribute(name = "valid-status-codes")
	public void setValidStatusCodes(String validStatusCodes) {
		this.validStatusCodes = validStatusCodes;
	}
}

@XmlRootElement(name = "get-fields")
class APIConfigPrints {
	List<APIConfigPrint> apiConfigPrintList = null;

	public List<APIConfigPrint> getApiConfigPrintList() {
		return apiConfigPrintList;
	}

	@XmlElement(name = "field")
	public void setApiConfigPrintList(List<APIConfigPrint> apiConfigPrintList) {
		this.apiConfigPrintList = apiConfigPrintList;
	}

}

@XmlRootElement(name = "scenario-context")
class Context {
	List<APIConfigPrint> contextFields = null;

	public List<APIConfigPrint> getContextFields() {
		return contextFields;
	}

	@XmlElement(name = "field")
	public void setContextFields(List<APIConfigPrint> contextFields) {
		this.contextFields = contextFields;
	}

}

@XmlRootElement(name = "validations")
class APIConfigValidations {
	List<APIConfigValidate> apiConfigValidationList = null;
	List<APIRepeatConfig> apiRepeatConfig = null;
	String recordRange;
	String scenarioNameFields;
	String sourceName;
	String sourceType;

	public List<APIConfigValidate> getApiConfigValidationList() {
		return apiConfigValidationList;
	}

	@XmlElement(name = "validate")
	public void setApiConfigValidationList(List<APIConfigValidate> apiConfigValidationList) {
		this.apiConfigValidationList = apiConfigValidationList;
	}

	public String getRecordRange() {
		return recordRange == null ? null : recordRange.trim();
	}

	@XmlAttribute(name = "records-range")
	public void setRecordRange(String recordRange) {
		this.recordRange = recordRange;
	}

	public String getScenarioNameFields() {
		return scenarioNameFields == null ? null : scenarioNameFields.trim();
	}

	@XmlAttribute(name = "scenario-name-fields")
	public void setScenarioNameFields(String scenarioNameFields) {
		this.scenarioNameFields = scenarioNameFields;
	}

	public String getSourceName() {
		return sourceName == null ? null : sourceName.trim();
	}

	@XmlAttribute(name = "source-name")
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceType() {
		return sourceType == null ? null : sourceType.trim();
	}

	@XmlAttribute(name = "source-type")
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public List<APIRepeatConfig> getApiRepeatConfig() {
		return apiRepeatConfig;
	}

	@XmlElement(name = "repeat")
	public void setApiRepeatConfig(List<APIRepeatConfig> apiRepeatConfig) {
		this.apiRepeatConfig = apiRepeatConfig;
	}

}

@XmlRootElement(name = "repeat")
class APIRepeatConfig {
	String when;
	int counter;
	List<APIConfigValidate> apiConfigValidationList = null;

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public List<APIConfigValidate> getApiConfigValidationList() {
		return apiConfigValidationList;
	}

	@XmlElement(name = "validate")
	public void setApiConfigValidationList(List<APIConfigValidate> apiConfigValidationList) {
		this.apiConfigValidationList = apiConfigValidationList;
	}
}

@XmlRootElement(name = "field")
class APIConfigPrint {
	String name;
	String path;
	String source;

	public String getName() {
		return name == null ? null : name.trim();
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path == null ? null : path.trim();
	}

	@XmlAttribute(name = "path")
	public void setPath(String path) {
		this.path = path;
	}

	public String getSource() {
		return source;
	}

	@XmlAttribute(name = "source")
	public void setSource(String source) {
		this.source = source;
	}
}

@XmlRootElement(name = "validate")
class APIConfigValidate {
	String name;
	String path;
	String validationType;
	String expectedValue;
	String expression;
	String messageId;
	String onValidationFailure;
	String compareTo;
	String compareOptions;
	String exlusionPaths;
	String arrayPathsToIgnore;
	String condition;

	public String getName() {
		return name == null ? null : name.trim();
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path == null ? null : path.trim();
	}

	@XmlAttribute(name = "path")
	public void setPath(String path) {
		this.path = path;
	}

	public String getValidationType() {
		return validationType == null ? null : validationType.trim();
	}

	@XmlAttribute(name = "validation-type")
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	@XmlAttribute(name = "expected-value")
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getMessageId() {
		return messageId == null ? null : messageId.trim();
	}

	@XmlAttribute(name = "messageId")
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getOnValidationFailure() {
		return onValidationFailure == null ? null : onValidationFailure.trim();
	}

	@XmlAttribute(name = "on-validation-failure")
	public void setOnValidationFailure(String onValidationFailure) {
		this.onValidationFailure = onValidationFailure;
	}

	public String getCompareTo() {
		return compareTo == null ? null : compareTo.trim();
	}

	@XmlAttribute(name = "compare-to")
	public void setCompareTo(String compareTo) {
		this.compareTo = compareTo;
	}

	public String getCompareOptions() {
		return compareOptions == null ? null : compareOptions.trim();
	}

	@XmlAttribute(name = "compare-options")
	public void setCompareOptions(String compareOptions) {
		this.compareOptions = compareOptions;
	}

	public String getExlusionPaths() {
		return exlusionPaths == null ? null : exlusionPaths.trim();
	}

	@XmlAttribute(name = "exclusion-paths")
	public void setExlusionPaths(String exlusionPaths) {
		this.exlusionPaths = exlusionPaths;
	}

	public String getArrayPathsToIgnore() {
		return arrayPathsToIgnore == null ? null : arrayPathsToIgnore.trim();
	}

	@XmlAttribute(name = "array-paths-to-ignore-order")
	public void setArrayPathsToIgnore(String arrayPathsToIgnore) {
		this.arrayPathsToIgnore = arrayPathsToIgnore;
	}

	public String getCondition() {
		return condition;
	}

	@XmlAttribute(name = "pre-condition")
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getExpression() {
		return expression;
	}

	@XmlAttribute(name = "expression")
	public void setExpression(String expression) {
		this.expression = expression;
	}

}
