package com.shc.automation.api.test.framework.internal.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.internal.connect.DocumentDBManager;
import com.shc.automation.utils.json.JsonUtils;

import net.sf.json.JSONObject;

/**
 * @author spoojar
 *
 */
public class APINoSQLResponseLogger {
	protected final Logger log = Logger.getLogger(this.getClass().getName());

	public ObjectId saveToNoSQL(APIResponse response) {
		if (response == null) {
			log.error("NULL Response received by NoSQL Logger");
			return null;
		}

		String json = APIJsonTransformer.convertToJson(response);

		JSONObject tempJson = new JSONObject();
		tempJson.put("Created_Timestamp", new Date());
		tempJson.put(APITestConstants.API_TEST_RESULT_CLASS, response.getClass());
		tempJson.put(APITestConstants.API_TEST_RESULT_OBJECT, json);
		Document mongoDbObject = Document.parse(tempJson.toString());

		return DocumentDBManager.INSTANCE.insertDocumentToCollection(mongoDbObject);
	}

	public String getAPIResponseFromMongo(String documentId) {
		Document doc = getDocument(documentId);
		if (doc != null)
			return doc.toJson();
		return null;
	}

	public Document getDocument(String documentId) {
		String sourceName = APITestConstants.ENVIRONMENT_VARIABLE;
		if (StringUtils.isEmpty(sourceName) || "ENV".equals(sourceName)) {
			sourceName = "PROD";
		}
		MongoClient mongoClient = APIConfigManager.getNoSQLSessionFactory(sourceName.toUpperCase(), true);
		if (mongoClient == null) {
			log.error("Connection Error: NoSQL for Test : " + APITestContext.get().getMethodName());
			return null;
		}
		MongoCollection<Document> dbCollection = mongoClient.getDatabase(APITestConstants.DEFAULT_DOC_DB_NAME)
				.getCollection(APITestConstants.API_PERF_COLLECION);
		if (dbCollection == null) {
			log.error("Error initializing the MongoDB Collection :" + APITestConstants.DEFAULT_DOC_DB_NAME + "." + APITestConstants.API_PERF_COLLECION);
			return null;
		}
		Document query = new Document();
		Object objectId = null;
		try {
			objectId = new ObjectId(documentId);
		} catch (IllegalArgumentException iae) {
			log.error("Not a valid Document Object Id " + documentId);
			objectId = documentId;
		}
		query.put("_id", objectId);
		FindIterable<Document> docs = dbCollection.find(query);
		List<Document> documents = docs.into(new ArrayList<Document>());

		if (CollectionUtils.isEmpty(documents)) {
			System.out.println("No result found for Document Id :" + documentId);
			return null;
		}

		return documents.get(0);
	}

	public String getContentJsonFromResponseItem(String documentId, String scenarioName, Integer stepNumber, Integer itemNumber) {
		String doc = getAPIResponseFromMongo(documentId);
		if (doc != null) {
			JSONObject responseJson = JSONObject.fromObject(doc);
			String className = (String) JsonUtils.readFromJSON(responseJson, "APITestResultClass");

			if (StringUtils.isBlank(className)) {
				return responseJson.toString();
			}

			StringBuilder path = new StringBuilder("APITestResultObject.responseItems.['" + scenarioName + "'].");
			StringBuilder alternatePath = new StringBuilder("APITestResultObject.responseItems.['" + scenarioName + "'].");
			if ("com.shc.automation.api.test.framework.entities.APITestResponse".equals(className)) {
				path.append("responseContent.APITestResponse");
				alternatePath.append("responseContent");
			}
			if ("com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse".equals(className)) {
				path.append("testChainResponse[" + stepNumber + "].responseContent.APITestResponse");
				alternatePath.append("testChainResponse[" + stepNumber + "].responseContent");
			}
			if ("com.shc.automation.api.test.framework.entities.APICompareTestsResponse".equals(className)) {
				path.append("response" + itemNumber + ".responseContent.APITestResponse");
				alternatePath.append("response" + itemNumber + ".responseContent");
			}
			if ("com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse".equals(className)) {
				path.append("chainCompareResponseList[" + stepNumber + "].response" + itemNumber + ".responseContent.APITestResponse");
				alternatePath.append("chainCompareResponseList[" + stepNumber + "].response" + itemNumber + ".responseContent");
			}

			Object responseObject = JsonUtils.readFromJSON(responseJson, path.toString());
			if (responseObject == null) {
				responseObject = JsonUtils.readFromJSON(responseJson, alternatePath.toString());
			}
			if (responseObject != null) {
				String responseStr = responseObject.toString();
				if (responseStr.startsWith("com.fasterxml.jackson.core.JsonParseException")) {
					return null;
				}
				return responseStr;
			}
		}

		return null;
	}
}