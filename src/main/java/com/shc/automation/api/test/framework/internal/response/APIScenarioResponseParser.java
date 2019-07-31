package com.shc.automation.api.test.framework.internal.response;

import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import com.shc.automation.utils.json.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APIScenarioResponseParser {
    private static Logger logger = Logger.getLogger("APIScenarioResponseParser");

    public APIScenarioResponse parseResponse(CloseableHttpResponse responseStream) {
        APIScenarioResponse responseItem = new APIScenarioResponse();
        if (responseStream == null) {
            return responseItem;
        }
        try {
            responseItem.setResponseContent(getBody(responseStream));
        }catch (APITestException e){
            responseItem.setApiError(e);
        }
        responseItem.setResponseCode(getStatusCode(responseStream));
        responseItem.setReason(getReasonPhrase(responseStream));
        responseItem.setResponseHeaders(getResponseHeaders(responseStream));
        responseItem.setResponseType(getResponseType(responseStream));

        return responseItem;
    }

    public int getStatusCode(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    public String getReasonPhrase(CloseableHttpResponse response) {
        return response.getStatusLine().getReasonPhrase();
    }

    public Map<String, String> getResponseHeaders(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        Map<String, String> responseHeaders = new HashMap<String, String>();
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                String headerName = headers[i].getName().replace(".", " ");
                responseHeaders.put(headerName, headers[i].getValue());
            }
        }
        return responseHeaders;
    }

    public static String getBody(CloseableHttpResponse response) throws APITestException {

        if (response.getEntity() == null) {
            throw new APITestException("Error in processing API Response Empty response recieved from API.");
        }
        try {
            String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            return responseBody;
        } catch (IOException e) {
            throw new APITestException("Error in processing API Response Empty response relieved from API.");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getResponseType(CloseableHttpResponse response) {
        Header[] contentType = response.getHeaders("Content-type");
        String contentTypeStr = null;
        if (contentType != null && contentType.length > 0) {
            contentTypeStr = contentType[0].getValue();
        }
        return contentTypeStr;
    }

    public Object formatResponseContent(String response, String responseFormat) {
        if (StringUtils.isBlank(response)) {
            return response;
        }

        JSONObject responseJson = null;
        try {
            String jsonString = response;
            logger.info("Response Format :" + responseFormat);

            responseFormat = responseFormat == null ? "" : responseFormat;
            if (responseFormat.startsWith("text/xml") || responseFormat.startsWith("application/xml") || jsonString.startsWith("<")) {
                jsonString = JsonUtils.convertXMLToJson(response, true).toString();
            }

            Object cachedResponse = null;

            if (jsonString.trim().startsWith("[")) {
                cachedResponse = JSONArray.fromObject(jsonString);
            } else if (jsonString.toString().startsWith("{")) {
                cachedResponse = JSONObject.fromObject(jsonString);
            } else {
                cachedResponse = jsonString;
            }
            responseJson = APITestUtils.wrapAPIResponse(cachedResponse);

        } catch (Exception e) {
            logger.error("Error in Parsing the Response!. Storing response as-is :\n" + e.getMessage());
            responseJson = APITestUtils.wrapAPIResponse(response);
        }
        return responseJson;

    }
}
