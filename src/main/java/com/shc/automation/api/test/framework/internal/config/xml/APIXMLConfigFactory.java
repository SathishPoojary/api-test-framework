package com.shc.automation.api.test.framework.internal.config.xml;

import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.APITestConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class APIXMLConfigFactory {
    private Logger logger = Logger.getLogger("APIXMLConfigFactory");
    private final String PROJECT_CONFIG_FILE = "api/project-config.xml";
    private final String DATA_SOURCES_FILE = "api/data-sources.xml";
    private Map<String, APIConfiguration> apiConfigMap;
    private Map<String, APITestConfiguration> apiTestConfigMap;
    private List<String> loadedFileList;


    public APIXMLConfigFactory() {
        apiConfigMap = new ConcurrentHashMap<>();
        apiTestConfigMap = new ConcurrentHashMap<>();
        loadedFileList = new CopyOnWriteArrayList<>();
    }

    public Map<String, APIPackageConfig> getProjectPackageConfig() {
        APIPackagesConfig packageConfigs = APIXMLTransformer.INSTANCE.transform(PROJECT_CONFIG_FILE, APIPackagesConfig.class);
        Map<String, APIPackageConfig> projectPackageConfigs = new HashMap<>();
        if (packageConfigs == null) {
            logger.warn("Packages are not configured for Test Workspace / Project");
            return projectPackageConfigs;
        }

        List<APIPackageConfig> packageList = packageConfigs.getPackages();
        if (CollectionUtils.isEmpty(packageList)) {
            logger.warn("No Packages found for Test Workspace / Project");
            return projectPackageConfigs;
        }


        for (APIPackageConfig packageConfig : packageList) {
            projectPackageConfigs.put(packageConfig.getName(), packageConfig);
        }
        System.out.println("Packages for the project configured successfully");

        return projectPackageConfigs;
    }

    public Map<String, APIDataSourceConnectConfig> getProjectDataSources() {
        APIDataSourceConnectConfigs dataSources = APIXMLTransformer.INSTANCE.transform(DATA_SOURCES_FILE, APIDataSourceConnectConfigs.class);
        Map<String, APIDataSourceConnectConfig> projectDataSources = new HashMap<>();
        if (dataSources == null) {
            logger.warn("Data sources are not configured for Test Workspace / Project :" + DATA_SOURCES_FILE);
            return projectDataSources;
        }

        List<APIDataSourceConnectConfig> dataSrcList = dataSources.getDataSources();
        if (CollectionUtils.isEmpty(dataSrcList)) {
            logger.warn("No Data sources found for Test Workspace / Project");
            return projectDataSources;
        }

        for (APIDataSourceConnectConfig dataSrc : dataSrcList) {
            projectDataSources.put(dataSrc.getName(), dataSrc);
        }
        System.out.println("Data sources for the project configured successfully");

        return projectDataSources;
    }

    public APITestConfiguration getConfigurationForTest(String apiTestConfigFileName, String testName) {
        if (!isFileLoaded(apiTestConfigFileName)) {
            loadApiTestConfig(apiTestConfigFileName);
        }

        APITestConfiguration config = apiTestConfigMap.get(apiTestConfigFileName + "." + testName);
        if (config == null) {
            apiTestConfigFileName = APITestConstants.API_RESOURCE_BASE_DIR + APITestConstants.API_TESTS_CONFIG_FILE;
            config = apiTestConfigMap.get(apiTestConfigFileName + "." + testName);
        }

        return config;
    }

    public APIConfiguration getConfigurationForApi(String apiConfigFileName, String apiName) {
        if (!isFileLoaded(apiConfigFileName)) {
            loadApiConfig(apiConfigFileName);
        }
        APIConfiguration apiConfiguration = apiConfigMap.get(apiConfigFileName + "." + apiName);
        if (apiConfiguration == null) {
            apiConfigFileName = APITestConstants.API_RESOURCE_BASE_DIR + APITestConstants.API_CONFIG_FILE;
            apiConfiguration = apiConfigMap.get(apiConfigFileName + "." + apiName);
        }

        if (apiConfiguration == null) {
            logger.error("Config Error: API config not found for API :" + apiName + " in file :" + apiConfigFileName);
        }

        return apiConfiguration;
    }


    public boolean loadApiConfig(String apiConfigFile) {
        if (isFileLoaded(apiConfigFile))
            return true;
        APIConfiguations apiConfigs = APIXMLTransformer.INSTANCE.transform(apiConfigFile, APIConfiguations.class);
        if (apiConfigs == null) {
            logger.warn("API Definitons not found : " + apiConfigFile);
            return false;
        }

        List<APIConfiguration> apiConfigList = apiConfigs.getApiConfigurations();
        if (CollectionUtils.isEmpty(apiConfigList)) {
            logger.warn("API Definitons not found or Empty: " + apiConfigFile);
            return false;
        }

        for (APIConfiguration apiConfig : apiConfigList) {
            apiConfigMap.put(apiConfigFile + "." + apiConfig.name, apiConfig);
        }
        loadedFileList.add(apiConfigFile);

        System.out.println("API Config File loaded successfully :" + apiConfigFile);

        return true;
    }

    public boolean loadApiTestConfig(String apiTestConfigFile) {
        if (isFileLoaded(apiTestConfigFile))
            return true;
        APITestConfigurations apiTestConfigs = APIXMLTransformer.INSTANCE.transform(apiTestConfigFile, APITestConfigurations.class);
        if (apiTestConfigs == null) {
            logger.warn("API Test Definitons not found : " + apiTestConfigFile);
            return false;
        }

        List<APITestConfiguration> apiTestConfigList = apiTestConfigs.getApiTestConfigurations();
        if (CollectionUtils.isEmpty(apiTestConfigList)) {
            logger.warn("API Test Definitons not found or Empty: " + apiTestConfigFile);
            return false;
        }

        for (APITestConfiguration apiTestConfig : apiTestConfigList) {
            apiTestConfigMap.put(apiTestConfigFile + "." + apiTestConfig.testName, apiTestConfig);
        }
        loadedFileList.add(apiTestConfigFile);

        System.out.println("API Test Config File loaded successfully :" + apiTestConfigFile);

        return true;
    }

    private Boolean isFileLoaded(String fileName) {
        if (loadedFileList.contains(fileName)) {
            return true;
        }

        return false;
    }
}
