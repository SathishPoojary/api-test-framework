/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.util.concurrent.BlockingQueue;

import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.exception.APITestException;

/**
 * @author spoojar
 *
 */
public class APITestItemExecutor implements Runnable {
	private final BlockingQueue<APITestResponseItem> responseQueue;
	private final APITestRequestItem requestItem;
	private final APIHttpClientManager apiHttpClientManager;

	public APITestItemExecutor(BlockingQueue<APITestResponseItem> queue, APITestRequestItem requestItem, APIHttpClientManager apiHttpClientManager) {
		this.responseQueue = queue;
		this.requestItem = requestItem;
		this.apiHttpClientManager = apiHttpClientManager;
	}

	@Override
	public void run() {
		if (apiHttpClientManager.getRateLimiter() != null) {
			double waitTime = apiHttpClientManager.getRateLimiter().acquire();
			System.out.println("Wait for next Request to execute :" + waitTime);
		}
		try {
			APITestResponseItem responseItem = apiHttpClientManager.executeRequest(requestItem, null, null);
			responseQueue.put(responseItem);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (APITestException e) {
			e.printStackTrace();
		}

	}
}
