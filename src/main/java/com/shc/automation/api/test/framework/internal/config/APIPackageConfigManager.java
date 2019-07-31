package com.shc.automation.api.test.framework.internal.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.config.xml.APIConfiguration;
import com.shc.automation.api.test.framework.internal.config.xml.APIPackageConfig;
import com.shc.automation.api.test.framework.internal.config.xml.APITestConfiguration;
import com.shc.automation.api.test.framework.internal.config.xml.APIXMLConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

@Singleton
public class APIPackageConfigManager {
    private Logger logger = Logger.getLogger("APIPackageConfigManager");
    private Map<String, APIPackageConfig> projectPackageConfigs;


    private final APIXMLConfigFactory xmlConfigFactory;

    @Inject
    public APIPackageConfigManager(APIXMLConfigFactory xmlConfigFactory) {
        this.xmlConfigFactory = xmlConfigFactory;
        projectPackageConfigs = xmlConfigFactory.getProjectPackageConfig();
    }

    public APIPackageConfig getPackageConfig(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            logger.warn("Empty package name. Not able to retrieve the package config");
            return null;
        }
        APIPackageConfig packageConfig = projectPackageConfigs.get(packageName.trim());
        if (packageConfig != null) {
            return packageConfig;
        }
        if (packageName.indexOf(".") != -1) {
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            return getPackageConfig(packageName);
        } else {
            return null;
        }
    }

    public String getAPITestConfigForPackage(){
        String apiTestConfigFile = APITestContext.get().getExecutionConfig().getApiTestConfigFile();
        if (StringUtils.isBlank(apiTestConfigFile)) {
            String resourceFolder = APITestContext.get().getExecutionConfig().getResourceFolder();
            if (StringUtils.isNotBlank(resourceFolder)) {
                apiTestConfigFile = APITestConstants.API_RESOURCE_BASE_DIR + "/" + resourceFolder + APITestConstants.API_TESTS_CONFIG_FILE;
            } else {
                apiTestConfigFile = APITestConstants.API_RESOURCE_BASE_DIR + APITestConstants.API_TESTS_CONFIG_FILE;
            }
        }
        return apiTestConfigFile;
    }

    public String getAPIConfigForPackage(){
        String apiConfigFile = APITestContext.get().getExecutionConfig().getApiConfigFile();
        if (StringUtils.isBlank(apiConfigFile)) {
            String resourceFolder = APITestContext.get().getExecutionConfig().getResourceFolder();
            if (StringUtils.isNotBlank(resourceFolder)) {
                apiConfigFile = APITestConstants.API_RESOURCE_BASE_DIR + "/" + resourceFolder +  APITestConstants.API_CONFIG_FILE;
            } else {
                apiConfigFile = APITestConstants.API_RESOURCE_BASE_DIR + APITestConstants.API_CONFIG_FILE;
            }
        }
        return apiConfigFile;
    }

    public APITestConfiguration getConfigurationForTest(String apiTestConfigFile, String testName) {
        return xmlConfigFactory.getConfigurationForTest(apiTestConfigFile, testName);
    }

    public APIConfiguration getConfigurationForApi(String apiConfigFile, String apiToTest) {
        return xmlConfigFactory.getConfigurationForApi(apiConfigFile, apiToTest);
    }
}
