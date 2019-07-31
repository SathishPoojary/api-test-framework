/**
 *
 */
package com.shc.automation.api.test.framework.model;

import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.xml.APIPackageConfig;

/**
 * @author spoojar
 *
 */
public class APIExecutionConfig {

    private String apiConfigFile = null;
    private String apiTestConfigFile = null;

    private String packageName;
    private String resourceFolder;
    private String sqlConnectionName;
    private String noSqlConnectionName;

    private Boolean turnOffValidation = false;
    private Boolean turnOffResponseParsing = false;
    private Boolean printJsonResponseInReport = true;
    private Boolean persistSession = false;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getResourceFolder() {
        return resourceFolder;
    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public String getSqlConnectionName() {
        return sqlConnectionName;
    }

    public void setSqlConnectionName(String sqlConnectionName) {
        this.sqlConnectionName = sqlConnectionName;
    }

    public String getNoSqlConnectionName() {
        return noSqlConnectionName;
    }

    public void setNoSqlConnectionName(String noSqlConnectionName) {
        this.noSqlConnectionName = noSqlConnectionName;
    }

    public Boolean getTurnOffValidation() {
        return turnOffValidation;
    }

    public void setTurnOffValidation(Boolean turnOffValidation) {
        this.turnOffValidation = turnOffValidation;
    }

    public Boolean getTurnOffResponseParsing() {
        return turnOffResponseParsing;
    }

    public void setTurnOffResponseParsing(Boolean turnOffResponseParsing) {
        this.turnOffResponseParsing = turnOffResponseParsing;
    }

    public Boolean getPrintJsonResponseInReport() {
        return printJsonResponseInReport;
    }

    public void setPrintJsonResponseInReport(Boolean printJsonResponseInReport) {
        this.printJsonResponseInReport = printJsonResponseInReport;
    }

    public Boolean getPersistSession() {
        return persistSession;
    }

    public void setPersistSession(Boolean persistSession) {
        this.persistSession = persistSession;
    }

    public String getApiConfigFile() {
        return apiConfigFile;
    }

    public void setApiConfigFile(String apiConfigFile) {
        this.apiConfigFile = apiConfigFile;
    }

    public String getApiTestConfigFile() {
        return apiTestConfigFile;
    }

    public void setApiTestConfigFile(String apiTestConfigFile) {
        this.apiTestConfigFile = apiTestConfigFile;
    }

    public void configurePackage(String packageName, APIPackageConfig packageConfig) {
        this.packageName = packageConfig.getName();
        this.resourceFolder = packageConfig.getResourceFolder();
        this.sqlConnectionName = packageConfig.getSqlConnectionName();
        this.noSqlConnectionName = packageConfig.getNoSqlConnectionName();
    }
}
