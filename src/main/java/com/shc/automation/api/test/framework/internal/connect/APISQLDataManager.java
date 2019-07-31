package com.shc.automation.api.test.framework.internal.connect;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.internal.config.APIDataSourceConfigManager;
import com.shc.automation.api.test.framework.model.APIEndpointInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class APISQLDataManager {

    private Logger log = Logger.getLogger("APISQLDataManager");
    private Map<String, APIEndpointInfo> endPointMap;
    private Map<String, SessionFactory> connectionPool;

    private final APIDataSourceConfigManager datasourceConfigManager;

    @Inject
    public APISQLDataManager(APIDataSourceConfigManager datasourceConfigManager) {
        this.datasourceConfigManager = datasourceConfigManager;
        connectionPool = new ConcurrentHashMap<>();
        createEndpointMap();
    }

    private void createEndpointMap() {
        endPointMap = new ConcurrentHashMap<>();

        SessionFactory sessionFactory = getSQLSessionFactory(datasourceConfigManager.getDefaultSQLDataSourceName());
        String query = "SELECT aec.name as apiName, e.name as envName, aec.version, aec.base_url, tf.name as testFunctionName, tsf.name as testSubFunctionName FROM automation_config.api_endpoint_config aec "
                + "left join automation_config.test_sub_function tsf ON aec.test_sub_function_id = tsf.id "
                + "inner join automation_config.environment e ON aec.environment_id = e.id "
                + "inner join automation_config.test_function tf ON aec.test_function_id = tf.id";

        Session session = sessionFactory.openSession();
        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = session.createSQLQuery(query).list();
            endPointMap = results.stream().collect(Collectors.toMap(row -> mapEndpointKey(row), row -> mapEndpointValue(row)));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in retrieving records from DB :" + e);
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }

    }

    private String mapEndpointKey(Object[] row) {
        return (row[0].toString() +
                row[1].toString() +
                row[2].toString()).toLowerCase();
    }

    private APIEndpointInfo mapEndpointValue(Object[] row) {
        return new APIEndpointInfo(row[0].toString(),
                row[2].toString(),
                row[1].toString(),
                row[3].toString(),
                row[4].toString(),
                row[5] == null ? "" : row[5].toString());
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
     * {@link com.shc.automation.api.test.framework.model.APIEndpointInfo }
     * object.
     */
    public APIEndpointInfo getAPIEndPoint(String name, String env, String version) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(env) || StringUtils.isBlank(version)) {
            log.error("Empty/NULL input to retrieve the Service End Points");
            return null;
        }

        return endPointMap.get((name + env + version).toLowerCase());
    }

    public List<Map<String, Object>> getRecords(String connectionName, String query, int fromIndex, int toIndex) {
        Session session = null;
        try {
            System.out.println("Getting the Records from Query:\n" + query + " [ " + fromIndex + " - " + toIndex + " ] ");
            if (StringUtils.isNotEmpty(query)) {
                if (StringUtils.isBlank(connectionName)) {
                    connectionName = datasourceConfigManager.getSQLDataSource();
                }
                SessionFactory factory = getSQLSessionFactory(connectionName);
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

    public SessionFactory getSQLSessionFactory(String sourceName) {
        if (connectionPool.containsKey(sourceName)) {
            return connectionPool.get(sourceName);
        }

        SessionFactory sessionFactory = null;
        try {
            Configuration config = datasourceConfigManager.getSQLConfig(sourceName);
            StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
            ServiceRegistry sr = ssrb.applySettings(config.getProperties()).build();
            sessionFactory = config.buildSessionFactory(sr);

            connectionPool.put(sourceName, sessionFactory);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }

    public void close() {
        Iterator<String> conIter = connectionPool.keySet().iterator();
        while (conIter.hasNext()) {
            String connection = conIter.next();
            SessionFactory factory = connectionPool.get(connection);


            System.out.println("Closing NoSQL Connection :" + connection);
            if (factory == null)
                return;
            factory.close();

        }
        connectionPool.clear();
    }


}
