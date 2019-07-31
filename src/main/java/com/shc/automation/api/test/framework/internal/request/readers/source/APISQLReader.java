/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers.source;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.config.APIPropertyQueryConfig;
import com.shc.automation.api.test.framework.internal.connect.APISQLDataManager;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author spoojar
 *
 */
public class APISQLReader implements APITestDataReader {
    private static final Logger log = Logger.getLogger("APISQLReader");
    private final APISQLDataManager sqlDataManager;
    private final APIPropertyQueryConfig propertyQueryConfig;

    @Inject
    public APISQLReader(APISQLDataManager sqlDataManager, APIPropertyQueryConfig propertyQueryConfig) {
        this.sqlDataManager = sqlDataManager;
        this.propertyQueryConfig = propertyQueryConfig;
    }

    @Override
    public Map<String, Map<String, Object>> processRequestSource(APITestDataSource requestSource, Map<String, Object> parameters) {
        if (requestSource == null) {
            log.error("Please check the CONFIG : SQL Source is NULL");
            return null;
        }
        String sqlQueryName = requestSource.getSourceName();
        if (StringUtils.isEmpty(sqlQueryName)) {
            log.error("Please check the CONFIG : SQL Query Name is NULL or Empty");
            return null;
        }
        String query = getQuery(propertyQueryConfig.getSQLQuery(sqlQueryName), parameters);
        if (StringUtils.isBlank(query)) {
            log.error(sqlQueryName + " Query not Defined in Properties file.");
            return null;
        }
        List<Map<String, Object>> results = sqlDataManager.getRecords(requestSource.getConnectionName(), query, requestSource.getFromIndex(), requestSource.getToIndex());

        if (CollectionUtils.isEmpty(results)) {
            log.error("No Records found with SQL Query :" + sqlQueryName);
            return null;
        }

        AtomicInteger scenarioIndex = new AtomicInteger(1);
        Map<String, Map<String, Object>> records = results.stream().collect(Collectors.toMap(
                result -> processScenarioName(requestSource.getScenarioFields(), result, scenarioIndex.getAndIncrement()),
                result -> result));

        if (MapUtils.isEmpty(records)) {
            log.error("!!!! No records found in EXCEL :" + requestSource.getSourceName() + " [ " + requestSource.getFromIndex() + " - "
                    + requestSource.getToIndex() + " ] ");
        }
        return records;
    }

    public String getQuery(String query, Map<String, Object> contextRecords) {
        if (MapUtils.isEmpty(contextRecords)) {
            return query;
        }
        if (StringUtils.isBlank(query)) {
            return query;
        }
        if (query.indexOf('{') < 0) {
            return query;
        }
        Iterator<String> names = contextRecords.keySet().iterator();
        StringBuilder queryStr = new StringBuilder(query);
        while (names.hasNext()) {
            String name = names.next();
            String TemplateStr = "{" + name + "}";
            if (query.contains(TemplateStr)) {
                int start = queryStr.indexOf(TemplateStr);
                Object paramValue = contextRecords.get(name);
                String value = paramValue == null ? null : paramValue.toString();
                if (start > -1) {
                    int end = queryStr.indexOf(TemplateStr) + (TemplateStr).length();
                    queryStr.replace(start, end, value);
                }
            }
        }
        return queryStr.toString();
    }

}
