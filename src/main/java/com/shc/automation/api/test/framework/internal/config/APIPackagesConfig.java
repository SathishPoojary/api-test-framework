/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author spoojar
 *
 */

@XmlRootElement( name = "packages" )
public class APIPackagesConfig {
	List<APIPackageConfig> packages;

	public List<APIPackageConfig> getPackages() {
		return packages;
	}

	@XmlElement( name = "package" )
	public void setPackages(List<APIPackageConfig> packages) {
		this.packages = packages;
	}
}
