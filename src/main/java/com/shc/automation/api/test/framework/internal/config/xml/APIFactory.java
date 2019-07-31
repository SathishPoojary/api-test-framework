package com.shc.automation.api.test.framework.internal.config.xml;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.exception.APITestSourceException;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.model.API;
import com.shc.automation.api.test.framework.model.request.ParameterType;
import com.shc.automation.api.test.framework.model.request.RequestType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class APIFactory {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final APIConfigManager configManager;

    @Inject
    public APIFactory(APIConfigManager configManager) {
        this.configManager = configManager;
    }

    public API getAPI(String apiToTest, String apiConfigFile) throws APITestSourceException {
        APIConfiguration apiConfiguration = getAPIConfig(apiToTest, apiConfigFile);
        return generateAPI(apiConfiguration);
    }

    public APIConfiguration getAPIConfig(String apiToTest, String apiConfigFile) throws APITestSourceException {
        if (StringUtils.isBlank(apiToTest)) {
            logger.error("Config Error: Invalid configuration for Test -> API to Test attribute not found");
            throw new APITestSourceException("Config Error: API(api-to-test) not configured for test ");
        }
        if (StringUtils.isBlank(apiConfigFile)) {
            apiConfigFile = configManager.getAPIConfigForPackage();
        }
        APIConfiguration apiConfiguration = configManager.getConfigurationForApi(apiConfigFile, apiToTest);
        if (apiConfiguration == null) {
            logger.error("Config Error: API config not found for test ");
            throw new APITestSourceException("Config Error: Definition not found for API:" + apiToTest + " in config file :" + apiConfigFile);
        }

        return apiConfiguration;
    }

    private API generateAPI(APIConfiguration apiConfiguration) {
        API api = new API();

        api.setApiName(apiConfiguration.getName());
        api.setRequestType(RequestType.getRequestType(apiConfiguration.getType()));
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
            api.setUrlParameterType(ParameterType.getParameterType(urlParamsConfig.getType()));
            api.setUrlParameters(urlParamsConfig);
        }

        APIConfigHeaderParam headerParameterConfig = apiConfiguration.getHeaderParameters();
        if (headerParameterConfig != null) {
            List<APIConfigParam> headerParameters = headerParameterConfig.getHeaderParameters();
            if (CollectionUtils.isNotEmpty(headerParameters)) {
                api.setHeaderParameters(headerParameters);
            }
        }

        return api;
    }
}
