package com.shc.automation.api.test.framework.process;

import com.shc.automation.api.test.framework.entities.APITestResponseItem;

/**
 * This class is used to add custom or complex validations and prints to the
 * response. It is used only when this validations are complex and cannot be
 * added through the test configuration file. <br/>
 * For example, if validation requires iterating through the JSON response or
 * special conditions which cannot be added through test configuration attribute
 * 
 * @author sathish_poojary
 * 
 */
public class APIAfterResponseProcess {
	protected APITestResponseItem responseItem;

	public APIAfterResponseProcess(APITestResponseItem responseItem) {
		this.responseItem = responseItem;
	}

	/**
	 * Custom/Complex validations which will be executed once all hygiene
	 * (configured) validations are passed.
	 * 
	 * @return pass or fail
	 */
	public boolean validate() {
		return true;
	}

	/**
	 * Custom/Complex prints which will be printed in the log/excel
	 */
	public void print() {

	}

	/**
	 * Update the JSON response content to prepare it for comparison with
	 * another response. For example, if two API response JSON structures are
	 * different, this method can be used for updating one of the responses so
	 * that structures are in sync before comparison
	 * 
	 */
	public Object updateResponseForComparison() {
		return null;
	}

}
