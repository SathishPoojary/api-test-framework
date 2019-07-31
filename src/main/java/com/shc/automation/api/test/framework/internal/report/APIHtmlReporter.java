/**
 * 
 */
package com.shc.automation.api.test.framework.internal.report;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;

/**
 * @author spoojar
 *
 */
public class APIHtmlReporter {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public StringBuilder report(APIResponse testResponse) {
		if (testResponse instanceof APITestResponse)
			return report((APITestResponse) testResponse);
		if (testResponse instanceof APICompareTestsResponse)
			return report((APICompareTestsResponse) testResponse);
		if (testResponse instanceof APIChainCompareTestsResponse)
			return report((APIChainCompareTestsResponse) testResponse);
		if (testResponse instanceof APIChainTestsResponse)
			return report((APIChainTestsResponse) testResponse);

		return null;
	}

	public StringBuilder report(APITestResponse testResponse) {
		StringBuilder printer = new StringBuilder();

		Map<String, APITestResponseItem> responseItems = testResponse.getResponseItems();
		Iterator<APITestResponseItem> iter = responseItems.values().iterator();
		log.info("Logging results for " + responseItems.size() + " responses");

		APITestResponseItem responseItem = null;
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
	public StringBuilder report(APIChainCompareTestsResponse testResponse) {
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
