/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * @author spoojar
 *
 */
public class APITestResponse extends APIResponse {
	private static final long serialVersionUID = -4067682937103437434L;
	private String serviceUrl;
	private Map<String, APITestResponseItem> responseItems = new ConcurrentHashMap<String, APITestResponseItem>();

	public Map<String, APITestResponseItem> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(Map<String, APITestResponseItem> responseItems) {
		this.responseItems = responseItems;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void addResponseItem(String scenario, APITestResponseItem responseItem) {
		this.responseItems.put(scenario, responseItem);
	}

	public String getReportFormat() {
		if (StringUtils.isBlank(reportFormat))
			return "html";
		return reportFormat;
	}
}
