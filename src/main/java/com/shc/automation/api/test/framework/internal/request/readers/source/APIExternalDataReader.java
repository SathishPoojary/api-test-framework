package com.shc.automation.api.test.framework.internal.request.readers.source;

import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class APIExternalDataReader {

    public Map<String, Map<String, Object>> updateExternalParams(APIRequest request, Map<String, Map<String, Object>> sourceRecords) {
        Map<String, Map<String, Object>> externalUrlParams = request.getExternalUrlParams();
        if (MapUtils.isNotEmpty(externalUrlParams)) {
            if (sourceRecords == null) {
                return externalUrlParams;
            }
            Set<String> scenarios = externalUrlParams.keySet();
            Iterator<String> scenarioIter = scenarios.iterator();
            Map<String, Object> record;
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

    public Map<String, Map<String, Object>> updateExternalPayloads(APIRequest request, Map<String, Map<String, Object>> payloadRecords) {
        Map<String, String> externalPayloads = request.getExternalPayload();
        if (MapUtils.isNotEmpty(externalPayloads)) {
            if (payloadRecords == null) {
                payloadRecords = new HashMap<>(externalPayloads.size());
            }

            Set<String> scenarios = externalPayloads.keySet();
            Iterator<String> scenariosIter = scenarios.iterator();
            Map<String, Object> record;
            Object payload;
            while (scenariosIter.hasNext()) {
                String scenario = scenariosIter.next();
                payload = externalPayloads.get(scenario);
                record = new HashMap<>();
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
