package com.shc.automation.api.test.framework.internal.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "api-config")
class APIConfiguations {
	List<APIConfiguration> apiConfigurations;

	public List<APIConfiguration> getApiConfigurations() {
		return apiConfigurations;
	}

	@XmlElement(name = "api")
	public void setApiConfigurations(List<APIConfiguration> apiConfigurations) {
		this.apiConfigurations = apiConfigurations;
	}

}