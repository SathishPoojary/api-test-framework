/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers.source;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.internal.connect.APINoSQLDataManager;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author spoojar
 *
 */
public class APINoSQLReader implements APITestDataReader {
    private static final Logger log = Logger.getLogger("APINoSQLReader");

    private final APINoSQLDataManager noSQLDataManager;

    @Inject
    public APINoSQLReader(APINoSQLDataManager noSQLDataManager) {
        this.noSQLDataManager = noSQLDataManager;
    }

    @Override
    public Map<String, Map<String, Object>> processRequestSource(APITestDataSource requestSource, Map<String, Object> contextRecords) {
        if (requestSource == null) {
            log.error("Please check the CONFIG : MONGO Source is NULL");
            return null;
        }
        String noSQLSource = requestSource.getSourceName();
        if (StringUtils.isEmpty(noSQLSource)) {
            log.error("Please check the CONFIG : MONGO Input Source name is NULL");
            return null;
        }

        List<JSONObject> results = noSQLDataManager.getDocument(requestSource.getConnectionName(), noSQLSource, requestSource.getFromIndex(), requestSource.getToIndex());
        if (CollectionUtils.isEmpty(results)) {
            log.error("No Records or Payload found with Collection name :" + noSQLSource);
            return null;
        }

        AtomicInteger scenarioIndex = new AtomicInteger(1);
        Map<String, Map<String, Object>> records = results.stream()
                .filter(Objects::nonNull)
                .map(result -> result.remove("_id"))
                .collect(Collectors.toMap(
                        result -> processScenarioName(requestSource.getScenarioFields(), result, scenarioIndex.getAndIncrement()),
                        result -> getRecord(result)));

        if (MapUtils.isEmpty(records)) {
            log.error("!!!! No records found in NoSQL :" + requestSource.getSourceName() + " [ " + requestSource.getFromIndex() + " - "
                    + requestSource.getToIndex() + " ] ");
        }
        return records;

    }

    public Map<String, Object> getRecord(Object result) {
        Map record = new HashMap<>();
        record.put(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER, result);
        return record;
    }
}
