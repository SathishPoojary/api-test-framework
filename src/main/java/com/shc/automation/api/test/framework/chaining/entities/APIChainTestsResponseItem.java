/**
 * 
 */
package com.shc.automation.api.test.framework.chaining.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.internal.report.APIHtmlTestItemLog;

/**
 * Result Entity of a single Chain Test. This will contain a chain or linked
 * list of chain step responses.
 * 
 * @author sathish_poojary
 * 
 */
public class APIChainTestsResponseItem implements Serializable {

	private static final long serialVersionUID = -336633615498651261L;

	private String scenarioName;
	private List<APITestResponseItem> testChainResponse;
	private ResultType result = null;
	
	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType result) {
		this.result = result;
	}

	public APIChainTestsResponseItem(String scenario) {
		this.scenarioName = scenario;
		testChainResponse = new LinkedList<APITestResponseItem>();
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenario) {
		this.scenarioName = scenario;
	}
	
	public String getEscapedScenarioName(){
		return UrlEscapers.urlFormParameterEscaper().escape(scenarioName);
	}

	public List<APITestResponseItem> getTestChainResponse() {
		return testChainResponse;
	}

	public void setTestChainResponse(List<APITestResponseItem> testChainResponse) {
		this.testChainResponse = testChainResponse;
	}

	public void addTestStepResponse(APITestResponseItem stepResponseItem) {
		testChainResponse.add(stepResponseItem);
	}

	public StringBuilder getHtmlString() {
		return APIHtmlTestItemLog.generateHtmlLog(this);
	}	
}
