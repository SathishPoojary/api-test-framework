package com.shc.automation.api.test.framework.internal.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.shc.automation.TestHarnessProperties;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.utils.PasswordEncryptor;

public class APIConfigManager {

	private static final Logger log = Logger.getLogger("APIConfigManager");
	private static Map<String, Object> apiConnectionFactory = new HashMap<String, Object>();

	public static void configurePackage(String packageName) {
		APIPackageConfig config = APITestContext.get().getTestConfig().getTestPackageConfig();
		APIPackageConfig packageConfig = APIEntitiesFactory.getInstance(false).getPackageConfig(packageName);
		if (packageConfig != null) {
			config.setName(packageName);
			if (StringUtils.isBlank(config.getNoSqlConnectionName()))
				config.setNoSqlConnectionName(packageConfig.getNoSqlConnectionName());
			if (StringUtils.isBlank(config.getSqlConnectionName()))
				config.setSqlConnectionName(packageConfig.getSqlConnectionName());
			if (StringUtils.isBlank(config.getResourceFolder()))
				config.setResourceFolder(packageConfig.getResourceFolder());
			if (config.getPrintInEmailableReport() == null) {
				config.setPrintInEmailableReport(packageConfig.getPrintInEmailableReport() == null ? false : true);
			}

		} else {
			log.warn("Module/Project - " + packageName + " not configured. Using the default configuration");
			config.setName(packageName);
			APITestContext.get().getTestConfig().setApiConfigFile("api/" + APITestConstants.API_CONFIG_FILE);
			APITestContext.get().getTestConfig().setApiTestConfigFile("api/" + APITestConstants.API_TESTS_CONFIG_FILE);
		}
	}

	public static MongoClient getNoSQLSessionFactory(String sourceName) {
		return getNoSQLSessionFactory(sourceName, false);
	}

	public static MongoClient getNoSQLSessionFactory(String sourceName, boolean requestFromOHM) {
		APIEntitiesFactory entitiesFactory = null;
		if (requestFromOHM) {
			entitiesFactory = APIEntitiesFactory.getInstance(true);
		} else {
			entitiesFactory = APIEntitiesFactory.getInstance(false);
		}

		APIDataSource datasource = entitiesFactory.getDataSource(sourceName);
		if (datasource == null) {
			log.error("No Data Sources configured with source name :" + sourceName);
			return null;
		}

		String datasourceName = datasource.getName();
		if (apiConnectionFactory.containsKey(datasourceName)) {
			return (MongoClient) apiConnectionFactory.get(datasourceName);
		}

		MongoClient mongoClient = null;
		try {
			String password = datasource.getPassword();
			if (StringUtils.isNotBlank(datasource.getUsername()) && StringUtils.isNotBlank(password)) {
				if (password.startsWith("ENC(")) {
					password = new PasswordEncryptor().decrypt(password);
				}
				mongoClient = new MongoClient(new ServerAddress(datasource.getUrl()),
						Arrays.asList(MongoCredential.createCredential(datasource.getUsername(), datasource.getDefaultSchema(), password.toCharArray())));

			} else {
				mongoClient = new MongoClient(datasource.getUrl());
			}
			synchronized (apiConnectionFactory) {
				apiConnectionFactory.put(datasource.getName(), mongoClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mongoClient.getConnectPoint().replaceAll(":\\d+", ":28017");
		return mongoClient;
	}

	public static SessionFactory getSQLSessionFactory(String sourceName) {
		if (apiConnectionFactory.containsKey(sourceName)) {
			return (SessionFactory) apiConnectionFactory.get(sourceName);
		}

		SessionFactory sessionFactory = null;
		try {
			Configuration config = getSQLConfig(sourceName);
			StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
			ServiceRegistry sr = ssrb.applySettings(config.getProperties()).build();
			sessionFactory = config.buildSessionFactory(sr);

			synchronized (apiConnectionFactory) {
				apiConnectionFactory.put(sourceName, sessionFactory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sessionFactory;
	}

	public static String getNoSQLDataSource() {
		String source = APITestContext.get().getTestConfig().getTestPackageConfig().getNoSqlConnectionName();
		if (StringUtils.isBlank(source)) {
			source = getDefaultNoSQLDataSourceName();
		}
		return source;
	}

	public static String getSQLDataSource() {
		String source = APITestContext.get().getTestConfig().getTestPackageConfig().getSqlConnectionName();
		if (StringUtils.isBlank(source)) {
			source = getDefaultSQLDataSourceName();
		}
		return source;
	}

	public static String getDefaultSQLDataSourceName() {
		return "API-Default-SQL";
	}

	public static String getDefaultNoSQLDataSourceName() {
		return getEnvironment();
	}

	public static String getEnvironment() {
		String sourceName = TestHarnessProperties.DB_ENV;
		if (StringUtils.isEmpty(sourceName) || "ENV".equalsIgnoreCase(sourceName)) {
			sourceName = "PROD";
		}
		return sourceName.toUpperCase();
	}

	public static Properties getProperties(String propertyFile) {
		Properties projectDBConfig = new Properties();
		try {
			projectDBConfig.load(APIConfigManager.class.getClassLoader().getResourceAsStream(propertyFile));
		} catch (Exception e) {
			log.error("Exception in reading the Properties :" + propertyFile);
		}
		if (projectDBConfig == null || projectDBConfig.isEmpty()) {
			log.info("No project specific DB config found, Using API DB");
		}

		return projectDBConfig;
	}

	public static void closeAllConnections() {
		Iterator<String> conIter = apiConnectionFactory.keySet().iterator();

		while (conIter.hasNext()) {
			String connection = conIter.next();
			Object factory = apiConnectionFactory.get(connection);

			if (factory instanceof SessionFactory) {
				System.out.println("Closing SQL Connection :" + connection);
				closeSQLFactory((SessionFactory) factory);
			}
			if (factory instanceof MongoClient) {
				System.out.println("Closing NoSQL Connection :" + connection);
				closeNoSQLFactory((MongoClient) factory);
			}
		}
		apiConnectionFactory.clear();
	}

	private static Configuration getSQLConfig(String conn) {
		APIDataSource connection = APIEntitiesFactory.getInstance(false).getDataSource(conn);
		Configuration config = new Configuration().configure(APITestConstants.API_INTERNAL_SQL_CONFIG);

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

	private static void decryptPassowrd(Configuration config) {
		String password = config.getProperty("connection.password");
		if (StringUtils.isNotBlank(password)) {
			password = new PasswordEncryptor().decrypt(password);
			config.setProperty("connection.password", password);
		}
		String hibpassword = config.getProperty("hibernate.connection.password");
		if (StringUtils.isNotBlank(hibpassword)) {
			hibpassword = new PasswordEncryptor().decrypt(hibpassword);
			config.setProperty("hibernate.connection.password", hibpassword);
		} else {
			config.setProperty("hibernate.connection.password", password);
		}
	}

	/**
	 * @param sessionFactory
	 */
	public static void closeSQLFactory(SessionFactory sessionFactory) {
		if (sessionFactory == null || sessionFactory.isClosed()) {
			sessionFactory = null;
			return;
		}
		if (sessionFactory instanceof SessionFactoryImpl) {
			SessionFactoryImpl sfo = (SessionFactoryImpl) sessionFactory;
			ConnectionProvider conn = sfo.getConnectionProvider();
			if (conn instanceof C3P0ConnectionProvider) {
				((C3P0ConnectionProvider) conn).stop();
			}
		}
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}

	public static void closeNoSQLFactory(MongoClient client) {
		if (client == null)
			return;
		client.close();
		client = null;
	}

}
