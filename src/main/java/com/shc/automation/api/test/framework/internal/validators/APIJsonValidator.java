package com.shc.automation.api.test.framework.internal.validators;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.ValidationType;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonCompareUtils;
import com.shc.automation.utils.json.JsonMismatchField;

public class APIJsonValidator {

	public Boolean validate(APIValidationField validator) {
		if (validator == null) {
			return true;
		}
		Boolean validResponse = true;

		Object expectedResponseValue = validator.getExpectedResponseValue();
		Object actualResponseValue = validator.getActualResponseValue();
		List<String> excludes = validator.getExcludes();
		List<String> arrayPathListToIgnoreOrder = validator.getArrayPathListToIgnoreOrder();

		if (expectedResponseValue != null && actualResponseValue != null) {
			if (expectedResponseValue instanceof List) {
				expectedResponseValue = APITestUtils.wrapAPIResponse(expectedResponseValue);
				actualResponseValue = APITestUtils.wrapAPIResponse(actualResponseValue);
				if (CollectionUtils.isNotEmpty(excludes)) {
					List<String> excludePaths = new ArrayList<String>(excludes.size());
					for (String path : excludes) {
						excludePaths.add(APITestUtils.getAbsoluteResponsePath(path));
					}
					excludes = excludePaths;
				}
				if (CollectionUtils.isNotEmpty(arrayPathListToIgnoreOrder)) {
					List<String> excludePaths = new ArrayList<String>(arrayPathListToIgnoreOrder.size());
					for (String path : arrayPathListToIgnoreOrder) {
						excludePaths.add(APITestUtils.getAbsoluteResponsePath(path));
					}
					arrayPathListToIgnoreOrder = excludePaths;
				}
			}
		}

		List<JsonMismatchField> differences = null;
		try {

			differences = JsonCompareUtils.deepCompare(JSONObject.fromObject(expectedResponseValue), JSONObject.fromObject(actualResponseValue), excludes,
					validator.getCompareOption(), arrayPathListToIgnoreOrder);
		} catch (Exception e) {
			e.printStackTrace();
			validator.setValidationResult(false);
			updateValidationMessage(validator);
			return false;
		}

		ValidationType validationType = validator.getValidationType();

		if (validationType == ValidationType.EQUALS && !CollectionUtils.isEmpty(differences))
			validResponse = false;
		if (validationType == ValidationType.NOT_EQUALS && CollectionUtils.isEmpty(differences))
			validResponse = false;
		if (validationType == ValidationType.CONTAINS_VALUE) {
			if (CollectionUtils.isNotEmpty(differences)) {
				JsonMismatchField diff = null;
				for (int i = 0; i < differences.size(); i++) {
					diff = differences.get(i);
					if (diff.getRightValue() == null) {
						validResponse = false;
						break;
					}

				}
			}
		}
		if (validationType == ValidationType.NOT_CONTAINS_VALUE) {
			if (CollectionUtils.isNotEmpty(differences)) {
				JsonMismatchField diff = null;
				for (int i = 0; i < differences.size(); i++) {
					diff = differences.get(i);
					if (diff.getRightValue() != null) {
						validResponse = false;
						break;
					}

				}
			}
		}
		validator.setValidationResult(validResponse);
		validator.setDifferences(differences);
		updateValidationMessage(validator);
		return validResponse;

	}

	private void updateValidationMessage(APIValidationField validator) {
		String name = validator.getValidationName();
		if (StringUtils.isEmpty(name)) {
			name = APITestUtils.getAbsoluteResponsePath(validator.getResponsePath());
			if (APITestConstants.COMPARE_TWO_RESPONSES_BASE_PATH.equals(name))
				name = "Full Response";
		}

		StringBuilder str = new StringBuilder();
		List<JsonMismatchField> differences = validator.getDifferences();

		if (CollectionUtils.isNotEmpty(differences)) {
			str = new StringBuilder(differences.size() + " mismatch(s) found on comparison of [ " + name + " ]");
		} else {
			if (!validator.getValidationResult())
				str = new StringBuilder("Error occured in  [" + name + "] comparison ");
			else
				str = new StringBuilder("No mismatches found in comparsion of " + name);
		}
		validator.setValidationMessage(str.toString());
	}

}
