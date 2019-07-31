package com.shc.automation.api.test.framework.internal.executors;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.model.request.TestType;

import java.util.Map;

@Singleton
public class APITestExecutorFactory {

    private final Map<TestType, Provider<APITestExecutorBase>> testTypeExecutorMap;

    @Inject
    public APITestExecutorFactory(Map<TestType, Provider<APITestExecutorBase>> testTypeExecutorMap) {
        this.testTypeExecutorMap = testTypeExecutorMap;
    }

    public APITestExecutorBase getAPITestExecutor(TestType testType) {
        return testTypeExecutorMap.get(testType).get();
    }
}
