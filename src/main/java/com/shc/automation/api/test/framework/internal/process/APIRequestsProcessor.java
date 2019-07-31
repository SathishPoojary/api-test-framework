/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.process.APIBeforeRequestProcess;

/**
 * @author spoojar
 *
 */
public class APIRequestsProcessor {

	public List<APITestRequestItem> generateAPIRequests(APITestRequest request) throws APITestException {
		beforeRequest(request);
		request.setServiceUrl(APIUrlProcessor.getServiceUrl(request));

		APISourceDataProcessor dataProcessor = new APISourceDataProcessor();

		Map<String, Map<String, Object>> urlParamRecords = dataProcessor.getDataRecords(request.getUrlParamInputSource(), null);
		urlParamRecords = APIExternalDataProcessor.updateExternalParams(request, urlParamRecords);

		Map<String, Map<String, Object>> payloadRecords = dataProcessor.getDataRecords(request.getPayloadInputSource(), null);
		payloadRecords = APIExternalDataProcessor.updateExternalPayloads(request, payloadRecords);

		List<APITestRequestItem> requests = dataProcessor.processDataRecords(request, urlParamRecords, payloadRecords);

		return requests;
	}

	private void beforeRequest(APITestRequest request) {
		APIBeforeRequestProcess testProcess = getBeforeRequestProcess(request.getBeforeRequestProcess());
		if (testProcess != null) {
			String externalUrl = testProcess.generateServiceUrl();
			if (StringUtils.isNotBlank(externalUrl)) {
				request.setBaseUrl(externalUrl);
			}
			String externalPath = testProcess.generateServicePath();
			if (StringUtils.isNotBlank(externalPath)) {
				if (StringUtils.isNoneBlank(request.getRequestPath())) {
					externalPath = request.getRequestPath() + externalPath;
				}
				request.setRequestPath(externalPath);
			}

			Map<String, Map<String, Object>> urlParams = testProcess.generateUrlParams();
			if (MapUtils.isNotEmpty(urlParams)) {
				if (MapUtils.isNotEmpty(request.getExternalUrlParams()))
					request.getExternalUrlParams().putAll(urlParams);
				else
					request.setExternalUrlParams(urlParams);
			}

			Map<String, String> headerParams = testProcess.generateRequestHeaders();
			if (MapUtils.isNotEmpty(headerParams)) {
				if (MapUtils.isNotEmpty(request.getHeaderParameters()))
					request.getHeaderParameters().putAll(headerParams);
				else
					request.setHeaderParameters(headerParams);
			}

			Map<String, String> payloads = testProcess.generatePayloads();
			if (MapUtils.isNotEmpty(payloads)) {
				if (MapUtils.isNotEmpty(request.getExternalPayload()))
					request.getExternalPayload().putAll(payloads);
				else
					request.setExternalPayload(payloads);
			}
			Map<String, Map<String, Object>> scenarioContextValues = testProcess.generateScenarioContext();
			if (MapUtils.isNotEmpty(scenarioContextValues)) {
				request.setContextValues(scenarioContextValues);
			}
		}
	}

	public static APIBeforeRequestProcess getBeforeRequestProcess(String preProcess) {
		if (StringUtils.isBlank(preProcess))
			return null;
		try {
			return (APIBeforeRequestProcess) Class.forName(preProcess).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
