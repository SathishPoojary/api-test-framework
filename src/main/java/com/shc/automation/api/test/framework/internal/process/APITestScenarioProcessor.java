/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author sathish_poojary
 * 
 */
public class APITestScenarioProcessor {

	public static String generateScenarioNameFromURLInputRecord(List<String> scenarioFieldList, Map<String, Object> paramMap, int scenarioIndex) {
		if (CollectionUtils.isEmpty(scenarioFieldList)) {
			return String.valueOf(scenarioIndex == -1 ? 1 : scenarioIndex);
		}

		StringBuilder scenario = new StringBuilder();
		Iterator<String> scenarioIter = scenarioFieldList.iterator();
		String scenarioFieldName = null;
		boolean containsField = false;
		while (scenarioIter.hasNext()) {
			scenarioFieldName = scenarioIter.next();
			if (paramMap.containsKey(scenarioFieldName)) {
				containsField = true;
				scenario.append(paramMap.get(scenarioFieldName).toString());
			}
			scenario.append(" | ");
		}

		if (containsField) {
			return scenario.substring(0, scenario.length() - 3);
		} else {
			if (scenarioIndex == -1)
				return scenarioFieldList.get(0);
			else
				return scenarioFieldList.get(0) + " " + String.valueOf(scenarioIndex);
		}

	}

	public static String generateScenarioNameFromURLQueryString(List<String> scenarioFieldList, String url, int scenarioIndex) {
		if (CollectionUtils.isEmpty(scenarioFieldList)) {
			return String.valueOf(scenarioIndex == -1 ? 1 : scenarioIndex);
		}

		StringBuilder scenario = new StringBuilder();
		Iterator<String> scenarioIter = scenarioFieldList.iterator();
		String scenarioFieldName = null;
		boolean containsField = false;
		while (scenarioIter.hasNext()) {
			scenarioFieldName = scenarioIter.next();
			int index = url.indexOf(scenarioFieldName + "=");
			if (index != -1) {
				containsField = true;
				int endIndex = url.indexOf('&', index);
				if (endIndex != -1)
					scenario.append(url.substring(index + scenarioFieldName.length() + 1, endIndex));
				else
					scenario.append(url.substring(index + scenarioFieldName.length() + 1));
			}
			scenario.append(" | ");
		}
		if (containsField) {
			return scenario.substring(0, scenario.length() - 3);
		} else {
			if (scenarioIndex == -1)
				return scenarioFieldList.get(0);
			else
				return scenarioFieldList.get(0) + " " + String.valueOf(scenarioIndex);
		}
	}

	public static String generateScenarioNameFromPayloadInputRecord(List<String> scenarioFieldList, Object payload, int scenarioIndex) {
		if (CollectionUtils.isEmpty(scenarioFieldList)) {
			return String.valueOf(scenarioIndex == -1 ? 1 : scenarioIndex);
		}

		StringBuilder scenario = new StringBuilder();
		Iterator<String> scenarioIter = scenarioFieldList.iterator();
		String scenarioFieldName = null;
		Object value = null;
		boolean containsField = false;
		while (scenarioIter.hasNext()) {
			scenarioFieldName = scenarioIter.next();
			value = APITestUtils.readFromJSON(payload, scenarioFieldName, false);

			if (value != null) {
				containsField = true;
				scenario.append(value.toString());
			}
			scenario.append(" | ");
		}
		if (containsField) {
			return scenario.substring(0, scenario.length() - 3);
		} else {
			if (scenarioIndex == -1)
				return scenarioFieldList.get(0);
			else
				return scenarioFieldList.get(0) + " " + String.valueOf(scenarioIndex);
		}
	}

}
