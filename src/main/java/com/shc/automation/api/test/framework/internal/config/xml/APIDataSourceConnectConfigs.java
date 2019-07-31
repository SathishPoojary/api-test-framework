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
@XmlRootElement(name = "data-sources")
public class APIDataSourceConnectConfigs {
	List<APIDataSourceConnectConfig> dataSources;
	
	public List<APIDataSourceConnectConfig> getDataSources() {
		return dataSources;
	}

	@XmlElement( name = "data-source" )
	public void setDataSources(List<APIDataSourceConnectConfig> dataSources) {
		this.dataSources = dataSources;
	}
	

}
