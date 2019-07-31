package com.shc.automation.api.test.framework.internal.report;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.types.ObjectId;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import com.shc.automation.api.test.framework.entities.APIRequestParameter;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.TestType;

/**
 * @author spoojar
 *
 */
public class APITestReportUtils {
	private final static String SCENARIO_DIV = "<div class=\"$1 card separator stack stack-st1\" onclick=\"showDetails(\'$2\');\"><div class=\"right-col\">$3 <b>Test Scenario </b> </FONT> :<FONT COLOR=\"DarkBlue\"> <b> $4 </b></FONT></div>";
	private final static String SCENARIO_DETAILS_DIV = "<div id=\"$1\" class=\"right-col detailed-analysis-log card card-dark\" style=\"display: $2;width:96%;\" onclick=\"stopProp(event);\">";
	private final static String SCENARIO_RESULT_IMG = "<FONT COLOR=\"$1\" class=\"status-icon\" style=\"font-weight: bold;\" size=\"$2\">&nbsp;&nbsp;&#8730;&nbsp;&nbsp;</FONT><FONT COLOR=\"$1\">";

	private final static String OPENED_FOLDER = "<li><div id=\"Id_$1\" class=\"ExpandCollapse\"  onclick=\"takeAction(event);\">-</div><div class=\"Folder\" style=\"word-wrap:break-word;\"> <FONT COLOR=\"DarkBlue\">$2&nbsp;</FONT><FONT COLOR=\"#2B60DE\">$3</FONT></div></li><ul id=\"ExpandCollapseId_$1\">";
	private final static String CLOSED_FOLDER = "<li><div id=\"Id_$1\" class=\"ExpandCollapse\"  onclick=\"takeAction(event);\">+</div><div class=\"Folder\" style=\"word-wrap:break-word;\"> <FONT COLOR=\"DarkBlue\">$2&nbsp;</FONT><FONT COLOR=\"#2B60DE\">$3</FONT></div></li><ul id=\"ExpandCollapseId_$1\" style=\"display:none\">";
	private final static String EMPTY_FOLDER = "<li><div id=\"Id_$1\" class=\"ExpandCollapse\" \">-</div><div class=\"Folder\" style=\"word-wrap:break-word;\"> <FONT COLOR=\"DarkBlue\">$2&nbsp;</FONT><FONT COLOR=\"#2B60DE\">$3</FONT></div></li>";

	private final static String LOG_FOLDER_START_CHAIN = "<li style=\"background-color:#F5E68C;\"><div id=\"Id_$1\" class=\"ExpandCollapse\"  onclick=\"takeAction(event);\">-</div><div class=\"Folder\" style=\"word-wrap:break-word;\">$2<b>Step $3 >>> &nbsp;</b></FONT><FONT COLOR=\"#660343\">$4</FONT></div></li><ul id=\"ExpandCollapseId_$1\">";
	private final static String LOG_FOLDER_START_CHAIN_CLOSED = "<li style=\"background-color:#F5E68C;\"><div id=\"Id_$1\" class=\"ExpandCollapse\"  onclick=\"takeAction(event);\">+</div><div class=\"Folder\" style=\"word-wrap:break-word;\">$2<b>Step $3 >>> &nbsp;</b></FONT><FONT COLOR=\"#660343\">$4</FONT></div></li><ul id=\"ExpandCollapseId_$1\"  style=\"display:none\">";

	private final static String LOG_TITLELINE = "<li><FONT COLOR=\"PURPLE\">$1 : </FONT>&nbsp;&nbsp;<FONT COLOR=\"#2B60DE\">$2</FONT></li>";
	private final static String LOG_JSON = "<FONT COLOR=\"#2B60DE\">$1</FONT>";

	private final static String PASS_SIGN = "<FONT COLOR=\"GREEN\" style=\"font-weight: bold;\">&nbsp;&nbsp;&#8730;&nbsp;&nbsp;</FONT>";
	private final static String FAIL_SIGN = "<FONT COLOR=\"RED\" style=\"font-weight: bold;\">&nbsp;&nbsp;&#215;&nbsp;&nbsp;</FONT>";

	private final static String LOG_VALIDATION_FAIL = "<li><FONT COLOR=\"RED\">&#215;&nbsp;&nbsp;$1&nbsp;</FONT>&nbsp;&nbsp;<FONT COLOR=\"#2B60DE\">$2</FONT></li>";
	private final static String LOG_VALIDATION_PASS = "<li><FONT COLOR=\"GREEN\">&#8730;&nbsp;&nbsp;$1&nbsp;</FONT><FONT COLOR=\"#2B60DE\">$2</FONT></li>";

	private final static String END_TEST_FOLDER = "</div>";
	private final static String END_ITEM_FOLDER = "</ul>";

	public final static String START_TEST_FOLDER = "<div id=\"apiTestResponseContentId\" class=\"outer\">";

