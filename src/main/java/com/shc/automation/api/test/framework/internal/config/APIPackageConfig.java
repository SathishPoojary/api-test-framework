/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.BooleanUtils;

/**
 * @author spoojar
 *
 */
@XmlRootElement(name = "package")
public class APIPackageConfig {
	String packageName;
	String resourceFolder;
	Boolean printInEmailableReport;
	private String sqlConnectionName;
	private String noSqlConnectionName;

	public String getResourceFolder() {
		return resourceFolder == null ? null : resourceFolder.trim();
	}

	@XmlElement(name = "resource-folder")
	public void setResourceFolder(String folder) {
		this.resourceFolder = folder;
	}

	public String getSqlConnectionName() {
		return sqlConnectionName == null ? null : sqlConnectionName.trim();
	}

	@XmlElement(name = "sql-data-source")
	public void setSqlConnectionName(String sqlConnectionName) {
		this.sqlConnectionName = sqlConnectionName;
	}

	public String getNoSqlConnectionName() {
		return noSqlConnectionName == null ? null : noSqlConnectionName.trim();
	}

	@XmlElement(name = "nosql-data-source")
	public void setNoSqlConnectionName(String mongoConnectionName) {
		this.noSqlConnectionName = mongoConnectionName;
	}

	public String getName() {
		return packageName == null ? null : packageName.trim();
	}

	@XmlElement(name = "name")
	public void setName(String name) {
		this.packageName = name;
	}

	public Boolean getPrintInEmailableReport() {
		return BooleanUtils.toBoolean(printInEmailableReport);
	}

	@XmlElement(name = "print-email-report")
	public void setPrintInEmailableReport(Boolean printInEmailableReport) {
		this.printInEmailableReport = printInEmailableReport;
	}
}
