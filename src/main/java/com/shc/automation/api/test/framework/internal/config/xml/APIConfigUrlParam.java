package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author spoojar
 *
 */

@XmlRootElement(name = "url-params")
public class APIConfigUrlParam {
	String type;
	List<APIConfigParam> urlParameters;
	String recordRange;
	String scenarioNameFields;
	String sourceName;
	String sourceType;

	public String getType() {
		return type;
	}

	@XmlAttribute(name = "param-type")
	public void setType(String type) {
		this.type = type == null ? null : type.trim();
	}

	public List<APIConfigParam> getUrlParameters() {
		return urlParameters;
	}

	@XmlElement(name = "param")
	public void setUrlParameters(List<APIConfigParam> urlParameters) {
		this.urlParameters = urlParameters;
	}

	public String getRecordRange() {
		return recordRange == null ? null : recordRange.trim();
	}

	@XmlAttribute(name = "records-range")
	public void setRecordRange(String recordRange) {
		this.recordRange = recordRange;
	}

	public String getScenarioNameFields() {
		return scenarioNameFields == null ? null : scenarioNameFields.trim();
	}

	@XmlAttribute(name = "scenario-name-fields")
	public void setScenarioNameFields(String scenarioNameFields) {
		this.scenarioNameFields = scenarioNameFields;
	}

	public String getSourceName() {
		return sourceName == null ? null : sourceName.trim();
	}

	@XmlAttribute(name = "source-name")
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceType() {
		return sourceType == null ? null : sourceType.trim();
	}

	@XmlAttribute(name = "source-type")
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}
