/**
 * 
 */
package com.shc.automation.api.test.framework.chaining.entities;

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
public class APITestChain {
	private String name;
	private boolean enableSession = true;

	protected List<APITestChainStep> testSteps;
	private Map<String, Object> chainContext = new HashMap<String, Object>();

	public APITestChain(String name) {
		this.name = name;
		testSteps = new LinkedList<APITestChainStep>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnableSession() {
		return enableSession;
	}

	public void setEnableSession(boolean enableSession) {
		this.enableSession = enableSession;
	}

	public List<APITestChainStep> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<APITestChainStep> testSteps) {
		this.testSteps = testSteps;
	}

	public void addTestStep(APITestChainStep testStep) {
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

}
