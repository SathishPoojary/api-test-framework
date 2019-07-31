package com.shc.automation.api.test.framework.internal.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.config.xml.APIConfiguration;
import com.shc.automation.api.test.framework.internal.config.xml.APITestConfiguration;
import com.shc.automation.api.test.framework.internal.connect.APINoSQLDataManager;
import com.shc.automation.api.test.framework.internal.connect.APISQLDataManager;

@Singleton
public class APIConfigManager {

    private final APISQLDataManager apisqlDataManager;
    private final APINoSQLDataManager noSQLDataManager;
    private final APIPackageConfigManager packageConfigManager;

    @Inject
    public APIConfigManager(APISQLDataManager apisqlDataManager, APINoSQLDataManager noSQLDataManager, APIPackageConfigManager packageConfigManager) {
        this.apisqlDataManager = apisqlDataManager;
        this.noSQLDataManager = noSQLDataManager;
        this.packageConfigManager = packageConfigManager;
    }

    public void configurePackage(String packageName) {
        APITestContext.get().getExecutionConfig().configurePackage(packageName, packageConfigManager.getPackageConfig(packageName));
    }

    public void closeAllConnections() {
        apisqlDataManager.close();
        noSQLDataManager.close();
    }

    public String getAPITestConfigForPackage() {
        return packageConfigManager.getAPITestConfigForPackage();
    }


    public APITestConfiguration getConfigurationForTest(String apiTestConfigFile, String testName) {
        return packageConfigManager.getConfigurationForTest(apiTestConfigFile, testName);
    }

    public String getAPIConfigForPackage() {
        return packageConfigManager.getAPIConfigForPackage();
    }

    public APIConfiguration getConfigurationForApi(String apiConfigFile, String testName) {
        return packageConfigManager.getConfigurationForApi(apiConfigFile, testName);
    }
}
