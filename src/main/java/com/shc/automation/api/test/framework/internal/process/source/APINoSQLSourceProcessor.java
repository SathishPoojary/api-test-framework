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

import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.internal.connect.DocumentDBManager;
import com.shc.automation.api.test.framework.internal.process.APITestScenarioProcessor;

import net.sf.json.JSONObject;

/**
 * @author spoojar
 *
 */
public class APINoSQLSourceProcessor implements APIDataSourceMarker {

	private static final Logger log = Logger.getLogger("APIExcelSourceProcessor");

	public Map<String, Map<String, Object>> processRequestSource(APITestInputSource requestSource, Map<String, Object> contextRecords) {
		if (requestSource == null) {
			log.error("Please check the CONFIG : MONGO Source is NULL");
			return null;
		}
		String noSQLSource = requestSource.getSourceName();
		if (StringUtils.isEmpty(noSQLSource)) {
			log.error("Please check the CONFIG : MONGO Input Source name is NULL");
			return null;
		}

		List<JSONObject> results = DocumentDBManager.INSTANCE.getDocument(requestSource.getConnectionName(), noSQLSource, requestSource.getFromIndex(), requestSource.getToIndex());
		if (CollectionUtils.isEmpty(results)) {
			log.error("No Records or Payload found with Collection name :" + noSQLSource);
			return null;
		}

		Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>();
		Map<String, Object> record = null;
		Iterator<JSONObject> iter = results.iterator();

		int scenarioIndex = 1;
		while (iter.hasNext()) {
			JSONObject obj = iter.next();

			if (obj != null) {
				obj.remove("_id");
				record = new HashMap<String, Object>();
				record.put(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER, obj);
				String scenarioName = APITestScenarioProcessor.generateScenarioNameFromPayloadInputRecord(requestSource.getScenarioFields(), obj,
						scenarioIndex++);
				records.put(scenarioName, record);
			}

		}

		if (MapUtils.isEmpty(records)) {
			log.error("!!!! No records found in NoSQL :" + requestSource.getSourceName() + " [ " + requestSource.getFromIndex() + " - "
					+ requestSource.getToIndex() + " ] ");
		}
		return records;

	}
}
