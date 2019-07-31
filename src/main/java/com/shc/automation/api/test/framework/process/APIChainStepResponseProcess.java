/**
 * 
 */
package com.shc.automation.api.test.framework.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.Cookie;

import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.internal.process.APIUrlProcessor;
import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * This class is used to set up or populate the API test request for the next
 * step in the chain execution. It uses the API response from the current step
 * to generate input parameters, PayLoad and/or headers for the next step. In
 * addition, it provides methods to get/set values to/from context which can be
 * used across the chain.
 * 
 * @author sathish_poojary
 * 
 */
public class APIChainStepResponseProcess extends APIAfterResponseProcess {
	protected APITestResponseItem stepResponseItem;

	/**
	 * Create the process on the current step response Item. This item will be used
	 * to generate the inputs for the next step and update the context.
	 * 
	 * @param stepResponseItem
	 */
	public APIChainStepResponseProcess(APITestResponseItem stepResponseItem) {
		super(stepResponseItem);
		this.stepResponseItem = stepResponseItem;
	}

	/**
	 * Process URL Parameters for the next step API Request
	 * 
	 * @return Map of URL Parameter name and Parameter object.
	 */
	public Map<String, Object> processUrlParamsForNextStep() {
		return null;
	}

	/**
	 * Process the Payload for the next step API Request
	 * 
	 * @return Payload string. By default, pass the whole response content
	 */
	public Object processPayloadForNextStep() {
		return null;
	}

	/**
	 * Process Header parameters for the next step API Request
	 * 
	 * @return Map of Header Parameter Name and Parameter Value
	 */
	public Map<String, String> processHeaderForNextStep() {
		return null;
	}

	/**
	 * Cookies to be added to the next request to maintain the state
	 * 
	 * @return Array of Cookies
	 */
	public List<Cookie> processCookiesForNextStep() {
		return null;
	}

	/**
	 * Store data in context for future test steps
	 * 
	 * @return
	 */
	public Map<String, Object> updateTestChainContext() {
		return null;
	}

	/**
	 * Return chain context value for key
	 * 
	 * @param key
	 * @return
	 */
	public Object getContextValue(String key) {
		Map<String, Object> chainTestContext = responseItem.getContext();
		if (chainTestContext != null) {
			return chainTestContext.get(key);
		}
		System.out.println("Context not set. Return NULL for key :" + key);
		return null;
	}

	/**
	 * Set the overall chain context
	 * 
	 * @param chainTestContext
	 */
	public void setChainTestContext(final Map<String, Object> chainTestContext) {
		if (chainTestContext == null)
			return;
		responseItem.getContext().putAll(chainTestContext);
	}

	/**
	 * Get the chain context for the current chain test
	 * 
	 * @return
	 */
	public Map<String, Object> getChainTestContext() {
		return responseItem.getContext();
	}

	/**
	 * Update request item for the next step
	 * 
	 * @param nextStepRequestItem
	 */
	public void process(APITestRequestItem nextStepRequestItem) {
		if (nextStepRequestItem == null) {
			return;
		}

		Object payloadObj = processPayloadForNextStep();
		if (payloadObj != null) {
			String payload = payloadObj.toString();
			if (StringUtils.isNotBlank(payload)) {
				nextStepRequestItem.setPayload(payload);
			}
		}

		Map<String, String> headers = processHeaderForNextStep();
		if (MapUtils.isNotEmpty(headers)) {
			if (nextStepRequestItem.getHeaders() == null) {
				nextStepRequestItem.setHeaders(headers);
			} else {
				nextStepRequestItem.getHeaders().putAll(headers);
			}
		}

		Map<String, Object> context = responseItem.getContext();
		if (MapUtils.isNotEmpty(context)) {
			context.putAll(nextStepRequestItem.getScenarioContext());
		} else {
			context = nextStepRequestItem.getScenarioContext();
		}

		Map<String, Object> processContext = updateTestChainContext();
		if (MapUtils.isNotEmpty(processContext)) {
			context.putAll(processContext);
		}
		nextStepRequestItem.setScenarioContext(context);

		Map<String, Object> urlParameters = new HashMap<String, Object>(context);
		Map<String, Object> urlParams = processUrlParamsForNextStep();
		if (MapUtils.isNotEmpty(urlParams)) {
			urlParameters.putAll(urlParams);
		}
		new APIUrlProcessor().updateURLParams(nextStepRequestItem, urlParameters);
		
		String payload = nextStepRequestItem.getPayload();
		if (StringUtils.isNotBlank(payload)) {
			payload = APITestUtils.replaceValuesInTemplate(payload, context);
			nextStepRequestItem.setPayload(payload);
		}

		List<Cookie> stepCookies = processCookiesForNextStep();
		List<Cookie> cookies = responseItem.getCookies();
		if (stepCookies != null) {
			if (cookies != null)
				cookies.addAll(stepCookies);
			else
				cookies = stepCookies;

			responseItem.setCookies(cookies);
		}
	}

}
