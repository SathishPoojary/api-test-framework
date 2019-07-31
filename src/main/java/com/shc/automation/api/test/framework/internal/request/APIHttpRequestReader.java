/**
 *
 */
package com.shc.automation.api.test.framework.internal.request;

import com.google.common.net.UrlEscapers;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.model.request.APIRequestParameter;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.request.ParameterType;
import com.shc.automation.api.test.framework.model.request.RequestType;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.util.*;

/**
 * @author spoojar
 *
 */
public class APIHttpRequestReader {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final String userAgent;
    private final Integer connectionTimeout;
    private final Integer httpSocketTimeout;
    private final String defaultContentType;
    private final Map<RequestType, Provider<HttpRequestBase>> typeToRequestMap;

    @Inject
    public APIHttpRequestReader(@Named("user-agent") final String userAgent,
                                @Named("default-content-type") final String defaultContentType,
                                @Named("connection-time-out") final Integer connectionTimeout,
                                @Named("http-socket-time-out") final Integer httpSocketTimeout,
                                Map<RequestType, Provider<HttpRequestBase>> typeToRequestMap) {
        this.userAgent = userAgent;
        this.defaultContentType = defaultContentType;
        this.connectionTimeout = connectionTimeout;
        this.httpSocketTimeout = httpSocketTimeout;
        this.typeToRequestMap = typeToRequestMap;
    }

    public HttpRequestBase read(APIScenarioRequest request) throws APITestException {
        HttpRequestBase apiHttpMethod = typeToRequestMap.get(request.getRequestType()).get();
        setHeaders(apiHttpMethod, request);
        setTimeouts(apiHttpMethod, request);
        setURL(apiHttpMethod, request);
        setPayload(apiHttpMethod, request);

        return apiHttpMethod;
    }

    public void setURL(HttpRequestBase apiHttpMethod, APIScenarioRequest request) throws APITestException {
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
                if (ParameterType.path == type) {
                    pathParamString.append("/" + name + "/" + encodedValue);
                } else if (ParameterType.noname == type) {
                    pathParamString.append("/" + encodedValue);
                } else if (ParameterType.colon == type) {
                    queryParamString.append("&" + name + ":" + encodedValue);
                } else if (ParameterType.form == type) {
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

        addUrlToRequest(apiHttpMethod, urlStr.toString(), formParameters);
    }

    private void addUrlToRequest(HttpRequestBase apiHttpMethod, String url, List<NameValuePair> formParameters) {
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

    private void setPayload(HttpRequestBase apiHttpMethod, APIScenarioRequest request) {
        String payload = request.getPayload();
        if (StringUtils.isBlank(payload)) {
            return;
        }
        StringEntity entity = new StringEntity(payload, "UTF-8");
        entity.setContentType(defaultContentType);
        ((HttpEntityEnclosingRequestBase) apiHttpMethod).setEntity(entity);
    }

    private void setTimeouts(HttpRequestBase apiHttpMethod, APIScenarioRequest request) {
        Integer socketTimeout = request.getSocketTimeout();
        if (socketTimeout == null) {
            socketTimeout = httpSocketTimeout;
        }
        RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(socketTimeout).setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout).build();
        apiHttpMethod.setConfig(requestConfig);
    }

    private Map<String, String> setHeaders(HttpRequestBase apiHttpMethod, APIScenarioRequest request) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Accept", defaultContentType);
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
        apiHttpMethod.addHeader("User-Agent", userAgent);
        return headerMap;
    }
}
