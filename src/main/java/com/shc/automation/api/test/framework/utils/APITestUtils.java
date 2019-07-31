/**
 * 
 */
package com.shc.automation.api.test.framework.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APIDataSourceType;
import com.shc.automation.api.test.framework.entities.APIPrintField;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.ResultType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.APIResponseItemProcessor;
import com.shc.automation.api.test.framework.internal.config.QueryProperty;
import com.shc.automation.api.test.framework.internal.connect.DocumentDBManager;
import com.shc.automation.api.test.framework.internal.connect.SQLDBManager;
import com.shc.automation.api.test.framework.internal.process.source.APIExcelSourceProcessor;
import com.shc.automation.api.test.framework.internal.validators.APIResponseValidator;
import com.shc.automation.utils.json.JsonMismatchField;
import com.shc.automation.utils.json.JsonUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * @author spoojar
 * 
 */
public class APITestUtils {
	private static Logger log = Logger.getLogger("APITestUtils");

	/**
	 * Retrieve the Document(PayLoad) from Mongo database collection for a given
	 * document id. This method is used to get the PayLoad directly from Mongo
	 * (instead of using API Test configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * @param documentId
	 *            ID of the document
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongoById(String dbName, String collectionName, String documentId) {
		return getPayloadFromMongoDB(dbName, collectionName, -1, -1, documentId, null);
	}

	/**
	 * Retrieve the Document(PayLoad) from Mongo database collection for a given
	 * document id. This method is used to get the PayLoad directly from Mongo
	 * (instead of using API Test configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * @param documentId
	 *            ID of the document
	 * @param scenarioFieldPath
	 *            Path in the retrieved document whose value will be considered as
	 *            scenario name
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongoById(String dbName, String collectionName, String documentId, String scenarioFieldPath) {
		return getPayloadFromMongoDB(dbName, collectionName, -1, -1, documentId, scenarioFieldPath);
	}

	/**
	 * Retrieve all Document(PayLoad)s from Mongo database collection. This method
	 * is used to get the PayLoad directly from Mongo (instead of using API Test
	 * configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongo(String dbName, String collectionName) {
		return getPayloadFromMongoDB(dbName, collectionName, -1, -1, null, null);
	}

	/**
	 * Retrieve all Document(PayLoad)s from Mongo database collection. This method
	 * is used to get the PayLoad directly from Mongo (instead of using API Test
	 * configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * @param scenarioFieldPath
	 *            Path in the retrieved document whose value will be considered as
	 *            scenario name
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongo(String dbName, String collectionName, String scenarioFieldPath) {
		return getPayloadFromMongoDB(dbName, collectionName, -1, -1, null, scenarioFieldPath);
	}

	/**
	 * Retrieve all Document(PayLoad)s from Mongo database collection. This method
	 * is used to get the PayLoad directly from Mongo (instead of using API Test
	 * configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * @param skipRecords
	 *            Number of records will be skipped from the query result (Beginning
	 *            range)
	 * @param maxRecords
	 *            Number of records to be returned as a result
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongo(String dbName, String collectionName, int skipRecords, int maxRecords) {
		return getPayloadFromMongoDB(dbName, collectionName, skipRecords, maxRecords, null, null);
	}

	/**
	 * Retrieve all Document(PayLoad)s from Mongo database collection. This method
	 * is used to get the PayLoad directly from Mongo (instead of using API Test
	 * configuration)<br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param dbName
	 *            Mongo Database name where the document is stored
	 * @param collectionName
	 *            Mongo Collection name where the document is stored
	 * @param skipRecords
	 *            Number of records will be skipped from the query result (Beginning
	 *            range)
	 * @param maxRecords
	 *            Number of records to be returned as a result
	 * @param scenarioFieldPath
	 *            Path in the retrieved document whose value will be considered as
	 *            scenario name
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> getPayloadFromMongo(String dbName, String collectionName, int skipRecords, int maxRecords, String scenarioFieldPath) {
		return getPayloadFromMongoDB(dbName, collectionName, skipRecords, maxRecords, null, scenarioFieldPath);
	}

	private static Map<String, String> getPayloadFromMongoDB(String dbName, String collectionName, int skipRecords, int maxRecords, String documentId,
			String scenarioFieldPath) {

		String targetObject = null;
		if (collectionName.indexOf('[') != -1) {
			targetObject = collectionName.substring(collectionName.indexOf('[') + 1, collectionName.length() - 1);
			collectionName = collectionName.substring(0, collectionName.indexOf('['));
		}

		log.info("Retriving Payload from source :" + collectionName);
		log.info("Target Object from Source :" + targetObject);

		List<JSONObject> records = null;
		if (StringUtils.isNotBlank(documentId)) {
			records = DocumentDBManager.INSTANCE.getDocumentById(dbName, collectionName, documentId);
		} else {
			records = DocumentDBManager.INSTANCE.getDocument(dbName, collectionName, skipRecords, maxRecords);
		}

		if (records == null) {
			return null;
		}

		Iterator<JSONObject> iter = records.iterator();
		Map<String, String> payloadList = new HashMap<String, String>();
		String scenarioName = null;
		int scenarioIndex = 1;
		while (iter.hasNext()) {
			JSONObject obj = iter.next();
			obj.remove("_id");

			if (obj != null) {
				String payload = obj.toString();
				if (StringUtils.isNotBlank(targetObject)) {
					Object json = readFromJSON(obj, targetObject, false);
					if (json == null)
						continue;
					payload = json.toString();
				}
				Object scenario = readFromJSON(obj, scenarioFieldPath, false);
				if (scenario == null) {
					scenarioName = String.valueOf(scenarioIndex++);
				} else {
					scenarioName = scenario.toString();
				}

				if (StringUtils.isNotBlank(payload)) {
					payloadList.put(scenarioName, payload);
				}

			}
		}
		if (MapUtils.isEmpty(payloadList)) {
			log.error("No payload found in collection :" + dbName + "." + collectionName + "[" + targetObject + "]");
		}

		return payloadList;
	}

	/**
	 * This method is used to generate the API PayLoad from the PayLoad template
	 * Stored in the Mongo Database collection <br/>
	 * This method will retrieve the Template stored in Mongo DB <br/>
	 * Search for Strings or replacement keys (identified by the Key in the
	 * payloadEntries) <br/>
	 * Replace the Keys identified in the template by the actual values from the
	 * payloadEntries <br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param templateDB
	 *            Name of the mongo database where template is stored
	 * @param templateCollection
	 *            Name of the mongo collection where the template is stored
	 * @param payloadEntries
	 *            List of PayLoad Maps where each Map contains<br/>
	 *            Key - String to be searched and replaced in Mongo Templace Value -
	 *            Actual value after replacement in the template
	 * @param scenarioFieldName
	 *            Entry in the payloadEntries Map to be considered as Scenario Name.
	 *            If no match found, incremental values will be considered
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */
	public static Map<String, String> generatePayloadFromTemplate(String templateDB, String templateCollection, List<Map<String, Object>> payloadEntries,
			String scenarioFieldName) {
		Map<String, String> templates = getPayloadFromMongo(templateDB, templateCollection);
		Map<String, String> payloads = new HashMap<String, String>(payloadEntries.size());
		if (MapUtils.isNotEmpty(templates)) {
			String template = (String) templates.values().iterator().next();
			payloads = getPayloadMap(payloadEntries, scenarioFieldName, template);

		} else {
			log.error("Template not found in MongoDB :" + templateCollection);
		}
		return payloads;
	}

