package com.shc.automation.api.test.framework.internal.connect;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.internal.config.APIEntitiesFactory;

import net.sf.json.JSONObject;

public enum DocumentDBManager {
	INSTANCE;
	private static Logger log = Logger.getLogger("DocumentDBManager");

	public ObjectId insertDocumentToCollection(Document document) {
		String datasourceName = APIConfigManager.getDefaultNoSQLDataSourceName();
		MongoClient client = APIConfigManager.getNoSQLSessionFactory(datasourceName);
		if (client == null) {
			log.error("Connect Error in saving the response to NoSQL for Test : " + APITestContext.get().getMethodName());
			return null;
		}

		MongoCollection<Document> dbCollection = client.getDatabase(APITestConstants.DEFAULT_DOC_DB_NAME).getCollection(APITestConstants.API_PERF_COLLECION);
		if (dbCollection == null) {
			log.error("Error initializing the MongoDB Collection for Saving Response");
			return null;
		}
		try {
			dbCollection.insertOne(document);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error in saving the service response to Mongo :" + e.getMessage());
			return null;
		}

		return (ObjectId) document.get("_id");
	}

	public List<JSONObject> getDocument(String db, String collection, int fromIndex, int toIndex) {

		MongoCollection<Document> dbCollection = getMongoCollection(db, collection);
		return getDocument(dbCollection, fromIndex, toIndex);

	}

	public List<JSONObject> getDocumentById(String db, String collection, String documentId) {
		MongoCollection<Document> dbCollection = getMongoCollection(db, collection);

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

		List<JSONObject> resultDocs = new ArrayList<JSONObject>();
		for (Document doc : docs) {
			resultDocs.add(JSONObject.fromObject(doc.toJson()));
		}

		return resultDocs;
	}

	private MongoCollection<Document> getMongoCollection(String db, String collection) {
		String datasourceName = APIConfigManager.getNoSQLDataSource();
		MongoClient mongoClient = APIConfigManager.getNoSQLSessionFactory(datasourceName);
		if (mongoClient == null) {
			log.error("Connection Error: NoSQL for Test : " + APITestContext.get().getMethodName());
			return null;
		}

		if (StringUtils.isEmpty(db)) {
			db = APIEntitiesFactory.getInstance(false).getDataSource(datasourceName).getDefaultSchema();
		}
		db = (StringUtils.isEmpty(db) ? APITestConstants.DEFAULT_DOC_DB_NAME : db);
		MongoCollection<Document> dbCollection = mongoClient.getDatabase(db).getCollection(collection);
		if (dbCollection == null) {
			log.error("Error initializing the MongoDB Collection :" + db + "." + collection);
			return null;
		}
		return dbCollection;
	}

	/**
	 * @param projectConfig
	 * @param db
	 * @param collection
	 * @param fromIndex
	 * @param toIndex
	 * @param mongoClient
	 * @return
	 */
	private List<JSONObject> getDocument(MongoCollection<Document> dbCollection, int fromIndex, int toIndex) {
		if (dbCollection == null) {
			return null;
		}

		System.out.println("Getting the Documents from :" + dbCollection.getNamespace() + " [ " + fromIndex + " - " + toIndex + " ] ");

		FindIterable<Document> docs = dbCollection.find();
		List<Document> resultSet = new ArrayList<Document>();

		if (fromIndex > 0 && toIndex > 0) {
			int recordsToRet = Math.abs(toIndex - fromIndex) + 1;
			docs.skip(fromIndex).limit(recordsToRet).into(resultSet);
		} else if (fromIndex > 0) {
			docs.limit(fromIndex).into(resultSet);
		} else if (toIndex > 0) {
			docs.limit(toIndex).into(resultSet);
		} else {
			docs.into(resultSet);
		}

		List<JSONObject> resultDocs = new ArrayList<JSONObject>();
		for (Document doc : resultSet) {
			resultDocs.add(JSONObject.fromObject(doc));
		}

		return resultDocs;
	}
}
