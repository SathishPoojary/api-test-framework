package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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