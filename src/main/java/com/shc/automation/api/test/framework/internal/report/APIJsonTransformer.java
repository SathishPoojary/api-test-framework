/**
 *
 */
package com.shc.automation.api.test.framework.internal.report;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.model.response.chain.compare.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.chain.compare.APIChainCompareTestsBaseResponse;
import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponse;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponse;
import com.shc.automation.api.test.framework.model.response.compare.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.model.request.TestType;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import com.shc.automation.api.test.framework.model.response.APIResponse;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.chain.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author spoojar
 *
 */
public class APIJsonTransformer {

    private static Gson getGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIBaseResponse.class, new APIResponseAdapter());
        gson.registerTypeAdapter(APIResponse.class, new APIResponseAdapter());
        gson.registerTypeAdapter(APIChainTestsResponse.class, new APIResponseAdapter());
        gson.registerTypeAdapter(APICompareTestsResponse.class, new APIResponseAdapter());
        gson.registerTypeAdapter(APIChainCompareTestsBaseResponse.class, new APIResponseAdapter());
        return gson.create();
    }

    public static String convertToJson(APIBaseResponse response) {
        return getGson().toJson(response);
    }

    public static APIScenarioResponse convertToAPIResponseItem(String json) {

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIValidation.class, new APIValidatorAdapter());
        if (StringUtils.isBlank(json)) {
            return null;
        }
        APIScenarioResponse responseItem = gson.create().fromJson(json, APIScenarioResponse.class);

        return responseItem;
    }

    public static String convertToJSON(APIScenarioResponse responseItem) {
        try {
            return new APITestResponseItemAdapter().toJson(responseItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static APIBaseResponse convertToAPIResponse(String json) {
        return getGson().fromJson(json, APIBaseResponse.class);
    }

    public static APIBaseResponse getSummary(String json) {
        JSONObject jsonObject = JSONObject.fromObject(json);
        Object responseObject = jsonObject.get(APITestConstants.API_TEST_RESULT_OBJECT);
        if (responseObject == null) {
            return null;
        }
        return new GsonBuilder().create().fromJson(responseObject.toString(), APIBaseResponse.class);
    }

}

class APIValidatorAdapter extends TypeAdapter<APIValidation> {
    @Override
    public void write(JsonWriter out, APIValidation value) throws IOException {
        String path = value.getResponsePath();
        String name = value.getValidationName();
        if (path != null && path.endsWith("]")) {
            value.setResponsePath(path + "*");
        }
        if (name != null && name.endsWith("]")) {
            value.setValidationName(name + "*");
        }

        new GsonBuilder().create().toJson(value, value.getClass(), out);
    }

    @Override
    public APIValidation read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        return new GsonBuilder().create().fromJson(value, APIValidation.class);
    }
}

class APIResponseAdapter extends TypeAdapter<APIBaseResponse> {
    @Override
    public void write(JsonWriter out, APIBaseResponse value) throws IOException {
        getRespBuilder().create().toJson(value, value.getClass(), out);
    }

    private GsonBuilder getRespBuilder() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIScenarioResponse.class, new APITestResponseItemAdapter());
        gson.registerTypeAdapter(APIChainTestsResponseItem.class, new APIChainTestResponseItemAdapter());
        gson.registerTypeAdapter(APICompareTestsResponseItem.class, new APICompareTestResponseItemAdapter());
        gson.registerTypeAdapter(APIChainCompareTestResponseItem.class, new APIChainCompareTestResponseItemAdapter());
        return gson;
    }

    @Override
    public APIBaseResponse read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(value.toString());
        String responseClass = jsonObject.getString(APITestConstants.API_TEST_RESULT_CLASS);
        if (StringUtils.isEmpty(responseClass)) {
            return null;
        }

        Object responseObject = jsonObject.get(APITestConstants.API_TEST_RESULT_OBJECT);
        if (responseObject == null) {
            return null;
        }

        try {
            APIBaseResponse response = (APIBaseResponse) getRespBuilder().create().fromJson(JSONObject.fromObject(responseObject).toString(),
                    Class.forName(responseClass));
            if (response.getTestType() == null) {
                if ("com.shc.automation.api.test.framework.entities.APIResponse".equals(responseClass)) {
                    response.setTestType(TestType.standalone);
                }
                if ("com.shc.automation.api.test.framework.entities.compare.APICompareTestsResponse".equals(responseClass)) {
                    response.setTestType(TestType.comparative);
                }
                if ("com.shc.automation.api.test.framework.entities.chain.APIChainTestsResponse".equals(responseClass)) {
                    response.setTestType(TestType.chain);
                }
                if ("com.shc.automation.api.test.framework.entities.chain.APIChainCompareTestsBaseResponse".equals(responseClass)) {
                    response.setTestType(TestType.comparativechain);
                }

            }
            response.setValidationFrequencyTable(APITestReportUtils.getMismatchFrequencyHtml(APITestUtils.getMismatchedFieldFrequencyMap(response)));
            return response;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}

