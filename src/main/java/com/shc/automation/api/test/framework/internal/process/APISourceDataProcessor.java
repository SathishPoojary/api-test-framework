/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.shc.automation.api.test.framework.entities.APIDataSourceType;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.APIRequestItemGenerator;
import com.shc.automation.api.test.framework.internal.process.source.APIDataSourceMarker;
import com.shc.automation.api.test.framework.internal.process.source.APIExcelSourceProcessor;
import com.shc.automation.api.test.framework.internal.process.source.APINoSQLSourceProcessor;
import com.shc.automation.api.test.framework.internal.process.source.APISQLSourceProcessor;
import com.shc.automation.api.test.framework.internal.process.source.APITextFileSourceProcessor;

/**
 * @author spoojar
 *
 */
public class APISourceDataProcessor {
	private APIDataSourceMarker getDataSourceProcessor(String sourceType) {
		if (APIDataSourceType.excel.toString().equalsIgnoreCase(sourceType)) {
			return new APIExcelSourceProcessor();
		}
		if (APIDataSourceType.sql.toString().equalsIgnoreCase(sourceType)) {
			return new APISQLSourceProcessor();
		}
		if (APIDataSourceType.mongo.toString().equalsIgnoreCase(sourceType)) {
			return new APINoSQLSourceProcessor();
		}
		if (APIDataSourceType.file.toString().equalsIgnoreCase(sourceType)) {
			return new APITextFileSourceProcessor();
		}
		return null;
	}

	public Map<String, Map<String, Object>> getDataRecords(APITestInputSource source, Map<String, Object> contextRecords) {
		if (source != null) {
			String sourceType = source.getSourceType();
			APIDataSourceMarker dataProcessor = getDataSourceProcessor(sourceType);

			if (dataProcessor != null) {
				return dataProcessor.processRequestSource(source, contextRecords);
			}
		}
		return null;
	}

	public List<APITestRequestItem> processDataRecords(APITestRequest request, Map<String, Map<String, Object>> urlParamRecords,
			Map<String, Map<String, Object>> payloadRecords) throws APITestException {

		APITestRequestItem requestItem = null;
		APIRequestItemGenerator requestItemGenerator = new APIRequestItemGenerator();
		List<APITestRequestItem> requests = new ArrayList<APITestRequestItem>();

		int payloadSize = payloadRecords == null ? 0 : payloadRecords.size();
		int urlParamSize = urlParamRecords == null ? 0 : urlParamRecords.size();

		String scenario = "1";
		if (payloadSize == 0 && urlParamSize == 0) {
			if (request.getPayloadInputSource() != null || request.getUrlParamInputSource() != null) {
				throw new APITestException("No Records found in Input Source. Please Check!");
			}
			requestItem = requestItemGenerator.createRequestItem(request, scenario, null, null);
			requests.add(requestItem);
		} else {
			Iterator<String> scenarios = null;
			if (urlParamSize >= payloadSize) {
				scenarios = urlParamRecords.keySet().iterator();
			} else {
				scenarios = payloadRecords.keySet().iterator();
			}

			while (scenarios.hasNext()) {
				scenario = scenarios.next();
				Map<String, Object> payloadRecord = payloadSize == 0 ? null
						: (payloadSize == 1 ? payloadRecords.values().iterator().next() : payloadRecords.get(scenario));
				Map<String, Object> urlRecord = urlParamSize == 0 ? null
						: (urlParamSize == 1 ? urlParamRecords.values().iterator().next() : urlParamRecords.get(scenario));
				requestItem = requestItemGenerator.createRequestItem(request, scenario, urlRecord, payloadRecord);
				requests.add(requestItem);
			}
		}

		return requests;
	}

}