	/**
	 * This method is used to generate the API PayLoad from the template String
	 * passed as parameter <br/>
	 * This method will search the template for replacement keys (identified by the
	 * Key in the payloadEntries) <br/>
	 * Replace the Keys identified in the template by the actual values from the
	 * payloadEntries <br/>
	 * The PayLoad map obtained through this method is either set in the
	 * APITestRequest(externalPayload) or passed to APITestManager::runTest()
	 * through one of the overloaded methods.
	 * 
	 * @param payloadEntries
	 *            List of PayLoad Maps where each Map contains<br/>
	 *            Key - String to be searched and replaced in Mongo Templace Value -
	 *            Actual value after replacement in the template
	 * @param scenarioFieldName
	 *            Entry in the payloadEntries Map to be considered as Scenario Name.
	 *            If no match found, incremental values will be considered
	 * @param template
	 *            template String used to generate PayLoad
	 * 
	 * @return Map where Scenario name as the key and PayLoad as the value. Target
	 *         API will be executed based on the scenario name and number of
	 *         elements in this map.
	 */

	public static Map<String, String> getPayloadMap(List<Map<String, Object>> payloadEntries, String scenarioFieldName, String template) {
		Object scenario = null;
		String scenarioName = null;
		Map<String, Object> payloadMap = null;
		Map<String, String> payloads = new HashMap<String, String>(payloadEntries.size());
		if (StringUtils.isNotBlank(template)) {
			for (int i = 0; i < payloadEntries.size(); i++) {
				payloadMap = payloadEntries.get(i);
				if (StringUtils.isNotBlank(scenarioFieldName)) {
					scenario = payloadMap.get(scenarioFieldName);
				}
				if (scenario == null || StringUtils.isBlank(scenario.toString())) {
					scenarioName = String.valueOf(i + 1);
				} else {
					scenarioName = scenario.toString();
				}
				payloads.put(scenarioName, replaceValuesInTemplate(template, payloadMap));
			}
		}
		return payloads;
	}

