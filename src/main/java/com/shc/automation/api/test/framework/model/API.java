package com.shc.automation.api.test.framework.model;

import com.shc.automation.api.test.framework.internal.config.xml.APIConfigParam;
import com.shc.automation.api.test.framework.internal.config.xml.APIConfigUrlParam;
import com.shc.automation.api.test.framework.model.request.APIRequestParameter;
import com.shc.automation.api.test.framework.model.request.ParameterType;
import com.shc.automation.api.test.framework.model.request.RequestType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity class to define an API. It specifies all API details such as API URL,
 * end points, type of requests it take, Parameters and PayLoad and Throttling.
 * 
 * @author spoojar
 *
 */
public class API implements Serializable {
	private static final long serialVersionUID = 8221698581213966622L;
	private String apiName;
	private String testEnvironment;
	private String serviceEndPointName;
	private RequestType requestType;
	private ParameterType urlParameterType;
	private String requestPath;
	private List<APIRequestParameter> urlParameters;
	private Map<String, String> headerParameters;
	private String payLoad;
	private String payLoadType;
	private Double requestsPerSecond;
	private Integer socketTimeout;
	private String endpointVersion;

	private String baseUrl;

	public API() {
		urlParameters = new ArrayList<>();
		headerParameters = new HashMap<>();
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getTestEnvironment() {
		return testEnvironment == null ? "" : testEnvironment;
	}

	public void setTestEnvironment(String environment) {
		this.testEnvironment = environment;
	}

	public String getServiceEndPointName() {
		return serviceEndPointName;
	}

	public void setServiceEndPointName(String serviceEndPointName) {
		this.serviceEndPointName = serviceEndPointName;
	}
	

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public ParameterType getUrlParameterType() {
		return urlParameterType;
	}

	public void setUrlParameterType(ParameterType urlParameterType) {
		this.urlParameterType = urlParameterType;
	}

	public List<APIRequestParameter> getUrlParameters() {
		return urlParameters;
	}

	public void setUrlParameters(List<APIRequestParameter> urlParameters) {
		this.urlParameters = urlParameters;
	}

	public void setUrlParameters(APIConfigUrlParam urlParamsConfig) {
		List<APIConfigParam> configParamList = urlParamsConfig.getUrlParameters();

		if (CollectionUtils.isEmpty(configParamList)) {
			return;
		}

		for (APIConfigParam param : configParamList) {
			String name = param.getName();
			String value = param.getValue();
			String typeStr = param.getType();

			ParameterType type = this.urlParameterType;
			if (StringUtils.isNotBlank(typeStr) && !"default".equalsIgnoreCase(typeStr)) {
				type = ParameterType.getParameterType(typeStr);
			}
			if (type == ParameterType.noname && StringUtils.isBlank(param.getName())) {
				name = String.valueOf(Math.random());
			}

			APIRequestParameter parameter = new APIRequestParameter(name, value, type);
			parameter.setEncodeValue(param.getEncode());

			boolean override = param.getOverride();
			if (!override) {
				if (StringUtils.isBlank(param.getColumn()))
					parameter.setInputColumnName(param.getName());
				else
					parameter.setInputColumnName(param.getColumn());
			} else {
				parameter.setOverride(override);
			}

			this.urlParameters.add(parameter);
		}
	}

	public void setHeaderParameters(List<APIConfigParam> headerParametersConfigList){
		for (APIConfigParam param : headerParametersConfigList) {
			headerParameters.put(param.getName(), param.getValue());
		}
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public void setHeaderParameters(Map<String, String> headerParameters) {
		this.headerParameters = headerParameters;
	}

	public String getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}

	public String getPayLoadType() {
		return payLoadType;
	}

	public void setPayLoadType(String payLoadType) {
		this.payLoadType = payLoadType;
	}

	public Double getRequestsPerSecond() {
		return requestsPerSecond;
	}

	public void setRequestsPerSecond(Double requestsPerSecond) {
		this.requestsPerSecond = requestsPerSecond;
	}

	public String toString() {
		StringBuilder stringBuff = new StringBuilder("\nAPI Name :" + apiName);
		if (!StringUtils.isEmpty(testEnvironment))
			stringBuff.append("\nURL :" + testEnvironment);
		if (!StringUtils.isEmpty(serviceEndPointName))
			stringBuff.append("\nService End Point :" + serviceEndPointName);
		stringBuff.append("Request  Type :" + requestType);
		if (!StringUtils.isEmpty(requestPath))
			stringBuff.append("    Path :" + requestPath);
		if (!StringUtils.isEmpty(endpointVersion))
			stringBuff.append("    Endpoint Version :" + endpointVersion);
		if (urlParameters != null && !urlParameters.isEmpty())
			stringBuff.append("\n  URL Parameters :" + urlParameters);
		if (!StringUtils.isEmpty(payLoad))
			stringBuff.append("\n  Payload :" + payLoad);
		stringBuff.append("\nThrottling (Request/Sec) :" + (requestsPerSecond == null ? "None" : requestsPerSecond));

		return stringBuff.toString();

	}

	public void addUrlParameter(APIRequestParameter parameter) {
		urlParameters.add(parameter);
	}

	public void addHeaderParameter(String name, String value) {
		this.headerParameters.put(name, value);

	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getEndpointVersion() {
		return endpointVersion;
	}

	public void setEndpointVersion(String endpointVersion) {
		this.endpointVersion = endpointVersion;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}