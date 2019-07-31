/**
 * 
 */
package com.shc.automation.api.test.framework.model.request.chain;

import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.request.TestType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * API Test Chain Request containing the steps to execute
 * 
 * @author spoojar
 *
 */
public class APIChainTestRequest extends APIBaseRequest {
	private final TestType testType = TestType.chain;
	private boolean enableSession = true;

	protected List<APIChainTestStepRequest> testSteps;
	private Map<String, Object> chainContext = new HashMap<String, Object>();

	public APIChainTestRequest(String testName) {
		this.testName = testName;
		testSteps = new LinkedList<>();
	}

	public boolean isEnableSession() {
		return enableSession;
	}

	public void setEnableSession(boolean enableSession) {
		this.enableSession = enableSession;
	}

	public List<APIChainTestStepRequest> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<APIChainTestStepRequest> testSteps) {
		this.testSteps = testSteps;
	}

	public void addTestStep(APIChainTestStepRequest testStep) {
		this.testSteps.add(testStep);
	}

	public Map<String, Object> getChainContext() {
		return chainContext;
	}

	public void setChainContext(Map<String, Object> chainContext) {
		this.chainContext = chainContext;
	}

	public void addToChainContext(String name, Object value) {
		if (chainContext == null) {
			chainContext = new HashMap<String, Object>();
		}
		chainContext.put(name, value);
	}

	public TestType getTestType() {
		return testType;
	}
}
