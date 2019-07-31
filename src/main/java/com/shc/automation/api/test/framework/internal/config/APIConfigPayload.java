package com.shc.automation.api.test.framework.internal.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author spoojar
 *
 */

@XmlRootElement(name = "payload")
class APIConfigPayload {
	String type;
	String payLoad;
	String recordRange;
	String scenarioNameFields;
	String sourceName;
	String sourceType;
	private String templateSource;

	public String getType() {
		return type == null ? null : type.trim();
	}

	@XmlAttribute(name = "payload-type")
	public void setType(String type) {
		this.type = type;
	}

	public String getPayLoad() {
		return payLoad == null ? null : payLoad.trim();
	}

	@XmlValue
	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
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

	public String getTemplateSource() {
		return this.templateSource;
	}

	@XmlAttribute(name = "template-source")
	public void setTemplateSource(String templateSource) {
		this.templateSource = templateSource;
	}

}
