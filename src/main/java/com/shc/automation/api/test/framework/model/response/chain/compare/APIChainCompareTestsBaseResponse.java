/**
 * 
 */
package com.shc.automation.api.test.framework.model.response.chain.compare;


import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author spoojar
 *
 */
public class APIChainCompareTestsBaseResponse extends APIBaseResponse {
	private static final long serialVersionUID = 4076598774510344758L;

	private Map<String, APIChainCompareTestResponseItem> responseItems = null;

	public APIChainCompareTestsBaseResponse() {
		responseItems = new ConcurrentHashMap<String, APIChainCompareTestResponseItem>();
	}

	public Map<String, APIChainCompareTestResponseItem> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(Map<String, APIChainCompareTestResponseItem> responseItems) {
		this.responseItems = responseItems;
	}

	public void addScenarioResponse(String scenario, APIChainCompareTestResponseItem scenarioResponse) {
		responseItems.put(scenario, scenarioResponse);
	}

	public String getReportFormat() {
		if (StringUtils.isBlank(reportFormat))
			return "html";
		return reportFormat;
	}

}
