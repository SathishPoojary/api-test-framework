package com.shc.automation.api.test.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsRequest;
import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APITestChain;
import com.shc.automation.api.test.framework.chaining.entities.APITestChainStep;
import com.shc.automation.api.test.framework.entities.APICompareTestsRequest;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.TestType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.exception.APITestSourceException;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.internal.config.APITestFactory;

/**
 * Main Interface exposed by the API framework to test cases. Test cases call
 * APITestManager to run tests of different types with different options. Makes
 * appropriate calls to configurations, factories and executors based on the
 * options provided in requests Provides responses to the test cases
 * 
 * @author spoojar
 * 
 */
public class APITestManager {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private Map<String, APITestResponse> testResultsMap;
	private Map<String, APIChainTestsResponse> chainCompareTestResultsMap;

	/**
	 * Set up the configurations for the test and create manager instance. Set
	 * default configuration Set Project or package specific configurations
	 * 
	 */
	public APITestManager() {
		APIConfigManager.configurePackage(getPackageName());
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName) throws APITestException {
		return runTest(testName, null, null, null);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param invocationCount
	 *            Number of times each request needs to be executed (repeatedly)
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName, Integer invocationCount) throws APITestException {
		return runTest(testName, null, null, invocationCount);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...) <br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName, String environment) throws APITestException {
		return runTest(testName, environment, null, null);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...) <br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param invocationCount
	 *            Number of times each request needs to be executed (repeatedly)
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName, String environment, Integer invocationCount) throws APITestException {
		return runTest(testName, environment, null, invocationCount);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...) <br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * 
	 * @param version
	 *            API or Service version needs to be executed
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName, String environment, String version) throws APITestException {
		return runTest(testName, environment, version, null);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...) <br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param invocationCount
	 *            Number of times each request needs to be executed (repeatedly)
	 * @param version
	 *            API or Service version needs to be executed
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	public APITestResponse runTest(String testName, String environment, String version, Integer invocationCount) throws APITestException {
		APITestRequest request = getAPITestRequest(testName, environment, version, null);
		request.setInvocationCount(invocationCount);
		return runTest(request);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param test
	 *            Test Request Object with all required fields set
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestSourceException
	 */
	public APITestResponse runTest(APITestRequest request) throws APITestException {
		return execute(request);
	}

	/**
	 * Execute the API Test Request and return the response. All the logic for a
	 * standalone API test are executed through this method.
	 * 
	 * @param request
	 *            containing all the required information and configs to execute the
	 *            test
	 * @return Response containing all response items for the configured scenarios
	 * @throws APITestSourceException
	 */
	private APITestResponse execute(APITestRequest request) throws APITestException {
		log.info("\n\nStarting the TEST *************************************************" + request.getTestName());
		APITestResponse testResponse = null;
		if (request != null) {
			APITestExecutor executor = new APITestExecutor();
			testResponse = executor.runTest(request);
			if (executor.getApiClientManager() != null) {
				executor.getApiClientManager().close();
			}
		}

		if (testResponse != null) {
			testResponse.setRequestType(request.getRequestType().toString());
			testResponse.setTestType(TestType.standalone);
		}

		return testResponse;
	}

	/**
	 * Generate the API Test Request Entity from the configuration for a given
	 * environment and record range
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param range
	 *            Range of records to be picked up for the test from the API input
	 *            source (For eg: 0-99)
	 * 
	 * @return API Test Request Object
	 * 
	 * @throws APITestException
	 */
	public APITestRequest getAPITestRequest(String testName, String environment, String range) throws APITestException {
		return getAPITestRequest(testName, environment, null, range);
	}

	public APITestRequest getAPITestRequest(String testName, String environment, String version, String range) throws APITestException {
		APITestRequest request = null;

		request = new APITestFactory().getAPITestRequest(testName);

		if (request == null) {
			throw new APITestException("Not able to generate the API Test Request for test :" + testName);
		}

		if (request.getUrlParamInputSource() != null) {
			request.getUrlParamInputSource().setRecordRange(range);
		}
		if (request.getPayloadInputSource() != null) {
			request.getPayloadInputSource().setRecordRange(range);
		}

		if (StringUtils.isNotBlank(environment)) {
			request.setEnvironment(environment);
		}

		// Set endpoint version
		if (StringUtils.isNotBlank(version)) {
			request.setEndpointVersion(version);
			;
		} else {
			request.setEndpointVersion(APITestConstants.DAFAULT_API_ENDPOINT_VERSION);
		}

		return request;
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param payloadMap
	 *            Map of Scenario Name and PayLoad String. API will be executed for
	 *            each of the scenarios in the map.
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithPayload(String testName, Map<String, String> payloadMap) throws APITestException {
		return runTestWithPayload(testName, null, payloadMap);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param payloadMap
	 *            Map of Scenario Name and PayLoad String. API will be executed for
	 *            each of the scenarios in the map.
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithPayload(String testName, String environment, Map<String, String> payloadMap) throws APITestException {
		return runTestWithUrlParamAndPayloads(testName, environment, null, payloadMap);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param urlParams
	 *            Map of Scenario Names and URL Parameters <br/>
	 *            URL Parameters - Map of Parameter Name and Parameter Value <br/>
	 *            Parameter Value can be either String or APITestParameter
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithUrlParams(String testName, Map<String, Map<String, Object>> urlParams) throws APITestException {
		return runTestWithUrlParams(testName, null, urlParams);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...) <br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param urlParams
	 *            Map of Scenario Names and URL Parameters <br/>
	 *            URL Parameters - Map of Parameter Name and Parameter Value <br/>
	 *            Parameter Value can be either String or APITestParameter
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithUrlParams(String testName, String environment, Map<String, Map<String, Object>> urlParams) throws APITestException {
		return runTestWithUrlParamAndPayloads(testName, environment, urlParams, null);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param urlParams
	 *            Map of Scenario Names and URL Parameters <br/>
	 *            URL Parameters - Map of Parameter Name and Parameter Value <br/>
	 *            Parameter Value can be either String or APITestParameter
	 * @param payloadMap
	 *            Map of Scenario Name and PayLoad String. API will be executed for
	 *            each of the scenarios in the map.
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithUrlParamAndPayloads(String testName, String environment, Map<String, Map<String, Object>> urlParams,
			Map<String, String> payloadMap) throws APITestException {
		return runTestWithAPIConfig(testName, environment, urlParams, payloadMap, null);
	}

	/**
	 * Execute an API Test and return the list of results
	 * 
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param urlParams
	 *            Map of Scenario Names and URL Parameters <br/>
	 *            URL Parameters - Map of Parameter Name and Parameter Value <br/>
	 *            Parameter Value can be either String or APITestParameter
	 * @param payloadMap
	 *            Map of Scenario Name and PayLoad String. API will be executed for
	 *            each of the scenarios in the map.
	 * @param apiConfigFile
	 *            API Configuration file path where the API to be tested is
	 *            specified. By default api-config.xml is picked up.
	 * 
	 * @return APITestResponse containing the APITestResponseItems for each of the
	 *         scenarios executed by the test.
	 * 
	 * @throws APITestException
	 */
	@Deprecated
	/*
	 * This method will be removed in future releases. Use getAPITestRequest()
	 * method to get the APITestRequest object, Set the payloadMap, urlParamMap etc
	 * and call execute(APITestRequest) method.
	 */
	public APITestResponse runTestWithAPIConfig(String testName, String environment, Map<String, Map<String, Object>> urlParams, Map<String, String> payloadMap,
			String apiConfigFile) throws APITestException {
		APITestResponse responses = null;
		APITestRequest request = null;
		APITestContext.get().getTestConfig().setApiConfigFile(apiConfigFile);
		request = getAPITestRequest(testName, environment, null);

		request.setExternalPayload(payloadMap);
		request.setExternalUrlParams(urlParams);
		responses = execute(request);

		return responses;
	}

	/**
	 * Generate a step request for a API Chain Test
	 * 
	 * @param stepName
	 *            Name of chain test step. This is used mainly for reporting or logs
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param stepResponseProcessClass
	 *            Fully qualified name of the Class responsible for processing the
	 *            response of the current step and providing the inputs for the next
	 *            step. By default the response is passed as is to the next step.
	 * 
	 * @return APITestChainStep Step for the Chain Test Execution
	 * 
	 * @throws APITestException
	 */
	public APITestChainStep getAPITestChainStep(String stepName, String testName, String environment, String stepResponseProcessClass) throws APITestException {
		return getAPITestChainStep(stepName, testName, environment, stepResponseProcessClass, 0);
	}

	/**
	 * Generate a step request for a API Chain Test
	 * 
	 * @param stepName
	 *            Name of chain test step. This is used mainly for reporting or logs
	 * @param testName
	 *            Name of the test to be executed. testName is the test-name
	 *            attribute configured in api-tests-config.xml
	 * @param environment
	 *            Name of the API environment where the test needs to be run
	 *            (production | QA | stress...)<br/>
	 *            Environment URLs are configured in MySQL DB table
	 *            "aeng_prod_productdb.service_endpoints"<br/>
	 *            NAME column of the table is the end-point attribute configured in
	 *            the api-tests-config.xml
	 * @param stepResponseProcessClass
	 *            Fully qualified name of the Class responsible for processing the
	 *            response of the current step and providing the inputs for the next
	 *            step. By default the response is passed as is to the next step.
	 *            
	 * @param sleepTimeAfterStepExecution 
	 * 			  Sleep time after the execution of this step
	 * 
	 * @return APITestChainStep Step for the Chain Test Execution
	 * 
	 * @throws APITestException
	 */
	public APITestChainStep getAPITestChainStep(String stepName, String testName, String environment, String stepResponseProcessClass,
			long sleepTimeAfterStepExecution) throws APITestException {
		APITestChainStep testStep = new APITestChainStep();
		testStep.setStepRequest(getAPITestRequest(testName, environment, null));
		testStep.setStepName(stepName);
		testStep.setStepResponseProcess(stepResponseProcessClass);
		testStep.setSleepTimeAfterStepExecution(sleepTimeAfterStepExecution);

		return testStep;
	}

	/**
	 * Execute multiple API Tests in parallel and return the response
	 * 
	 * @param testRequests
	 *            Map of API Test Name and API Test Request.
	 * 
	 * @return Map of API Test Name and API Test Response.
	 * 
	 * @throws APITestSourceException
	 */
	public Map<String, APITestResponse> runTests(final Map<String, APITestRequest> testRequests) throws APITestException {
		if (MapUtils.isEmpty(testRequests)) {
			log.error("Test List is Empty");
			return null;
		}
		testResultsMap = new HashMap<String, APITestResponse>();
		ExecutorService executor = Executors.newFixedThreadPool(testRequests.size());
		Iterator<String> testIter = testRequests.keySet().iterator();

		while (testIter.hasNext()) {
			final String testCase = testIter.next();
			final APITestRequest request = testRequests.get(testCase);
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try {
						execute(testCase, request);
					} catch (APITestException e) {
						e.printStackTrace();
					}
				}
			};
			executor.execute(runner);
		}

		executor.shutdown();
		while (!executor.isTerminated())
			;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
		log.info("\nCompleted all tests in the list");
		return testResultsMap;

	}

	private void execute(String testCase, APITestRequest request) throws APITestException {
		APITestResponse testResponse = execute(request);
		testResultsMap.put(testCase, testResponse);

	}

	/**
	 * Run multiple tests in parallel. Compare the responses from the tests based on
	 * the scenario.
	 * 
	 * @param testRequests
	 *            Map of API Test Name and API Test Requests
	 * @param excludes
	 *            List of JSON Paths to be excluded from the comparison (for eg:
	 *            $.cart.timestamp)
	 * 
	 * @return APICompareTestsResponse containing List of comparison results for
	 *         each of the APITestResponseItems based on scenarios
	 */
	public APICompareTestsResponse runTestsAndCompareResults(Map<String, APITestRequest> testRequests, List<String> excludes) throws APITestException {
		return runTestsAndCompareResults(null, testRequests, excludes);
	}

	/**
	 * Run multiple tests in parallel. Compare the responses from the tests based on
	 * the scenario.
	 * 
	 * @param testName
	 *            Name of the test. Used for report/logging
	 * @param testRequests
	 *            Map of API Test Name and API Test Requests
	 * @param excludes
	 *            List of JSON Paths to be excluded from the comparison (for eg:
	 *            $.cart.timestamp)
	 * 
	 * @return APICompareTestsResponse containing List of comparison results for
	 *         each of the APITestResponseItems based on scenarios
	 */
	public APICompareTestsResponse runTestsAndCompareResults(String testName, Map<String, APITestRequest> testRequests, List<String> excludes)
			throws APITestException {
		APICompareTestsRequest compareTestsRequest = new APICompareTestsRequest();
		compareTestsRequest.setName(testName);
		compareTestsRequest.setTestsToCompare(testRequests);
		compareTestsRequest.setPathsToExcludeFromCompare(excludes);

		return runTestsAndCompareResults(compareTestsRequest);

	}

	public static APITestManager getInstance(){
		return new APITestManager();
	}

	/**
	 * Main method to execute a comparative API test.
	 * 
	 * @param compareTestsRequest
	 *            containing all the required information for comparative test
	 * @return response containing compare results
	 * @throws APITestSourceException
	 */
	public APICompareTestsResponse runTestsAndCompareResults(APICompareTestsRequest compareTestsRequest) throws APITestException {
		if (compareTestsRequest == null) {
			log.error("Invalid Tests Compare Request - NULL");
			return null;
		}
		Map<String, APITestRequest> testsToCompare = compareTestsRequest.getTestsToCompare();
		if (MapUtils.isEmpty(testsToCompare)) {
			log.error("No Tests found to compare in the Request");
			return null;
		}
		if (testsToCompare.size() > 2) {
			log.warn("Supports only 2 tests for compare. Comparing only the first two test Responses...");
		}
		runTests(testsToCompare);
		APICompareTestsResponse compareResponse = new APICompareTestExecutor().compareTests(testResultsMap, compareTestsRequest.getPathsToExcludeFromCompare(),
				compareTestsRequest.getCompareOption(), compareTestsRequest.getArrayPathListToIgnoreOrder());
		if (compareResponse != null) {
			compareResponse.setTestName(compareTestsRequest.getName());
			compareResponse.setTestType(TestType.comparative);
		}

		return compareResponse;
	}

	@Deprecated
	public void turnOffLogger(Boolean turnOff) {

	}

	/**
	 * Turn OFF / ON Response parsing. If true, Response from API will not be
	 * converted to JSON Object and returned as is (String) in the
	 * APITestResponseItem. All the validation and printing need to be taken care by
	 * the test case itself. No print or validation performed in the API. API will
	 * parse the response by default.
	 * 
	 * @param turnOff
	 */
	public void turnOffResponseParsing(Boolean turnOff) {
		APITestContext.get().getTestConfig().setTurnOffResponseParsing(turnOff);
	}

	/**
	 * Turn OFF / ON API Validation. If true, API will not perform configured (XML
	 * or After Response Process) validations. API will perform validations by
	 * default.
	 * 
	 * @param turnOff
	 */
	public void turnOffValidation(Boolean turnOff) {
		APITestContext.get().getTestConfig().setTurnOffValidation(turnOff);
	}

	/**
	 * Turn OFF / ON Save response to MONGO DB. If false, API Test Responses will be
	 * stored in the Mongo DB. By default, responses are not stored in Mongo DB.
	 * 
	 * @param turnOff
	 */
	@Deprecated
	public void turnOffSaveResponseToMongo(Boolean turnOff) {

	}

	/**
	 * Return the HTTP Client used for the test in the response for future use like
	 * Session Handling. If true, HTTP Client responsible for API execution will be
	 * stored in the respective APITestResponseItem. By Default, HTTP Client will
	 * not be saved.
	 * 
	 * @param save
	 */
	public void saveSessionInResponse(Boolean save) {
		APITestContext.get().getTestConfig().setPersistSession(save);
	}

	/**
	 * Turn Off / On report failures only. Used for Reporting/Logging. If true, only
	 * the failed scenarios will be displayed in the reports. By default, all the
	 * scenarios will be displayed in the report.
	 * 
	 * @param onlyFailures
	 */
	@Deprecated
	public void reportOnlyFailures(Boolean onlyFailures) {
	}

	/**
	 * Print the Whole JSON Response string from the API in the report or log. By
	 * default, JSON string will not be printed in the log.
	 * 
	 * @param print
	 */
	public void printAPIJSONResponseInLog(Boolean print) {
		APITestContext.get().getTestConfig().setPrintJsonResponseInReport(print);
	}

	/**
	 * Close all Data base connections used by the Test API
	 */
	public static void close() {
		// APIConnectionManager.closeAllConnections();
	}

	/**
	 * Execute an API Chain Test containing multiple steps
	 * 
	 * @param testChain
	 *            Test Chain with multiple Steps to be performed
	 * @return APIChainTestsResponse
	 * 
	 * @throws APITestSourceException
	 */
	public APIChainTestsResponse runTestChain(APITestChain testChain) throws APITestException {
		saveSessionInResponse(true);
		APIChainTestsResponse chainTestResponse = new APITestChainExecutor().executeTestsChain(testChain);
		if (chainTestResponse == null) {
			log.error("Empty response from API on Test Chain Execution :" + testChain.getName());
			return chainTestResponse;
		}
		chainTestResponse.setTestType(TestType.chain);
		return chainTestResponse;
	}

	/**
	 * @param chainCompareTestsRequest
	 * @return
	 * @throws APITestException
	 */
	public APIChainCompareTestsResponse runChainsAndCompareResults(APIChainCompareTestsRequest chainCompareTestsRequest) throws APITestException {
		if (chainCompareTestsRequest == null) {
			log.error("API Chain Compare Tests : Request is NULL");
			return null;
		}
		Map<String, APITestChain> chainRequests = chainCompareTestsRequest.getChainsToCompare();
		if (MapUtils.isEmpty(chainRequests)) {
			log.error("API Chain Compare Tests : Request is EMPTY");
			return null;
		}
		if (chainRequests.size() == 1) {
			log.error("API Chain Compare Tests : Only one REQUEST Found, Cannot compare");
			return null;
		}
		runChainTests(chainRequests);
		APIChainCompareTestsResponse compareResponse = new APIChainCompareTestsExecutor().compareChainTests(chainCompareTestResultsMap,
				chainCompareTestsRequest.getPathsToExcludeFromCompare(), chainCompareTestsRequest.getCompareOption(),
				chainCompareTestsRequest.getArrayPathListToIgnoreOrder());
		if (compareResponse != null) {
			compareResponse.setTestName(chainCompareTestsRequest.getName());
			compareResponse.setTestType(TestType.comparativechain);
		}

		return compareResponse;
	}

	/**
	 * Execute multiple API Tests in parallel and return the response
	 * 
	 * @param testRequests
	 *            Map of API Test Name and API Test Request.
	 * 
	 * @return Map of API Test Name and API Test Response.
	 * 
	 * @throws APITestSourceException
	 */
	public Map<String, APIChainTestsResponse> runChainTests(final Map<String, APITestChain> chainRequests) throws APITestException {
		if (MapUtils.isEmpty(chainRequests)) {
			log.error("Chain Test List to compare is Empty");
			return null;
		}
		chainCompareTestResultsMap = new HashMap<String, APIChainTestsResponse>();
		ExecutorService executor = Executors.newFixedThreadPool(chainRequests.size());
		Iterator<String> testIter = chainRequests.keySet().iterator();

		while (testIter.hasNext()) {
			final String testCase = testIter.next();
			final APITestChain request = chainRequests.get(testCase);
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try {
						execute(testCase, request);
					} catch (APITestException e) {
						e.printStackTrace();
					}
				}
			};
			executor.execute(runner);
		}

		executor.shutdown();
		while (!executor.isTerminated())
			;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
		log.info("\nCompleted all tests in the list");
		return chainCompareTestResultsMap;

	}

	private void execute(String testCase, APITestChain request) throws APITestException {
		APIChainTestsResponse response = runTestChain(request);
		chainCompareTestResultsMap.put(testCase, response);
	}

	private String getPackageName() {
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		String fullClassName = stackElements[2].getClassName();
		if ((null == fullClassName) || ("".equals(fullClassName)))
			return "";
		int lastDot = fullClassName.lastIndexOf('.');
		if (0 >= lastDot)
			return "";

		return fullClassName.substring(0, lastDot);
	}
}
