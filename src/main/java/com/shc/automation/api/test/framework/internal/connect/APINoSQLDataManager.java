package com.shc.automation.api.test.framework.internal.connect;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.internal.config.APIDataSourceConfigManager;
import com.shc.automation.api.test.framework.internal.config.xml.APIDataSourceConnectConfig;
import com.shc.automation.utils.PasswordEncryptor;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class APINoSQLDataManager {
    private Logger logger = Logger.getLogger("SQLDBManager");

    private Map<String, MongoClient> noSQLConnectionPool;
    public final String DEFAULT_DOC_DB_NAME = "carsApiDb";
    public final String API_PERF_COLLECION = "api_test_response_store";

    private final APIDataSourceConfigManager datasourceConfigManager;

    @Inject
    public APINoSQLDataManager(APIDataSourceConfigManager datasourceConfigManager) {
        this.datasourceConfigManager = datasourceConfigManager;
        noSQLConnectionPool = new ConcurrentHashMap<>();
    }

    private MongoClient getNoSQLSessionFactory(String datasourceName) {
        if (noSQLConnectionPool.containsKey(datasourceName)) {
            return noSQLConnectionPool.get(datasourceName);
        }

        APIDataSourceConnectConfig datasource = datasourceConfigManager.getNoSQLConfig(datasourceName);
        if (datasource == null) {
            logger.error("No Data Sources configured with source name :" + datasourceName);
            return null;
        }

        MongoClient mongoClient = null;
        try {
            String password = datasource.getPassword();
            if (StringUtils.isNotBlank(datasource.getUsername()) && StringUtils.isNotBlank(password)) {
                mongoClient = new MongoClient(new ServerAddress(datasource.getUrl()),
                        Arrays.asList(MongoCredential.createCredential(datasource.getUsername(), datasource.getDefaultSchema(), decryptPassword(password).toCharArray())));

            } else {
                mongoClient = new MongoClient(datasource.getUrl());
            }

            noSQLConnectionPool.put(datasource.getName(), mongoClient);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mongoClient;
    }

    public ObjectId insertToResponseStore(Document document) {
        MongoClient client = getNoSQLSessionFactory(datasourceConfigManager.getDefaultNoSQLDataSourceName());
        if (client == null) {
            logger.error("Connect Error in saving the response to NoSQL for Test : " + APITestContext.get().getMethodName());
            return null;
        }

        MongoCollection<Document> dbCollection = client.getDatabase(DEFAULT_DOC_DB_NAME).getCollection(API_PERF_COLLECION);
        if (dbCollection == null) {
            logger.error("Error initializing the MongoDB Collection for Saving Response");
            return null;
        }
        try {
            dbCollection.insertOne(document);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in saving the service response to Mongo :" + e.getMessage());
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
            logger.error("Not a valid Document Object Id " + documentId);
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
        MongoClient mongoClient = getNoSQLSessionFactory(datasourceConfigManager.getNoSQLDataSource());
        if (mongoClient == null) {
            logger.error("Connection Error: NoSQL for Test : " + APITestContext.get().getMethodName());
            return null;
        }

        if (StringUtils.isEmpty(db)) {
            db = datasourceConfigManager.getDataSource(datasourceConfigManager.getNoSQLDataSource()).getDefaultSchema();
        }
        db = (StringUtils.isEmpty(db) ? DEFAULT_DOC_DB_NAME : db);
        MongoCollection<Document> dbCollection = mongoClient.getDatabase(db).getCollection(collection);
        if (dbCollection == null) {
            logger.error("Error initializing the MongoDB Collection :" + db + "." + collection);
            return null;
        }
        return dbCollection;
    }

    private String decryptPassword(String password) {
        if (password.startsWith("ENC(")) {
            password = new PasswordEncryptor().decrypt(password);
        }
        return password;
    }

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

    public void close() {
        Iterator<String> conIter = noSQLConnectionPool.keySet().iterator();
        while (conIter.hasNext()) {
            String connection = conIter.next();
            MongoClient factory = noSQLConnectionPool.get(connection);


            System.out.println("Closing NoSQL Connection :" + connection);
            if (factory == null)
                return;
            factory.close();

        }
        noSQLConnectionPool.clear();
    }


}
