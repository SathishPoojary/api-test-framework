package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.internal.report.APIHtmlTestItemLog;
import com.shc.automation.utils.json.JsonMismatchField;

/**
 * Response Item from API Comparison Test. Response Item will be generated for
 * each of the API test requests.
 * 
 * @author spoojar
 *
 */
public class APICompareTestsResponseItem implements Serializable {

	private static final long serialVersionUID = 2882676880263117753L;

	private String scenarioName;

	private APITestResponseItem response1;
	private APITestResponseItem response2;

	private List<JsonMismatchField> mismatches;

	private ResultType result;

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String testName) {
		this.scenarioName = testName;
	}

	public String getEscapedScenarioName() {
		return UrlEscapers.urlFormParameterEscaper().escape(scenarioName);
	}

	public APITestResponseItem getResponse1() {
		return response1;
	}

	public void setResponse1(APITestResponseItem response1) {
		this.response1 = response1;
	}

	public APITestResponseItem getResponse2() {
		return response2;
	}

	public void setResponse2(APITestResponseItem response2) {
		this.response2 = response2;
	}

	public List<JsonMismatchField> getMismatches() {
		return mismatches;
	}

	public void setMismatches(List<JsonMismatchField> mismatches) {
		this.mismatches = mismatches;
	}

	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType compareResult) {
		this.result = compareResult;
	}

	public StringBuilder getHtmlString() {
		return APIHtmlTestItemLog.generateHtmlLog(this);
	}

	public String getMismatchString() {
		StringBuilder str = null;
		if (CollectionUtils.isNotEmpty(mismatches)) {
			str = new StringBuilder(
					mismatches.size() + " mismatches found on Comparison of " + response1.getDisplayName() + " and " + response2.getDisplayName());
		} else if (!ResultType.PASSED.equals(result)) {
			if (response1 == null || response1.getApiError() != null || response2 == null || response2.getApiError() != null) {
				str = new StringBuilder("Cannot Compare : One of the tests resulted in ERROR");
			} else {
				str = new StringBuilder("Cannot Compare : One of the tests failed");
			}

		} else {
			str = new StringBuilder("No mismatches found in comparsion of " + response1.getDisplayName() + " and " + response2.getDisplayName());
		}

		return str.toString();
	}

}