class APITestResponseItemAdapter extends TypeAdapter<APIScenarioResponse> {

    @Override
    public void write(JsonWriter out, APIScenarioResponse value) throws IOException {
        if (value != null && !value.getPrintContentInReport())
            value.setResponseContent(null);
        getItemBuilder().create().toJson(value, APIScenarioResponse.class, out);
    }

    private GsonBuilder getItemBuilder() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIValidation.class, new APIValidatorAdapter());
        return gson;
    }

    @Override
    public APIScenarioResponse read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        APIScenarioResponse responseItem = getItemBuilder().create().fromJson(value.toString(), APIScenarioResponse.class);
        if (responseItem != null && responseItem.getPrintContentInReport()) {
            responseItem.setResponseContent(null);
        }
        return responseItem;
    }

}

class APIChainTestResponseItemAdapter extends TypeAdapter<APIChainTestsResponseItem> {

    @Override
    public void write(JsonWriter out, APIChainTestsResponseItem value) throws IOException {
        getItemBuilder().create().toJson(value, value.getClass(), out);
    }

    private GsonBuilder getItemBuilder() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIScenarioResponse.class, new APITestResponseItemAdapter());
        gson.registerTypeAdapter(APIValidation.class, new APIValidatorAdapter());
        return gson;
    }

    @Override
    public APIChainTestsResponseItem read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        APIChainTestsResponseItem responseItem = getItemBuilder().create().fromJson(value.toString(), APIChainTestsResponseItem.class);
        return responseItem;
    }

}

class APICompareTestResponseItemAdapter extends TypeAdapter<APICompareTestsResponseItem> {

    @Override
    public void write(JsonWriter out, APICompareTestsResponseItem value) throws IOException {
        getItemBuilder().create().toJson(value, value.getClass(), out);
    }

    private GsonBuilder getItemBuilder() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APIScenarioResponse.class, new APITestResponseItemAdapter());
        gson.registerTypeAdapter(APIValidation.class, new APIValidatorAdapter());
        return gson;
    }

    @Override
    public APICompareTestsResponseItem read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        APICompareTestsResponseItem responseItem = getItemBuilder().create().fromJson(value.toString(), APICompareTestsResponseItem.class);
        return responseItem;
    }

}

class APIChainCompareTestResponseItemAdapter extends TypeAdapter<APIChainCompareTestResponseItem> {

    @Override
    public void write(JsonWriter out, APIChainCompareTestResponseItem value) throws IOException {
        getItemBuilder().create().toJson(value, value.getClass(), out);
    }

    private GsonBuilder getItemBuilder() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(APICompareTestsResponseItem.class, new APICompareTestResponseItemAdapter());
        return gson;
    }

    @Override
    public APIChainCompareTestResponseItem read(JsonReader in) throws IOException {
        JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        APIChainCompareTestResponseItem responseItem = getItemBuilder().create().fromJson(value.toString(), APIChainCompareTestResponseItem.class);
        return responseItem;
    }

}
