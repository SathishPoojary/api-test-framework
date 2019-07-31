/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers.source;

import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author spoojar
 *
 */
public interface APITestDataReader {
    Map<String, Map<String, Object>> processRequestSource(APITestDataSource requestSource, Map<String, Object> parameters);

    default String processScenarioName(List<String> scenarioFieldList, Object record, int scenarioIndex) {
        if (CollectionUtils.isEmpty(scenarioFieldList)) {
            return String.valueOf(scenarioIndex < 1 ? 1 : scenarioIndex);
        }

        String scenarioName = scenarioFieldList.stream().map(field -> getFieldValue(record, field)).collect(Collectors.joining());

        if (StringUtils.isEmpty(scenarioName)) {
            if (scenarioIndex < 1)
                return scenarioFieldList.get(0);
            else
                return scenarioFieldList.get(0) + " " + scenarioIndex;
        }

        return scenarioName.substring(0, scenarioName.length() - 3);
    }

    default String getFieldValue(Object record, String field) {
        Object value = APITestUtils.readFromJSON(record, field, false);
        return value == null ? "" : value.toString() + " | ";
    }

}
