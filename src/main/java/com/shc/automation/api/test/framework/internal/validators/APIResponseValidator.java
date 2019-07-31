/**
 * 
 */
package com.shc.automation.api.test.framework.internal.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.primitives.Primitives;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.OnValidationFailureOption;
import com.shc.automation.api.test.framework.entities.ValidationType;
import com.shc.automation.api.test.framework.internal.APIExpressionEvaluator;
import com.shc.automation.api.test.framework.process.APIAfterResponseProcess;
import com.shc.automation.api.test.framework.utils.APITestUtils;

import net.sf.json.JSONNull;
import net.sf.json.util.JSONUtils;

/**
 * @author spoojar
 *
 */
public class APIResponseValidator {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	public Boolean validate(APITestResponseItem response, APIAfterResponseProcess postTestProcess) {
		boolean validate = true;

		List<APIValidationField> validators = response.getValidators();
		if (CollectionUtils.isNotEmpty(validators)) {
			validate = validateFields(response, validators);
		}
		if (postTestProcess != null) {
			validate = validate && postTestProcess.validate();
		}
		return validate;

	}

	private boolean validateFields(APITestResponseItem response, List<APIValidationField> validators) {

		List<String> mandatoryValidations = new ArrayList<String>();
		boolean validated = true;
		for (int i = 0; i < validators.size(); i++) {
			APIValidationField validationField = validators.get(i);
			String path = validationField.getResponsePath();
			if (OnValidationFailureOption.FAIL_AND_STOP_VALIDATIONS.equals(validationField.getValidationFailureOption())) {
				boolean passed = validateField(response, validationField);
				if (!passed) {
					log.warn("Stopping all further validations based on the given option for failure");
					validators.clear();
					validators.add(validationField);
					return false;
				} else {
					mandatoryValidations.add(path);
				}
			}
		}

		for (int i = 0; i < validators.size(); i++) {
			APIValidationField validationField = validators.get(i);
			String path = validationField.getResponsePath();
			if (!mandatoryValidations.contains(path)) {
				boolean passed = validateField(response, validationField);
				if (!passed) {
					log.info("Validation :" + validationField.getResponsePath() + " failed for scenario :" + response.getScenarioName());
					if (!OnValidationFailureOption.MARK_TEST_FAILED.equals(validationField.getValidationFailureOption())) {
						response.failTestOnValidationFailure(false);
					}
					if (OnValidationFailureOption.MARK_VALIDATION_FAILED.equals(validationField.getValidationFailureOption())) {
						passed = true;
					}

				}
				validated = validated & passed;
			}
		}
		validators.removeIf(Objects::isNull);
		return validated;
	}

	public boolean validateField(APITestResponseItem response, APIValidationField validationField) {
		Object object = null;
		String preCondition = validationField.getValidationCondition();
		if (StringUtils.isNotBlank(preCondition) && !APIExpressionEvaluator.validate(preCondition)) {
			System.out.println("SKIPPED Validation: " + validationField.getName() + "  Precondition [" + preCondition + " ] not satisfied ");
			int index = response.getValidators().indexOf(validationField);
			if (index >= 0)
				response.getValidators().set(index, null);
			return true;
		}
		if (ValidationType.EXPRESSION == validationField.getValidationType()) {
			boolean result = APIExpressionEvaluator.validate(validationField.getExpression());
			validationField.setValidationResult(result);
			validationField.updateValidationMessage();
			return result;
		}
		object = APITestUtils.readFromJSON(response.getResponseContent(), validationField.getResponsePath(), true);

		validationField.setActualResponseValue(object);
		return validateWithActual(validationField);
	}

	@SuppressWarnings("rawtypes")
	private boolean validateWithActual(APIValidationField validationField) {
		Object actualResponseValue = validationField.getActualResponseValue();

		if (JSONNull.getInstance().equals(actualResponseValue) || StringUtils.isBlank(actualResponseValue.toString()) || isSimpleValue(actualResponseValue)) {
			return new APIPrimitiveValidator().validate(validationField);
		}
		if (actualResponseValue instanceof List && allSimpleValues((List) actualResponseValue)) {
			if (((List) actualResponseValue).size() == 1) {
				return new APIPrimitiveValidator().validate(validationField);
			}
			return new APIArrayValidator().validate(validationField);
		}
		ValidationType validationType = validationField.getValidationType();
		if (ValidationType.EQUALS != validationType && ValidationType.NOT_EQUALS != validationType) {
			return new APIPrimitiveValidator().validate(validationField);
		}
		if (JSONUtils.mayBeJSON(actualResponseValue.toString())) {
			return new APIJsonValidator().validate(validationField);
		}

		return new APIPrimitiveValidator().validate(validationField);
	}

	@SuppressWarnings("rawtypes")
	private static boolean allSimpleValues(List array) {

		for (int i = 0; i < array.size(); ++i) {
			if (!Primitives.isWrapperType(array.get(i).getClass()) && !(array.get(i) instanceof String)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isSimpleValue(Object value) {
		if (!Primitives.isWrapperType(value.getClass()) && !(value instanceof String)) {
			return false;
		}
		return true;
	}

}
