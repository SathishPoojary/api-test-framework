package com.shc.automation.api.test.framework.internal.connect;

import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.request.APIHttpRequestReader;
import com.shc.automation.api.test.framework.internal.response.APIScenarioResponseParser;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class APIHttpConnection {
    private Logger logger = Logger.getLogger("APIHttpConnection");

    private final APIHttpRequestReader httpRequestReader;
    private final APIScenarioResponseParser responseParser;
    private HttpClientBuilder apiHttpClientBuilder;
    private PoolingHttpClientConnectionManager apiHttpConnectionManager;
    private RateLimiter rateLimiter;

    @Inject
    public APIHttpConnection(APIHttpRequestReader httpRequestReader, APIScenarioResponseParser responseParser) {
        this.httpRequestReader = httpRequestReader;
        this.responseParser = responseParser;
    }


    public APIScenarioResponse execute(final APIScenarioRequest scenarioRequest) throws IOException, APITestException {
        HttpRequestBase httpRequest = httpRequestReader.read(scenarioRequest);
        if (!isValidRequest(httpRequest)) {
            throw new APITestException("API Request Error : API METHOD / URL NOT FOUND");
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpClientContext sessionContext = getSessionContext();
        CloseableHttpResponse response = null;
        APIScenarioResponse scenarioResponse = null;
        try {
            StopWatch timeRecorder = new StopWatch();

            timeRecorder.start();
            response = httpClient.execute(httpRequest, sessionContext);
            timeRecorder.stop();

            scenarioResponse = responseParser.parseResponse(response);
            scenarioResponse.setRequestUrl(httpRequest.getURI().toString());
            scenarioResponse.setExecutionTime(timeRecorder.getNanoTime());
            if (maintainSession()) {
                scenarioResponse.saveSession(httpClient, sessionContext);
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpRequest != null) {
                    httpRequest.releaseConnection();
                }
                if (!maintainSession()) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return scenarioResponse;
    }

    public boolean isValidRequest(HttpRequestBase httpRequest) {
        return httpRequest != null && httpRequest.getURI() != null && StringUtils.isNotBlank(httpRequest.getURI().toString());
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public boolean maintainSession() {
        return APITestContext.get().getExecutionConfig().getPersistSession();
    }

    public CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient;
        if (maintainSession()) {
            CookieStore cookieStore = new BasicCookieStore();
            httpClient = apiHttpClientBuilder.setDefaultCookieStore(cookieStore).setRedirectStrategy(new LaxRedirectStrategy()).build();
        } else {
            httpClient = apiHttpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy()).build();
        }
        return httpClient;
    }

    public HttpClientContext getSessionContext() {
        if (maintainSession()) {
            return new HttpClientContext();
        }
        return null;
    }

    public void setApiHttpClientBuilder(HttpClientBuilder apiHttpClientBuilder) {
        this.apiHttpClientBuilder = apiHttpClientBuilder;
    }

    public void setApiHttpConnectionManager(PoolingHttpClientConnectionManager apiHttpConnectionManager) {
        this.apiHttpConnectionManager = apiHttpConnectionManager;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void close() {
        if (apiHttpConnectionManager != null) {
            apiHttpConnectionManager.close();
        }
    }
}
