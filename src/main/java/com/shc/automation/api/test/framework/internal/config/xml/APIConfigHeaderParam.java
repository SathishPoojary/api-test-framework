/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author spoojar
 *
 */

@XmlRootElement(name = "header-params")
public class APIConfigHeaderParam {
	List<APIConfigParam> headerParameters;
	String recordRange;
	String scenarioNameFields;
	String sourceName;
	String sourceType;

	public List<APIConfigParam> getHeaderParameters() {
		return headerParameters;
	}

	@XmlElement(name="param")
	public void setHeaderParameters(List<APIConfigParam> headerParameters) {
		this.headerParameters = headerParameters;
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
