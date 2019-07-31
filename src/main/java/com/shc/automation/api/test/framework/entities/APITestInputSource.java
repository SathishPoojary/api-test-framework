package com.shc.automation.api.test.framework.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class APITestInputSource {
	private String sourceType;
	private String connectionName;
	private String sourceName;
	private String sourcePath;

	private int fromIndex;
	private int toIndex;

	private List<String> scenarioFields;

	private String payloadTemplate = null;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public int getToIndex() {
		return toIndex;
	}

	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}

	public List<String> getScenarioFields() {
		if (scenarioFields == null)
			scenarioFields = new ArrayList<String>();
		return scenarioFields;
	}

	public void setScenarioFields(List<String> scenarioFields) {
		this.scenarioFields = scenarioFields;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getPayloadTemplate() {
		return payloadTemplate;
	}

	public void setPayloadTemplate(String payloadTemplate) {
		this.payloadTemplate = payloadTemplate;
	}

	public void setRecordRange(String recordRange) {
		if (StringUtils.isBlank(recordRange)) {
			return;
		}
		fromIndex = -1;
		toIndex = -1;
		if (StringUtils.isNotBlank(recordRange)) {
			if (recordRange.indexOf('-') != -1) {
				String from = recordRange.substring(0, recordRange.indexOf('-'));
				String to = recordRange.substring(recordRange.indexOf('-') + 1);

				fromIndex = NumberUtils.isCreatable(from) ? NumberUtils.toInt(from) : -1;
				toIndex = NumberUtils.isCreatable(to) ? NumberUtils.toInt(to) : -1;
			} else {
				fromIndex = NumberUtils.isCreatable(recordRange) ? NumberUtils.toInt(recordRange) : -1;
			}
		}
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public boolean equals(APITestInputSource source) {
		if (this.sourceType != null && !this.sourceType.equals(source.sourceType)) {
			return false;
		}
		if (this.sourceName != null && !this.sourceName.equals(source.sourceName)) {
			return false;
		}
		if (this.sourcePath != null && !this.sourcePath.equals(source.sourcePath)) {
			return false;
		}
		if (this.fromIndex >= 0 && this.fromIndex != source.fromIndex) {
			return false;
		}
		if (this.toIndex >= 0 && this.toIndex != source.toIndex) {
			return false;
		}
		return true;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
}
