/**
 * 
 */
package com.shc.automation.api.test.framework.internal.validators;

import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.ValidationType;

/**
 * @author spoojar
 *
 */
public class APIPrimitiveValidator {

	public Boolean validate(APIValidationField validator) {
		if (validator == null) {
			return true;
		}

		String actual = (validator.getActualResponseValue() == null ? "" : validator.getActualResponseValue().toString());
		String expected = (validator.getExpectedResponseValue() == null ? "" : validator.getExpectedResponseValue().toString());
		ValidationType validationType = validator.getValidationType();

		boolean valid = true;
		if (validationType == ValidationType.EMPTY) {
			if (!isEmptyValue(actual))
				valid = false;
		}
		if (validationType == ValidationType.EQUALS) {
			try {
				if (Double.parseDouble(actual) != Double.parseDouble(expected)) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				if (!actual.equals(expected))
					valid = false;
			}
		}
		if (validationType == ValidationType.CONTAINS_NODE) {
			if (isBlankNode(validator.getActualResponseValue()))
				valid = false;
		}
		if (validationType == ValidationType.NOT_EMPTY) {
			if (isEmptyValue(actual))
				valid = false;
		}
		if (validationType == ValidationType.NOT_EQUALS) {
			try {
				if (Double.parseDouble(actual) == Double.parseDouble(expected)) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				if (actual.equals(expected))
					valid = false;
			}
		}
		if (validationType == ValidationType.NOT_CONTAINS_NODE) {
			if (!isBlankNode(validator.getActualResponseValue()))
				valid = false;
		}
		if (validationType == ValidationType.CONTAINS_VALUE) {
			if (!actual.equals(expected)) {
				if (StringUtils.isEmpty(expected) || (actual.indexOf(expected) == -1))
					valid = false;
			}

		}
		if (validationType == ValidationType.NOT_CONTAINS_VALUE) {
			if (actual.equals(expected)) {
				if (StringUtils.isEmpty(expected) || actual.indexOf(expected) != -1) {
					valid = false;
				}
			}
		}
		if (validationType == ValidationType.NOT_CONTAINS_VALUE) {
			if (actual.equals(expected)) {
				if (StringUtils.isEmpty(expected) || actual.indexOf(expected) != -1) {
					valid = false;
				}
			}
		}
		if (validationType == ValidationType.GREATER_THAN) {
			try {
				if (Double.parseDouble(actual) <= Double.parseDouble(expected)) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				valid = false;
			}
		}
		if (validationType == ValidationType.LESSER_THAN) {
			try {
				if (Double.parseDouble(actual) >= Double.parseDouble(expected)) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				valid = false;
			}
		}

		validator.setValidationResult(valid);
		validator.updateValidationMessage();
		return valid;
	}

	private Boolean isEmptyValue(String actualResponseValue) {
		if (StringUtils.isBlank(actualResponseValue))
			return true;
		return false;
	}

	private Boolean isBlankNode(Object actualResponseValue) {
		if (actualResponseValue == null)
			return true;
		return false;
	}
}
