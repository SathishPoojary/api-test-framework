/**
 * 
 */
package com.shc.automation.api.test.framework.utils;

import java.util.List;

import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.entities.ValidationType;
import com.shc.automation.api.test.framework.internal.validators.APIResponseValidator;
import com.shc.automation.utils.json.JsonCompareOption;

/**
 * @author sathish_poojary
 * 
 */
public class APICustomValidationUtils {

	/**
	 * Add a validation to the response item. This method is called explicitly
	 * from subclasses APIAfterResponseProcess::validate to add custom or
	 * complex validations to the API test which cannot be directly configurable
	 * 
	 * @param responseItem
	 *            response to which the validation to be added
	 * @param name
	 *            Name of the validation. If blank, path is used as Name
	 * @param path
	 *            Path in the API JSon response needs to be validated
	 * @param typeOfValidation
	 *            Type of Validation needs to performed on the response
	 * @param desiredValue
	 *            Desired Value in the JSon Response for the given path
	 * @return result whether validation is passed or failed
	 */
	public static Boolean addValidation(APITestResponseItem responseItem, String name, String path, ValidationType typeOfValidation, String desiredValue) {
		return addValidation(responseItem, name, path, typeOfValidation, desiredValue, null);
	}

	/**
	 * Add a validation to the response item. This method is called explicitly
	 * from subclasses APIAfterResponseProcess::validate to add custom or
	 * complex validations to the API test which cannot be directly configurable
	 * 
	 * @param responseItem
	 *            response to which the validation to be added
	 * @param name
	 *            Name of the validation. If blank, path is used as Name
	 * @param path
	 *            Path in the API JSon response needs to be validated
	 * @param typeOfValidation
	 *            Type of Validation needs to performed on the response
	 * @param desiredValue
	 *            Desired Value in the JSon Response for the given path
	 * @param validationMessage
	 *            message to be printed in the report or logger
	 * 
	 * @return result whether validation is passed or failed
	 */
	public static Boolean addValidation(APITestResponseItem responseItem, String name, String path, ValidationType typeOfValidation, String desiredValue, String validationMessage) {

		APIValidationField validation = new APIValidationField(name, path, desiredValue, typeOfValidation);
		validation.setValidationMessage(validationMessage);
		boolean validate = new APIResponseValidator().validateField(responseItem, validation);
		if (!validate) {
			responseItem.setResult(ResultType.FAILED);
		} else if (responseItem.getResult() != ResultType.FAILED) {
			responseItem.setResult(ResultType.PASSED);

		}
		responseItem.setValidator(validation);
		return validate;
	}

	/**
	 * Add JSon validation to the response item. This method compares JSon
	 * Object derived from response for the given path and desiredValue and
	 * lists the mismatches found in comparison <br/>
	 * This method is called explicitly from subclasses
	 * APIAfterResponseProcess::validate to add custom or complex validations to
	 * the API test which cannot be directly configurable.
	 * 
	 * @param responseItem
	 *            response to which the validation to be added
	 * @param name
	 *            Name of the validation. If blank, path is used as Name
	 * @param path
	 *            Path in the API JSon response needs to be validated
	 * @param typeOfValidation
	 *            Type of Validation needs to performed on the response
	 * @param desiredValue
	 *            Desired Value in the JSon Response for the given path
	 * @param excludes
	 *            List of JSon Paths in the response need to be excluded from
	 *            comparison
	 * 
	 * @return result whether validation is passed or failed
	 */

	public static Boolean addJsonValidation(APITestResponseItem responseItem, String name, String path, ValidationType typeOfValidation, Object desiredValue, List<String> excludes) {
		return addJsonValidation(responseItem, name, path, typeOfValidation, desiredValue, excludes, null);
	}

	/**
	 * Add JSon validation to the response item. This method compares JSon
	 * Object derived from response for the given path and desiredValue and
	 * lists the mismatches found in comparison <br/>
	 * This method is called explicitly from subclasses
	 * APIAfterResponseProcess::validate to add custom or complex validations to
	 * the API test which cannot be directly configurable.
	 * 
	 * @param responseItem
	 *            response to which the validation to be added
	 * @param name
	 *            Name of the validation. If blank, path is used as Name
	 * @param path
	 *            Path in the API JSon response needs to be validated
	 * @param typeOfValidation
	 *            Type of Validation needs to performed on the response
	 * @param desiredValue
	 *            Desired Value in the JSon Response for the given path
	 * @param excludes
	 *            List of JSon Paths in the response need to be excluded from
	 *            comparison
	 * @param validationMessage
	 *            message to be printed in the report or logger
	 * 
	 * @return result whether validation is passed or failed
	 */
	public static Boolean addJsonValidation(APITestResponseItem responseItem, String name, String path, ValidationType typeOfValidation, Object desiredValue, List<String> excludes,
			String validationMessage) {

		return addJsonValidation(responseItem, name, path, typeOfValidation, desiredValue, excludes, null, null, validationMessage);
	}

	public static Boolean addJsonValidation(APITestResponseItem responseItem, String name, String path, ValidationType typeOfValidation, Object desiredValue, List<String> excludes,
			JsonCompareOption compareOption, List<String> arrayPathListToIgnoreOrder, String validationMessage) {
		boolean validate = false;

		APIValidationField validation = new APIValidationField(name, path, typeOfValidation);
		validation.setExpectedResponseValue(desiredValue);
		validation.setExcludes(excludes);
		validation.setValidationMessage(validationMessage);
		validation.setCompareOption(compareOption);
		validation.setArrayPathListToIgnoreOrder(arrayPathListToIgnoreOrder);

		validate = new APIResponseValidator().validateField(responseItem, validation);

		if (!validate) {
			responseItem.setResult(ResultType.FAILED);
		} else if (responseItem.getResult() != ResultType.FAILED) {
			responseItem.setResult(ResultType.PASSED);

		}
		responseItem.setValidator(validation);
		return validate;

	}

	/**
	 * This method is used to manually set the overall test status after adding
	 * custom validations to the response item from test case. This will mark
	 * the test case as failed and add the scenario to the failed scenario list
	 * if the response item validation status is failed
	 * 
	 * @param response
	 *            overall test case response whose status needs to be set
	 * @param responseItem
	 *            Individual response item whose status changed because of
	 *            custom validation from the test case
	 */
	public static void setValidationStatus(APITestResponse response, APITestResponseItem responseItem) {
		if (responseItem == null || !responseItem.isValidResult()) {
			response.setTestSuccessful(false);
			response.addFailedScenario(responseItem.getScenarioName());
		}
	}


	/**
	 * Add a custom validation message to the response Item. This method is
	 * called from the test case once the API response is received to add custom
	 * messages to the response items and in turn to the report
	 * 
	 * @param responseItem
	 *            - Response Item where the validation message to be added
	 * @param message
	 *            - Validation Message
	 * @param passValidation
	 *            - Mark validation passed (true) or failed (false)
	 * @param failScenario
	 *            - Mark the responseItem or the scenario failed (true) if
	 *            passValidation is false
	 * @return
	 */
	public static APIValidationField addValidationMessage(APITestResponseItem responseItem, String message, boolean passValidation, boolean failScenario) {
		APIValidationField validation = new APIValidationField(null, null, null);
		validation.setValidationMessage(message);
		validation.setValidationResult(passValidation);

		if (failScenario && !passValidation) {
			responseItem.setResult(ResultType.FAILED);
		}
		responseItem.setValidator(validation);
		return validation;
	}

}
