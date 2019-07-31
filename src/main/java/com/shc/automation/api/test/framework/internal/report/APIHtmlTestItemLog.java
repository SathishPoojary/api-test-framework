package com.shc.automation.api.test.framework.internal.report;


import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.chain.compare.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.ResultType;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonMismatchField;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author spoojar
 *
 */
public class APIHtmlTestItemLog {

	public static StringBuilder generateHtmlLog(APIScenarioResponse responseItem) {
		StringBuilder testItemLog = new StringBuilder();
		if (responseItem == null)
			return testItemLog;

		testItemLog.append(APITestReportUtils.getScenarioDiv(responseItem.getScenarioName(), ResultType.PASSED.equals(responseItem.getResult())));
		testItemLog.append(getResponseItemLog(responseItem));
		testItemLog.append(APITestReportUtils.endDiv()).append(APITestReportUtils.endDiv());

		return testItemLog;
	}

	private static String getResponseItemLog(APIScenarioResponse responseItem) {
		StringBuilder testItemLog = new StringBuilder();

		testItemLog.append(APITestReportUtils.createEmptyFolder("URL : ", responseItem.getRequestUrl() == null ? "" : responseItem.getRequestUrl()));
		testItemLog.append(APITestReportUtils.getURLParamLog(responseItem.getUrlParameters()));

		Object payload = responseItem.getPayLoad();
		if (payload != null) {
			testItemLog.append(APITestReportUtils.getJsonLog("Payload : ", "", responseItem.getPayloadString()));
		}

		Throwable e = responseItem.getApiError();
		if (e != null) {
			return testItemLog.append(APITestReportUtils.getResultLog("Error : " + e.getMessage(), false)).toString();
		}

		testItemLog.append(APITestReportUtils.getResponseContentLog(true, responseItem.getExecutionTime(),
				responseItem.getResponseContentString()));
		addPostResponseLog(responseItem, testItemLog, 0);

		return testItemLog.toString();
	}

	private static void addPostResponseLog(APIScenarioResponse responseItem, StringBuilder testItemLog, int itemNumber) {
		String title = "Validation Results ";
		if (itemNumber > 0) {
			title += itemNumber;
		}
		testItemLog.append(APITestReportUtils.getItemResultLog(title, ResultType.PASSED.equals(responseItem.getResult())));
		testItemLog.append(APITestReportUtils.getResponseCodeVerificationLog(responseItem));
		testItemLog.append(getValidatorLogs(responseItem.getValidators()));
		testItemLog.append(getPrinterLogs(responseItem.getPrinters(), itemNumber));
	}

	public static StringBuilder generateHtmlLog(APICompareTestsResponseItem responseItem) {
		StringBuilder testItemLog = new StringBuilder();
		if (responseItem == null)
			return testItemLog;

		boolean compareResult = ResultType.PASSED.equals(responseItem.getResult()) ? true : false;
		testItemLog.append(APITestReportUtils.getScenarioDiv(responseItem.getScenarioName(), compareResult));
		APIScenarioResponse responseItem1 = responseItem.getResponse1();
		APIScenarioResponse responseItem2 = responseItem.getResponse2();
		if (responseItem1 == null || responseItem2 == null) {
			testItemLog.append(APITestReportUtils.getVerificationLog("Test Failed : Invalid Test Response ", false));
		} else {
			testItemLog.append(APITestReportUtils.createEmptyFolder("URL 1: ", responseItem1.getRequestUrl()));
			testItemLog.append(APITestReportUtils.createEmptyFolder("URL 2: ", responseItem2.getRequestUrl()));

			Object payload = responseItem1.getPayLoad();
			if (payload != null) {
				testItemLog.append(APITestReportUtils.getJsonLog("Payload 1: ", "", responseItem1.getPayloadString()));
			}
			payload = responseItem2.getPayLoad();
			if (payload != null) {
				testItemLog.append(APITestReportUtils.getJsonLog("Payload 2: ", "", responseItem2.getPayloadString()));
			}
			addPostResponseLog(responseItem1, testItemLog, 1);
			addPostResponseLog(responseItem2, testItemLog, 2);
			testItemLog.append(APITestReportUtils.getItemResultLog("Compare Result ", compareResult));
			testItemLog.append(APITestReportUtils.getVerificationLog(responseItem.getMismatchString(), compareResult));

			List<JsonMismatchField> mismatches = responseItem.getMismatches();
			if (!compareResult && CollectionUtils.isNotEmpty(mismatches)) {
				JsonMismatchField mismatch = null;

				for (int i = 0; i < mismatches.size(); i++) {
					mismatch = mismatches.get(i);
					String path = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (i + 1) + ". " + APITestUtils.getAbsoluteResponsePath(mismatch.getMismatchPath());
					String value = mismatch.getLeftValue() + "&nbsp;&nbsp:&nbsp;&nbsp" + mismatch.getRightValue();
					testItemLog.append(APITestReportUtils.getKeyValueLog(path, value));
				}
			}
			testItemLog.append(APITestReportUtils.endDiv());
		}
		testItemLog.append(APITestReportUtils.endDiv());

		return testItemLog;
	}

