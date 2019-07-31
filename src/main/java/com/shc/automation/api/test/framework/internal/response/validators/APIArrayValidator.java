/**
 * 
 */
package com.shc.automation.api.test.framework.internal.response.validators;

import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.ValidationType;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareOption;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author spoojar
 *
 */
public class APIArrayValidator implements APIValidator {

	@Override
	public APIValidation validate(APIValidation validator) {
		if (validator == null) {
			return validator;
		}

		boolean validResponse = true;
		ValidationType validationType = validator.getValidationType();
		if (validationType == ValidationType.EMPTY) {
			if (!isEmptyValue(validator.getActualResponseValue()))
				validResponse = false;
		}
		if (validationType == ValidationType.NOT_EMPTY) {
			if (isEmptyValue(validator.getActualResponseValue()))
				validResponse = false;
		}
		if (validationType == ValidationType.CONTAINS_NODE) {
			if (isEmptyValue(validator.getActualResponseValue()))
				validResponse = false;
		}
		if (validationType == ValidationType.NOT_CONTAINS_NODE) {
			if (!isEmptyValue(validator.getActualResponseValue()))
				validResponse = false;
		}

		if (validResponse) {
			List<String> actual = new ArrayList<String>();
			if (!isEmptyValue(validator.getActualResponseValue())) {
				String actualStr = validator.getActualResponseValue().toString();
				actualStr = actualStr.trim().substring(1, actualStr.length() - 1);
				actualStr = escape(actualStr);
				actual = APITestUtils.getListFromString(actualStr);
			}

			List<String> expected = new ArrayList<String>();
			String expectedStr = (validator.getExpectedResponseValue() == null ? "" : validator.getExpectedResponseValue().toString());
			if (expectedStr.startsWith("[") && expectedStr.endsWith("]")) {
				expectedStr = expectedStr.trim().substring(1, expectedStr.length() - 1);
			}
			if (StringUtils.isNotBlank(expectedStr)) {
				expectedStr = escape(expectedStr);
				expected = APITestUtils.getListFromString(expectedStr);
			}
			if (validator.getCompareOption() == JsonCompareOption.IGNORE_ARRAY_ORDER) {
				Collections.sort(actual);
				Collections.sort(expected);
			}

			if (validationType == ValidationType.EQUALS) {
				validResponse = actual.equals(expected);
			}
			if (validationType == ValidationType.NOT_EQUALS) {
				validResponse = !actual.equals(expected);
			}
			if (validationType == ValidationType.CONTAINS_VALUE) {
				validResponse = actual.containsAll(expected);
			}
			if (validationType == ValidationType.NOT_CONTAINS_VALUE) {
				validResponse = !actual.containsAll(expected);
			}
		}

		validator.setValidationResult(validResponse);
		validator.updateValidationMessage();
		return validator;

	}

	@SuppressWarnings("rawtypes")
	private Boolean isEmptyValue(Object actualResponseValue) {
		boolean blankNode = false;
		if (actualResponseValue == null)
			blankNode = true;
		else if (actualResponseValue instanceof List && CollectionUtils.isEmpty((List) actualResponseValue))
			blankNode = true;

		return blankNode;
	}

	private String escape(String str) {
		str = str.replaceAll("\"", "");
		str = str.replaceAll("\\\\", "");

		return str;
	}
}
