package com.shc.automation.api.test.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.APIHttpClientManager;
import com.shc.automation.api.test.framework.internal.APITestItemExecutor;
import com.shc.automation.api.test.framework.internal.APIResponseItemProcessor;
import com.shc.automation.api.test.framework.internal.process.APIRequestsProcessor;

public class APITestExecutor {

	private Logger log = Logger.getLogger(this.getClass().getName());

	private APITestResponse testResponse = null;
	private APIHttpClientManager apiClientManager = null;

	public APITestExecutor() {
		testResponse = new APITestResponse();
	}

	/**
	 * @param apiTestRequest
	 * @return
	 * @throws APIException
	 */
	protected APITestResponse runTest(APITestRequest apiTestRequest) throws APITestException {
		testResponse.setTestName(apiTestRequest.getTestName());
		Integer invocationCount = apiTestRequest.getInvocationCount();
		if (invocationCount == null || invocationCount < 1)
			invocationCount = 1;
		try {
			List<APITestRequestItem> requestItems = new APIRequestsProcessor().generateAPIRequests(apiTestRequest);

			if (!CollectionUtils.isEmpty(requestItems)) {
				if (invocationCount == 1) {
					executeTest(apiTestRequest, requestItems);
				} else {
					List<APITestRequestItem> repeatRequestsList = new ArrayList<APITestRequestItem>(requestItems.size() * invocationCount);
					for (int i = 0; i < requestItems.size(); i++) {
						APITestRequestItem request = requestItems.get(i);
						for (int j = 0; j < invocationCount; j++) {
							repeatRequestsList.add(request.getCopy(j + 1));
						}
					}
					executeTest(apiTestRequest, repeatRequestsList);
				}
				testResponse.setTotalRequests(requestItems.size() * invocationCount);
			}

		} catch (APITestException e) {
			log.error("Error in test execution :" + apiTestRequest.getTestName(), e);
			testResponse.setTestError(e);
			testResponse.setTestSuccessful(false);
		}

		return testResponse;
	}

	/**
	 * @param apiTestRequest
	 * @param methods
	 */
	protected void executeTest(final APITestRequest apiTestRequest, List<APITestRequestItem> requestItems) {
		testResponse.setServiceUrl(apiTestRequest.getServiceUrl());
		testResponse.setReportFormat(apiTestRequest.getReportFormat());

		apiClientManager = new APIHttpClientManager(apiTestRequest.getThreadPoolSize(), requestItems.size(), apiTestRequest.getRequestsPerSecond());

		ExecutorService requestExecutor = Executors.newFixedThreadPool(apiTestRequest.getThreadPoolSize());
		ExecutorService responseProcessor = Executors.newFixedThreadPool(apiTestRequest.getThreadPoolSize());

		System.out.println("+++++++++++++Starting execution of TEST..." + apiTestRequest.getTestName() + "+++++++++++++");

		final BlockingQueue<APITestResponseItem> queue = new LinkedBlockingQueue<APITestResponseItem>(requestItems.size());
		final APITestResponseItem POISON_PILL = new APITestResponseItem();
		for (int i = 0; i < requestItems.size(); i++) {
			final APITestRequestItem requestItem = requestItems.get(i);
			if (requestItem == null) {
				continue;
			}
			Runnable executor = new APITestItemExecutor(queue, requestItem, apiClientManager);
			requestExecutor.execute(executor);
		}

		Runnable processor = new Runnable() {
			private volatile boolean isRunning = true;

			@Override
			public void run() {
				try {
					while (isRunning) {
						APITestResponseItem response = queue.take();
						if (POISON_PILL == response) {
							isRunning = false;
							return;
						}
						updateAPITestResponse(new APIResponseItemProcessor().update(apiTestRequest, response));

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (APITestException e) {
					e.printStackTrace();
				}
			}

		};

		responseProcessor.execute(processor);

		requestExecutor.shutdown();
		while (!requestExecutor.isTerminated())
			;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			queue.put(POISON_PILL);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		responseProcessor.shutdown();
		while (!responseProcessor.isTerminated())
			;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("+++++++++++++Completed execution of TEST:" + apiTestRequest.getTestName() + "+++++++++++++");

	}

	public APIHttpClientManager getApiClientManager() {
		return apiClientManager;
	}

	private void updateAPITestResponse(APITestResponseItem responseItem) {
		if (responseItem == null) {
			return;
		}
		ResultType result = responseItem.getResult();
		if (result != ResultType.PASSED) {
			if (responseItem.isFailTestOnValidationFailure() && testResponse.isTestSuccessful())
				testResponse.setTestSuccessful(false);
			testResponse.addFailedScenario(responseItem.getScenarioName());
		}

		testResponse.addResponseItem(responseItem.getScenarioName(), responseItem);
	}

}
