package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "api")
public class APIConfiguration {
	@Deprecated
	String url;
	String name;
	String type;
	String defaultEnvironment;
	String endpointVersion;
	String path;
	String endPointName;
	Double throttle;
	APIConfigPayload payLoad;
	Integer responseWaitTime;
	APIConfigUrlParam urlParamConfig;
	APIConfigHeaderParam headerParameters;

	public String getName() {
		return name != null ? name.trim() : null;
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type == null ? null : type.trim();
	}

	@XmlAttribute(name = "type")
	public void setType(String type) {
		this.type = type;
	}
	
	@Deprecated
	public String getUrl() {
		return url == null ? null : url.trim();
	}

	@Deprecated
	@XmlElement(name = "url")
	public void setUrl(String url) {
		this.url = url;
	}

	public String getDefaultEnvironment() {
		return defaultEnvironment == null ? null : defaultEnvironment.trim();
	}

	@XmlElement(name = "default-environment")
	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}

	public String getPath() {
		return path == null ? null : path.trim();
	}

	@XmlElement(name = "path")
	public void setPath(String path) {
		this.path = path;
	}

	public String getEndPointName() {
		return endPointName == null ? null : endPointName.trim();
	}

	@XmlElement(name = "end-point")
	public void setEndPointName(String endPointName) {
		this.endPointName = endPointName;
	}

	public Double getThrottle() {
		return throttle;
	}

	@XmlElement(name = "request-per-sec")
	public void setThrottle(Double throttle) {
		this.throttle = throttle;
	}

	public APIConfigPayload getPayLoad() {
		return payLoad;
	}

	@XmlElement(name = "payload")
	public void setPayLoad(APIConfigPayload payLoad) {
		this.payLoad = payLoad;
	}

	public Integer getResponseWaitTime() {
		return responseWaitTime;
	}

	@XmlElement(name = "socket-timeout")
	public void setResponseWaitTime(Integer responseWaitTime) {
		this.responseWaitTime = responseWaitTime;
	}

	public APIConfigUrlParam getUrlParameters() {
		return urlParamConfig;
	}

	@XmlElement(name = "url-params")
	public void setUrlParameters(APIConfigUrlParam urlParamConfig) {
		this.urlParamConfig = urlParamConfig;
	}

	public APIConfigHeaderParam getHeaderParameters() {
		return headerParameters;
	}

	@XmlElement(name = "header-params")
	public void setHeaderParameters(APIConfigHeaderParam headerParameters) {
		this.headerParameters = headerParameters;
	}

	public String getEndpointVersion() {
		return endpointVersion == null ? null : endpointVersion.trim();
	}

	@XmlElement(name = "endpoint-version")
	public void setEndpointVersion(String endpointVersion) {
		this.endpointVersion = endpointVersion;
	}

}