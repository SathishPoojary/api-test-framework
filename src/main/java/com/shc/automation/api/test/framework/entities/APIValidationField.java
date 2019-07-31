package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shc.automation.api.test.framework.internal.APIMessageFactory;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareOption;
import com.shc.automation.utils.json.JsonMismatchField;

public class APIValidationField implements Serializable {

	private static final long serialVersionUID = -3949004722945355249L;

	private String validationName;
	private String responsePath;
	private Object expectedResponseValue;
	private String expression;
	private Object actualResponseValue;
	private ValidationType validationType = ValidationType.NOT_EMPTY;
	private Boolean validationResult = Boolean.TRUE;
	private OnValidationFailureOption validationFailureOption = OnValidationFailureOption.MARK_TEST_FAILED;
	private String validationMessage;
	private String messageId = null;

	private List<String> excludes;
	private List<String> arrayPathListToIgnoreOrder = null;
	private JsonCompareOption compareOption = null;
	private List<JsonMismatchField> differences = null;

	private String validationCondition;

	public APIValidationField(String fieldName, String fieldPath, String desiredValue, ValidationType validationType) {
		this.validationName = fieldName;
		this.responsePath = fieldPath;
		this.expectedResponseValue = desiredValue;
		this.validationType = validationType;
	}

	public APIValidationField(String fieldName, String fieldPath, ValidationType validationType) {
		this.setValidationName(fieldName);
		this.responsePath = fieldPath;
		this.validationType = validationType;
	}

	public Boolean getValidationResult() {
		return validationResult;
	}

	public void setValidationResult(Boolean validationResult) {
		this.validationResult = validationResult;
	}

	public Object getExpectedResponseValue() {
		return expectedResponseValue;
	}

	public void setExpectedResponseValue(Object desiredFieldValue) {
		this.expectedResponseValue = desiredFieldValue;
	}

	public ValidationType getValidationType() {
		return validationType;
	}

	public void setValidationType(ValidationType validationType) {
		this.validationType = validationType;
	}

