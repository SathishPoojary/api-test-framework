/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.shc.automation.TestHarnessProperties;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.API;
import com.shc.automation.api.test.framework.entities.APIPrintField;
import com.shc.automation.api.test.framework.entities.APIRequestParameter;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.OnValidationFailureOption;
import com.shc.automation.api.test.framework.entities.ParameterType;
import com.shc.automation.api.test.framework.entities.RequestType;
import com.shc.automation.api.test.framework.entities.ValidationType;
import com.shc.automation.api.test.framework.exception.APITestSourceException;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareOption;

/**
 * @author spoojar
 *
 */
public class APITestFactory {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public APITestRequest getAPITestRequest(String testName) throws APITestSourceException {
		if (StringUtils.isBlank(testName)) {
			log.error("NULL or Empty Test Name to generate Test Request");
			return null;
		}
		String apiTestConfigFile = APITestContext.get().getTestConfig().getApiTestConfigFile();
		if (StringUtils.isBlank(apiTestConfigFile)) {
			String resourceFolder = APITestContext.get().getTestConfig().getTestPackageConfig().getResourceFolder();
			if (StringUtils.isNotBlank(resourceFolder)) {
				apiTestConfigFile = "api/" + resourceFolder + "/" + APITestConstants.API_TESTS_CONFIG_FILE;
			} else {
				apiTestConfigFile = "api/" + APITestConstants.API_TESTS_CONFIG_FILE;
			}
		}

		APITestConfiguration testConfiguration = APIEntitiesFactory.getInstance(false).getConfigurationForTest(apiTestConfigFile, testName);
		if (testConfiguration == null) {
			log.error("Config Error: Test config not found for test :" + testName + " in file :" + apiTestConfigFile);
			throw new APITestSourceException("Config Error: Definition not found for test :" + testName + " in file :" + apiTestConfigFile);
		}

		APIConfiguration apiConfiguration = getApiConfig(testConfiguration, testName);

		return generateAPIRequest(testConfiguration, apiConfiguration);
	}

	private APIConfiguration getApiConfig(APITestConfiguration testConfiguration, String testName) throws APITestSourceException {
		String apiToTest = testConfiguration.getApiToTest();
		if (StringUtils.isBlank(apiToTest)) {
			log.error("Config Error: Invalid configuration for Test :" + testName + " -> API to Test attribute not found");
			throw new APITestSourceException("Config Error: API(api-to-test) not configured for test :" + testName);
		}
		String apiConfigFile = APITestContext.get().getTestConfig().getApiConfigFile();
		if (StringUtils.isBlank(apiConfigFile)) {
			apiConfigFile = testConfiguration.getApiConfigFile();
		}
		if (StringUtils.isBlank(apiConfigFile)) {
			String resourceFolder = APITestContext.get().getTestConfig().getTestPackageConfig().getResourceFolder();
			if (StringUtils.isNotBlank(resourceFolder)) {
				apiConfigFile = "api/" + resourceFolder + "/" + APITestConstants.API_CONFIG_FILE;
			} else {
				apiConfigFile = "api/" + APITestConstants.API_CONFIG_FILE;
			}
		}

		APIConfiguration apiConfiguration = APIEntitiesFactory.getInstance(false).getConfigurationForApi(apiConfigFile, apiToTest);
		if (apiConfiguration == null) {
			log.error("Config Error: API config not found for test :" + testName);
			throw new APITestSourceException("Config Error: Definition not found for API:" + apiToTest + " in config file :" + apiConfigFile);
		}

		return apiConfiguration;

	}

	private APITestRequest generateAPIRequest(APITestConfiguration testConfiguration, APIConfiguration apiConfiguration) throws APITestSourceException {
		APITestRequest apiTest = APITestRequest.getInstance();
		setTestDataEnvironment(apiTest);

		apiTest.setTestName(testConfiguration.getTestName());
		apiTest.setApiToTest(generateAPI(apiConfiguration));
		getApiRequestFromConfig(testConfiguration, apiTest);
		getContext(testConfiguration, apiTest);
		getApiResponseConfig(testConfiguration, apiTest);

		return apiTest;
	}

