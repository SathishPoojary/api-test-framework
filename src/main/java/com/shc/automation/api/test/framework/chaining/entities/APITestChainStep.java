package com.shc.automation.api.test.framework.chaining.entities;

import com.shc.automation.api.test.framework.entities.APITestRequest;

/**
 * Represents API Chain Step to be executed by a test chain.
 * 
 * @author spoojar
 *
 */
public class APITestChainStep {
	private APITestRequest stepRequest;
	private String stepName;
	private String stepResponseProcess;
	private Boolean printResponseContentInReport = null;
	private long sleepTimeAfterStepExecution = 0;

	public APITestChainStep() {
	}

	public APITestChainStep(APITestRequest stepRequest) {
		this.stepRequest = stepRequest;
	}

	public APITestChainStep(String stepName, APITestRequest stepRequest) {
		this.stepName = stepName;
		this.stepRequest = stepRequest;
	}

	public APITestChainStep(String stepName, APITestRequest stepRequest, String stepResponseProcess) {
		this.stepName = stepName;
		this.stepRequest = stepRequest;
		this.stepResponseProcess = stepResponseProcess;
	}

	public APITestRequest getStepRequest() {
		return stepRequest;
	}

	public void setStepRequest(APITestRequest stepRequest) {
		this.stepRequest = stepRequest;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepResponseProcess() {
		return stepResponseProcess;
	}

	public void setStepResponseProcess(String stepResponseProcess) {
		this.stepResponseProcess = stepResponseProcess;
	}

	public Boolean getPrintResponseContentInReport() {
		return printResponseContentInReport;
	}

	public void setPrintResponseContentInReport(Boolean printResponseContentInReport) {
		this.printResponseContentInReport = printResponseContentInReport;
	}

	public long getSleepTimeAfterStepExecution() {
		return sleepTimeAfterStepExecution;
	}

	public void setSleepTimeAfterStepExecution(long sleepTimeAfterStepExecution) {
		this.sleepTimeAfterStepExecution = sleepTimeAfterStepExecution;
	}

}