	protected static final String SUMMARY_TABLE = "<br/><table id=\"summaryId\" width=\"50%\" ><thead><tr><th>Total Scenarios</th><th style=\"background-color:#C5D88A\" >Passed</th><th style=\"background-color:#D88A8A\">Failed</th></tr></thead><tbody><tr><td><b>$1</b></td><td><b>$2</b></td><td><b>$3</b></td></tr><tbody></table>";
	protected static final String MISMATCH_FREQUENCY_TABLE = "<br/><br/><table id=\"reportId\" width=\"51%\"><tr><td><a href=\"javascript:showFrequency();\">Mismatch Table</a></td></tr><tr><td><table id=\"FrequencyTable\" align=\"center\" style=\"display: none;\"><thead><tr style=\"background-color:#335CD6;color:white;\"><th>Field Name</th><th>No. of Occurances</th></tr></thead>";

	protected static final String REPORT_FORMAT_VIEW_DROPDOWN = "<div id=\"action\" align=\"center\"><strong class=\"aStyle\">Report Format &nbsp;&nbsp; </strong> <select id=\"$1\" class=\"aSelect\"><option value=\"None\">Select One</option><option value=\"htmlFormat\">Html</option><option value=\"excelFormat\">Excel</option></select></div>";

	private static String CSS = null;
	private static String JS = null;

