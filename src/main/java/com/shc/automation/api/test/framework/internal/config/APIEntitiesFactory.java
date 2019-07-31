/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestConstants;

/**
 * @author spoojar
 *
 */
public class APIEntitiesFactory {
	private final Logger log = Logger.getLogger(APIEntitiesFactory.class.getName());

	private final String DATA_SOURCES_FILE = "api/data-sources.xml";
	private final String PROJECT_CONFIG_FILE = "api/project-config.xml";

	private Map<String, APIDataSource> projectDataSources;
	private Map<String, APIPackageConfig> projectPackageConfigs;
	private Map<String, APIConfiguration> apiConfigMap;
	private Map<String, APITestConfiguration> apiTestConfigMap;

	private static APIEntitiesFactory INSTANCE;
	private List<String> loadedFileList;

	public static synchronized APIEntitiesFactory getInstance(boolean ohmInstance) {
		if (ohmInstance) {
			if (INSTANCE == null) {
				System.out.println("Creating API Entities factorty for reporting");
				INSTANCE = new APIEntitiesFactory();
			}
		} else {
			if (INSTANCE == null) {
				System.out.println("Creating API Entities factorty for Tests");
				INSTANCE = new APIEntitiesFactory(true);
			}
		}
		return INSTANCE;
	}

	private APIEntitiesFactory() {
		loadedFileList = new ArrayList<String>();
		projectDataSources = new HashMap<String, APIDataSource>();
		loadDataSources(APITestConstants.API_INTERNAL_NoSQL_CONFIG);
	}

	private APIEntitiesFactory(boolean apiInstance) {
		this();
		projectPackageConfigs = new HashMap<String, APIPackageConfig>();
		apiConfigMap = new ConcurrentHashMap<String, APIConfiguration>();
		apiTestConfigMap = new ConcurrentHashMap<String, APITestConfiguration>();

		loadDataSources(DATA_SOURCES_FILE);
		loadPackages();
		loadApiConfig("api/" + APITestConstants.API_CONFIG_FILE);
		loadApiTestConfig("api/" + APITestConstants.API_TESTS_CONFIG_FILE);

	}

	public APIPackageConfig getPackageConfig(String packageName) {
		if (StringUtils.isBlank(packageName)) {
			log.warn("Empty package name. Not able to retrieve the package config");
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

	public APIDataSource getDataSource(String dataSourceName) {
		if (StringUtils.isBlank(dataSourceName)) {
			log.warn("Empty data source name. Not able to retrieve the Data Source");
			return null;
		}
		return projectDataSources.get(dataSourceName.trim());
	}

	public APITestConfiguration getConfigurationForTest(String apiTestConfigFileName, String testName) {
		if (!isFileLoaded(apiTestConfigFileName)) {
			loadApiTestConfig(apiTestConfigFileName);
		}

		APITestConfiguration config = apiTestConfigMap.get(apiTestConfigFileName + "." + testName);
		if (config == null) {
			apiTestConfigFileName = "api/" + APITestConstants.API_TESTS_CONFIG_FILE;
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
			apiConfigFileName = "api/" + APITestConstants.API_CONFIG_FILE;
			apiConfiguration = apiConfigMap.get(apiConfigFileName + "." + apiName);
		}

		if (apiConfiguration == null) {
			log.error("Config Error: API config not found for API :" + apiName + " in file :" + apiConfigFileName);
		}

		return apiConfiguration;
	}

	public APIDataSource getDatasource(String dataSourceFile, String datasourceName) {
		APIDataSources dataSources = APIXMLTransformer.INSTANCE.transform(dataSourceFile, APIDataSources.class);
		if (dataSources == null) {
			log.warn("Data sources are not configured :" + dataSourceFile);
			return null;
		}
		List<APIDataSource> dataSrcList = dataSources.getDataSources();
		if (CollectionUtils.isEmpty(dataSrcList)) {
			log.warn("No Data sources found for Test Workspace / Project");
			return null;
		}
		for (APIDataSource dataSrc : dataSrcList) {
			if (datasourceName.equals(dataSrc.getName())) {
				return dataSrc;
			}
		}
		return null;
	}

	private void loadDataSources(String dataSourceFile) {
		APIDataSources dataSources = APIXMLTransformer.INSTANCE.transform(dataSourceFile, APIDataSources.class);
		if (dataSources == null) {
			log.warn("Data sources are not configured for Test Workspace / Project :" + dataSourceFile);
			return;
		}

		List<APIDataSource> dataSrcList = dataSources.getDataSources();
		if (CollectionUtils.isEmpty(dataSrcList)) {
			log.warn("No Data sources found for Test Workspace / Project");
			return;
		}

		for (APIDataSource dataSrc : dataSrcList) {
			projectDataSources.put(dataSrc.getName(), dataSrc);
		}
		System.out.println("Data sources for the project configured successfully");
	}

	private void loadPackages() {
		APIPackagesConfig packageConfigs = APIXMLTransformer.INSTANCE.transform(PROJECT_CONFIG_FILE, APIPackagesConfig.class);
		if (packageConfigs == null) {
			log.warn("Packages are not configured for Test Workspace / Project");
			return;
		}

		List<APIPackageConfig> packageList = packageConfigs.getPackages();
		if (CollectionUtils.isEmpty(packageList)) {
			log.warn("No Packages found for Test Workspace / Project");
			return;
		}

		for (APIPackageConfig packageConfig : packageList) {
			projectPackageConfigs.put(packageConfig.getName(), packageConfig);
		}
		System.out.println("Packages for the project configured successfully");
	}

	public boolean loadApiConfig(String apiConfigFile) {
		if (isFileLoaded(apiConfigFile))
			return true;
		APIConfiguations apiConfigs = APIXMLTransformer.INSTANCE.transform(apiConfigFile, APIConfiguations.class);
		if (apiConfigs == null) {
			log.warn("API Definitons not found : " + apiConfigFile);
			return false;
		}

		List<APIConfiguration> apiConfigList = apiConfigs.getApiConfigurations();
		if (CollectionUtils.isEmpty(apiConfigList)) {
			log.warn("API Definitons not found or Empty: " + apiConfigFile);
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
			log.warn("API Test Definitons not found : " + apiTestConfigFile);
			return false;
		}

		List<APITestConfiguration> apiTestConfigList = apiTestConfigs.getApiTestConfigurations();
		if (CollectionUtils.isEmpty(apiTestConfigList)) {
			log.warn("API Test Definitons not found or Empty: " + apiTestConfigFile);
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
