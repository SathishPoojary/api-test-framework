package com.shc.automation.api.test.framework.model.request.chain;

import com.shc.automation.api.test.framework.model.request.APIRequest;

/**
 * Represents API Chain Step to be executed by a test chain.
 * 
 * @author spoojar
 *
 */
public class APIChainTestStepRequest {
	private APIRequest stepRequest;
	private String stepName;
	private String stepResponseProcess;
	private Boolean printResponseContentInReport = null;
	private long sleepTimeAfterStepExecution = 0;

	public APIChainTestStepRequest() {
	}

	public APIChainTestStepRequest(APIRequest stepRequest) {
		this.stepRequest = stepRequest;
	}

	public APIChainTestStepRequest(String stepName, APIRequest stepRequest) {
		this.stepName = stepName;
		this.stepRequest = stepRequest;
	}

	public APIChainTestStepRequest(String stepName, APIRequest stepRequest, String stepResponseProcess) {
		this.stepName = stepName;
		this.stepRequest = stepRequest;
		this.stepResponseProcess = stepResponseProcess;
	}

	public APIRequest getStepRequest() {
		return stepRequest;
	}

	public void setStepRequest(APIRequest stepRequest) {
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
