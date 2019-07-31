/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.entities.APIPrintField;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author spoojar
 *
 */
public class APIContextHelper {

	public void updateContextFromInput(APITestRequest request, Map<String, Object> scenarioContext, Map<String, Object> record,
			APITestInputSource inputSource) {
		if (MapUtils.isEmpty(record)) {
			return;
		}
		List<APIPrintField> context = request.getContextFields();
		if (CollectionUtils.isEmpty(context)) {
			return;
		}
		String inputSourceName = (inputSource == null ? "" : inputSource.getSourceName());
		for (int i = 0; i < context.size(); i++) {
			APIPrintField field = context.get(i).getCopy();
			String source = field.getSource();

			if (inputSourceName.equalsIgnoreCase(source) || APITestConstants.API_INPUT_SOURCE.equals(source)) {
				String path = field.getResponsePath();
				Object value = APITestUtils.getValueFromRecord(path, record);
				if (value != null) {
					scenarioContext.put(field.getPrintName(), value);

				}
			}
		}

	}

	public void updateContextValuesFromResponse(APITestRequest request, APITestResponseItem response) {
		Iterator<APIPrintField> iter = request.getContextFields().iterator();
		while (iter.hasNext()) {
			APIPrintField printField = iter.next();
			if (StringUtils.isEmpty(printField.getSource()) || APITestConstants.API_RESPONSE_SOURCE.equals(printField.getSource())) {
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

	public void updateValidationConditionsFromContext(APITestResponseItem response) {
		Map<String, Object> context = response.getContext();
		if (MapUtils.isEmpty(context)) {
			return;
		}
		List<APIValidationField> validators = response.getValidators();
		if (CollectionUtils.isEmpty(validators)) {
			return;
		}
		List<String> keyList = context.keySet().stream().sorted(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}

		}).collect(Collectors.toList());

		Iterator<String> keys = keyList.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			for (APIValidationField validation : validators) {
				String condition = validation.getValidationCondition();
				if (StringUtils.isNotBlank(condition) && condition.toString().indexOf(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key) > -1) {
					String value = context.get(key) == null ? null : context.get(key).toString();
					condition = condition.replace(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key, value);
					validation.setValidationCondition(condition);
				}
				String expression = validation.getExpression();
				if (StringUtils.isNotBlank(expression) && expression.toString().indexOf(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key) > -1) {
					String value = context.get(key) == null ? null : context.get(key).toString();
					expression = expression.replace(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key, value);
					validation.setExpression(expression);
				}
				Object expected = validation.getExpectedResponseValue();
				if (expected != null && expected.toString().contains(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key)) {
					String value = context.get(key) == null ? "" : context.get(key).toString();
					String expStr = expected.toString().replace(APITestConstants.API_CONTEXT_IDENTIFIER_PREFIX + key, value);
					validation.setExpectedResponseValue(expStr);
				}
			}
		}
	}
}
