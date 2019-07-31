/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author spoojar
 * 
 */
public class APITestRequestItem {
	private RequestType requestType = RequestType.get;
	private String scenarioName;
	private String url = null;
	private Map<String, String> headers = null;
	private List<APIRequestParameter> urlParameters = null;
	private String payload = null;
	private Integer socketTimeout;
	private List<APIValidationField> validators = null;
	private String validStatusCodes = null;
	private Boolean digitalSignatureRequired = false;
	private Map<String, Object> scenarioContext = null;

	public APITestRequestItem() {
		urlParameters = new ArrayList<APIRequestParameter>();
		validators = new ArrayList<APIValidationField>();
		scenarioContext = new HashMap<String, Object>();
	}

	public List<APIValidationField> getValidators() {
		return validators;
	}

	public void setValidators(List<APIValidationField> validators) {
		this.validators = validators;
	}

	public void addValidator(APIValidationField validator) {
		this.validators.add(validator);
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public List<APIRequestParameter> getUrlParameters() {
		return urlParameters;
	}

	public void setUrlParameters(List<APIRequestParameter> urlParameters) {
		this.urlParameters = urlParameters;
	}

	public void addUrlParameter(APIRequestParameter urlParameter) {
		this.urlParameters.add(urlParameter);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public APITestRequestItem getCopy(int repeatCount) {
		APITestRequestItem copy = new APITestRequestItem();
		if (repeatCount > 0)
			copy.scenarioName = this.scenarioName + " ( Repeat# : " + repeatCount + " ) ";
		else
			copy.scenarioName = this.scenarioName;
		copy.headers = this.headers;
		copy.url = this.url;
		copy.requestType = this.requestType;
		copy.socketTimeout = this.socketTimeout;

		copy.payload = this.payload;

		if (CollectionUtils.isNotEmpty(urlParameters)) {
			for (APIRequestParameter param : urlParameters) {
				copy.addUrlParameter(param.copy());
			}
		}

		if (CollectionUtils.isNotEmpty(validators)) {
			Iterator<APIValidationField> iter = validators.iterator();
			while (iter.hasNext()) {
				copy.addValidator(iter.next().getCopy());
			}
		}
		if (MapUtils.isNotEmpty(scenarioContext)) {
			copy.scenarioContext.putAll(scenarioContext);
		}
		return copy;
	}

	public String getValidStatusCodes() {
		return validStatusCodes;
	}

	public void setValidStatusCodes(String validStatusCodes) {
		this.validStatusCodes = validStatusCodes;
	}

	public Boolean getDigitalSignatureRequired() {
		return digitalSignatureRequired;
	}

	public void setDigitalSignatureRequired(Boolean digitalSignatureRequired) {
		if (digitalSignatureRequired == null)
			digitalSignatureRequired = false;
		this.digitalSignatureRequired = digitalSignatureRequired;
	}

	public Map<String, Object> getScenarioContext() {
		return scenarioContext;
	}

	public void setScenarioContext(Map<String, Object> scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	public void addToContext(String name, Object value) {
		if (StringUtils.isBlank(name)) {
			return;
		}
		scenarioContext.put(name, value);
	}
}
