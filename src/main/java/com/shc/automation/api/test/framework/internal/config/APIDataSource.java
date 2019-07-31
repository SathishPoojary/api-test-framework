/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "data-source")
public class APIDataSource {
	String name;
	String url;
	String username;
	String password;
	String defaultSchema;
	String driver;
	String dialect;

	@XmlElement(name = "url")
	public String getUrl() {
		return url == null ? null : url.trim();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlElement(name = "username")
	public String getUsername() {
		return username == null ? null : username.trim();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "password")
	public String getPassword() {
		return password == null ? null : password.trim();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlElement(name = "schema")
	public String getDefaultSchema() {
		return defaultSchema == null ? null : defaultSchema.trim();
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public String getName() {
		return name == null ? null : name.trim();
	}
	
	@XmlElement(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "driver")
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@XmlElement(name = "dialect")
	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

}
