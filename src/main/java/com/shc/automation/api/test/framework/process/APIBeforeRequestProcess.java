/**
 * 
 */
package com.shc.automation.api.test.framework.process;

import java.util.Map;

import com.shc.automation.api.test.framework.entities.APITestRequest;

/**
 * This class is used to generate the API URL Parameters, PayLoads and Request
 * Headers which cannot be directly configured in the test configuration file
 * 
 * @author sathish_poojary
 * 
 */
public class APIBeforeRequestProcess {
	protected APITestRequest request = null;

	public APIBeforeRequestProcess() {

	}

	public APIBeforeRequestProcess(APITestRequest request) {
		this.request = request;
	}

	/**
	 * Method to generate Service URL Params. API will be called for each scenario
	 * in the generated Map
	 * 
	 * @return Map with Key : Scenario Name Value : Map of Param Name and Param
	 *         Value.
	 */
	public Map<String, Map<String, Object>> generateUrlParams() {
		return null;
	}

	/**
	 * Method to generate service payloads. API will be called for each scenario in
	 * the generated Map
	 * 
	 * @return Map with Key : Scenario Name Value : Payload String
	 */
	public Map<String, String> generatePayloads() {
		return null;
	}

	/**
	 * Method to generate dynamic Header Parameters
	 * 
	 * @return Map with Key : Header Parameter Name Value : Header Parameter Value
	 */
	public Map<String, String> generateRequestHeaders() {
		return null;
	}

	/**
	 * Method to set the URL. Used to generate URL dynamically from test cases
	 * config
	 * 
	 * @return
	 */
	public String generateServiceUrl() {
		return null;
	}

	/**
	 * Method to set the API Path. Used to generate path dynamically from test cases
	 * 
	 * @return
	 */
	public String generateServicePath() {
		return null;
	}

	/**
	 * @return
	 */
	public Map<String, Map<String, Object>> generateScenarioContext() {
		return null;
	}

}
