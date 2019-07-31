/**
 * 
 */
package com.shc.automation.api.test.framework.internal.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import com.shc.automation.api.test.framework.entities.APIEndpointInfo;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;

/**
 * @author spoojar
 * 
 */
public enum SQLDBManager {
	INSTANCE();

	private Logger log = Logger.getLogger("SQLDBManager");
	private Map<String, APIEndpointInfo> endPointMap = null;
	private Map<String, APIEndpointInfo> oldEndPointMap = null;

	private SQLDBManager() {
		createNewEndpointMap();
		createOldEndpointMap();
	}

	private void createNewEndpointMap() {
		if (endPointMap == null) {
			endPointMap = new HashMap<String, APIEndpointInfo>();
			Session session = null;
			SessionFactory sessionFactory = null;
			try {
				String sqlConn = APIConfigManager.getDefaultSQLDataSourceName();
				sessionFactory = APIConfigManager.getSQLSessionFactory(sqlConn);

				String query = "SELECT aec.name as apiName, e.name as envName, aec.version, aec.base_url, tf.name as testFunctionName, tsf.name as testSubFunctionName FROM automation_config.api_endpoint_config aec "
						+ "left join automation_config.test_sub_function tsf ON aec.test_sub_function_id = tsf.id "
						+ "inner join automation_config.environment e ON aec.environment_id = e.id "
						+ "inner join automation_config.test_function tf ON aec.test_function_id = tf.id";
				session = sessionFactory.openSession();
				@SuppressWarnings("unchecked")
				List<Object[]> results = session.createSQLQuery(query).list();

				Object[] row = null;
				for (int i = 0; i < results.size(); i++) {
					row = results.get(i);
					if (row.length < 6)
						continue;

					String ep = null;
					APIEndpointInfo apiEndpointInfo = new APIEndpointInfo((String) row[0], (String) row[2], (String) row[1], (String) row[3], (String) row[4],
							(String) row[5]);

					if (StringUtils.isNotBlank(((String) row[2]))) {
						ep = (((String) row[0]) + ((String) row[1]) + ((String) row[2])).toLowerCase();
						endPointMap.put(ep, apiEndpointInfo);

					} else {
						ep = (((String) row[0]) + ((String) row[1])).toLowerCase();
						endPointMap.put(ep, apiEndpointInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.flush();
					session.close();
				}
			}
		}
	}

	public void createOldEndpointMap() {
		if (oldEndPointMap == null) {
			oldEndPointMap = new HashMap<String, APIEndpointInfo>();
			Session session = null;
			SessionFactory sessionFactory = null;
			try {
				String sqlConn = APIConfigManager.getDefaultSQLDataSourceName();
				sessionFactory = APIConfigManager.getSQLSessionFactory(sqlConn);

				String query = "SELECT name, env_type, url FROM aeng_prod_productdb.service_endpoints";
				session = sessionFactory.openSession();
				@SuppressWarnings("unchecked")
				List<Object[]> results = session.createSQLQuery(query).list();

				Object[] row = null;
				for (int i = 0; i < results.size(); i++) {
					row = results.get(i);
					if (row.length < 3)
						continue;
					String ep = (((String) row[0]) + ((String) row[1])).toLowerCase();
					APIEndpointInfo apiEndpointInfo = new APIEndpointInfo(((String) row[0]), ((String) row[1]), ((String) row[2]));
					oldEndPointMap.put(ep, apiEndpointInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.flush();
					session.close();
				}
			}
		}
	}

	/**
	 * <p>
	 * This method is responsible to return service endpoint url.
	 * </p>
	 * 
	 * @param name
	 *            - Endpoint Name
	 * @param env
	 *            - Environment name
	 * @param version
	 *            - version number as a string and version can be NULL
	 * @return - returns the service endpoint url.
	 */
	public String getServiceEP(String name, String env, String version) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(env) || StringUtils.isBlank(version)) {
			log.error("Empty/NULL input to retrieve the Service End Points");
			return null;
		}

		// TO support backward compatibility:
		APIEndpointInfo apiEndpointInfo = null;
		// Step 1. Check the key in new endpoint map
		if (StringUtils.isNotBlank(version)) {
			apiEndpointInfo = endPointMap.get((name + env + version).toLowerCase());
			if (apiEndpointInfo != null)
				return apiEndpointInfo.getBaseUrl();
		}
		// Step 3. if not identified then look into old api endpoint
		// The following will be supported for backward compatibility
		apiEndpointInfo = oldEndPointMap.get((name + env).toLowerCase());

		if (apiEndpointInfo != null) {
			log.warn("You are still using the old endpoint:{ Name:" + apiEndpointInfo.getName() + ",url:" + apiEndpointInfo.getBaseUrl() + ",env:"
					+ apiEndpointInfo.getEnvironment()
					+ "}: We recommend you to migrate the old endpoint to new endpoint strucutre using CARS Dashboard for better data organization and to get better analytics.");
		}

		return (apiEndpointInfo != null ? apiEndpointInfo.getBaseUrl() : null);

	}

	/**
	 * <p>
	 * This method is responsible to return service endpoint details
	 * </p>
	 * 
	 * @param name
	 * @param env
	 * @param version
	 * @return a
	 *         {@link com.shc.automation.api.test.framework.entities.APIEndpointInfo }
	 *         object.
	 */
	public APIEndpointInfo getServiceEPInfo(String name, String env, String version) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(env) || StringUtils.isBlank(version)) {
			log.error("Empty/NULL input to retrieve the Service End Points");
			return null;
		}

		// TO support backward compatibility:
		APIEndpointInfo apiEndpointInfo = null;
		// Step 1. Check the key in new endpoint map
		if (StringUtils.isNotBlank(version)) {
			apiEndpointInfo = endPointMap.get((name + env + version).toLowerCase());
		} else {
			// Step 3. if not identified then look into old api endpoint
			// The following will be supported for backward compatibility
			apiEndpointInfo = oldEndPointMap.get((name + env).toLowerCase());
		}
		return apiEndpointInfo;
	}

	public List<Map<String, Object>> getRecords(String query, int fromIndex, int toIndex) {
		return getRecords(null, query, fromIndex, toIndex);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRecords(String connectionName, String query, int fromIndex, int toIndex) {
		Session session = null;
		try {
			System.out.println("Getting the Records from Query:\n" + query + " [ " + fromIndex + " - " + toIndex + " ] ");
			if (StringUtils.isNotEmpty(query)) {
				if (StringUtils.isBlank(connectionName)) {
					connectionName = APIConfigManager.getSQLDataSource();
				}
				SessionFactory factory = APIConfigManager.getSQLSessionFactory(connectionName);
				session = factory.openSession();
				Query sqlQuery = session.createSQLQuery(query);
				sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
				if (fromIndex > 0 && toIndex > 0) {
					return sqlQuery.setFirstResult(fromIndex).setMaxResults(Math.abs(toIndex - fromIndex) + 1).list();
				}
				if (fromIndex > 0) {
					return sqlQuery.setMaxResults(fromIndex).list();
				}
				if (toIndex > 0) {
					return sqlQuery.setMaxResults(toIndex).list();
				}
				return sqlQuery.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error in retreiving records from DB :" + e);
		} finally {
			if (session != null) {
				session.flush();
				session.close();
			}
		}
		return null;
	}

}