	public String getValidationName() {
		return validationName;
	}

	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}

	public Object getActualResponseValue() {
		return actualResponseValue;
	}

	public void setActualResponseValue(Object actualResponseValue) {
		this.actualResponseValue = actualResponseValue;
	}

	public String getResponsePath() {
		return responsePath;
	}

	public void setResponsePath(String responsePath) {
		this.responsePath = responsePath;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String message) {
		this.validationMessage = message;
	}

	public OnValidationFailureOption getValidationFailureOption() {
		return validationFailureOption;
	}

	public void setValidationFailureOption(OnValidationFailureOption validationFailureOption) {
		this.validationFailureOption = validationFailureOption;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public List<String> getArrayPathListToIgnoreOrder() {
		return arrayPathListToIgnoreOrder;
	}

	public void setArrayPathListToIgnoreOrder(List<String> arrayPathListToIgnoreOrder) {
		this.arrayPathListToIgnoreOrder = arrayPathListToIgnoreOrder;
	}

	public JsonCompareOption getCompareOption() {
		return compareOption;
	}

	public void setCompareOption(JsonCompareOption compareOption) {
		this.compareOption = compareOption;
	}

	public List<JsonMismatchField> getDifferences() {
		return differences;
	}

	public void setDifferences(List<JsonMismatchField> differences) {
		this.differences = differences;
	}

	@JsonIgnore
	public APIValidationField getCopy() {
		APIValidationField validation = new APIValidationField(validationName, getResponsePath(), validationType);
		validation.expectedResponseValue = this.expectedResponseValue;
		validation.actualResponseValue = this.actualResponseValue;
		validation.validationResult = this.validationResult;
		validation.validationFailureOption = this.validationFailureOption;
		validation.messageId = this.messageId;
		validation.differences = this.differences;
		validation.excludes = this.excludes;
		validation.compareOption = this.compareOption;
		validation.arrayPathListToIgnoreOrder = this.arrayPathListToIgnoreOrder;
		validation.validationCondition = this.validationCondition;
		validation.expression = this.expression;

		return validation;
	}

	public void updateValidationMessage() {
		String messageFromResource = null;
		if (StringUtils.isNotBlank(messageId)) {
			messageFromResource = APIMessageFactory.getInstance().getMessage(messageId.trim(), validationResult);
		}
		if (validationResult == null) {
			validationResult = false;
		}

		String errorMessage = null;
		if (StringUtils.isBlank(messageFromResource) || !validationResult) {
			String path = APITestUtils.getAbsoluteResponsePath(responsePath);
			if (StringUtils.isNotEmpty(validationName)) {
				validationMessage = "Validation Name : " + validationName + " ";
			} else {
				validationMessage = "Response Path : " + path + " ";
			}

			if (validationType == ValidationType.EQUALS)
				errorMessage = "Expected : " + getStr(expectedResponseValue) + " EQUALS  Actual : " + getStr(actualResponseValue);
			if (validationType == ValidationType.NOT_EQUALS)
				errorMessage = "Expected : " + getStr(expectedResponseValue) + " NOT_EQUALS  Actual : " + getStr(actualResponseValue);
			if (validationType == ValidationType.GREATER_THAN)
				errorMessage = "Actual : " + getStr(actualResponseValue) + " GREATER_THAN  Expected : " + getStr(expectedResponseValue);
			if (validationType == ValidationType.LESSER_THAN)
				errorMessage = "Actual : " + getStr(actualResponseValue) + " LESSER_THAN  Expected : " + getStr(expectedResponseValue);
			if (validationType == ValidationType.EMPTY)
				errorMessage = "Expected : EMPTY Actual : " + getStr(actualResponseValue);
			if (validationType == ValidationType.NOT_EMPTY)
				errorMessage = "Expected : NOT_EMPTY Actual : " + getStr(actualResponseValue);
			if (validationType == ValidationType.CONTAINS_NODE) {
				errorMessage = "Response CONTAINS_NODE : " + path;
				validationMessage = "";
			}
			if (validationType == ValidationType.NOT_CONTAINS_NODE) {
				errorMessage = "Response NOT_CONTAINS_NODE : " + path;
				validationMessage = "";
			}
			if (validationType == ValidationType.CONTAINS_VALUE) {
				errorMessage = "Path : " + path + " CONTAINS_VALUE : " + getStr(expectedResponseValue);
				validationMessage = "";
			}
			if (validationType == ValidationType.NOT_CONTAINS_VALUE) {
				errorMessage = "Path : " + path + " NOT_CONTAINS_VALUE : " + getStr(expectedResponseValue);
				validationMessage = "";
			}
			if (ValidationType.EXPRESSION.equals(validationType)) {
				if (StringUtils.isNotEmpty(validationName)) {
					validationMessage = "Validation Name : " + validationName + " ";
				}
				errorMessage = " ( Expression : " + expression + " ) ";
			}
			validationMessage = validationMessage + errorMessage;
		}

		if (StringUtils.isNotBlank(messageFromResource)) {
			if (!validationResult) {
				messageFromResource = messageFromResource + " --> " + errorMessage ;
			}
			validationMessage = messageFromResource;
		}
	}

	private String getStr(Object value) {
		if (value == null || value.toString().trim().length() == 0) {
			return "null/empty";
		}

		return value.toString().trim();
	}

	public String toString() {
		return validationMessage == null ? "" : validationMessage;
	}

	public String getValidationCondition() {
		return validationCondition;
	}

	public void setValidationCondition(String validationCondition) {
		this.validationCondition = validationCondition;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getName() {
		String path = APITestUtils.getAbsoluteResponsePath(responsePath);
		String name = null;
		if (StringUtils.isNotEmpty(validationName)) {
			name = validationName;
		} else {
			name = path;
		}
		return name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
