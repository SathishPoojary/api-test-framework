package com.shc.automation.api.test.framework.internal.config;

import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.config.injector.APIDependencyInjector;
import com.shc.automation.api.test.framework.internal.config.xml.APIDataSourceConnectConfig;
import com.shc.automation.api.test.framework.internal.config.xml.APIXMLConfigFactory;
import com.shc.automation.utils.PasswordEncryptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import java.util.Map;

@Singleton
public class APIDataSourceConfigManager {
    private final Logger logger = Logger.getLogger("APIDataSourceConfigManager");

    private final String API_DEFAULT_SQL_DATA_SOURCE = "API-Default-SQL";
    private final String API_INTERNAL_SQL_CONFIG = "sql-data-source.xml";
    private final String API_INTERNAL_NoSQL_CONFIG = "nosql-data-source.xml";

    private Map<String, APIDataSourceConnectConfig> projectDataSources;

    public APIDataSourceConfigManager() {
        projectDataSources = APIDependencyInjector.INSTANCE.getInstance(APIXMLConfigFactory.class).getProjectDataSources();
    }

    public APIDataSourceConnectConfig getDataSource(String dataSourceName) {
        if (StringUtils.isBlank(dataSourceName)) {
            logger.warn("Empty data source name. Not able to retrieve the Data Source");
            return null;
        }
        return projectDataSources.get(dataSourceName.trim());
    }

    public String getDefaultSQLDataSourceName() {
        return API_DEFAULT_SQL_DATA_SOURCE;
    }

    public String getDefaultNoSQLDataSourceName() {
        return API_INTERNAL_NoSQL_CONFIG;
    }

    public String getNoSQLDataSource() {
        String source = APITestContext.get().getExecutionConfig().getNoSqlConnectionName();
        if (StringUtils.isBlank(source)) {
            source = getDefaultNoSQLDataSourceName();
        }
        return source;
    }

    public String getSQLDataSource() {
        String source = APITestContext.get().getExecutionConfig().getSqlConnectionName();
        if (StringUtils.isBlank(source)) {
            source = getDefaultSQLDataSourceName();
        }
        return source;
    }

    public APIDataSourceConnectConfig getNoSQLConfig(String conn) {
        APIDataSourceConnectConfig connection = getDataSource(conn);
        connection.setPassword(decrypt(connection.getPassword()));

        return connection;
    }

    public Configuration getSQLConfig(String conn) {
        APIDataSourceConnectConfig connection = getDataSource(conn);
        Configuration config = new Configuration().configure(API_INTERNAL_SQL_CONFIG);

        if (connection != null && StringUtils.isNotBlank(connection.getUrl())) {
            String url = connection.getUrl();

            config.setProperty("connection.url", url);
            config.setProperty("hibernate.connection.url", url);

            String username = connection.getUsername();
            if (StringUtils.isNotBlank(username)) {
                config.setProperty("connection.username", username);
                config.setProperty("hibernate.connection.username", username);
            }

            String password = connection.getPassword();
            if (StringUtils.isNotBlank(password)) {
                config.setProperty("connection.password", password);
                config.setProperty("hibernate.connection.password", password);
            }

            String schema = connection.getDefaultSchema();
            if (StringUtils.isNotBlank(schema)) {
                config.setProperty("hibernate.default_schema", schema);
            }
            String driver = connection.getDriver();
            if (StringUtils.isNotBlank(driver)) {
                config.setProperty("hibernate.connection.driver_class", driver);
            }

            String dialect = connection.getDialect();
            if (StringUtils.isNotBlank(dialect)) {
                config.setProperty("dialect", dialect);
            }
        }
        decryptPassowrd(config);
        return config;

    }

    private void decryptPassowrd(Configuration config) {
        config.setProperty("connection.password", decrypt(config.getProperty("connection.password")));
        config.setProperty("hibernate.connection.password", decrypt(config.getProperty("connection.password")));
        config.setProperty("hibernate.connection.password", decrypt(config.getProperty("hibernate.connection.password")));
    }

    private String decrypt(String password) {
        if (StringUtils.isNotBlank(password) && password.startsWith("ENC(")) {
            return new PasswordEncryptor().decrypt(password);
        }
        return password;
    }
}
