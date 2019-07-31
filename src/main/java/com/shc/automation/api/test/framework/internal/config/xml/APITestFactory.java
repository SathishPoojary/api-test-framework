/**
 *
 */
package com.shc.automation.api.test.framework.internal.config.xml;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.shc.automation.TestHarnessProperties;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.exception.APITestSourceException;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APIRequestParameter;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import com.shc.automation.api.test.framework.model.response.OnValidationFailureOption;
import com.shc.automation.api.test.framework.model.response.ValidationType;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareOption;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APITestFactory {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final APIFactory apiFactory;
    private final APIConfigManager configManager;

    @Inject
    public APITestFactory(APIFactory apiFactory, APIConfigManager configManager) {
        this.apiFactory = apiFactory;
        this.configManager = configManager;
    }

    public APIRequest getAPITestRequest(String testName, String environment, String version) throws APITestException {
        APIRequest request = getAPIRequest(testName);

        if (request == null) {
            throw new APITestException("Not able to generate the API Test Request for test :" + testName);
        }

        if (StringUtils.isNotBlank(environment)) {
            request.setEnvironment(environment);
        }

        if (StringUtils.isNotBlank(version)) {
            request.setEndpointVersion(version);
        } else {
            request.setEndpointVersion(APITestConstants.DAFAULT_API_ENDPOINT_VERSION);
        }

        return request;
    }

    public APIRequest getAPIRequest(String testName) throws APITestSourceException {
        if (StringUtils.isBlank(testName)) {
            log.error("NULL or Empty Test Name to generate Test Request");
            return null;
        }

        String apiTestConfigFile = configManager.getAPITestConfigForPackage();
        APITestConfiguration testConfiguration = configManager.getConfigurationForTest(apiTestConfigFile, testName);
        if (testConfiguration == null) {
            log.error("Config Error: Test config not found for test :" + testName + " in file :" + apiTestConfigFile);
            throw new APITestSourceException("Config Error: Definition not found for test :" + testName + " in file :" + apiTestConfigFile);
        }

        return generateAPIRequest(testConfiguration);
    }

    private APIRequest generateAPIRequest(APITestConfiguration testConfiguration) throws APITestSourceException {
        APIRequest apiRequest = APIRequest.getInstance();
        setTestDataEnvironment(apiRequest);

        apiRequest.setTestName(testConfiguration.getTestName());
        apiRequest.setAPI(apiFactory.getAPI(testConfiguration.getApiToTest(), testConfiguration.getApiConfigFile()));
        getApiRequestFromConfig(testConfiguration, apiRequest);
        getContext(testConfiguration, apiRequest);
        getApiResponseConfig(testConfiguration, apiRequest);

        return apiRequest;
    }

    private void getContext(APITestConfiguration testConfiguration, APIRequest apiTest) {
        Context context = testConfiguration.getContext();
        if (context != null) {
            List<APIConfigPrint> contextFields = context.getContextFields();
            if (CollectionUtils.isNotEmpty(contextFields)) {
                apiTest.setContextFields(generatePrints(contextFields));
            }
        }

    }

    private void getApiResponseConfig(APITestConfiguration testConfiguration, APIRequest apiTest) throws APITestSourceException {
        APIConfigResponse responseConfig = testConfiguration.getApiResponseConfig();
        if (responseConfig != null) {
            apiTest.setAfterResponseProcess(responseConfig.getAfterResponse());
            apiTest.setReportFormat(responseConfig.getReportFormat());
            apiTest.setValidStatusCodes(responseConfig.getValidStatusCodes());

            APIConfigPrints printConfigurations = responseConfig.getPrintConfigs();
            if (printConfigurations != null) {
                List<APIConfigPrint> printConfigs = printConfigurations.getApiConfigPrintList();
                if (CollectionUtils.isNotEmpty(printConfigs)) {
                    apiTest.setPrintFields(generatePrints(printConfigs));
                }
            }

            APIConfigValidations validationConfigurations = responseConfig.getValidateConfigs();
            if (validationConfigurations != null) {
                APITestDataSource validationSource = getInputSource(apiTest.getTestName(), validationConfigurations.getSourceType(),
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

    private List<APIPrint> generatePrints(List<APIConfigPrint> printConfigs) {
        List<APIPrint> printFieldList = new ArrayList<APIPrint>();

        for (APIConfigPrint printConfig : printConfigs) {
            APIPrint printer = new APIPrint(printConfig.getName(), printConfig.getPath());
            printer.setSource(StringUtils.isBlank(printConfig.getSource()) ? "response" : printConfig.getSource());
            printFieldList.add(printer);
        }

        return printFieldList;
    }

    private void setValidationFields(APIRequest apiTest, List<APIConfigValidate> validatorConfigs) {

        for (APIConfigValidate validateConfig : validatorConfigs) {

            String expectedValue = validateConfig.getExpectedValue();
            String compareTo = validateConfig.getCompareTo();
            if (StringUtils.isNotBlank(compareTo)) {
                APIPrint contextField = new APIPrint(compareTo, compareTo);
                contextField.setSource(APITestConstants.API_INPUT_SOURCE);
                apiTest.addContextField(contextField);
                expectedValue = APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + compareTo;
            }

            APIValidation validator = new APIValidation(validateConfig.getName(), validateConfig.getPath(), expectedValue,
                    ValidationType.getValidationType(validateConfig.getValidationType()));
            validator.setMessageId(validateConfig.getMessageId());
            validator.setValidationFailureOption(OnValidationFailureOption.getValidationFailureOption(validateConfig.getOnValidationFailure()));

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

    private void getApiRequestFromConfig(APITestConfiguration testConfiguration, APIRequest apiTest) throws APITestSourceException {
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
                apiTest.getApiToTest().setUrlParameters(urlParamsConfig);
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


    private APITestDataSource getInputSource(String testName, String sourceType, String source, String range, List<String> scenarioNames,
                                             String testDataEnvironment) throws APITestSourceException {
        if (StringUtils.isBlank(sourceType) || StringUtils.isBlank(source)) {
            log.info("Input Source is not configured for ->" + testName + " . Using XML Config...");
            return null;
        }

        APITestDataSource inputSource = new APITestDataSource();
        inputSource.setSourceType(sourceType);

        source = updateEnvSourceName(source, testDataEnvironment);

        if (source.indexOf('.') != -1) {
            String connectionName = source.substring(0, source.indexOf('.'));
            source = source.substring(source.indexOf('.') + 1);
            inputSource.setConnectionName(connectionName);
        }

        setSourceName(source, inputSource);

        inputSource.setRecordRange(range);
        inputSource.setScenarioFields(scenarioNames);

        return inputSource;
    }

    private void setSourceName(String source, APITestDataSource inputSource) {
        if (source.indexOf('[') != -1) {
            String targetPath = source.substring(source.indexOf('[') + 1, source.length() - 1);
            inputSource.setSourceName(source.substring(0, source.indexOf('[')));
            inputSource.setSourcePath(targetPath);
        } else {
            inputSource.setSourceName(source);
        }
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

    private void setTestDataEnvironment(APIRequest request) {
        String environment = TestHarnessProperties.getProperty(APITestConstants.PROP_API_DATA_ENV, null);
        if (StringUtils.isBlank(environment) || "All".equalsIgnoreCase(environment)) {
            environment = "prod";
        }
        System.out.println("Data Environment for Test " + request.getTestName() + " set to :" + environment);
        request.setDataEnvironment(environment);
    }


    private Map<String, String> getHeaderParameters(List<APIConfigParam> headerParametersConfigList) {
        Map<String, String> headerParameters = new HashMap<String, String>();
        for (APIConfigParam param : headerParametersConfigList) {
            headerParameters.put(param.name, param.value);
        }
        return headerParameters;
    }

    private List<String> getScenarioFieldList(String scenarioFields) {
        if (StringUtils.isBlank(scenarioFields)) {
            return new ArrayList<>(1);
        }
        return Lists.newArrayList(Splitter.on(",").trimResults().split(scenarioFields));
    }

}
