/**
 * 
 */
package com.shc.automation.api.test.framework;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.entities.ValidationType;
import com.shc.automation.api.test.framework.internal.validators.APIJsonValidator;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareOption;

/**
 * @author sathish_poojary
 * 
 */
public class APICompareTestExecutor {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * @param testResultsMap
	 * @param pathsToExcludeFromCompare
	 * @return
	 */
	public APICompareTestsResponse compareTests(Map<String, APITestResponse> testResultsMap, List<String> pathsToExcludeFromCompare) {
		return compareTests(testResultsMap, pathsToExcludeFromCompare, null, null);
	}

	/**
	 * @param testResultsMap
	 * @param pathsToExcludeFromCompare
	 * @param compareOption
	 * @param arrayPathListToIgnoreOrder
	 * @return
	 */
	public APICompareTestsResponse compareTests(Map<String, APITestResponse> testResultsMap, List<String> pathsToExcludeFromCompare,
			JsonCompareOption compareOption, List<String> arrayPathListToIgnoreOrder) {
		if (MapUtils.isEmpty(testResultsMap) || testResultsMap.size() < 2) {
			log.warn("No test responses to compare... Please verify");
			return null;
		}

		Iterator<String> resultIter = testResultsMap.keySet().iterator();
		String displayKey1 = resultIter.next();
		String displayKey2 = resultIter.next();
		APITestResponse expectedTestResponse = testResultsMap.get(displayKey1);
		APITestResponse actualTestResponse = testResultsMap.get(displayKey2);

		if (expectedTestResponse == null || actualTestResponse == null) {
			log.error("*************Null Response for Tests... Please Verify***************");
			return null;
		}
		APICompareTestsResponse compareResponse = new APICompareTestsResponse();
		Map<String, APITestResponseItem> responseItems1 = expectedTestResponse.getResponseItems();
		Map<String, APITestResponseItem> responseItems2 = actualTestResponse.getResponseItems();
		Iterator<String> scenarioIter = responseItems1.keySet().iterator();
		actualTestResponse.getFailedScenarioList().clear();
		String scenarioName = null;
		APICompareTestsResponseItem compareResponseItem = null;
		APITestResponseItem responseItem1 = null;
		APITestResponseItem responseItem2 = null;

		while (scenarioIter.hasNext()) {
			scenarioName = scenarioIter.next();
			if (responseItems2.containsKey(scenarioName)) {
				responseItem1 = responseItems1.get(scenarioName);
				responseItem2 = responseItems2.get(scenarioName);
				responseItem1.setDisplayName(displayKey1);
				responseItem2.setDisplayName(displayKey2);
				compareResponseItem = compareResponses(responseItem1, responseItem2, pathsToExcludeFromCompare, compareOption, arrayPathListToIgnoreOrder);
				compareResponseItem.setScenarioName(scenarioName);
				compareResponse.addCompareTestResponseItem(compareResponseItem);
				if (!ResultType.PASSED.equals(compareResponseItem.getResult())) {
					compareResponse.setTestSuccessful(false);
					compareResponse.addFailedScenario(scenarioName);
				}

			}
		}
		if (compareResponse.isTestSuccessful() == null)
			compareResponse.setTestSuccessful(true);
		
		compareResponse.setTotalRequests(testResultsMap.size());

		return compareResponse;
	}

	/**
	 * Compare two API Responses and add validation field to actual response
	 * item
	 * 
	 * @param expectedResponse
	 * @param actualResponse
	 * @param excludes
	 * 
	 * @return
	 */
	public APICompareTestsResponseItem compareResponses(APITestResponseItem expectedResponse, APITestResponseItem actualResponse,
			List<String> pathsToExcludeFromCompare) {
		return compareResponses(expectedResponse, actualResponse, pathsToExcludeFromCompare, null, null);
	}

	/**
	 * Compare two API Responses and add validation field to actual response
	 * item
	 * 
	 * @param expectedResponse
	 * @param actualResponse
	 * @param excludes
	 * @return
	 */
	public APICompareTestsResponseItem compareResponses(APITestResponseItem expectedResponse, APITestResponseItem actualResponse,
			List<String> pathsToExcludeFromCompare, JsonCompareOption compareOption, List<String> arrayPathListToIgnoreOrder) {
		APICompareTestsResponseItem compareResponseItem = new APICompareTestsResponseItem();
		if (expectedResponse == null || actualResponse == null || !expectedResponse.isValidResult() || !actualResponse.isValidResult()) {
			log.error("Failed to Compare: Expected and/or Actual API Response is NULL or Invalid ");
			compareResponseItem.setResponse1(expectedResponse);
			compareResponseItem.setResponse2(actualResponse);
			compareResponseItem.setResult(ResultType.FAILED);
			return compareResponseItem;
		}

		try {
			APIValidationField validation = new APIValidationField(null, APITestConstants.COMPARE_TWO_RESPONSES_BASE_PATH, ValidationType.EQUALS);
			Object expected = expectedResponse.getResponseToCompare();
			if (expected == null) {
				expected = APITestUtils.readFromJSON(expectedResponse.getResponseContent(), "$", true);
			}
			Object actual = actualResponse.getResponseToCompare();
			if (actual == null) {
				actual = APITestUtils.readFromJSON(actualResponse.getResponseContent(), "$", true);
			}

			validation.setExpectedResponseValue(expected);
			validation.setActualResponseValue(actual);
			validation.setExcludes(pathsToExcludeFromCompare);
			validation.setArrayPathListToIgnoreOrder(arrayPathListToIgnoreOrder);
			validation.setCompareOption(compareOption);

			compareResponseItem.setResponse1(expectedResponse);
			compareResponseItem.setResponse2(actualResponse);
			compareResponseItem.setResult(new APIJsonValidator().validate(validation) ? ResultType.PASSED : ResultType.FAILED);
			compareResponseItem.setMismatches(validation.getDifferences());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to Compare: Error in comparing scenario :" + expectedResponse.getScenarioName(), e);
			compareResponseItem.setResponse1(expectedResponse);
			compareResponseItem.setResponse2(actualResponse);
			compareResponseItem.setResult(ResultType.FAILED);

		}

		return compareResponseItem;
	}

}