	/**
	 * This method is used to get Results of a Query as a List containing map of
	 * Key(Column Name) / Value (Column Value) format. The result can be directly
	 * passed as Query Parameters to API through APITestManager::runTest overloaded
	 * method or set in the APITestRequest(externalURLParameters)
	 * 
	 * @param queryName
	 *            Name of the query store in the properties file (api-data-db)
	 * 
	 * @return query results in Key/Value Map List
	 */
	public static List<Map<String, Object>> getRecordsFromSQLQuery(String queryName) {
		if (StringUtils.isEmpty(queryName))
			return null;

		String query = QueryProperty.INSTANCE.getSQLQuery(queryName);
		List<Map<String, Object>> list = SQLDBManager.INSTANCE.getRecords(query, 0, 0);
		if (list == null || list.isEmpty()) {
			log.error("No records found for :" + queryName);
			return null;
		}
		return list;

	}

	/**
	 * This method is used to get Rows of a excel file as a List containing map of
	 * Key(Column Name) / Value (Column Value) format. The result can be directly
	 * passed as Query Parameters to API through APITestManager::runTest overloaded
	 * method or set in the APITestRequest(externalURLParameters)
	 * 
	 * @param queryName
	 *            Name of the excel reference in the properties file (api-file-db)
	 * 
	 * @return Excel records in Key/Value Map List
	 */
	public static List<Map<String, Object>> getRecordsFromExcel(String excelSourceProperty) {
		return getRecordsFromExcel(excelSourceProperty, -1, -1);

	}

	/**
	 * This method is used to get Rows of a excel file as a List containing map of
	 * Key(Column Name) / Value (Column Value) format. The result can be directly
	 * passed as Query Parameters to API through APITestManager::runTest overloaded
	 * method or set in the APITestRequest(externalURLParameters)
	 * 
	 * @param queryName
	 *            Name of the excel reference in the properties file (api-file-db)
	 * 
	 * @return Excel records in Key/Value Map List
	 */
	public static List<Map<String, Object>> getRecordsFromExcel(String excelSourceProperty, int startIndex, int endIndex) {
		if (StringUtils.isEmpty(excelSourceProperty)) {
			log.error("Empty Excel Source Property :" + excelSourceProperty);
			return null;
		}
		APITestInputSource source = new APITestInputSource();
		source.setSourceType(APIDataSourceType.excel.toString());
		if (excelSourceProperty.indexOf('[') != -1) {
			String targetPath = excelSourceProperty.substring(excelSourceProperty.indexOf('[') + 1, excelSourceProperty.length() - 1);
			source.setSourceName(excelSourceProperty.substring(0, excelSourceProperty.indexOf('[')));
			source.setSourcePath(targetPath);
		} else {
			source.setSourceName(excelSourceProperty);
		}
		source.setFromIndex(startIndex);
		source.setToIndex(endIndex);

		Map<String, Map<String, Object>> records = new APIExcelSourceProcessor().processRequestSource(source, null);

		if (records != null) {
			return new ArrayList<Map<String, Object>>(records.values());
		}

		return null;
	}

