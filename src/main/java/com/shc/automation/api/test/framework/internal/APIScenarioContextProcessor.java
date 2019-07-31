/**
 *
 */
package com.shc.automation.api.test.framework.internal;

import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author spoojar
 *
 */
public class APIScenarioContextProcessor {

    public void processScenarioContextFromTestData(APIRequest request,
                                                   APIScenarioRequest scenarioRequest,
                                                   Map<String, Object> urlRecord,
                                                   Map<String, Object> payloadRecord,
                                                   Map<String, Object> validationRecord) {
        processScenarioContextFromTestData(request, scenarioRequest.getScenarioContext(), urlRecord, request.getUrlParamInputSource());
        processScenarioContextFromTestData(request, scenarioRequest.getScenarioContext(), payloadRecord, request.getPayloadInputSource());
        processScenarioContextFromTestData(request, scenarioRequest.getScenarioContext(), validationRecord, request.getValidationInputSource());
    }

    private void processScenarioContextFromTestData(APIRequest request,
                                                   Map<String, Object> scenarioContext,
                                                   Map<String, Object> record,
                                                   APITestDataSource inputSource) {
        if (MapUtils.isEmpty(record)) {
            return;
        }
        List<APIPrint> context = request.getContextFields();
        if (CollectionUtils.isEmpty(context)) {
            return;
        }
        String inputSourceName = (inputSource == null ? APITestConstants.API_INPUT_SOURCE : inputSource.getSourceName());

        scenarioContext.putAll(
                context.stream()
                        .filter(field -> inputSourceName.equalsIgnoreCase(field.getSource()))
                        .collect(Collectors.toMap(
                                field -> field.getPrintName(),
                                field -> APITestUtils.getValueFromRecord(field.getResponsePath(), record)
                        )));
    }

    public void processScenarioContextFromResponse(APIRequest request, APIScenarioResponse response) {
        Iterator<APIPrint> iter = request.getContextFields().iterator();
        while (iter.hasNext()) {
            APIPrint printField = iter.next();
            if (APITestConstants.API_RESPONSE_SOURCE.equals(printField.getSource())) {
                String path = printField.getResponsePath();
                Object object = APITestUtils.readFromJSON(response.getResponseContent(), path, true);
                if (object != null) {
                    response.addContext(printField.getPrintName(), object);
                }
            }
            if (APITestConstants.API_HEADER_SOURCE.equals(printField.getSource())) {
                String path = printField.getResponsePath();
                Object object = response.getResponseHeaders().get(path);
                if (object != null) {
                    response.addContext(printField.getPrintName(), object);
                }
            }
        }
    }

    public void updateValidationsFromScenarioContext(APIScenarioResponse response) {
        Map<String, Object> context = response.getContext();
        if (MapUtils.isEmpty(context)) {
            return;
        }
        List<APIValidation> validators = response.getValidators();
        if (CollectionUtils.isEmpty(validators)) {
            return;
        }
        List<String> keyList = context.keySet().stream().sorted(Comparator.comparing(String::length)).collect(Collectors.toList());

        Iterator<String> keys = keyList.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = context.get(key) == null ? "" : context.get(key).toString();
            for (APIValidation validation : validators) {
                validation.setValidationCondition(replaceWithContextValue(validation.getValidationCondition(), key, value));
                validation.setExpression(replaceWithContextValue(validation.getExpression(), key, value));
                Object expected = validation.getExpectedResponseValue();
                if (expected != null) {
                    validation.setExpectedResponseValue(replaceWithContextValue(expected.toString(), key, value));
                }
            }
        }
    }

    private String replaceWithContextValue(String validation, String key, String value) {
        if (StringUtils.isEmpty(validation)) {
            return validation;
        }
        if (validation.contains(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key)) {
            return validation.replace(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key, value);
        }
        return validation;
    }
}
