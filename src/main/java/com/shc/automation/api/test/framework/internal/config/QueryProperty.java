package com.shc.automation.api.test.framework.internal.config;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestConstants;

public enum QueryProperty {
	INSTANCE;
	private Logger log = Logger.getLogger("QueryProperty");
	private Properties dbQueries;
	private Properties fileQueries;

	private QueryProperty() {
		dbQueries = new Properties();
		fileQueries = new Properties();
		loadProperties(dbQueries, APITestConstants.API_DATA_DB_QUERIES);
		loadProperties(fileQueries, APITestConstants.API_DATA_FILES);
	}

	private void loadProperties(Properties properties, String fileSource) {
		try {
			properties.load(QueryProperty.class.getClassLoader().getResourceAsStream(fileSource));
			System.out.println("Property loaded successfully :"+fileSource);
		} catch (Exception e) {
			log.error("Error loading the Properties file :"+fileSource, e);			
		}
	}

	public String getSQLQuery(String queryName) {
		if (dbQueries == null || StringUtils.isBlank(dbQueries.getProperty(queryName))) {
			log.error("No Query configured with name :" + queryName);
			return null;
		}
		return dbQueries.getProperty(queryName);
	}

	public String getFileName(String fileQuery) {
		if (fileQueries == null || StringUtils.isBlank(fileQueries.getProperty(fileQuery))) {
			log.info("No Query configured with name :" + fileQuery);
			return null;
		}
		return fileQueries.getProperty(fileQuery);
	}

}
