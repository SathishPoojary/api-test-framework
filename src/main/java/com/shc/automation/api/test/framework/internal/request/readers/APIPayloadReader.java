/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers;

import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.model.request.APIDataSourceType;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APIPayloadReader {

    public String create(APIRequest request, Map<String, Object> scenarioContext, Map<String, Object> payloadRecord) {
        Object payRecord = null;
        if (MapUtils.isNotEmpty(payloadRecord)) {
            String payloadKey = getPayloadKey(request);
            payRecord = APITestUtils.getValueFromRecord(payloadKey, payloadRecord);
        }
        String payloadTemplate = payRecord == null ? request.getPayLoad() : payRecord.toString();

        String payload = "";
        if (StringUtils.isNotBlank(payloadTemplate)) {
            payload = APITestUtils.replaceValuesInTemplate(payloadTemplate, scenarioContext);
            payload = transform(request.getPayLoadType(), payload);

        }
        return payload;
    }

    private String transform(String payloadType, String payload) {
        if ("xml".equalsIgnoreCase(payloadType)) {
            payload = JsonUtils.convertJsonToXML(payload);
        }
        return payload;
    }

    private String getPayloadKey(APIRequest request) {
        APITestDataSource payloadSrc = request.getPayloadInputSource();
        if (payloadSrc == null)
            return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER;
        if (StringUtils.isEmpty(payloadSrc.getSourcePath()))
            return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER;
        if (APIDataSourceType.mongo.toString().equalsIgnoreCase(payloadSrc.getSourceType()))
            return APITestConstants.API_JSON_BASE_PATH_IDENTIFIER + "." + payloadSrc.getSourcePath();

        return payloadSrc.getSourcePath();

    }
}
