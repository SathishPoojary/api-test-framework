package com.shc.automation.api.test.framework.internal.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class APIExternalDataProcessor {

	public static Map<String, Map<String, Object>> updateExternalParams(APITestRequest request, Map<String, Map<String, Object>> sourceRecords) {
		Map<String, Map<String, Object>> externalUrlParams = request.getExternalUrlParams();
		if (MapUtils.isNotEmpty(externalUrlParams)) {
			if (sourceRecords == null) {
				return externalUrlParams;
			}
			Set<String> scenarios = externalUrlParams.keySet();
			Iterator<String> scenarioIter = scenarios.iterator();
			Map<String, Object> record = null;
			while (scenarioIter.hasNext()) {
				String scenario = scenarioIter.next();
				record = externalUrlParams.get(scenario);
				if (sourceRecords.containsKey(scenario)) {
					sourceRecords.get(scenario).putAll(record);
				} else {
					sourceRecords.put(scenario, record);
				}
			}
		}

		return sourceRecords;
	}

	public static Map<String, Map<String, Object>> updateExternalPayloads(APITestRequest request, Map<String, Map<String, Object>> payloadRecords) {
		Map<String, String> externalPayloads = request.getExternalPayload();
		if (MapUtils.isNotEmpty(externalPayloads)) {
			if (payloadRecords == null) {
				payloadRecords = new HashMap<String, Map<String, Object>>(externalPayloads.size());
			}

			Set<String> scenarios = externalPayloads.keySet();
			Iterator<String> scenariosIter = scenarios.iterator();
			Map<String, Object> record = null;
			Object payload = null;
			while (scenariosIter.hasNext()) {
				String scenario = scenariosIter.next();
				payload = externalPayloads.get(scenario);
				record = new HashMap<String, Object>();
				if (payload != null) {
					if (payload.toString().startsWith("{")) {
						payload = JSONObject.fromObject(payload);
					}
					if (payload.toString().startsWith("[")) {
						payload = JSONArray.fromObject(payload);
					}
				}
				record.put(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER, payload);
				if (payloadRecords.containsKey(scenario)) {
					payloadRecords.get(scenario).putAll(record);
				} else {
					payloadRecords.put(scenario, record);
				}
			}
		}
		return payloadRecords;
	}
}
