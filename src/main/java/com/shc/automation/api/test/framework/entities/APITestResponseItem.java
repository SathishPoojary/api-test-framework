/**
 * 
 */
package com.shc.automation.api.test.framework.entities;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.internal.report.APIHtmlTestItemLog;
import com.shc.automation.api.test.framework.utils.APITestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author spoojar
 * 
 */
public class APITestResponseItem implements Serializable {
	private static final long serialVersionUID = -2640731601205374320L;
	private String scenarioName;
	private String displayName = null;
	private String baseUrl;
	private String requestUrl;
	private String endpoint;
	private String environment;
	private RequestType requestType = RequestType.get;
	private Object payLoad;
	private List<APIRequestParameter> urlParameters;
	private Map<String, String> requestHeaders = null;
	private transient Map<String, Object> context = null;

	private boolean failTestOnValidationFailure = true;
	private Boolean printContentInReport = false;
	private Boolean digitalSignatureRequired = false;

	private Object responseContent;
	private Object responseToCompare;
	private Integer responseCode;
	private String reason;
	private String responseType;
	private Map<String, String> responseHeaders = null;
	private long executionTime;
	private Integer retryCount = 0;

	private ResultType result;
	private List<APIValidationField> validators;
	private List<APIPrintField> printers;
	private Throwable apiError = null;

	private String validStatusCodes;
	private Boolean validResponseCode = null;

	private transient CloseableHttpClient httpClient = null;
	private transient List<Cookie> cookies = null;

	public APITestResponseItem() {
		validators = new ArrayList<APIValidationField>();
		printers = new ArrayList<APIPrintField>();
	}

	/**
	 * Get the API Test Response Content. Usually a JSON Response Object.
	 * 
	 * @return
	 */
	public Object getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(Object responseContent) {
		this.responseContent = responseContent;
	}

