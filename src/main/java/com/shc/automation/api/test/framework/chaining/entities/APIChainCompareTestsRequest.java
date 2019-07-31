package com.shc.automation.api.test.framework.chaining.entities;

import java.util.List;
import java.util.Map;

import com.shc.automation.utils.json.JsonCompareOption;

public class APIChainCompareTestsRequest {
	private String name;
	private Map<String, APITestChain> chainsToCompare;
	private List<String> pathsToExcludeFromCompare;
	private List<String> arrayPathListToIgnoreOrder;
	private JsonCompareOption compareOption;

	public Map<String, APITestChain> getChainsToCompare() {
		return chainsToCompare;
	}

	public void setChainsToCompare(Map<String, APITestChain> chainsToCompare) {
		this.chainsToCompare = chainsToCompare;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPathsToExcludeFromCompare() {
		return pathsToExcludeFromCompare;
	}

	public void setPathsToExcludeFromCompare(List<String> pathsToExcludeFromCompare) {
		this.pathsToExcludeFromCompare = pathsToExcludeFromCompare;
	}

	public List<String> getArrayPathListToIgnoreOrder() {
		return arrayPathListToIgnoreOrder;
	}

	public void setArrayPathListToIgnoreOrder(List<String> arrayPathListToIgnoreOrder) {
		this.arrayPathListToIgnoreOrder = arrayPathListToIgnoreOrder;
	}

	public JsonCompareOption getCompareOption() {
		return compareOption;
	}

	public void setCompareOption(JsonCompareOption compareOption) {
		this.compareOption = compareOption;
	}

}
