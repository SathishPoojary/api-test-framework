/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "param")
class APIConfigParam {
	String name;
	String value;
	String column;
	boolean override;
	String type;
	boolean encode;

	public String getName() {
		return name == null ? null : name.trim();
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value == null ? null : value.trim();
	}

	@XmlAttribute(name = "value")
	public void setValue(String value) {
		this.value = value;
	}

	public String getColumn() {
		return column == null ? null : column.trim();
	}

	@XmlAttribute(name = "column")
	public void setColumn(String column) {
		this.column = column;
	}

	public boolean getOverride() {
		return override;
	}

	@XmlAttribute(name = "override")
	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getType() {
		return type == null ? null : type.trim();
	}

	@XmlAttribute(name = "type")
	public void setType(String type) {
		this.type = type;
	}

	public boolean getEncode() {
		return encode;
	}

	@XmlAttribute(name = "encode")
	public void setEncode(boolean encode) {
		this.encode = encode;
	}

}
