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
@XmlRootElement(name = "data-sources")
public class APIDataSources {
	List<APIDataSource> dataSources;
	
	public List<APIDataSource> getDataSources() {
		return dataSources;
	}

	@XmlElement( name = "data-source" )
	public void setDataSources(List<APIDataSource> dataSources) {
		this.dataSources = dataSources;
	}
	

}
