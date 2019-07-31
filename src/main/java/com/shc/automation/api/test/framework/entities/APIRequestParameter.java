package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;

public class APIRequestParameter implements Serializable {
	private static final long serialVersionUID = 3293845297648598216L;
	private String paramName;
	private Object paramValue;
	private ParameterType type = null;
	private String inputColumnName = null;

	private boolean encodeValue = false;
	private boolean override = false;

	public APIRequestParameter() {

	}

	public APIRequestParameter(String paramName, String paramValue) {
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	public APIRequestParameter(String paramName, String paramValue, ParameterType type) {
		this(paramName, paramValue);
		this.type = type;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getParamValue() {
		return paramValue;
	}

	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}

	public String toString() {
		return "{Name=" + paramName + " Value=" + paramValue + "}";
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public boolean encodeValue() {
		return encodeValue;
	}

	public void setEncodeValue(boolean encodeValue) {
		this.encodeValue = encodeValue;
	}

	public String getInputColumnName() {
		return inputColumnName;
	}

	public void setInputColumnName(String inputColumnName) {
		this.inputColumnName = inputColumnName;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public APIRequestParameter copy() {
		APIRequestParameter copy = new APIRequestParameter();
		copy.paramName = this.paramName;
		copy.paramValue = this.paramValue;
		copy.type = this.type;
		copy.inputColumnName = this.inputColumnName;
		copy.encodeValue = this.encodeValue;
		copy.override = this.override;

		return copy;
	}

}