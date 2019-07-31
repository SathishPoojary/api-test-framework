/**
 * 
 */
package com.shc.automation.api.test.framework.chaining.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;

/**
 * @author spoojar
 *
 */
public class APIChainCompareTestResponseItem implements Serializable {
	private static final long serialVersionUID = -120839375191433786L;
	private String scenarioName;
	private List<APICompareTestsResponseItem> chainCompareResponseList;
	private ResultType result = ResultType.PASSED;

	public APIChainCompareTestResponseItem() {
		chainCompareResponseList = new LinkedList<APICompareTestsResponseItem>();
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public List<APICompareTestsResponseItem> getChainCompareResponseList() {
		return chainCompareResponseList;
	}

	public void setChainCompareResponseList(List<APICompareTestsResponseItem> chainCompareResponseList) {
		this.chainCompareResponseList = chainCompareResponseList;
	}

	public void addChainCompareResponseItem(APICompareTestsResponseItem chainCompareResponseItem) {
		chainCompareResponseList.add(chainCompareResponseItem);
	}

	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType result) {
		this.result = result;
	}

	public String getEscapedScenarioName() {
		return UrlEscapers.urlFormParameterEscaper().escape(scenarioName);
	}

}
