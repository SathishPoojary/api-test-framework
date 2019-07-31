/**
 * 
 */
package com.shc.automation.api.test.framework;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.utils.json.JsonCompareOption;

/**
 * @author spoojar
 *
 */
public class APIChainCompareTestsExecutor extends APICompareTestExecutor {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * @param testResultsMap
	 * @param pathsToExcludeFromCompare
	 * @param compareOption
	 * @param arrayPathListToIgnoreOrder
	 * @return
	 */
	public APIChainCompareTestsResponse compareChainTests(Map<String, APIChainTestsResponse> testResultsMap, List<String> pathsToExcludeFromCompare,
			JsonCompareOption compareOption, List<String> arrayPathListToIgnoreOrder) {
		if (MapUtils.isEmpty(testResultsMap) || testResultsMap.size() < 2) {
			log.warn("No test responses to compare... Please verify");
			return null;
		}

		Iterator<String> resultIter = testResultsMap.keySet().iterator();
		String displayKey1 = resultIter.next();
		String displayKey2 = resultIter.next();
		APIChainTestsResponse expectedResponse = testResultsMap.get(displayKey1);
		APIChainTestsResponse actualResponse = testResultsMap.get(displayKey2);
		if (expectedResponse == null || actualResponse == null) {
			log.error("*************Null Response for Tests... Please Verify***************");
			return null;
		}

		Map<String, APIChainTestsResponseItem> expectedItems = expectedResponse.getResponseItems();
		Map<String, APIChainTestsResponseItem> actualItems = actualResponse.getResponseItems();
		Iterator<String> scenarioIter = expectedItems.keySet().iterator();
		actualResponse.getFailedScenarioList().clear();
		String scenarioName = null;

		APIChainTestsResponseItem expectedChainResponse = null;
		APIChainTestsResponseItem actualChainResponse = null;
		List<APITestResponseItem> expectedChainResponseItems = null;
		List<APITestResponseItem> actualChainResponseItems = null;

		APIChainCompareTestResponseItem chainCompareResponseItem = null;
		APICompareTestsResponseItem compareResponseItem = null;
		APIChainCompareTestsResponse compareResponse = new APIChainCompareTestsResponse();

		while (scenarioIter.hasNext()) {
			scenarioName = scenarioIter.next();
			chainCompareResponseItem = new APIChainCompareTestResponseItem();
			chainCompareResponseItem.setScenarioName(scenarioName);
			if (actualItems.containsKey(scenarioName)) {
				expectedChainResponse = expectedItems.get(scenarioName);
				actualChainResponse = actualItems.get(scenarioName);

				expectedChainResponseItems = expectedChainResponse.getTestChainResponse();
				actualChainResponseItems = actualChainResponse.getTestChainResponse();

				if (CollectionUtils.isEmpty(expectedChainResponseItems) || CollectionUtils.isEmpty(actualChainResponseItems)) {
					log.error("*************NO RESPONSE ITEMS FOUND FOR SCENARIO : " + scenarioName + "***************");
				}

				int size = expectedChainResponseItems.size() > actualChainResponseItems.size() ? expectedChainResponseItems.size()
						: actualChainResponseItems.size();

				for (int i = 0; i < size; i++) {
					APITestResponseItem expectedItem = (expectedChainResponseItems.size() > i) ? expectedChainResponseItems.get(i) : null;
					APITestResponseItem actualItem = (actualChainResponseItems.size() > i) ? actualChainResponseItems.get(i) : null;

					compareResponseItem = compareResponses(expectedItem, actualItem, pathsToExcludeFromCompare, compareOption, arrayPathListToIgnoreOrder);
					setScenarioNameForChainStep(compareResponseItem);
					chainCompareResponseItem.addChainCompareResponseItem(compareResponseItem);

					if (!ResultType.PASSED.equals(compareResponseItem.getResult())) {
						chainCompareResponseItem.setResult(compareResponseItem.getResult());
					}
				}

				compareResponse.addScenarioResponse(scenarioName, chainCompareResponseItem);
				if (!ResultType.PASSED.equals(chainCompareResponseItem.getResult())) {
					compareResponse.setTestSuccessful(false);
					compareResponse.addFailedScenario(scenarioName);
				}
			}
		}
		compareResponse.setTotalRequests(testResultsMap.size());
		return compareResponse;
	}

	private void setScenarioNameForChainStep(APICompareTestsResponseItem compareResponseItem) {
		if (compareResponseItem == null) {
			return;
		}
		String displayName1 = compareResponseItem.getResponse1() == null ? "" : compareResponseItem.getResponse1().getDisplayName();
		String displayName2 = compareResponseItem.getResponse2() == null ? "" : compareResponseItem.getResponse2().getDisplayName();

		compareResponseItem.setScenarioName(displayName1 + " == " + displayName2);
	}
}