	private void getContext(APITestConfiguration testConfiguration, APITestRequest apiTest) {
		Context context = testConfiguration.getContext();
		if (context != null) {
			List<APIConfigPrint> contextFields = context.getContextFields();
			if (CollectionUtils.isNotEmpty(contextFields)) {
				apiTest.setContextFields(generateFields(contextFields));
			}
		}

	}

	private void getApiResponseConfig(APITestConfiguration testConfiguration, APITestRequest apiTest) throws APITestSourceException {
		APIConfigResponse responseConfig = testConfiguration.getApiResponseConfig();
		if (responseConfig != null) {
			apiTest.setAfterResponseProcess(responseConfig.getAfterResponse());
			apiTest.setReportFormat(responseConfig.getReportFormat());
			apiTest.setValidStatusCodes(responseConfig.getValidStatusCodes());

			APIConfigPrints printConfigurations = responseConfig.getPrintConfigs();
			if (printConfigurations != null) {
				List<APIConfigPrint> printConfigs = printConfigurations.getApiConfigPrintList();
				if (CollectionUtils.isNotEmpty(printConfigs)) {
					apiTest.setPrintFields(generateFields(printConfigs));
				}
			}

			APIConfigValidations validationConfigurations = responseConfig.getValidateConfigs();
			if (validationConfigurations != null) {
				APITestInputSource validationSource = getInputSource(apiTest.getTestName(), validationConfigurations.getSourceType(),
						validationConfigurations.getSourceName(), validationConfigurations.getRecordRange(),
						getScenarioFieldList(validationConfigurations.getScenarioNameFields()), apiTest.getDataEnvironment());

				if (validationSource != null && ((apiTest.getUrlParamInputSource() == null || !apiTest.getUrlParamInputSource().equals(validationSource))
						&& (apiTest.getPayloadInputSource() == null || !apiTest.getPayloadInputSource().equals(validationSource)))) {
					apiTest.setValidationInputSource(validationSource);
				}
				List<APIConfigValidate> validatorConfigs = validationConfigurations.getApiConfigValidationList();
				if (CollectionUtils.isNotEmpty(validatorConfigs)) {
					setValidationFields(apiTest, validatorConfigs);
				}
			}

		}
	}

	private List<APIPrintField> generateFields(List<APIConfigPrint> printConfigs) {
		List<APIPrintField> printFieldList = new ArrayList<APIPrintField>();

		for (APIConfigPrint printConfig : printConfigs) {
			APIPrintField printer = new APIPrintField(printConfig.getName(), printConfig.getPath());
			printer.setSource(StringUtils.isBlank(printConfig.getSource()) ? "response" : printConfig.getSource());
			printFieldList.add(printer);
		}

		return printFieldList;
	}

	private void setValidationFields(APITestRequest apiTest, List<APIConfigValidate> validatorConfigs) {

		for (APIConfigValidate validateConfig : validatorConfigs) {

			String expectedValue = validateConfig.getExpectedValue();
			String compareTo = validateConfig.getCompareTo();
			if (StringUtils.isNotBlank(compareTo)) {
				APIPrintField contextField = new APIPrintField(compareTo, compareTo);
				contextField.setSource(APITestConstants.API_INPUT_SOURCE);
				apiTest.addContextField(contextField);
				expectedValue = APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + compareTo;
			}

			boolean isValueExpected = StringUtils.isNotBlank(expectedValue) || StringUtils.isNotBlank(compareTo);
			APIValidationField validator = new APIValidationField(validateConfig.getName(), validateConfig.getPath(), expectedValue,
					getValidationType(validateConfig.getValidationType(), isValueExpected));
			validator.setMessageId(validateConfig.getMessageId());
			validator.setValidationFailureOption(getValidationFailureOption(validateConfig.getOnValidationFailure()));

			String excludeStr = validateConfig.getExlusionPaths();
			String arrayPathsToIgnoreOrder = validateConfig.getArrayPathsToIgnore();
			String jsonCompareOption = validateConfig.getCompareOptions();

			validator.setExcludes(APITestUtils.getListFromString(excludeStr));
			validator.setArrayPathListToIgnoreOrder(APITestUtils.getListFromString(arrayPathsToIgnoreOrder));
			if ("ignore_array_order".equals(jsonCompareOption)) {
				validator.setCompareOption(JsonCompareOption.IGNORE_ARRAY_ORDER);
			}

			String condition = validateConfig.getCondition();
			if (StringUtils.isNotBlank(condition)) {
				validator.setValidationCondition(condition);
			}
			if (ValidationType.EXPRESSION.equals(validator.getValidationType())) {
				validator.setExpression(validateConfig.getExpression());
			}
			apiTest.addValidationField(validateConfig.getPath(), validator);

		}

	}

