/**
 * 
 */
package com.shc.automation.api.test.framework.internal.report;

import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponse;
import com.shc.automation.api.test.framework.model.response.chain.compare.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.model.response.chain.compare.APIChainCompareTestsBaseResponse;
import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponse;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.APIResponse;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APIHtmlReporter {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public StringBuilder report(APIBaseResponse testResponse) {
		if (testResponse instanceof APIResponse)
			return report((APIResponse) testResponse);
		if (testResponse instanceof APICompareTestsResponse)
			return report((APICompareTestsResponse) testResponse);
		if (testResponse instanceof APIChainCompareTestsBaseResponse)
			return report((APIChainCompareTestsBaseResponse) testResponse);
		if (testResponse instanceof APIChainTestsResponse)
			return report((APIChainTestsResponse) testResponse);

		return null;
	}

	public StringBuilder report(APIResponse testResponse) {
		StringBuilder printer = new StringBuilder();

		Map<String, APIScenarioResponse> responseItems = testResponse.getResponseItems();
		Iterator<APIScenarioResponse> iter = responseItems.values().iterator();
		log.info("Logging results for " + responseItems.size() + " responses");

		APIScenarioResponse responseItem = null;
		while (iter.hasNext()) {
			responseItem = iter.next();
			printer.append(APIHtmlTestItemLog.generateHtmlLog(responseItem));

		}

		return printer;
	}

	public StringBuilder report(APICompareTestsResponse compareResponse) {
		StringBuilder printer = new StringBuilder();

		Map<String, APICompareTestsResponseItem> compareResponseItems = compareResponse.getResponseItems();
		Iterator<APICompareTestsResponseItem> iter = compareResponseItems.values().iterator();
		log.info("Logging results for " + compareResponseItems.size() + " responses");

		APICompareTestsResponseItem compareResponseItem = null;
		while (iter.hasNext()) {
			compareResponseItem = iter.next();
			printer.append(APIHtmlTestItemLog.generateHtmlLog(compareResponseItem));
		}

		return printer;
	}

	public StringBuilder report(APIChainTestsResponse testResponse) {
		StringBuilder printer = new StringBuilder();

		Map<String, APIChainTestsResponseItem> responseItems = testResponse.getResponseItems();
		Iterator<APIChainTestsResponseItem> iter = responseItems.values().iterator();
		log.info("Logging results for " + responseItems.size() + " responses");

		APIChainTestsResponseItem chainItem = null;
		while (iter.hasNext()) {
			chainItem = iter.next();
			printer.append(APIHtmlTestItemLog.generateHtmlLog(chainItem));
		}

		return printer;
	}

	/**
	 * @param testResponse
	 * @return
	 */
	public StringBuilder report(APIChainCompareTestsBaseResponse testResponse) {
		StringBuilder printer = new StringBuilder();
		if(testResponse == null){
			return printer;
		}

		Map<String, APIChainCompareTestResponseItem> compareResponseItems = testResponse.getResponseItems();
		Iterator<String> iter = compareResponseItems.keySet().iterator();
		log.info("Logging results for " + compareResponseItems.size() + " responses");

		String scenario = null;
		while (iter.hasNext()) {
			scenario = iter.next();
			printer.append(APIHtmlTestItemLog.generateHtmlLog(compareResponseItems.get(scenario)));
		}

		return printer;
	}

}
