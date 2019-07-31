/**
 * 
 */
package com.shc.automation.api.test.framework.model.request.compare;

import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.TestType;
import com.shc.automation.utils.json.JsonCompareOption;

import java.util.List;
import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APICompareTestsRequest extends APIBaseRequest {
	private TestType testType = TestType.comparative;
	private Map<String, APIRequest> testsToCompare;
	private List<String> pathsToExcludeFromCompare;
	private List<String> arrayPathListToIgnoreOrder;
	private JsonCompareOption compareOption;

	public Map<String, APIRequest> getTestsToCompare() {
		return testsToCompare;
	}

	public void setTestsToCompare(Map<String, APIRequest> testsToCompare) {
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
	public TestType getTestType() {
		return testType;
	}
}
