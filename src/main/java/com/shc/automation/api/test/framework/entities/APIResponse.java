/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author spoojar
 *
 */
public class APIResponse implements Serializable {
	private static final long serialVersionUID = 4336552323004749132L;
	protected String testName;
	private String requestType = "GET";

	protected Boolean testSuccessful = true;
	protected String reportFormat;
	protected Throwable testError = null;

	protected List<String> failedScenarioList = new ArrayList<String>();

	protected String description = "";
	protected TestType testType = TestType.standalone;

	protected String validationFrequencyTable = null;

	protected String mongoStoreObjectId = null;
	
	protected Integer totalRequests = 0;

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	public void addFailedScenario(String scenarioName) {
		this.failedScenarioList.add(scenarioName);
	}

	public List<String> getFailedScenarioList() {
		return failedScenarioList;
	}

	public void setFailedScenarioList(List<String> failedScenarioList) {
		this.failedScenarioList = failedScenarioList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isTestSuccessful() {
		return testSuccessful;
	}

	public void setTestSuccessful(Boolean testSuccessful) {
		this.testSuccessful = testSuccessful;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String chainName) {
		this.testName = chainName;
	}

	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}

	public String getReportFormat() {
		return reportFormat;
	}

	public Throwable getTestError() {
		return testError;
	}

	public void setTestError(Throwable testError) {
		this.testError = testError;
	}

	public Integer getTotalRequests() {
		return totalRequests;
	}

	public Integer getTotalFailed() {
		return failedScenarioList.size();
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getValidationFrequencyTable() {
		return validationFrequencyTable;
	}

	public void setValidationFrequencyTable(String validationFrequencyTable) {
		this.validationFrequencyTable = validationFrequencyTable;
	}

	public String getMongoStoreObjectId() {
		return mongoStoreObjectId;
	}

	public void setMongoStoreObjectId(String mongoStoreId) {
		this.mongoStoreObjectId = mongoStoreId;
	}

	public void setTotalRequests(Integer totalRequests) {
		this.totalRequests = totalRequests;
	}
}
