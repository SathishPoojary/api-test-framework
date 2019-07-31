package com.shc.automation.api.test.framework.internal.config.injector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.shc.automation.api.test.framework.internal.executors.APITestExecutor;
import com.shc.automation.api.test.framework.internal.executors.APITestExecutorBase;
import com.shc.automation.api.test.framework.internal.request.readers.source.*;
import com.shc.automation.api.test.framework.model.request.APIDataSourceType;
import com.shc.automation.api.test.framework.model.request.RequestType;
import com.shc.automation.api.test.framework.model.request.TestType;
import org.apache.http.client.methods.*;

public class APIInjectConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("user-agent")).toInstance("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.94 Safari/537.36/SHC Automation/1.0 KTXN" );
        bind(Integer.class).annotatedWith(Names.named("connection-time-out")).toInstance(10000);
        bind(Integer.class).annotatedWith(Names.named("http-socket-time-out")).toInstance(5000);
        bind(String.class).annotatedWith(Names.named("default-content-type")).toInstance("application/json");
        configureHttpRequestByType();
        configureTestDataReaderByType();
        configureTestExecutorByType();
    }

    private void configureTestExecutorByType() {
        MapBinder<TestType, APITestExecutorBase> testTypeTestExecutorBinder = MapBinder.newMapBinder(binder(), TestType.class, APITestExecutorBase.class);
        testTypeTestExecutorBinder.addBinding(TestType.standalone).to(APITestExecutor.class);
    }

    private void configureTestDataReaderByType() {
        MapBinder<APIDataSourceType, APITestDataReader> testDataSourceProcessorBinder = MapBinder.newMapBinder(binder(), APIDataSourceType.class, APITestDataReader.class);
        testDataSourceProcessorBinder.addBinding(APIDataSourceType.excel).to(APIExcelReader.class);
        testDataSourceProcessorBinder.addBinding(APIDataSourceType.file).to(APITextFileReader.class);
        testDataSourceProcessorBinder.addBinding(APIDataSourceType.sql).to(APISQLReader.class);
        testDataSourceProcessorBinder.addBinding(APIDataSourceType.mongo).to(APINoSQLReader.class);
    }

    private void configureHttpRequestByType() {
        MapBinder<RequestType, HttpRequestBase> typeHttpRequestBinder = MapBinder.newMapBinder(binder(), RequestType.class, HttpRequestBase.class);
        typeHttpRequestBinder.addBinding(RequestType.get).to(HttpGet.class);
        typeHttpRequestBinder.addBinding(RequestType.post).to(HttpPost.class);
        typeHttpRequestBinder.addBinding(RequestType.put).to(HttpPut.class);
        typeHttpRequestBinder.addBinding(RequestType.delete).to(HttpDelete.class);
    }
}
