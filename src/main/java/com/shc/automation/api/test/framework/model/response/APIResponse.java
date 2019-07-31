/**
 * 
 */
package com.shc.automation.api.test.framework.model.response;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author spoojar
 *
 */
public class APIResponse extends APIBaseResponse {
	private static final long serialVersionUID = -4067682937103437434L;
	private String serviceUrl;
	private Map<String, APIScenarioResponse> responseItems = new ConcurrentHashMap<String, APIScenarioResponse>();

	public Map<String, APIScenarioResponse> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(Map<String, APIScenarioResponse> responseItems) {
		this.responseItems = responseItems;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void addResponseItem(String scenario, APIScenarioResponse responseItem) {
		this.responseItems.put(scenario, responseItem);
	}

	public String getReportFormat() {
		if (StringUtils.isBlank(reportFormat))
			return "html";
		return reportFormat;
	}
}
