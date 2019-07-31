/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author spoojar
 * 
 */
public class APIPrintField implements Serializable {

	private static final long serialVersionUID = 4157307723304302651L;

	protected String printName;
	protected String responsePath;
	protected Object responseValue;
	private String source;

	public APIPrintField() {
	}

	public APIPrintField getCopy() {
		APIPrintField print = new APIPrintField();
		print.printName = this.printName;
		print.responsePath = this.responsePath;
		print.responseValue = this.responseValue;
		print.source = this.source;

		return print;
	}

	public APIPrintField(String name, String path) {
		this(name, path, null);
	}

	public APIPrintField(String name, String path, String value) {
		this.printName = name;
		this.responsePath = path;
		this.responseValue = value;
	}

	public String getPrintName() {
		if (StringUtils.isBlank(printName))
			return APITestUtils.getAbsoluteResponsePath(responsePath);
		return printName;
	}

	public void setPrintName(String fieldName) {
		this.printName = fieldName;
	}

	public String getResponsePath() {
		return responsePath;
	}

	public void setResponsePath(String fieldPath) {
		this.responsePath = fieldPath;
	}

	public Object getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(Object fieldValue) {
		this.responseValue = fieldValue;
	}

	public String toString() {
		if (StringUtils.isNotBlank(printName))
			return printName + " = " + responseValue;
		else
			return APITestUtils.getAbsoluteResponsePath(responsePath) + " = " + responseValue;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