	private void getApiRequestFromConfig(APITestConfiguration testConfiguration, APITestRequest apiTest) throws APITestSourceException {
		APIConfigRequest requestConfig = testConfiguration.getApiRequestConfig();
		if (requestConfig != null) {
			String path = requestConfig.getPath();
			if (StringUtils.isNotBlank(path)) {
				if (StringUtils.isNotBlank(apiTest.getRequestPath()))
					path = apiTest.getRequestPath() + path;
				apiTest.setRequestPath(path);
			}
			apiTest.setBeforeRequestProcess(requestConfig.getBeforeRequest());
			apiTest.setThreadPoolSize(requestConfig.getThreadPoolSize());

			String defaultEnv = requestConfig.getDefaultEnvironment();
			if (StringUtils.isNotBlank(defaultEnv)) {
				apiTest.setEnvironment(defaultEnv);
			}

			APIConfigUrlParam urlParamsConfig = requestConfig.getUrlParamConfig();
			if (urlParamsConfig != null) {
				apiTest.setUrlParamInputSource(getInputSource(apiTest.getTestName(), urlParamsConfig.getSourceType(), urlParamsConfig.getSourceName(),
						urlParamsConfig.getRecordRange(), getScenarioFieldList(urlParamsConfig.getScenarioNameFields()), apiTest.getDataEnvironment()));
				List<APIRequestParameter> urlParamList = getAPIUrlParameters(urlParamsConfig, apiTest.getApiToTest().getUrlParameterType());
				apiTest.setUrlParameters(mergeParams(apiTest.getUrlParameters(), urlParamList));
			}

			APIConfigPayload payLoadConfig = requestConfig.getPayLoad();
			if (payLoadConfig != null) {
				apiTest.setPayloadInputSource(getInputSource(apiTest.getTestName(), payLoadConfig.getSourceType(), payLoadConfig.getSourceName(),
						payLoadConfig.getRecordRange(), getScenarioFieldList(payLoadConfig.getScenarioNameFields()), apiTest.getDataEnvironment()));

				String payload = payLoadConfig.getPayLoad();
				if (apiTest.getPayloadInputSource() != null) {
					if (StringUtils.isNotBlank(payLoadConfig.getTemplateSource())) {
						try {
							payload = FileUtils.readFileToString(
									new File(this.getClass().getClassLoader().getResource(payLoadConfig.getTemplateSource()).getFile()), "UTF-8");
						} catch (IOException e) {
							log.error("Error Opening the Template Source :" + payLoadConfig.getTemplateSource(), e);
						}

					}
					if (StringUtils.isNotEmpty(payload)) {
						apiTest.getPayloadInputSource().setPayloadTemplate(payload);
					}
				} else {
					apiTest.setPayLoad(payload);
				}
			}

		}
	}

	private List<APIRequestParameter> mergeParams(List<APIRequestParameter> staticUrlParamList, List<APIRequestParameter> dynamicUrlParamList) {
		if (CollectionUtils.isEmpty(staticUrlParamList)) {
			return dynamicUrlParamList;
		}
		if (CollectionUtils.isEmpty(dynamicUrlParamList)) {
			return staticUrlParamList;
		}
		List<APIRequestParameter> mergeParamList = new ArrayList<APIRequestParameter>();
		for (APIRequestParameter staticParam : staticUrlParamList) {
			String name = staticParam.getParamName();
			boolean staticParamFound = false;
			for (APIRequestParameter param : dynamicUrlParamList) {
				if (param.getParamName().equals(name)) {
					staticParamFound = true;
					break;
				}
			}
			if (!staticParamFound) {
				mergeParamList.add(staticParam);
			}
		}
		mergeParamList.addAll(dynamicUrlParamList);
		return mergeParamList;
	}

