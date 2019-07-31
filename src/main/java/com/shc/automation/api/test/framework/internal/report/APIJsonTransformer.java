/**
 * 
 */
package com.shc.automation.api.test.framework.internal.report;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestResponseItem;
import com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse;
import com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponse;
import com.shc.automation.api.test.framework.entities.APICompareTestsResponseItem;
import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APIRunConfig;
import com.shc.automation.api.test.framework.entities.APIRunHistory;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestResponse;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.entities.APIValidationField;
import com.shc.automation.api.test.framework.entities.TestType;
import com.shc.automation.api.test.framework.utils.APITestUtils;

import net.sf.json.JSONObject;

/**
 * @author spoojar
 *
 */
public class APIJsonTransformer {

	private static Gson getGson() {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(APIResponse.class, new APIResponseAdapter());
		gson.registerTypeAdapter(APITestResponse.class, new APIResponseAdapter());
		gson.registerTypeAdapter(APIChainTestsResponse.class, new APIResponseAdapter());
		gson.registerTypeAdapter(APICompareTestsResponse.class, new APIResponseAdapter());
		gson.registerTypeAdapter(APIChainCompareTestsResponse.class, new APIResponseAdapter());
		return gson.create();
	}

	public static String convertToJson(APIResponse response) {
		return getGson().toJson(response);
	}

	public static APITestResponseItem convertToAPIResponseItem(String json) {

		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(APIValidationField.class, new APIValidatorAdapter());
		if (StringUtils.isBlank(json)) {
			return null;
		}
		APITestResponseItem responseItem = gson.create().fromJson(json, APITestResponseItem.class);

		return responseItem;
	}

	public static String convertToJSON(APITestResponseItem responseItem) {
		try {
			return new APITestResponseItemAdapter().toJson(responseItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static APIResponse convertToAPIResponse(String json) {
		return getGson().fromJson(json, APIResponse.class);
	}

	public static APIResponse getSummary(String json) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		Object responseObject = jsonObject.get(APITestConstants.API_TEST_RESULT_OBJECT);
		if (responseObject == null) {
			return null;
		}
		return new GsonBuilder().create().fromJson(responseObject.toString(), APIResponse.class);
	}

	public static String convertToJson(final APIRunConfig config) {
		String response = convertToJSON(convertToAPIResponseItem(config.getConfig()));
		config.setConfig(null);
		JSONObject runConfig = JSONObject.fromObject(config);
		runConfig.put("config", response);

		return runConfig.toString();
	}
	
	public static String convertToJson(final APIRunHistory history) {
		String response = convertToJSON(convertToAPIResponseItem(history.getConfig()));
		history.setConfig(null);
		JSONObject runConfig = JSONObject.fromObject(history);
		runConfig.put("config", response);

		return runConfig.toString();
	}
}

class APIValidatorAdapter extends TypeAdapter<APIValidationField> {
	@Override
	public void write(JsonWriter out, APIValidationField value) throws IOException {
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
	public APIValidationField read(JsonReader in) throws IOException {
		JsonElement value = Streams.parse(in);
		if (value.isJsonNull()) {
			return null;
		}
		return new GsonBuilder().create().fromJson(value, APIValidationField.class);
	}
}

class APIResponseAdapter extends TypeAdapter<APIResponse> {
	@Override
	public void write(JsonWriter out, APIResponse value) throws IOException {
		getRespBuilder().create().toJson(value, value.getClass(), out);
	}

	private GsonBuilder getRespBuilder() {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(APITestResponseItem.class, new APITestResponseItemAdapter());
		gson.registerTypeAdapter(APIChainTestsResponseItem.class, new APIChainTestResponseItemAdapter());
		gson.registerTypeAdapter(APICompareTestsResponseItem.class, new APICompareTestResponseItemAdapter());
		gson.registerTypeAdapter(APIChainCompareTestResponseItem.class, new APIChainCompareTestResponseItemAdapter());
		return gson;
	}

	@Override
	public APIResponse read(JsonReader in) throws IOException {
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
			APIResponse response = (APIResponse) getRespBuilder().create().fromJson(JSONObject.fromObject(responseObject).toString(),
					Class.forName(responseClass));
			if (response.getTestType() == null) {
				if ("com.shc.automation.api.test.framework.entities.APITestResponse".equals(responseClass)) {
					response.setTestType(TestType.standalone);
				}
				if ("com.shc.automation.api.test.framework.entities.APICompareTestsResponse".equals(responseClass)) {
					response.setTestType(TestType.comparative);
				}
				if ("com.shc.automation.api.test.framework.chaining.entities.APIChainTestsResponse".equals(responseClass)) {
					response.setTestType(TestType.chain);
				}
				if ("com.shc.automation.api.test.framework.chaining.entities.APIChainCompareTestsResponse".equals(responseClass)) {
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

class APITestResponseItemAdapter extends TypeAdapter<APITestResponseItem> {

	@Override
	public void write(JsonWriter out, APITestResponseItem value) throws IOException {
		if (value != null && !value.getPrintContentInReport())
			value.setResponseContent(null);
		getItemBuilder().create().toJson(value, APITestResponseItem.class, out);
	}

	private GsonBuilder getItemBuilder() {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(APIValidationField.class, new APIValidatorAdapter());
		return gson;
	}

	@Override
	public APITestResponseItem read(JsonReader in) throws IOException {
		JsonElement value = Streams.parse(in);
		if (value.isJsonNull()) {
			return null;
		}
		APITestResponseItem responseItem = getItemBuilder().create().fromJson(value.toString(), APITestResponseItem.class);
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
		gson.registerTypeAdapter(APITestResponseItem.class, new APITestResponseItemAdapter());
		gson.registerTypeAdapter(APIValidationField.class, new APIValidatorAdapter());
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
		gson.registerTypeAdapter(APITestResponseItem.class, new APITestResponseItemAdapter());
		gson.registerTypeAdapter(APIValidationField.class, new APIValidatorAdapter());
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
