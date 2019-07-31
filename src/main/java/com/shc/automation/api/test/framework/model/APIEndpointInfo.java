package com.shc.automation.api.test.framework.model;

import java.io.Serializable;

/**
 * <p>
 * This object holds end point details
 * </p>
 * 
 * @author Alleiah Andalamala
 *
 */
public class APIEndpointInfo implements Serializable {

	private static final long serialVersionUID = 5317427177475673459L;
	private String name;
	private String version;
	private String environment;
	private String baseUrl;
	private String testfunctionName;
	private String testSubFunctionName;

	public APIEndpointInfo(String name, String environment, String baseUrl) {
		this.name = name;
		this.environment = environment;
		this.baseUrl = baseUrl;
	}

	public APIEndpointInfo(String name, String environment, String baseUrl, String testfunctionName) {
		this.name = name;
		this.environment = environment;
		this.baseUrl = baseUrl;
		this.testfunctionName = testfunctionName;
	}

	public APIEndpointInfo(String name, String version, String environment, String baseUrl, String testfunctionName) {
		this.name = name;
		this.version = version;
		this.environment = environment;
		this.baseUrl = baseUrl;
		this.testfunctionName = testfunctionName;
	}

	public APIEndpointInfo(String name, String version, String environment, String baseUrl, String testfunctionName,
			String testSubFunctionName) {
		this.name = name;
		this.version = version;
		this.environment = environment;
		this.baseUrl = baseUrl;
		this.testfunctionName = testfunctionName;
		this.testSubFunctionName = testSubFunctionName;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getTestfunctionName() {
		return testfunctionName;
	}

	public String getTestSubFunctionName() {
		return testSubFunctionName;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}