	/**
	 * This method is used to replace the Strings in the template identified by the
	 * keys, with respective values in the Map. This is mainly used for generating
	 * the PayLoad from the template
	 * 
	 * @param template
	 *            template String
	 * @param map
	 *            Keys in the Template To be replaced by the Values of this Map
	 * 
	 * @return result String containing final PayLoad
	 */
	public static String replaceValuesInTemplate(String template, Map<String, Object> map) {

		if (template == null) {
			return null;
		}
		if (map == null) {
			return template;
		}

		Iterator<String> placeholders = map.keySet().iterator();
		String placeholder = null;

		List<String> keySet = getAllKeysInDocument(template);

		while (placeholders.hasNext()) {
			placeholder = placeholders.next();
			Object value = APITestUtils.getValueFromRecord(placeholder, map);
			if (value == null) {
				continue;
			}

			String templateRepl = replaceValue(template, "${", "}", placeholder, value);
			if (!template.equals(templateRepl)) {
				template = templateRepl;
				continue;
			}
			templateRepl = replaceValue(template, "$", "", placeholder, value);
			if (!template.equals(templateRepl)) {
				template = templateRepl;
				continue;
			}
			if (keySet.contains(placeholder)) {
				continue;
			}
			templateRepl = replaceValue(template, "", "", placeholder, value);
			if (!template.equals(templateRepl)) {
				template = templateRepl;
			}
		}
		return template;
	}

	private static String replaceValue(String template, String placeholderPrefix, String placeholderSuffix, String placeholder, Object value) {
		placeholder = placeholderPrefix + placeholder + placeholderSuffix;

		String templateRepl = template.replaceAll(Pattern.quote(placeholder), Matcher.quoteReplacement(value.toString()));
		return templateRepl;
	}

	private static List<String> getAllKeysInDocument(String document) {
		List<String> keys = new ArrayList<String>();
		if (StringUtils.isEmpty(document)) {
			return keys;
		}
		Object jsonRecord = null;
		if (document.startsWith("<")) {
			jsonRecord = JsonUtils.convertXMLToJson(document);
		}
		if (document.startsWith("{")) {
			jsonRecord = JSONObject.fromObject(document);
		} else if (document.startsWith("[")) {
			jsonRecord = JSONArray.fromObject(document);
		}

		if (jsonRecord != null) {
			getAllKeys(null, jsonRecord, keys);
		}
		return keys;
	}

	private static void getAllKeys(String key, Object testRecord, List<String> keys) {
		if (testRecord == null) {
			return;
		}
		if (testRecord instanceof JSONArray) {
			if (allSimpleValues(((JSONArray) testRecord))) {
				keys.add(key);
			} else {
				for (int i = 0; i < ((JSONArray) testRecord).size(); i++) {
					Object value = ((JSONArray) testRecord).get(i);
					getAllKeys(key, value, keys);
				}
			}
		} else if (testRecord instanceof JSONObject) {
			for (Object keyObj : ((JSONObject) testRecord).keySet()) {
				String keyStr = (String) keyObj;
				Object keyvalue = ((JSONObject) testRecord).get(keyStr);
				getAllKeys(keyStr, keyvalue, keys);
			}

		} else {
			keys.add(key);
		}
	}

