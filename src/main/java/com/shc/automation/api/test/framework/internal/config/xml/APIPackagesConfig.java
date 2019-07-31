/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
