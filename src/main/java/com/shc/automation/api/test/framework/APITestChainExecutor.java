package com.shc.automation.api.test.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.chaining.entities.APITestChain;
import com.shc.automation.api.test.framework.chaining.entities.APITestChainStep;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.exception.APITestSourceException;
import com.shc.automation.api.test.framework.internal.APIResponseItemProcessor;
import com.shc.automation.api.test.framework.internal.process.APIRequestsProcessor;
import com.shc.automation.api.test.framework.process.APIChainStepResponseProcess;

public class APITestChainExecutor {
	private Logger log = Logger.getLogger(this.getClass().getName());
	private APITestExecutor apiTestExecutor = null;
	private Map<String, List<APITestRequestItem>> stepRequestsMap = null;

	public APITestChainExecutor() {
		apiTestExecutor = new APITestExecutor();
		stepRequestsMap = new HashMap<String, List<APITestRequestItem>>();
	}

	protected APIChainTestsResponse executeTestsChain(APITestChain testChain) throws APITestException {
		if (testChain == null) {
			log.error("Empty/NULL Object to execute the test chain");
			return null;
		}
		System.out.println("+++++++++++++Starting execution of TEST..." + testChain.getName() + "+++++++++++++");

		List<APITestChainStep> testSteps = testChain.getTestSteps();
		if (CollectionUtils.isEmpty(testSteps)) {
			log.error("No Steps found to execute the test chain :" + testChain.getName());
			return null;
		}

		APIChainTestsResponse chainTestResponse = new APIChainTestsResponse(testChain.getName());
		APITestChainStep firstTestChainStep = testSteps.get(0);

		APITestResponse firstStepResponse = apiTestExecutor.runTest(firstTestChainStep.getStepRequest());
		Map<String, APITestResponseItem> firstStepResponseItems = firstStepResponse.getResponseItems();
		if (MapUtils.isEmpty(firstStepResponseItems)) {
			throw new APITestSourceException("No Requests found for First Step in Chain " + testChain.getName());
		}
		chainTestResponse.setTotalRequests(firstStepResponseItems.size());

		Iterator<String> firstStepRespIter = firstStepResponseItems.keySet().iterator();
		String firstStepName = firstTestChainStep.getStepName();

		while (firstStepRespIter.hasNext()) {
			String scenario = firstStepRespIter.next();
			System.out.println("------- Starting the Chain Test Execution for Scenario :" + scenario);
			APITestResponseItem stepResponseItem = firstStepResponseItems.get(scenario);
			APITestChainStep testChainStep = firstTestChainStep;
			stepResponseItem.setDisplayName(firstStepName);
			APIChainTestsResponseItem chainTestRespItem = new APIChainTestsResponseItem(scenario);

			for (int i = 0; i < testSteps.size(); i++) {

				APIChainStepResponseProcess stepPostProcess = null;

				if (stepResponseItem.getValidResponse()) {
					stepPostProcess = getChainStepResponseProcess(testChainStep.getStepResponseProcess(), stepResponseItem);
					stepPostProcess.validate();
					stepPostProcess.print();
				}

				if (!stepResponseItem.isValidResult()) {
					log.error("Test Chain Step Failed :" + stepResponseItem.getDisplayName());
					chainTestRespItem.addTestStepResponse(stepResponseItem);
					chainTestRespItem.setResult(ResultType.FAILED);
					chainTestResponse.addFailedScenario(scenario);
					chainTestResponse.setTestSuccessful(false);
					break;
				} else {
					chainTestRespItem.setResult(ResultType.PASSED);
					chainTestRespItem.addTestStepResponse(stepResponseItem);
				}

				if (i == testSteps.size() - 1)
					break;
				if (testChainStep.getSleepTimeAfterStepExecution() > 0) {
					try {
						Thread.sleep(testChainStep.getSleepTimeAfterStepExecution());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				testChainStep = testSteps.get(i + 1);
				APITestRequestItem stepRequestItem = getStepRequest(scenario, testChainStep);

				if (stepRequestItem == null) {
					log.error("No Request found for Step: " + testChainStep.getStepName() + " for Scenario :" + scenario);
					break;
				}
				stepPostProcess.process(stepRequestItem);

				stepResponseItem = executeNextStep(scenario, stepRequestItem, testChainStep, stepResponseItem.getHttpClient(),
						stepResponseItem.getHttpClientContext());

			}
			if (stepResponseItem.getHttpClient() != null) {
				try {
					stepResponseItem.getHttpClient().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			chainTestResponse.addChainTestResponseItem(scenario, chainTestRespItem);
			System.out.println("------- Completed the Chain Test Execution for Scenario :" + scenario);

		}
		if (apiTestExecutor.getApiClientManager() != null) {
			apiTestExecutor.getApiClientManager().close();
		}
		System.out.println("+++++++++++++Completed execution of TEST..." + testChain.getName() + "+++++++++++++");
		return chainTestResponse;
	}

	/**
	 * @param httpClient
	 * @param chainStepProcess
	 * @param responseContent
	 * @param stepRequest
	 * @throws APITestException
	 *             exception
	 */
	private APITestRequestItem getStepRequest(String scenario, APITestChainStep testStep) throws APITestException {
		List<APITestRequestItem> requestItems = null;
		if (stepRequestsMap.containsKey(testStep.getStepName())) {
			requestItems = stepRequestsMap.get(testStep.getStepName());
		} else {
			APITestRequest stepRequest = testStep.getStepRequest();
			requestItems = new APIRequestsProcessor().generateAPIRequests(stepRequest);
			if (CollectionUtils.isEmpty(requestItems)) {
				throw new APITestSourceException("No Requests found for the Step : " + testStep.getStepName());
			}
			stepRequestsMap.put(testStep.getStepName(), requestItems);
		}

		return getStepRequestItemForScenario(scenario, requestItems);
	}

	/**
	 * @param config
	 * @param httpClient
	 * @param testStep
	 * @param chainStepProcess
	 * @param stepRequestsMap
	 * @param responseContent
	 * @return
	 * @throws APITestSourceException
	 */
	private APITestResponseItem executeNextStep(String scenario, APITestRequestItem requestItem, APITestChainStep testStep, CloseableHttpClient httpClient,
			HttpClientContext httpClientContext) throws APITestException {

		APITestResponseItem stepResponseItem = null;
		try {

			stepResponseItem = apiTestExecutor.getApiClientManager().executeRequest(requestItem, httpClient, httpClientContext);
			new APIResponseItemProcessor().update(testStep.getStepRequest(), stepResponseItem);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Test Chain Step Failed on Execution:" + testStep.getStepName());
			if (stepResponseItem == null) {
				stepResponseItem = new APITestResponseItem();
			}
			String message = "Step Execution Failed :" + e.getMessage() + "<br/>" + e.getStackTrace()[0];
			APITestException ate = new APITestException(message);
			stepResponseItem.setApiError(ate);
			stepResponseItem.setResult(ResultType.FAILED);

		}

		if (stepResponseItem != null) {
			stepResponseItem.setDisplayName(testStep.getStepName());
		}

		return stepResponseItem;
	}

	private APITestRequestItem getStepRequestItemForScenario(String scenario, List<APITestRequestItem> requestItems) {
		if (CollectionUtils.isEmpty(requestItems)) {
			return null;
		}
		for (APITestRequestItem requestItem : requestItems) {
			if (scenario.equals(requestItem.getScenarioName())) {
				return requestItem;
			}
		}
		APITestRequestItem requestItem = requestItems.get(0).getCopy(0);
		requestItem.setScenarioName(scenario);

		return requestItem;
	}

	private APIChainStepResponseProcess getChainStepResponseProcess(String process, APITestResponseItem responseItem) {
		if (StringUtils.isBlank(process))
			process = "com.shc.automation.api.test.framework.process.APIChainStepResponseProcess";
		try {
			return (APIChainStepResponseProcess) Class.forName(process).getConstructor(APITestResponseItem.class).newInstance(responseItem);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