	private static void loadJSAndCSS() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(APITestReportUtils.class.getClassLoader().getResourceAsStream("api.css")));
			String line = null;
			StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			CSS = builder.toString();
			reader.close();
			reader = new BufferedReader(new InputStreamReader(APITestReportUtils.class.getClassLoader().getResourceAsStream("api.js")));
			builder = new StringBuilder();
			line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			JS = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}

	protected static String startTestFolder() {
		return START_TEST_FOLDER;
	}

	protected static String getScenarioDiv(String scenario, Boolean result) {
		scenario = scenario == null ? "" : scenario;
		String id = getElementId();
		StringBuilder scenarioDiv = new StringBuilder();
		if (result) {
			String passScenarioImg = SCENARIO_RESULT_IMG.replace("$1", "GREEN").replace("$2", "3");
			scenarioDiv.append(SCENARIO_DIV.replace("$1", "passed").replace("$2", id).replace("$3", passScenarioImg).replace("$4", scenario));
			scenarioDiv.append(SCENARIO_DETAILS_DIV.replace("$1", id).replace("$2", "none"));
		} else {
			String failScenarioImg = SCENARIO_RESULT_IMG.replace("$1", "RED").replace("$2", "3");
			scenarioDiv.append(SCENARIO_DIV.replace("$1", "failed").replace("$2", id).replace("$3", failScenarioImg).replace("$4", scenario));
			scenarioDiv.append(SCENARIO_DETAILS_DIV.replace("$1", id).replace("$2", "inline-block"));
		}

		return scenarioDiv.toString();
	}

	protected static String getJsonLog(String title, String message, Object jsonObject) {
		String json = jsonObject.toString();
		if (json.startsWith("<")) {
			json = "<xmp>" + json + "</xmp>";
		} else {
			json = "<pre>" + json + "</pre>";
		}
		StringBuilder jsonLog = createFolder(title, message, true);
		jsonLog.append(LOG_JSON.replace("$1", json));
		jsonLog.append(endUL());

		return jsonLog.toString();
	}

	protected static String getVerificationLog(String message, Boolean result) {
		StringBuilder verificationLog = new StringBuilder();
		if (result)
			verificationLog.append(LOG_VALIDATION_PASS.replace("$1", "Verification Passed").replace("$2", message));
		else
			verificationLog.append(LOG_VALIDATION_FAIL.replace("$1", "Verification Failed").replace("$2", message));

		return verificationLog.toString();
	}

	protected static String getURLParamLog(List<APIRequestParameter> paramList) {
		StringBuilder paramLog = new StringBuilder();
		if (CollectionUtils.isEmpty(paramList)) {
			return paramLog.toString();
		}

		paramLog.append(createFolder("URL Parameters :", "", false));
		Iterator<APIRequestParameter> iter = paramList.iterator();

		while (iter.hasNext()) {
			APIRequestParameter param = iter.next();
			String key = param.getParamName();
			String value = param.getParamValue() == null ? "" : param.getParamValue().toString();
			paramLog.append(getKeyValueLog(key, value));
		}
		paramLog.append(endUL());

		return paramLog.toString();
	}

	protected static String getResponseContentLog(boolean printJson, long executionTime, Object responseContent) {
		StringBuilder responseContentLog = new StringBuilder();

		if (printJson && responseContent != null) {
			responseContentLog.append(getJsonLog("Response : ", executionTime + "(ms)", responseContent));
			// responseContentLog.append(endUL());
		} else {
			responseContentLog.append(createEmptyFolder("Response : ", executionTime + "(ms)"));
		}

		return responseContentLog.toString();
	}

	protected static StringBuilder getItemResultLog(String title, Boolean result) {
		return createFolder(title, (result ? PASS_SIGN : FAIL_SIGN), false);
	}

	protected static String getResponseCodeVerificationLog(APITestResponseItem responseItem) {
		String message = null;
		boolean result = true;

		if (responseItem.getValidResponse()) {
			message = "Valid HTTP Response Code :" + responseItem.getResponseCode();
		} else {
			message = "Invalid HTTP Response Code :" + responseItem.getResponseCode();
			result = false;
		}

		return getVerificationLog(message, result);
	}

	protected static String getKeyValueLog(String key, String value) {
		StringBuilder keyValLog = new StringBuilder();
		keyValLog.append(LOG_TITLELINE.replace("$1", key).replace("$2", value));
		return keyValLog.toString();
	}

	protected static StringBuilder createFolder(String title, String message, Boolean showClosed) {
		StringBuilder folderDiv = new StringBuilder();
		if (showClosed)
			folderDiv.append(CLOSED_FOLDER.replace("$1", UUID.randomUUID().toString()).replace("$2", title).replace("$3", message));
		else
			folderDiv.append(OPENED_FOLDER.replace("$1", UUID.randomUUID().toString()).replace("$2", title).replace("$3", message));

		return folderDiv;
	}

	protected static String createFolderForChain(Boolean itemResult, Boolean chainResult, int count) {
		String folderSelection = LOG_FOLDER_START_CHAIN;
		String resultFormatForStep = "";

		if (itemResult) {
			if (chainResult && count > 0) {
				folderSelection = LOG_FOLDER_START_CHAIN_CLOSED;
			}
			resultFormatForStep = SCENARIO_RESULT_IMG.replace("$1", "GREEN").replace("$2", "2");
		} else {
			resultFormatForStep = SCENARIO_RESULT_IMG.replace("$1", "RED").replace("$2", "2");
		}
		return folderSelection.replace("$1", UUID.randomUUID().toString()).replace("$2", resultFormatForStep).replace("$3", String.valueOf(count + 1));
	}

	protected static StringBuilder getResultLog(String title, Boolean result) {
		return createEmptyFolder(title, (result ? PASS_SIGN : FAIL_SIGN));
	}

	protected static StringBuilder createEmptyFolder(String title, String message) {
		return new StringBuilder(EMPTY_FOLDER.replace("$1", UUID.randomUUID().toString()).replace("$2", title).replace("$3", message));

	}

	public static StringBuilder endDiv() {
		return new StringBuilder(END_TEST_FOLDER);
	}

	protected static StringBuilder endUL() {
		return new StringBuilder(END_ITEM_FOLDER);
	}

	protected static String getReportFormatDropDown(ObjectId recordId) {
		return REPORT_FORMAT_VIEW_DROPDOWN.replace("$1", recordId.toString());
	}

	private static String getElementId() {
		return UUID.randomUUID().toString();
	}

	protected String getFormattedMessage(String message) {
		message = removeNonUtf8CompliantCharacters(message);
		return message;
	}

	private String removeNonUtf8CompliantCharacters(final String inString) {
		if (null == inString)
			return null;
		byte[] byteArr = inString.getBytes();
		for (int i = 0; i < byteArr.length; i++) {
			byte ch = byteArr[i];
			if (!((ch > 31 && ch < 253) || ch == '\t')) {
				byteArr[i] = ' ';
			}
		}
		return new String(byteArr);
	}

	public static String getMismatchFrequencyHtml(Map<String, Integer> mismatchFrequencyMap) {
		if (MapUtils.isEmpty(mismatchFrequencyMap))
			return "";

		StringBuilder frequencyTable = new StringBuilder(MISMATCH_FREQUENCY_TABLE);
		final List<String> sortedKeys = Ordering.natural().onResultOf(Functions.forMap(mismatchFrequencyMap))
				.immutableSortedCopy(mismatchFrequencyMap.keySet());
		frequencyTable.append("<tbody>");
		for (int i = sortedKeys.size() - 1; i >= 0; i--) {
			String path = sortedKeys.get(i);
			frequencyTable.append("<tr><td>").append(path).append("</td><td>").append(mismatchFrequencyMap.get(path)).append("</td></tr>");
		}
		frequencyTable.append("</tbody></table></td></tr></table>");
		return frequencyTable.toString();
	}

	public static String getSummary(String desc, TestType testType, int totalItems, int failed) {
		if (JS == null || CSS == null) {
			loadJSAndCSS();
		}
		String javascript = JS.replace("APITestDescriptionValue", desc).replace("APITestTypeValue", testType.toString());

		String summaryTab = APITestReportUtils.SUMMARY_TABLE;
		int passed = totalItems - failed;
		summaryTab = summaryTab.replace("$1", String.valueOf(totalItems));
		summaryTab = summaryTab.replace("$2", String.valueOf(passed));
		summaryTab = summaryTab.replace("$3", String.valueOf(failed));

		StringBuilder summary = new StringBuilder(CSS);
		summary.append(javascript);
		summary.append(summaryTab);
		summary.append("<br/><br/>");

		return summary.toString();
	}
}