	/**
	 * This method is used to add a Print Field to the API Response Item. The print
	 * field (a path value from respective API response) created by method will be
	 * displayed in the logger.
	 * 
	 * @param responseItem
	 *            APITestResponseItem containing the API JSon Response from where
	 *            the field to be extracted
	 * @param name
	 *            Name of the field to be printed
	 * @param path
	 *            JSon Path of the API JSon Response whose value needs to be printed
	 */
	public static void addPrinter(APITestResponseItem responseItem, String name, String path) {

		Object object = readFromJSON(responseItem.getResponseContent(), path, true);
		APIPrintField print = new APIPrintField();
		print.setPrintName(name);
		print.setResponsePath(path);
		print.setResponseValue(object == null ? null : object.toString());
		responseItem.addPrinter(print);
	}

	/**
	 * This method is used to get the frequency of a API response field appeared or
	 * mismatched in the comparison
	 * 
	 * @param response
	 *            API Compare Response from where the mismatch frequency map needs
	 *            to be generated
	 * 
	 * @return Map of JSON Path and No. of Times it appeared as a mismatch in the
	 *         overall comparison
	 */
	public static Map<String, Integer> getMismatchedFieldFrequencyMap(APIResponse response) {
		if (response == null || CollectionUtils.isEmpty(response.getFailedScenarioList())) {
			log.info("Response is Null or No failed scenarios");
			return null;
		}
		if (!(response instanceof APICompareTestsResponse)) {
			log.info("Not a compare test to get Frequency Map");
			return null;
		}
		Iterator<String> failedScenarioIter = response.getFailedScenarioList().iterator();
		Map<String, APICompareTestsResponseItem> responseItems = ((APICompareTestsResponse) response).getResponseItems();
		Map<String, Integer> responseMap = new HashMap<String, Integer>();
		while (failedScenarioIter.hasNext()) {
			String scenario = failedScenarioIter.next();
			APICompareTestsResponseItem responseItem = responseItems.get(scenario);
			if (responseItem != null) {

				List<JsonMismatchField> differences = responseItem.getMismatches();
				if (CollectionUtils.isNotEmpty(differences)) {
					for (int i = 0; i < differences.size(); i++) {
						String field = differences.get(i).getMismatchPath();
						responseMap.put(field, responseMap.containsKey(field) ? responseMap.get(field) + 1 : 1);
					}
				}
			}

		}
		return responseMap;
	}

	/**
	 * This method is used to update the API JSon Path to API Test JSon Path (API
	 * Test JSon path is different from actual API JSon Path since API wraps the
	 * entire response inside "APITestResponse"
	 * 
	 * @param path
	 *            - JSon Path needs to be updated
	 * 
	 * @return updated JSon path
	 */
	public static String getUpdatedResponsePath(String path) {
		if (StringUtils.isBlank(path))
			return path;
		if ("$".equals(path)) {
			return "$." + APITestConstants.API_RESPONSE_MAP_KEY;
		}
		if (path.startsWith("$")) {
			return "$." + APITestConstants.API_RESPONSE_MAP_KEY + path.substring(1);
		}
		return APITestConstants.API_RESPONSE_MAP_KEY + "." + path;
	}

	/**
	 * This method is used to get the API JSon Path from API Test JSon Path (API
	 * Test JSon path is different from actual API JSon Path since API wraps the
	 * entire response inside "APITestResponse"
	 * 
	 * @param path
	 *            JSon Path
	 * 
	 * @return JSon path
	 */
	public static String getAbsoluteResponsePath(String path) {
		if (StringUtils.isBlank(path))
			return path;
		if (path.startsWith(APITestConstants.API_RESPONSE_MAP_KEY))
			return path.replace(APITestConstants.API_RESPONSE_MAP_KEY + ".", "");
		if (path.startsWith("$"))
			return path.replace("." + APITestConstants.API_RESPONSE_MAP_KEY, "");

		return path;
	}

	public static String getThrowableStackTrace(Throwable throwable) {
		String stackTrace = "";
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		stackTrace = writer.toString();
		return stackTrace;
	}

