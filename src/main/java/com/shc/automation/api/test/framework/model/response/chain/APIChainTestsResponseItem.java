/**
 * 
 */
package com.shc.automation.api.test.framework.model.response.chain;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.internal.report.APIHtmlTestItemLog;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.ResultType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
	private List<APIScenarioResponse> testChainResponse;
	private ResultType result = null;
	
	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType result) {
		this.result = result;
	}

	public APIChainTestsResponseItem(String scenario) {
		this.scenarioName = scenario;
		testChainResponse = new LinkedList<APIScenarioResponse>();
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

	public List<APIScenarioResponse> getTestChainResponse() {
		return testChainResponse;
	}

	public void setTestChainResponse(List<APIScenarioResponse> testChainResponse) {
		this.testChainResponse = testChainResponse;
	}

	public void addTestStepResponse(APIScenarioResponse stepResponseItem) {
		testChainResponse.add(stepResponseItem);
	}

	public StringBuilder getHtmlString() {
		return APIHtmlTestItemLog.generateHtmlLog(this);
	}	
}