	/**
	 * Get the API Test Response Code.
	 * 
	 * @return
	 */
	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	/**
	 * Get the complete API Test request URL used to get this response.
	 * 
	 * @return
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long elapsedTestTime) {
		this.executionTime = elapsedTestTime;
	}

	public Throwable getApiError() {
		return apiError;
	}

	public List<APIValidationField> getValidators() {
		return validators;
	}

	public void setValidators(List<APIValidationField> validators) {
		this.validators = validators;
	}

	public void setValidator(APIValidationField validator) {
		this.validators.add(validator);
	}

	public void setApiError(Throwable apiError) {
		if (apiError != null) {
			result = ResultType.ERROR;
		}
		this.apiError = apiError;
	}

	public ResultType getResult() {
		return result;
	}

	public void setResult(ResultType apiTestResult) {
		this.result = apiTestResult;
	}

	public List<APIPrintField> getPrinters() {
		return printers;
	}

	public void setPrinters(List<APIPrintField> printFields) {
		this.printers = printFields;
	}

	public void addPrinter(APIPrintField printField) {
		this.printers.add(printField);
	}

	/**
	 * Get the Value for a Printer/Validation path defined in the configuration
	 * print-fields or validations
	 * 
	 * @param name
	 *            Name of the printer
	 * 
	 * @return
	 */
	public Object getPrintValue(String name) {
		APIPrintField field = null;
		for (int i = 0; i < printers.size(); i++) {
			field = printers.get(i);
			if (name.equals(field.getPrintName())) {
				return field.getResponseValue();
			}
		}
		APIValidationField valid = null;
		for (int i = 0; i < validators.size(); i++) {
			valid = validators.get(i);
			if (name.equals(valid.getValidationName())) {
				return valid.getActualResponseValue();
			}
		}
		return "";
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getEscapedScenarioName() {
		return UrlEscapers.urlFormParameterEscaper().escape(scenarioName);
	}

	/**
	 * Get the value for a specific path in the API Test Response. Returns NULL if
	 * path not found in the response.
	 * 
	 * @param path
	 * 
	 * @return
	 */
	public Object getValueFromPath(String path) {
		return APITestUtils.readFromJSON(this.responseContent, path, true);
	}

	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	public boolean getValidResponse() {
		if (validResponseCode == null) {
			validResponseCode = isValidResponse();

		}
		return validResponseCode;
	}

	private boolean isValidResponse() {

		if (this.apiError != null) {
			return false;
		}

		if (StringUtils.isNotBlank(validStatusCodes)) {
			if (validStatusCodes.indexOf(',') != -1) {
				List<String> statusCodes = APITestUtils.getListFromString(validStatusCodes);
				if (CollectionUtils.isNotEmpty(statusCodes) && !statusCodes.contains(String.valueOf(responseCode))) {
					return false;
				}
			} else if (validStatusCodes.indexOf('-') != -1) {

				String from = validStatusCodes.substring(0, validStatusCodes.indexOf('-'));
				String to = validStatusCodes.substring(validStatusCodes.indexOf('-') + 1);

				int fromRange = NumberUtils.isCreatable(from) ? NumberUtils.toInt(from) : 0;
				int toRange = NumberUtils.isCreatable(to) ? NumberUtils.toInt(to) : 300;

				if (responseCode < fromRange || responseCode > toRange) {
					return false;
				}
			} else {
				int validStatusCode = NumberUtils.isCreatable(validStatusCodes) ? NumberUtils.toInt(validStatusCodes) : 0;
				if (responseCode != validStatusCode) {
					return false;
				}
			}

		} else if (responseCode >= 300) {
			return false;
		}
		return true;

	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isValidResult() {
		if (!getValidResponse()) {
			return false;
		}
		if (result == ResultType.PASSED)
			return true;
		return false;
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public boolean isFailTestOnValidationFailure() {
		return failTestOnValidationFailure;
	}

	public void failTestOnValidationFailure(boolean failTestOnValidationFailure) {
		this.failTestOnValidationFailure = failTestOnValidationFailure;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String toString() {
		StringBuilder strBldr = new StringBuilder(scenarioName + "--------------------------------------------------");
		strBldr.append("\nRequest URL :" + requestUrl);
		if (payLoad != null)
			strBldr.append("\nPayload :" + payLoad);
		if (apiError != null)
			strBldr.append("\n ERROR ********" + apiError.toString());
		else {
			strBldr.append("\nResponse Code :---------" + responseCode + "--------");
			strBldr.append("\nResponse Type :" + responseType);
			strBldr.append("\nExecution Result :" + result);
			strBldr.append("\nExecution time :" + executionTime);
			strBldr.append("\nRetry Count :" + retryCount);
			strBldr.append("\n Validations : " + validators);

		}
		strBldr.append("\n----------------------------------------------------------------------------------\n");
		return strBldr.toString();

	}

	public Boolean getPrintContentInReport() {
		return printContentInReport;
	}

	public void setPrintContentInReport(Boolean printContentInReport) {
		this.printContentInReport = printContentInReport;
	}

	public StringBuilder getHtmlString() {
		return APIHtmlTestItemLog.generateHtmlLog(this);
	}

	public String getPayloadString() {
		if (payLoad == null || StringUtils.isBlank(payLoad.toString()))
			return "";

		String str = payLoad.toString();

		try {
			if (str.startsWith("<")) {
				return prettyPrintXml(payLoad.toString());
			}
			if (str.startsWith("{")) {
				return JSONObject.fromObject(payLoad).toString(4, 4);
			} else if (str.startsWith("[")) {
				return JSONArray.fromObject(payLoad).toString(4, 4);
			} else {
				return payLoad.toString();
			}
		} catch (Exception e) {
			return str;
		}
	}

	public static String prettyPrintXml(String xml) {
		final StringWriter sw;

		try {
			final OutputFormat format = OutputFormat.createPrettyPrint();
			final org.dom4j.Document document = DocumentHelper.parseText(xml);
			sw = new StringWriter();
			final XMLWriter writer = new XMLWriter(sw, format);
			writer.write(document);
		} catch (Exception e) {
			return xml;
		}
		return sw.toString();
	}

	public String getResponseContentString() {
		if (responseContent == null || StringUtils.isBlank(responseContent.toString()))
			return "";

		String str = responseContent.toString();

		try {
			if (str.startsWith("<")) {
				return prettyPrintXml(responseContent.toString());
			}

			Object response = responseContent;
			if (responseContent instanceof Map)
				response = APITestUtils.readFromJSON(responseContent, "$", true);
			if (response != null) {
				if (response.toString().startsWith("[")) {
					return JSONArray.fromObject(response).toString(4, 4);
				} else if (response.toString().startsWith("{")) {
					return JSONObject.fromObject(response).toString(4, 4);
				} else if (str.startsWith("<")) {
					return prettyPrintXml(responseContent.toString());
				} else {
					return response.toString();
				}
			}
		} catch (Exception e) {
			return str;
		}
		return str;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public List<APIRequestParameter> getUrlParameters() {
		return urlParameters;
	}

	public void setUrlParameters(List<APIRequestParameter> urlParameters) {
		this.urlParameters = urlParameters;
	}

	public String getValidStatusCodes() {
		return validStatusCodes;
	}

	public void setValidStatusCodes(String validStatusCodes) {
		this.validStatusCodes = validStatusCodes;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public Object getResponseToCompare() {
		return responseToCompare;
	}

	public void setResponseToCompare(Object responseToCompare) {
		this.responseToCompare = responseToCompare;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Boolean getDigitalSignatureRequired() {
		return digitalSignatureRequired;
	}

	public void setDigitalSignatureRequired(Boolean digitalSignatureRequired) {
		if (digitalSignatureRequired == null)
			digitalSignatureRequired = false;
		this.digitalSignatureRequired = digitalSignatureRequired;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public void addContext(String name, Object value) {
		this.context.put(name, value);
	}

	public Object getContextValue(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return this.context.get(name);
	}

	public HttpClientContext getHttpClientContext() {
		HttpClientContext context = null;
		if (CollectionUtils.isNotEmpty(cookies)) {
			context = HttpClientContext.create();
			CookieStore httpCookieStore = new BasicCookieStore();
			for (Cookie cookie : cookies) {
				httpCookieStore.addCookie(cookie);
			}
			context.setCookieStore(httpCookieStore);
		}
		return context;
	}
}