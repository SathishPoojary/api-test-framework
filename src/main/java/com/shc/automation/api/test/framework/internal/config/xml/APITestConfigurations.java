package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "api-test-suite")
class APITestConfigurations {
	String name;
	String apiConfigFile;
	List<APITestConfiguration> apiTestConfigurations;

	public String getName() {
		return name == null ? null : name.trim();
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getApiConfigFile() {
		return apiConfigFile == null ? null : apiConfigFile.trim();
	}

	@XmlAttribute(name = "api-config-file")
	public void setApiConfigFile(String apiConfigFile) {
		this.apiConfigFile = apiConfigFile;
	}

	public List<APITestConfiguration> getApiTestConfigurations() {
		return apiTestConfigurations;
	}

	@XmlElement(name = "api-test")
	public void setApiTestConfigurations(List<APITestConfiguration> apiTestConfigurations) {
		this.apiTestConfigurations = apiTestConfigurations;
	}

}