	public static StringBuilder generateHtmlLog(APIChainTestsResponseItem responseItem) {
		StringBuilder testItemLog = new StringBuilder();
		if (responseItem == null)
			return testItemLog;

		List<APIScenarioResponse> chainResp = responseItem.getTestChainResponse();

		if (CollectionUtils.isNotEmpty(chainResp)) {
			Iterator<APIScenarioResponse> respIter = chainResp.iterator();
			int count = 0;
			APIScenarioResponse stepResponseItem = respIter.next();
			testItemLog.append(APITestReportUtils.getScenarioDiv(stepResponseItem.getScenarioName(), ResultType.PASSED.equals(responseItem.getResult())));

			do {
				testItemLog.append(logChainStep(stepResponseItem, responseItem.getResult(), count));
				if (respIter.hasNext()) {
					stepResponseItem = respIter.next();
				} else {
					break;
				}
				count++;

			} while (true);

			for (int i = 0; i < count; i++) {
				testItemLog.append(APITestReportUtils.endUL());
			}

			testItemLog.append(APITestReportUtils.endDiv());
			testItemLog.append(APITestReportUtils.endDiv());
		}
		return testItemLog;
	}

	private static String logChainStep(APIScenarioResponse responseItem, ResultType chainResult, int count) {
		StringBuilder chainItemLog = new StringBuilder();
		if (responseItem == null)
			return chainItemLog.toString();

		String scenarioName = responseItem.getScenarioName();
		String displayName = responseItem.getDisplayName() == null ? scenarioName : responseItem.getDisplayName();

		try {
			String chainItemFolder = APITestReportUtils.createFolderForChain(ResultType.PASSED.equals(responseItem.getResult()),
					ResultType.PASSED.equals(chainResult), count);
			chainItemLog.append(chainItemFolder.replace("$4", "<b>" + displayName + "</b>"));
			chainItemLog.append(getResponseItemLog(responseItem));

			chainItemLog.append("<br/>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return chainItemLog.toString();
	}

	public static StringBuilder generateHtmlLog(APIChainCompareTestResponseItem chainCompareResponseItem) {

		StringBuilder testItemLog = new StringBuilder();
		if (chainCompareResponseItem == null)
			return testItemLog;

		String scenario = chainCompareResponseItem.getScenarioName();
		boolean result = ResultType.PASSED.equals(chainCompareResponseItem.getResult());
		testItemLog.append(APITestReportUtils.getScenarioDiv(scenario, result));

		List<APICompareTestsResponseItem> compareTestResponseItemList = chainCompareResponseItem.getChainCompareResponseList();
		if (CollectionUtils.isEmpty(compareTestResponseItemList)) {
			return testItemLog;
		}

		int count = 0;
		do {
			APICompareTestsResponseItem responseItem = compareTestResponseItemList.get(count);
			testItemLog.append(logCompareChainStep(responseItem, responseItem.getResult(), count));
			count++;

		} while (count < compareTestResponseItemList.size());

		for (int i = 0; i < count; i++) {
			testItemLog.append(APITestReportUtils.endUL());
		}

		testItemLog.append(APITestReportUtils.endDiv());
		testItemLog.append(APITestReportUtils.endDiv());
		return testItemLog;

	}

	public static StringBuilder generateChainCompareItemLog(APICompareTestsResponseItem responseItem, boolean compareResult) {
		StringBuilder testItemLog = new StringBuilder();
		if (responseItem == null)
			return testItemLog;

		APIScenarioResponse responseItem1 = responseItem.getResponse1();
		APIScenarioResponse responseItem2 = responseItem.getResponse2();
		if (responseItem1 == null || responseItem2 == null) {
			testItemLog.append(APITestReportUtils.getVerificationLog("Test Failed : Invalid Test Response ", false));
		} else {
			testItemLog.append(APITestReportUtils.createEmptyFolder("URL 1: ", responseItem1.getRequestUrl()));
			testItemLog.append(APITestReportUtils.createEmptyFolder("URL 2: ", responseItem2.getRequestUrl()));

			Object payload = responseItem1.getPayLoad();
			if (payload != null) {
				testItemLog.append(APITestReportUtils.getJsonLog("Payload 1: ", "", responseItem1.getPayloadString()));
			}
			payload = responseItem2.getPayLoad();
			if (payload != null) {
				testItemLog.append(APITestReportUtils.getJsonLog("Payload 2: ", "", responseItem2.getPayloadString()));
			}
			addPostResponseLog(responseItem1, testItemLog, 1);
			addPostResponseLog(responseItem2, testItemLog, 2);
			testItemLog.append(APITestReportUtils.getItemResultLog("Compare Result ", compareResult));
			testItemLog.append(APITestReportUtils.getVerificationLog(responseItem.getMismatchString(), compareResult));

			List<JsonMismatchField> mismatches = responseItem.getMismatches();
			if (CollectionUtils.isNotEmpty(mismatches)) {
				JsonMismatchField mismatch = null;

				for (int i = 0; i < mismatches.size(); i++) {
					mismatch = mismatches.get(i);
					String path = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (i + 1) + ". " + APITestUtils.getAbsoluteResponsePath(mismatch.getMismatchPath());
					String value = mismatch.getLeftValue() + "&nbsp;&nbsp:&nbsp;&nbsp" + mismatch.getRightValue();
					testItemLog.append(APITestReportUtils.getKeyValueLog(path, value));
				}
			}
		}

		return testItemLog;
	}

	private static String logCompareChainStep(APICompareTestsResponseItem responseItem, ResultType chainResult, int count) {
		StringBuilder chainItemLog = new StringBuilder();
		if (responseItem == null)
			return chainItemLog.toString();

		String scenarioName = responseItem.getScenarioName();
		String displayName = scenarioName;

		try {
			String chainItemFolder = APITestReportUtils.createFolderForChain(ResultType.PASSED.equals(responseItem.getResult()),
					ResultType.PASSED.equals(chainResult), count);
			chainItemLog.append(chainItemFolder.replace("$4", "<b>" + displayName + "</b>"));
			chainItemLog.append(generateChainCompareItemLog(responseItem, ResultType.PASSED.equals(responseItem.getResult())));
			chainItemLog.append("<br/>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return chainItemLog.toString();
	}

	private static String getValidatorLogs(List<APIValidation> validators) {
		StringBuilder validatorsLog = new StringBuilder();
		if (CollectionUtils.isEmpty(validators)) {
			return validatorsLog.append(APITestReportUtils.endUL()).toString();
		}

		Iterator<APIValidation> vals = validators.iterator();
		APIValidation validator = null;
		while (vals.hasNext()) {
			validator = vals.next();
			if (validator.getValidationResult()) {
				validatorsLog.append(APITestReportUtils.getVerificationLog(validator.toString(), true));
			} else {
				validatorsLog.append(APITestReportUtils.getVerificationLog(validator.toString(), false));
				List<JsonMismatchField> mismatches = validator.getDifferences();

				if (CollectionUtils.isNotEmpty(mismatches)) {
					JsonMismatchField mismatch = null;
					for (int i = 0; i < mismatches.size(); i++) {
						mismatch = mismatches.get(i);
						String path = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (i + 1) + " . " + APITestUtils.getAbsoluteResponsePath(mismatch.getMismatchPath());
						String value = mismatch.getLeftValue() + "&nbsp;&nbsp:&nbsp;&nbsp" + mismatch.getRightValue();
						validatorsLog.append(APITestReportUtils.getKeyValueLog(path, value));
					}
				}

			}
		}
		return validatorsLog.append(APITestReportUtils.endUL()).toString();
	}

	private static String getPrinterLogs(List<APIPrint> printers, int itemNumber) {
		StringBuilder printersLog = new StringBuilder();
		if (CollectionUtils.isEmpty(printers)) {
			return printersLog.toString();
		}
		String title = "Print Fields ";
		if (itemNumber > 0) {
			title += itemNumber;
		}
		printersLog.append(APITestReportUtils.createFolder(title, "", false));
		Iterator<APIPrint> prints = printers.iterator();
		APIPrint printer = null;
		while (prints.hasNext()) {
			printer = prints.next();
			String key = StringUtils.isBlank(printer.getPrintName()) ? APITestUtils.getAbsoluteResponsePath(printer.getResponsePath()) : printer.getPrintName();
			String value = printer.getResponseValue() == null ? "null" : printer.getResponseValue().toString();
			printersLog.append(APITestReportUtils.getKeyValueLog(key, value));
		}
		return printersLog.append(APITestReportUtils.endUL()).toString();
	}

	public static Object getJSONString(Object json) {
		if (json == null)
			return null;

		String str = json.toString();
		if (str.startsWith("<")) {
			return APIScenarioResponse.prettyPrintXml(json.toString());
		}

		return JSONObject.fromObject(json.toString());
	}
}
