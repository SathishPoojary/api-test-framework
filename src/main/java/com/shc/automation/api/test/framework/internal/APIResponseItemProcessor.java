package com.shc.automation.api.test.framework.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.process.APISourceDataProcessor;
import com.shc.automation.api.test.framework.internal.validators.APIResponseValidator;
import com.shc.automation.api.test.framework.process.APIAfterResponseProcess;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class APIResponseItemProcessor {
	protected Logger log = Logger.getLogger(this.getClass().getName());

	public APITestResponseItem create(final APITestRequestItem requestItem, final CloseableHttpResponse response, final HttpRequestBase method,
			final String url, final APITestException exception, final long elapsedTime, final int retryCount) {
		APITestResponseItem responseItem = new APITestResponseItem();

		try {
			responseItem.setRequestUrl(url);
			responseItem.setBaseUrl(requestItem.getUrl());
			responseItem.setExecutionTime(elapsedTime);
			responseItem.setRetryCount(retryCount);
			responseItem.setApiError(exception);
			responseItem.setPayLoad(requestItem.getPayload());
			responseItem.setScenarioName(requestItem.getScenarioName());
			responseItem.setUrlParameters(requestItem.getUrlParameters());
			responseItem.setValidStatusCodes(requestItem.getValidStatusCodes());
			responseItem.setRequestType(requestItem.getRequestType());
			responseItem.setContext(requestItem.getScenarioContext());

			if (response != null) {

				responseItem.setResponseContent(getBody(response));
				responseItem.setResponseCode(response.getStatusLine().getStatusCode());
				responseItem.setReason(response.getStatusLine().getReasonPhrase());
				responseItem.setRequestHeaders(requestItem.getHeaders());
				responseItem.setResponseHeaders(getResponseHeaders(response));
				Header[] contentType = response.getHeaders("Content-type");
				String contentTypeStr = null;
				if (contentType != null && contentType.length > 0) {
					contentTypeStr = contentType[0].getValue();
					responseItem.setResponseType(contentTypeStr);
				}

			}

			if (responseItem.getValidResponse()) {
				responseItem.setValidators(requestItem.getValidators());
			}

			responseItem.setPrintContentInReport(APITestContext.get().getTestConfig().getPrintJsonResponseInReport());
		} catch (Exception e) {
			e.printStackTrace();
			responseItem.setApiError(e);
		}

		return responseItem;
	}

	public APITestResponseItem update(APITestRequest request, APITestResponseItem responseItem) throws APITestException {
		responseItem.setEnvironment(request.getEnvironment());
		responseItem.setEndpoint(request.getServiceEndPointName());
		Boolean turnOffParsing = APITestContext.get().getTestConfig().getTurnOffResponseParsing();
		if (turnOffParsing || responseItem.getResponseContent() == null) {
			responseItem.setResult((responseItem.getValidResponse() ? ResultType.PASSED : ResultType.FAILED));
			return responseItem;
		} else {
			try {
				processResponseContent(responseItem);
			} catch (IOException e) {
				e.printStackTrace();
				throw new APITestException("Error in processing API Response for scenario :" + responseItem.getScenarioName(), e);
			}

		}
		if (!responseItem.getValidResponse()) {
			responseItem.setResult(ResultType.FAILED);
			return responseItem;
		}

		APIAfterResponseProcess postTestProcess = getAfterResponseProcess(request.getAfterResponseProcess(), responseItem);
		if (postTestProcess != null) {
			responseItem.setResponseToCompare(postTestProcess.updateResponseForComparison());
		}
		updateContext(request, responseItem);
		validate(responseItem, postTestProcess);
		print(responseItem, request, postTestProcess);

		return responseItem;
	}

	private void updateContext(APITestRequest request, APITestResponseItem responseItem) {
		APIContextHelper contextHelper = new APIContextHelper();
		contextHelper.updateContextValuesFromResponse(request, responseItem);
		Map<String, Map<String, Object>> validationRecords = new APISourceDataProcessor().getDataRecords(request.getValidationInputSource(),
				responseItem.getContext());
		Map<String, Object> validationRecord = null;
		if (MapUtils.isNotEmpty(validationRecords)) {
			validationRecord = validationRecords.get(responseItem.getScenarioName());
			if (validationRecord == null) {
				validationRecord = validationRecords.values().iterator().next();
			}
		}
		if (MapUtils.isNotEmpty(validationRecord)) {
			contextHelper.updateContextFromInput(request, responseItem.getContext(), validationRecord, request.getValidationInputSource());
		}
		contextHelper.updateValidationConditionsFromContext(responseItem);
	}

	public APITestResponseItem update(APITestResponseItem responseItem) throws APITestException {
		if (responseItem.getResponseContent() == null) {
			responseItem.setResult((responseItem.getValidResponse() ? ResultType.PASSED : ResultType.FAILED));
			return responseItem;
		}

		try {
			processResponseContent(responseItem);
		} catch (IOException e) {
			e.printStackTrace();
			throw new APITestException("Error in processing API Response for scenario :" + responseItem.getScenarioName(), e);
		}

		if (!responseItem.getValidResponse()) {
			responseItem.setResult(ResultType.FAILED);
		} else {
			validate(responseItem, null);
		}
		return responseItem;
	}

	public static String getBody(CloseableHttpResponse response) throws APITestException {

		if (response.getEntity() == null) {
			throw new APITestException("Error in processing API Response Empty response recieved from API.");
		}
		try {
			String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			return responseBody;
		} catch (IOException e) {
			// log.error("Error in retrieving the response from the Service Response :", e);
			throw new APITestException("Error in processing API Response Empty response recieved from API.");
		} finally {
			try {
				if (response != null) {
					response.close();
					response = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void processResponseContent(APITestResponseItem responseItem) throws IOException {
		String rawResponse = responseItem.getResponseContent() == null ? null : responseItem.getResponseContent().toString();
		responseItem.setResponseContent(getFormattedResponse(rawResponse, responseItem.getResponseType()));
	}

	/**
	 * @return the response
	 * @throws Exception
	 */
	private Object getFormattedResponse(String response, String responseFormat) {
		if (StringUtils.isBlank(response)) {
			return response;
		}

		JSONObject responseJson = null;
		try {
			String jsonString = response;
			log.info("Response Format :" + responseFormat);

			responseFormat = responseFormat == null ? "" : responseFormat;
			if (responseFormat.startsWith("text/xml") || responseFormat.startsWith("application/xml") || jsonString.startsWith("<")) {
				jsonString = JsonUtils.convertXMLToJson(response, true).toString();
			}

			Object cachedResponse = null;

			if (jsonString.trim().startsWith("[")) {
				cachedResponse = JSONArray.fromObject(jsonString);
			} else if (jsonString.toString().startsWith("{")) {
				cachedResponse = JSONObject.fromObject(jsonString);
			} else {
				cachedResponse = jsonString;
			}
			responseJson = APITestUtils.wrapAPIResponse(cachedResponse);

		} catch (Exception e) {
			log.error("Error in Parsing the Response!. Storing response as-is :\n" + e.getMessage());
			responseJson = APITestUtils.wrapAPIResponse(response);
		}
		return responseJson;

	}

	private Boolean validate(APITestResponseItem responseItem, APIAfterResponseProcess postTestProcess) {

		boolean validated = responseItem.getValidResponse();

		APIResponseValidator validator = new APIResponseValidator();
		validated = validated && validator.validate(responseItem, postTestProcess);
		responseItem.setResult((validated ? ResultType.PASSED : ResultType.FAILED));

		return validated;
	}

	private void print(APITestResponseItem responseItem, APITestRequest request, APIAfterResponseProcess postTestProcess) {
		APIResponsePrinter printer = new APIResponsePrinter();
		printer.print(request, responseItem, postTestProcess);
	}

	private APIAfterResponseProcess getAfterResponseProcess(String postProcess, APITestResponseItem responseItem) {
		if (StringUtils.isBlank(postProcess))
			return null;
		try {
			return (APIAfterResponseProcess) Class.forName(postProcess).getConstructor(APITestResponseItem.class).newInstance(responseItem);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, String> getResponseHeaders(CloseableHttpResponse response) {
		Header[] headers = response.getAllHeaders();
		Map<String, String> responseHeaders = new HashMap<String, String>();
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				String headerName = headers[i].getName().replace(".", " ");
				responseHeaders.put(headerName, headers[i].getValue());
			}
		}
		return responseHeaders;
	}

}
