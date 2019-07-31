/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.common.net.UrlEscapers;
import com.shc.automation.api.test.framework.entities.APIRequestParameter;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.ParameterType;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author spoojar
 *
 */
public class APIHttpRequest {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	protected final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36/SHC Automation/1.0 KTXN";
	protected final static String ACCEPT = "application/json";
	private HttpRequestBase apiHttpMethod = null;
	private APITestRequestItem request = null;

	public HttpRequestBase getMethod(APITestRequestItem request) throws APITestException {
		this.request = request;
		switch (request.getRequestType()) {
		case get:
			apiHttpMethod = new HttpGet();
			break;
		case post:
			apiHttpMethod = new HttpPost();
			break;
		case put:
			apiHttpMethod = new HttpPut();
			break;
		case delete:
			apiHttpMethod = new HttpDelete();
			break;
		default:
			apiHttpMethod = new HttpGet();
			break;
		}
		setHeaders();
		setTimeouts();
		setURL();
		setPayload();

		return apiHttpMethod;
	}

	public void setURL() throws APITestException {
		String baseUrl = request.getUrl();
		if (StringUtils.isBlank(baseUrl)) {
			log.error("Error!!!... URL not found for Test Scenario :" + request.getScenarioName());
			throw new APITestException("Error!!!... URL not found for Test Scenario :" + request.getScenarioName());
		}
		List<APIRequestParameter> params = request.getUrlParameters();
		List<NameValuePair> formParameters = new ArrayList<NameValuePair>();

		StringBuilder urlStr = new StringBuilder(baseUrl);

		if (CollectionUtils.isNotEmpty(params) || request.getDigitalSignatureRequired()) {
			Iterator<APIRequestParameter> paramIter = params.iterator();
			APIRequestParameter param = null;
			ParameterType type = null;

			StringBuilder pathParamString = new StringBuilder();
			StringBuilder queryParamString = new StringBuilder();

			while (paramIter.hasNext()) {
				param = paramIter.next();
				if (param == null) {
					continue;
				}
				String name = param.getParamName();
				String paramValue = param.getParamValue() == null ? "" : param.getParamValue().toString();
				if (StringUtils.isEmpty(name) && StringUtils.isEmpty(paramValue)) {
					continue;
				}
				String encodedValue = paramValue;
				if (param.encodeValue()) {
					encodedValue = UrlEscapers.urlFormParameterEscaper().escape(encodedValue);
				}

				type = param.getType();
				if (ParameterType.pathParam == type) {
					pathParamString.append("/" + name + "/" + encodedValue);
				} else if (ParameterType.nonameParam == type) {
					pathParamString.append("/" + encodedValue);
				} else if (ParameterType.colonParam == type) {
					queryParamString.append("&" + name + ":" + encodedValue);
				} else if (ParameterType.formParam == type) {
					formParameters.add(new BasicNameValuePair(name, paramValue));
				} else {
					queryParamString.append("&" + name + "=" + encodedValue);
				}
			}

			if (request.getDigitalSignatureRequired()) {
				String digiUrl = APITestUtils.getDigitalSignURL();
				queryParamString.append(digiUrl);
				System.out.println("Appended Digital URL :" + digiUrl);
			}

			int lastIndex = urlStr.length() - 1;
			if (urlStr.charAt(lastIndex) == '&' || urlStr.charAt(lastIndex) == '?' || urlStr.charAt(lastIndex) == '/') {
				urlStr.deleteCharAt(lastIndex);
			}

			if (urlStr.indexOf("?") != -1) {
				if (pathParamString.length() > 0) {
					urlStr.insert(urlStr.indexOf("?"), pathParamString);
				}
				if (queryParamString.length() > 0) {
					urlStr.append(queryParamString);
				}
			} else {
				if (pathParamString.length() > 0) {
					urlStr.append(pathParamString);
				}
				if (queryParamString.length() > 0) {
					queryParamString.deleteCharAt(0);
					urlStr.append("?" + queryParamString);
				}
			}
		}

		addUrlToRequest(urlStr.toString(), formParameters);
	}

	private void addUrlToRequest(String url, List<NameValuePair> formParameters) {
		if (url.endsWith("&") || url.endsWith("?")) {
			url = url.substring(0, url.length() - 1);
		}
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			apiHttpMethod.setURI(uriBuilder.build());
			if (apiHttpMethod instanceof HttpEntityEnclosingRequestBase && CollectionUtils.isNotEmpty(formParameters)) {
				((HttpEntityEnclosingRequestBase) apiHttpMethod).setEntity(new UrlEncodedFormEntity(formParameters, Consts.UTF_8));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void setPayload() {
		String payload = request.getPayload();
		if (StringUtils.isBlank(payload)) {
			return;
		}
		StringEntity entity = new StringEntity(payload, "UTF-8");
		entity.setContentType("application/json");
		((HttpEntityEnclosingRequestBase) apiHttpMethod).setEntity(entity);
	}

	private void setTimeouts() {
		Integer socketTimeout = request.getSocketTimeout();
		if (socketTimeout == null) {
			socketTimeout = APITestConstants.HTTP_SOCKET_TIME_OUT;
		}
		RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(socketTimeout).setConnectTimeout(APITestConstants.CON_TIME_OUT)
				.setConnectionRequestTimeout(APITestConstants.CON_TIME_OUT).build();
		apiHttpMethod.setConfig(requestConfig);
	}

	private Map<String, String> setHeaders() {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Accept", ACCEPT);
		Map<String, String> headers = request.getHeaders();
		if (headers != null) {
			headerMap.putAll(headers);
		}
		request.setHeaders(headerMap);

		Iterator<String> headerParamKeys = headerMap.keySet().iterator();
		while (headerParamKeys.hasNext()) {
			String headerKey = headerParamKeys.next();
			if (StringUtils.isNoneBlank(headerMap.get(headerKey))) {
				apiHttpMethod.addHeader(headerKey, headerMap.get(headerKey));
			}
		}
		apiHttpMethod.addHeader("User-Agent", USER_AGENT);
		return headerMap;
	}
}
