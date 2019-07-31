/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process.source;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.internal.config.QueryProperty;
import com.shc.automation.api.test.framework.internal.connect.SQLDBManager;
import com.shc.automation.api.test.framework.internal.process.APITestScenarioProcessor;

/**
 * @author spoojar
 *
 */
public class APISQLSourceProcessor implements APIDataSourceMarker {
	private static final Logger log = Logger.getLogger("APISQLSourceProcessor");

	public Map<String, Map<String, Object>> processRequestSource(APITestInputSource requestSource, Map<String, Object> contextRecords) {
		if (requestSource == null) {
			log.error("Please check the CONFIG : SQL Source is NULL");
			return null;
		}
		String sqlQueryName = requestSource.getSourceName();
		if (StringUtils.isEmpty(sqlQueryName)) {
			log.error("Please check the CONFIG : SQL Query Name is NULL or Empty");
			return null;
		}
		String query = getQuery(QueryProperty.INSTANCE.getSQLQuery(sqlQueryName), contextRecords);
		if (StringUtils.isBlank(query)) {
			log.error(sqlQueryName + " Query not Defined in Properties file.");
			return null;
		}
		List<Map<String, Object>> results = SQLDBManager.INSTANCE.getRecords(requestSource.getConnectionName(), query, requestSource.getFromIndex(), requestSource.getToIndex());

		Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>();
		if (CollectionUtils.isEmpty(results)) {
			log.error("No Records found with SQL Query :" + sqlQueryName);
			return null;
		}

		List<String> scenarioNames = requestSource.getScenarioFields();

		int scenarioIndex = 1;
		for (Map<String, Object> sqlRecord : results) {
			String scenarioName = APITestScenarioProcessor.generateScenarioNameFromURLInputRecord(scenarioNames, sqlRecord, scenarioIndex++);
			records.put(scenarioName, sqlRecord);
		}

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
