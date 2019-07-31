package com.shc.automation.api.test.framework.internal.config;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Properties;

@Singleton
public class APIPropertyQueryConfig {
    private Logger log = Logger.getLogger("APIPropertyQueryConfig");
    private static final String API_DATA_DB_QUERIES = "api/api-data-db.properties";
    private static final String API_DATA_FILES = "api/api-data-file.properties";
    private static final String MESSAGE_FILE = "api/messages.properties";

    private Properties dbQueries;
    private Properties fileQueries;
    private Properties messages;

    public APIPropertyQueryConfig() {
        dbQueries = new Properties();
        fileQueries = new Properties();
        messages = new Properties();
        loadProperties(dbQueries, API_DATA_DB_QUERIES);
        loadProperties(fileQueries, API_DATA_FILES);
        loadProperties(messages, MESSAGE_FILE);
    }

    private void loadProperties(Properties properties, String fileSource) {
        try {
            properties.load(APIPropertyQueryConfig.class.getClassLoader().getResourceAsStream(fileSource));
            System.out.println("Property loaded successfully :" + fileSource);
        } catch (Exception e) {
            log.error("Error loading the Properties file :" + fileSource, e);
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
            log.info("No File configured with name :" + fileQuery);
            return null;
        }
        return fileQueries.getProperty(fileQuery);
    }

    public String getMessage(String key) {
        if (messages == null || StringUtils.isBlank(messages.getProperty(key))) {
            log.info("No Message configured with name :" + key);
            return null;
        }
        return messages.getProperty(key);
    }


}
