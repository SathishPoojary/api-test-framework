package com.shc.automation.api.test.framework.internal.connect;

import com.google.common.util.concurrent.RateLimiter;
import com.shc.automation.api.test.framework.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.injector.APIDependencyInjector;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class APIHttpConnectionManager {
    public static APIHttpConnection create(Integer defaultConnectionsPerRoute, Double throttle) {
        APIHttpConnection connection = APIDependencyInjector.INSTANCE.getInstance(APIHttpConnection.class);
        HttpClientBuilder apiHttpClientBuilder = HttpClientBuilder.create();
        connection.setApiHttpClientBuilder(apiHttpClientBuilder);
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

        PoolingHttpClientConnectionManager apiHttpConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connection.setApiHttpConnectionManager(apiHttpConnectionManager);

        defaultConnectionsPerRoute = defaultConnectionsPerRoute == null ? APITestConstants.DEFAULT_THREAD_POOL_SIZE : defaultConnectionsPerRoute;

        apiHttpConnectionManager.setDefaultMaxPerRoute(defaultConnectionsPerRoute);
        apiHttpConnectionManager.setMaxTotal(defaultConnectionsPerRoute);
        apiHttpClientBuilder.setConnectionManager(apiHttpConnectionManager).setConnectionManagerShared(true);
        apiHttpClientBuilder.setUserAgent("SHC-API-Automation");

        if (throttle != null && throttle.doubleValue() > 0) {
            connection.setRateLimiter(RateLimiter.create(throttle));
        }

        return connection;
    }
}
