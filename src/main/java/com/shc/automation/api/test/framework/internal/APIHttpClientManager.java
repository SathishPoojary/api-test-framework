package com.shc.automation.api.test.framework.internal;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.google.common.util.concurrent.RateLimiter;
import com.shc.automation.api.test.framework.APITestContext;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.exception.APITestException;

public class APIHttpClientManager {
	private Logger log = Logger.getLogger(this.getClass().getName());

	private HttpClientBuilder apiHttpClientBuilder = null;
	private PoolingHttpClientConnectionManager apiHttpConnectionManager = null;
	private RateLimiter rateLimiter;

	public APIHttpClientManager(int defaultConnectionsPerRoute, int noOfRequests, Double throttle) {
		setupClient(defaultConnectionsPerRoute, noOfRequests, throttle);
	}

	private void setupClient(int defaultConnectionsPerRoute, int noOfRequests, Double throttle) {
		apiHttpClientBuilder = HttpClientBuilder.create();
		SSLContext sslContext = null;

		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();
			apiHttpClientBuilder.setSSLContext(sslContext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build();

		apiHttpConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		int maxConnections = noOfRequests > APITestConstants.DEFAULT_THREAD_POOL_SIZE ? APITestConstants.DEFAULT_THREAD_POOL_SIZE : noOfRequests;
		if (maxConnections < defaultConnectionsPerRoute) {
			defaultConnectionsPerRoute = maxConnections;
		}
		apiHttpConnectionManager.setDefaultMaxPerRoute(defaultConnectionsPerRoute);
		apiHttpConnectionManager.setMaxTotal(maxConnections);
		apiHttpClientBuilder.setConnectionManager(apiHttpConnectionManager).setConnectionManagerShared(true);
		apiHttpClientBuilder.setUserAgent("SHC-API-Automation");

		if (throttle != null && throttle.doubleValue() > 0) {
			rateLimiter = RateLimiter.create(throttle);
		}
	}

	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}

	public HttpClientBuilder getApiHttpClientBuilder() {
		return apiHttpClientBuilder;
	}

	public APITestResponseItem executeRequest(final APITestRequestItem requestItem, CloseableHttpClient http, HttpClientContext context)
			throws APITestException {
		Boolean persistSession = APITestContext.get().getTestConfig().getPersistSession();
		CookieStore httpCookieStore = context == null ? null : context.getCookieStore();
		if (http == null) {
			if (persistSession) {
				httpCookieStore = new BasicCookieStore();
				http = apiHttpClientBuilder.setDefaultCookieStore(httpCookieStore).setRedirectStrategy(new LaxRedirectStrategy()).build();
			} else {
				http = apiHttpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy()).build();
			}
		}
		APITestResponseItem responseItem = execute(requestItem, http, context);

		if (persistSession) {
			responseItem.setHttpClient(http);
			if (httpCookieStore != null) {
				responseItem.setCookies(httpCookieStore.getCookies());
			}
		} else {
			try {
				http.close();
				http = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return responseItem;
	}

	private APITestResponseItem execute(final APITestRequestItem requestItem, CloseableHttpClient http, HttpClientContext context) {

		String url = null;
		long elapsedTime = 0;
		int retryCount = 0;
		CloseableHttpResponse response = null;
		APITestResponseItem responseItem = null;
		APITestException exceptionInExecution = null;

		HttpRequestBase method = null;
		try {
			method = new APIHttpRequest().getMethod(requestItem);
			if (method == null || method.getURI() == null || StringUtils.isBlank(method.getURI().toString())) {
				log.error("Error!!!...METHOD / URL NOT FOUND");
				throw new APITestException("API Request Error : API METHOD / URL NOT FOUND");
			}
			url = method.getURI().toString();

			int maxRetryCount = APITestConstants.API_RETRY_COUNT;
			while (maxRetryCount > 0) {
				try {

					long startTime = System.nanoTime();
					response = http.execute(method, context);
					elapsedTime = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
					retryCount = APITestConstants.API_RETRY_COUNT - maxRetryCount + 1;
					maxRetryCount = -1;

					// TODO: Remove
					List<URI> redirectURIs = context != null ? context.getRedirectLocations() : null;
					if (redirectURIs != null && !redirectURIs.isEmpty()) {
						URI finalURI = redirectURIs.get(redirectURIs.size() - 1);
						System.out.println("**** API Call: URL Redirected to : " + finalURI + "***************");
					}

				} catch (SocketTimeoutException e) {
					log.error("SocketTimeoutException in execution of > " + "( " + url + " ) > " + e.getMessage());
					exceptionInExecution = new APITestException("Delay in receiving the response from API. Timeout occured", e);
					break;
				} catch (IOException e) {
					log.error("IOException in execution of ( " + url + " ) > " + e.getMessage());
					maxRetryCount--;
					if (maxRetryCount == 0) {
						e.printStackTrace();
						log.error("Execution Failed after maximum retry for :" + url);
						exceptionInExecution = new APITestException("Maximum Retry Reached. API Connection failed with Exception :" + e.getMessage(), e);
					} else {
						log.error("Retry#" + (APITestConstants.API_RETRY_COUNT - maxRetryCount + 1) + " to Connect :" + url);
						continue;
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Exception in Scenario ->" + requestItem.getScenarioName());
			e.printStackTrace();
			if (exceptionInExecution == null) {
				exceptionInExecution = new APITestException("Unexpected Error. Error in processing API response", e);
			}
		} finally {
			responseItem = new APIResponseItemProcessor().create(requestItem, response, method, url, exceptionInExecution, elapsedTime, retryCount);
			try {
				if (response != null) {
					response.close();
					response = null;
				}
				if (method != null) {
					method.releaseConnection();
					method = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.info("Completed Scenario  :   " + responseItem);

		return responseItem;
	}

	public void close() {
		if (apiHttpConnectionManager != null) {
			apiHttpConnectionManager.close();
		}
	}

}