	@SuppressWarnings("deprecation")
	private API generateAPI(APIConfiguration apiConfiguration) {
		API api = new API();

		api.setApiName(apiConfiguration.getName());
		api.setRequestType(getAPIRequestType(apiConfiguration.getType()));
		api.setTestEnvironment(apiConfiguration.getDefaultEnvironment());
		api.setRequestPath(apiConfiguration.getPath());
		api.setServiceEndPointName(apiConfiguration.getEndPointName());
		api.setRequestsPerSecond(apiConfiguration.getThrottle());
		api.setSocketTimeout(apiConfiguration.getResponseWaitTime());
		api.setBaseUrl(apiConfiguration.getUrl());
		api.setEndpointVersion(apiConfiguration.getEndpointVersion());

		if (apiConfiguration.getPayLoad() != null) {
			api.setPayLoadType(apiConfiguration.getPayLoad().getType());
			api.setPayLoad(apiConfiguration.getPayLoad().getPayLoad());
		}

		APIConfigUrlParam urlParamsConfig = apiConfiguration.getUrlParameters();
		if (urlParamsConfig != null) {
			api.setUrlParameterType(getParameterType(urlParamsConfig.getType()));
			api.setUrlParameters(getAPIUrlParameters(urlParamsConfig, api.getUrlParameterType()));
		}

		APIConfigHeaderParam headerParameterConfig = apiConfiguration.getHeaderParameters();
		if (headerParameterConfig != null) {
			List<APIConfigParam> headerParameters = headerParameterConfig.getHeaderParameters();
			if (CollectionUtils.isNotEmpty(headerParameters)) {
				api.setHeaderParameters(getHeaderParameters(headerParameters));
			}
		}

		return api;
	}

	private APITestInputSource getInputSource(String testName, String sourceType, String source, String range, List<String> scenarioNames,
			String testDataEnvironment) throws APITestSourceException {
		if (StringUtils.isBlank(sourceType) || StringUtils.isBlank(source)) {
			log.info("Input Source is not configured for ->" + testName + " . Using XML Config...");
			return null;
		}

		APITestInputSource inputSource = new APITestInputSource();
		inputSource.setSourceType(sourceType);

		source = updateEnvSourceName(source, testDataEnvironment);

		if (source.indexOf('.') != -1) {
			String connectionName = source.substring(0, source.indexOf('.'));
			source = source.substring(source.indexOf('.') + 1);
			inputSource.setConnectionName(connectionName);
		}

		if (source.indexOf('[') != -1) {
			String targetPath = source.substring(source.indexOf('[') + 1, source.length() - 1);
			inputSource.setSourceName(source.substring(0, source.indexOf('[')));
			inputSource.setSourcePath(targetPath);
		} else {
			inputSource.setSourceName(source);
		}

		inputSource.setRecordRange(range);
		inputSource.setScenarioFields(scenarioNames);

		return inputSource;
	}

	private String updateEnvSourceName(String srcName, String env) {
		if (srcName == null) {
			return srcName;
		}

		String envStr = "{" + APITestConstants.PROP_API_DATA_ENV + "}";
		if (srcName != null && srcName.contains(envStr)) {
			srcName = srcName.replace(envStr, env.toLowerCase());
		}
		return srcName;
	}

	private void setTestDataEnvironment(APITestRequest request) {
		String environment = TestHarnessProperties.getProperty(APITestConstants.PROP_API_DATA_ENV, null);
		if (StringUtils.isBlank(environment) || "All".equalsIgnoreCase(environment)) {
			environment = "prod";
		}
		System.out.println("Data Environment for Test " + request.getTestName() + " set to :" + environment);
		request.setDataEnvironment(environment);
	}

	private List<APIRequestParameter> getAPIUrlParameters(APIConfigUrlParam urlParamsConfig, ParameterType superParamType) {
		List<APIConfigParam> configParamList = urlParamsConfig.getUrlParameters();

		if (CollectionUtils.isEmpty(configParamList)) {
			return null;
		}

		List<APIRequestParameter> apiParamList = new ArrayList<APIRequestParameter>();
		for (APIConfigParam param : configParamList) {
			String name = param.getName();
			String value = param.getValue();
			String typeStr = param.getType();

			ParameterType type = superParamType;
			if (StringUtils.isNotBlank(typeStr) && !"default".equalsIgnoreCase(typeStr)) {
				type = getParameterType(typeStr);
			}
			if (type == ParameterType.nonameParam && StringUtils.isBlank(param.getName())) {
				name = String.valueOf(Math.random());
			}

			APIRequestParameter parameter = new APIRequestParameter(name, value, type);
			parameter.setEncodeValue(param.getEncode());

			boolean override = param.getOverride();
			if (!override) {
				if (StringUtils.isBlank(param.getColumn()))
					parameter.setInputColumnName(param.getName());
				else
					parameter.setInputColumnName(param.getColumn());
			} else {
				parameter.setOverride(override);
			}

			apiParamList.add(parameter);
		}
		return apiParamList;
	}

