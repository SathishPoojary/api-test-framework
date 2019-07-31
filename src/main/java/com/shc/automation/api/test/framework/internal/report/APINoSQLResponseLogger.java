package com.shc.automation.api.test.framework.internal.report;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.internal.connect.APINoSQLDataManager;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * @author spoojar
 */
public class APINoSQLResponseLogger {
    protected final Logger log = Logger.getLogger(this.getClass().getName());

    @Inject
    private APINoSQLDataManager noSQLDataManager;


    public ObjectId saveToNoSQL(APIBaseResponse response) {
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

        return noSQLDataManager.insertToResponseStore(mongoDbObject);
    }
}