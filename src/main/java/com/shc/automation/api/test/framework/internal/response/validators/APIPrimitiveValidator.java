/**
 *
 */
package com.shc.automation.api.test.framework.internal.response.validators;

import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.ValidationType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author spoojar
 *
 */
public class APIPrimitiveValidator implements APIValidator {

    @Override
    public APIValidation validate(APIValidation validator) {
        if (validator == null) {
            return validator;
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
            valid = isValid(actual, expected);
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
        return validator;
    }

    private boolean isValid(String actual, String expected) {
        boolean valid = true;
        if (actual.equals(expected)) {
            if (StringUtils.isEmpty(expected) || actual.indexOf(expected) != -1) {
                valid = false;
            }
        }
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
