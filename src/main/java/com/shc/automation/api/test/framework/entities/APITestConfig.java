/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import com.shc.automation.api.test.framework.internal.config.APIPackageConfig;

/**
 * @author spoojar
 *
 */
public class APITestConfig {

	private String apiConfigFile = null;
	private String apiTestConfigFile = null;
	private Boolean turnOffValidation = false;
	private Boolean turnOffResponseParsing = false;
	private Boolean printJsonResponseInReport = true;
	private Boolean persistSession = false;

	private APIPackageConfig testPackageConfig = null;

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

	public APIPackageConfig getTestPackageConfig() {
		if (testPackageConfig == null) {
			testPackageConfig = new APIPackageConfig();
		}
		return testPackageConfig;
	}

	public void setTestPackageConfig(APIPackageConfig testPackageConfig) {
		this.testPackageConfig = testPackageConfig;
	}

	public void setNoSqlConnectionName(String mongoConnectionName) {
		getTestPackageConfig().setNoSqlConnectionName(mongoConnectionName);
	}

	public void setSqlConnectionName(String sqlConnectionName) {
		getTestPackageConfig().setSqlConnectionName(sqlConnectionName);
	}

}