	/**
	 * This method is used to read a value from a given JSON object using the path.
	 * 
	 * @param jsonObject
	 *            JSONObject from which the path value need to be retrieved
	 * @param path
	 *            PATH in the JSONObject whose value need to be returned
	 * @param appendAPIKey
	 *            If true, path will be appended with "APITestResponse". This is
	 *            mainly used for the JSON response returned by API Framework.
	 * 
	 * @return Object (String, List) based on the return value. Refer to
	 *         https://github.com/jayway/JsonPath for more details.
	 */
	public static Object readFromJSON(Object jsonObject, String path, boolean appendAPIKey) {
		if (appendAPIKey) {
			path = getUpdatedResponsePath(path);
		}
		return JsonUtils.readFromJSON(jsonObject, path);
	}

	/**
	 * This method is used to read a value from a given JSON object using the path.
	 * 
	 * @param jsonObject
	 *            JSONObject from which the path value need to be retrieved
	 * @param path
	 *            PATH in the JSONObject whose value need to be returned
	 * 
	 * @return Object (String, List) based on the return value. Refer to
	 *         https://github.com/jayway/JsonPath for more details.
	 */
	public static Object readFromJSON(Object jsonObject, String path) {
		return readFromJSON(jsonObject, path, false);
	}

	/**
	 * @param aStr
	 * @return
	 */
	public static List<String> getListFromString(String aStr) {
		if (StringUtils.isBlank(aStr))
			return null;
		List<String> list = Lists.newArrayList(Splitter.on(",").trimResults().split(aStr));
		return list;
	}

	/**
	 * @param json
	 * @return
	 */
	public static JSONObject wrapAPIResponse(Object json) {
		if (json == null) {
			return null;
		}
		JSONObject apiJson = new JSONObject();
		apiJson.put(APITestConstants.API_RESPONSE_MAP_KEY, json);
		return apiJson;
	}

	/**
	 * @return
	 */
	public static String getDigitalSignURL() {
		HttpRequestBase apiHttpMethod = new HttpGet("http://cars.prod.ch3.s.com/user/services/dsjson");
		try {
			CloseableHttpResponse response = HttpClientBuilder.create().build().execute(apiHttpMethod);
			String respStr = APIResponseItemProcessor.getBody(response);
			if (respStr != null) {
				JSONObject respObj = JSONObject.fromObject(respStr);
				@SuppressWarnings("unchecked")
				Iterator<String> iter = respObj.keys();
				StringBuilder queryParamString = new StringBuilder("");
				while (iter.hasNext()) {
					String key = iter.next();
					queryParamString.append("&" + key + "=" + respObj.getString(key));
				}
				return queryParamString.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APITestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param responseItem
	 */
	public static boolean revalidateScenarioResponse(APITestResponseItem responseItem) {
		if (responseItem == null) {
			return false;
		}
		boolean valid = responseItem.getValidResponse();
		valid = valid & new APIResponseValidator().validate(responseItem, null);
		responseItem.setResult((valid ? ResultType.PASSED : ResultType.FAILED));

		return valid;
	}

	public static Object getValueFromRecord(String path, Map<String, Object> record) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		if (MapUtils.isEmpty(record)) {
			return null;
		}
		Object result = record.get(path);
		if (result == null && JSONUtils.mayBeJSON(record.toString())) {
			if (record.containsKey(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER) && !path.contains(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER)) {
				result = APITestUtils.readFromJSON(JSONObject.fromObject(record).get(APITestConstants.API_JSON_BASE_PATH_IDENTIFIER), path, false);
			} else {
				result = APITestUtils.readFromJSON(record, path, false);
			}
		}
		return result;

	}

	@SuppressWarnings("rawtypes")
	public static boolean allSimpleValues(List array) {

		for (int i = 0; i < array.size(); ++i) {
			if (!Primitives.isWrapperType(array.get(i).getClass()) && !(array.get(i) instanceof String)) {
				return false;
			}
		}
		return true;
	}
}
