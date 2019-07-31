package com.shc.automation.api.test.framework.chaining.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.shc.automation.api.test.framework.entities.APIResponse;

/**
 * Result Entity of a API Chain Test. This will contain map of Chain Response
 * Items. Each Chain response Item is a Chain or Linked List of Chain Step
 * response.
 * 
 * @author sathish_poojary
 * 
 */
public class APIChainTestsResponse extends APIResponse {
	private static final long serialVersionUID = 3615480341113803047L;
	private Map<String, APIChainTestsResponseItem> responseItems;

	public APIChainTestsResponse(String testName) {
		this.testName = testName;
		responseItems = new ConcurrentHashMap<String, APIChainTestsResponseItem>();
	}

	public Map<String, APIChainTestsResponseItem> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(Map<String, APIChainTestsResponseItem> chainTestsResponseItems) {
		this.responseItems = chainTestsResponseItems;
	}

	public void addChainTestResponseItem(String scenario, APIChainTestsResponseItem chainTestResponseItem) {
		responseItems.put(scenario, chainTestResponseItem);
	}

	public String getReportFormat() {
		return "html";
	}
	
}
