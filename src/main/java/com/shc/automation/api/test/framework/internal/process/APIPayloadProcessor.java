/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.entities.APIDataSourceType;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonUtils;

/**
 * @author spoojar
 *
 */
public class APIPayloadProcessor {

	public void updatePayload(APITestRequest request, APITestRequestItem requestItem, Map<String, Object> payloadRecord) {
		String payload = request.getPayloadInputSource() == null ? null : request.getPayloadInputSource().getPayloadTemplate();
		Map<String, Object> context = new HashMap<String, Object>(requestItem.getScenarioContext());

		if (StringUtils.isNotBlank(payload)) {
			if (MapUtils.isNotEmpty(payloadRecord)) {
				context.putAll(payloadRecord);

			}
		} else {
			Object payRecord = null;
			if (MapUtils.isNotEmpty(payloadRecord)) {
				String payloadKey = getPayloadKey(request);
				payRecord = APITestUtils.getValueFromRecord(payloadKey, payloadRecord);				
			}
			payload = payRecord == null ? request.getPayLoad() : payRecord.toString();
		}

		if (StringUtils.isNotBlank(payload)) {
			payload = APITestUtils.replaceValuesInTemplate(payload, context);
		}

		payload = transform(request.getPayLoadType(), payload);
		requestItem.setPayload(payload);
	}

	private String transform(String payloadType, String payload) {
		if ("xml".equalsIgnoreCase(payloadType)) {
			payload = JsonUtils.convertJsonToXML(payload);
		}
		return payload;
	}

	private String getPayloadKey(APITestRequest request) {
		APITestInputSource payloadSrc = request.getPayloadInputSource();
		if (payloadSrc == null)
			return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER;
		if (StringUtils.isEmpty(payloadSrc.getSourcePath()))
			return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER;
		if (APIDataSourceType.mongo.toString().equalsIgnoreCase(payloadSrc.getSourceType()))
			return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER + "." + payloadSrc.getSourcePath();

		return payloadSrc.getSourcePath();

	}
}
