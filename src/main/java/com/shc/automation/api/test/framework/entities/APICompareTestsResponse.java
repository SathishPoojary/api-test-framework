/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Result entity returned as a response from API Comparative tests. This will
 * contain the compare responses of all compare requests.
 * 
 * @author sathish_poojary
 * 
 */
public class APICompareTestsResponse extends APIResponse {
	private static final long serialVersionUID = -9045566428339280353L;
	private Map<String, APICompareTestsResponseItem> responseItems = null;
	
	public APICompareTestsResponse() {
		responseItems = new ConcurrentHashMap<String, APICompareTestsResponseItem>();
	}

	public Map<String, APICompareTestsResponseItem> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(Map<String, APICompareTestsResponseItem> compareTestResponseItems) {
		this.responseItems = compareTestResponseItems;
	}

	public void addCompareTestResponseItem(APICompareTestsResponseItem compareTestResponseItem) {
		this.responseItems.put(compareTestResponseItem.getScenarioName(), compareTestResponseItem);
	}	

	public String getReportFormat() {
		if (StringUtils.isBlank(reportFormat))
			return APIDataSourceType.excel.toString();
		return reportFormat;
	}
		
}
