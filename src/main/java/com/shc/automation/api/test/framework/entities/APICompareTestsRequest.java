/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.util.List;
import java.util.Map;

import com.shc.automation.utils.json.JsonCompareOption;

/**
 * @author spoojar
 *
 */
public class APICompareTestsRequest {
	private String name;
	private Map<String, APITestRequest> testsToCompare;
	private List<String> pathsToExcludeFromCompare;
	private List<String> arrayPathListToIgnoreOrder;
	private JsonCompareOption compareOption;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, APITestRequest> getTestsToCompare() {
		return testsToCompare;
	}

	public void setTestsToCompare(Map<String, APITestRequest> testsToCompare) {
		this.testsToCompare = testsToCompare;
	}

	public List<String> getPathsToExcludeFromCompare() {
		return pathsToExcludeFromCompare;
	}

	public void setPathsToExcludeFromCompare(List<String> pathsToExcludeFromCompare) {
		this.pathsToExcludeFromCompare = pathsToExcludeFromCompare;
	}

	public JsonCompareOption getCompareOption() {
		return compareOption;
	}

	public void setCompareOption(JsonCompareOption compareOption) {
		this.compareOption = compareOption;
	}

	public List<String> getArrayPathListToIgnoreOrder() {
		return arrayPathListToIgnoreOrder;
	}

	public void setArrayPathListToIgnoreOrder(List<String> arrayPathListToIgnoreOrder) {
		this.arrayPathListToIgnoreOrder = arrayPathListToIgnoreOrder;
	}

}
