/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.RequestType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.process.APIPayloadProcessor;
import com.shc.automation.api.test.framework.internal.process.APIUrlProcessor;

/**
 * @author spoojar
 *
 */
public class APIRequestItemGenerator {
	protected final static Logger log = Logger.getLogger("APIRequestItemGenerator");
	private APIUrlProcessor urlProcessor = null;
	private APIPayloadProcessor payloadProcessor = null;

	public APIRequestItemGenerator() {
		urlProcessor = new APIUrlProcessor();
		payloadProcessor = new APIPayloadProcessor();
	}

	public APITestRequestItem createRequestItem(APITestRequest request, String scenario, Map<String, Object> urlRecord, Map<String, Object> payloadRecord)
			throws APITestException {
		APITestRequestItem requestItem = new APITestRequestItem();
		requestItem.setScenarioName(scenario);
		requestItem.setRequestType(request.getRequestType());

		if (urlRecord == null) {
			requestItem.setUrlParameters(request.getUrlParameters());
		} else {
			urlProcessor.updateURLParams(requestItem, request, urlRecord);
		}

		if (StringUtils.isEmpty(requestItem.getUrl())) {
			requestItem.setUrl(request.getServiceUrl());
		}

		if (request.getRequestType() != RequestType.get) {
			payloadProcessor.updatePayload(request, requestItem, payloadRecord);
		}

		requestItem.setSocketTimeout(request.getApiToTest().getSocketTimeout());
		requestItem.setHeaders(request.getHeaderParameters());
		requestItem.setValidStatusCodes(request.getValidStatusCodes());

		if (!APITestContext.get().getTestConfig().getTurnOffResponseParsing() && !APITestContext.get().getTestConfig().getTurnOffValidation()) {
			setValidators(requestItem, request.getValidationFields());
		}

		APIContextHelper contextHelper = new APIContextHelper();
		contextHelper.updateContextFromInput(request, requestItem.getScenarioContext(), urlRecord, request.getUrlParamInputSource());
		contextHelper.updateContextFromInput(request, requestItem.getScenarioContext(), payloadRecord, request.getPayloadInputSource());
		if (MapUtils.isNotEmpty(request.getContextValues()) && MapUtils.isNotEmpty(request.getContextValues().get(scenario))) {
			requestItem.getScenarioContext().putAll(request.getContextValues().get(scenario));
		}
		return requestItem;
	}

	private void setValidators(APITestRequestItem requestItem, List<APIValidationField> validators) {
		List<APIValidationField> validatorList = new ArrayList<APIValidationField>();
		if (CollectionUtils.isEmpty(validators)) {
			return;
		}

		for (int i = 0; i < validators.size(); i++) {
			APIValidationField validator = validators.get(i).getCopy();
			validatorList.add(validator);

		}
		requestItem.setValidators(validatorList);
	}
}