	private Map<String, String> getHeaderParameters(List<APIConfigParam> headerParametersConfigList) {
		Map<String, String> headerParameters = new HashMap<String, String>();
		for (APIConfigParam param : headerParametersConfigList) {
			headerParameters.put(param.name, param.value);
		}
		return headerParameters;
	}

	private RequestType getAPIRequestType(String requestStr) {
		if (StringUtils.isEmpty(requestStr) || "GET".equalsIgnoreCase(requestStr))
			return RequestType.get;
		if ("POST".equalsIgnoreCase(requestStr))
			return RequestType.post;
		if ("PUT".equalsIgnoreCase(requestStr))
			return RequestType.put;
		if ("DELETE".equalsIgnoreCase(requestStr))
			return RequestType.delete;

		return RequestType.get;
	}

	private ParameterType getParameterType(String paramType) {

		if ("path".equalsIgnoreCase(paramType))
			return ParameterType.pathParam;
		if ("colon".equalsIgnoreCase(paramType))
			return ParameterType.colonParam;
		if ("noname".equalsIgnoreCase(paramType))
			return ParameterType.nonameParam;
		if ("form".equalsIgnoreCase(paramType))
			return ParameterType.formParam;

		return ParameterType.query;
	}

	public static ValidationType getValidationType(String fieldVal, Boolean isValueExpected) {

		if (!isValueExpected) {
			if (StringUtils.isEmpty(fieldVal) || "not_empty".equalsIgnoreCase(fieldVal))
				return ValidationType.NOT_EMPTY;
			if ("empty".equalsIgnoreCase(fieldVal))
				return ValidationType.EMPTY;
			if ("contains".equalsIgnoreCase(fieldVal))
				return ValidationType.CONTAINS_NODE;
			if ("not_contains".equalsIgnoreCase(fieldVal))
				return ValidationType.NOT_CONTAINS_NODE;
		} else {
			if ("equals".equalsIgnoreCase(fieldVal))
				return ValidationType.EQUALS;
			if ("not_equals".equalsIgnoreCase(fieldVal))
				return ValidationType.NOT_EQUALS;
			if ("contains".equalsIgnoreCase(fieldVal))
				return ValidationType.CONTAINS_VALUE;
			if ("not_contains".equalsIgnoreCase(fieldVal))
				return ValidationType.NOT_CONTAINS_VALUE;
			if ("greater_than".equalsIgnoreCase(fieldVal))
				return ValidationType.GREATER_THAN;
			if ("lesser_than".equalsIgnoreCase(fieldVal))
				return ValidationType.LESSER_THAN;
		}
		if ("expression".equals(fieldVal))
			return ValidationType.EXPRESSION;

		return ValidationType.NOT_EMPTY;
	}

	public static OnValidationFailureOption getValidationFailureOption(String fieldVal) {
		if (StringUtils.isBlank(fieldVal))
			return OnValidationFailureOption.MARK_TEST_FAILED;
		if ("mark_validation_failed".equals(fieldVal))
			return OnValidationFailureOption.MARK_VALIDATION_FAILED;
		if ("mark_scenario_failed".equals(fieldVal))
			return OnValidationFailureOption.MARK_SCENARIO_FAILED;
		if ("mark_test_failed".equals(fieldVal))
			return OnValidationFailureOption.MARK_TEST_FAILED;
		if ("fail_and_stop".equals(fieldVal))
			return OnValidationFailureOption.FAIL_AND_STOP_VALIDATIONS;

		return OnValidationFailureOption.MARK_TEST_FAILED;
	}

	private List<String> getScenarioFieldList(String scenarioFields) {
		if (StringUtils.isBlank(scenarioFields)) {
			return new ArrayList<String>(1);
		}
		return Lists.newArrayList(Splitter.on(",").trimResults().split(scenarioFields));
	}

}
