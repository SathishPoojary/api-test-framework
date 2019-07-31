package com.shc.automation.api.test.framework;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.exception.APITestException;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;
import com.shc.automation.api.test.framework.internal.config.injector.APIDependencyInjector;
import com.shc.automation.api.test.framework.internal.config.xml.APITestFactory;
import com.shc.automation.api.test.framework.internal.executors.APITestExecutorBase;
import com.shc.automation.api.test.framework.internal.executors.APITestExecutorFactory;
import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.TestType;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;
import org.apache.commons.lang3.StringUtils;

public class APITestManager {
    private final APITestFactory apiTestFactory;
    private final APITestExecutorFactory testExecutorFactory;

    @Inject
    APITestManager(APITestFactory apiTestFactory, APITestExecutorFactory testExecutorFactory) {
        this.apiTestFactory = apiTestFactory;
        this.testExecutorFactory = testExecutorFactory;
    }

    public static APITestManager getInstance() {
        APIDependencyInjector.INSTANCE.getInstance(APIConfigManager.class).configurePackage(getPackageName());
        return APIDependencyInjector.INSTANCE.getInstance(APITestManager.class);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName Name of the test to be executed. testName is the test-name
     *                 attribute configured in api-tests-config.xml
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName) throws APITestException {
        return runTest(testName, null, null, null);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName        Name of the test to be executed. testName is the test-name
     *                        attribute configured in api-tests-config.xml
     * @param invocationCount Number of times each request needs to be executed (repeatedly)
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName, Integer invocationCount) throws APITestException {
        return runTest(testName, null, null, invocationCount);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName    Name of the test to be executed. testName is the test-name
     *                    attribute configured in api-tests-config.xml
     * @param environment Name of the API environment where the test needs to be run
     *                    (production | QA | stress...) <br/>
     *                    Environment URLs are configured in MySQL DB table
     *                    "aeng_prod_productdb.service_endpoints"<br/>
     *                    NAME column of the table is the end-point attribute configured in
     *                    the api-tests-config.xml
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName, String environment) throws APITestException {
        return runTest(testName, environment, null, null);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName        Name of the test to be executed. testName is the test-name
     *                        attribute configured in api-tests-config.xml
     * @param environment     Name of the API environment where the test needs to be run
     *                        (production | QA | stress...) <br/>
     *                        Environment URLs are configured in MySQL DB table
     *                        "aeng_prod_productdb.service_endpoints"<br/>
     *                        NAME column of the table is the end-point attribute configured in
     *                        the api-tests-config.xml
     * @param invocationCount Number of times each request needs to be executed (repeatedly)
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName, String environment, Integer invocationCount) throws APITestException {
        return runTest(testName, environment, null, invocationCount);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName    Name of the test to be executed. testName is the test-name
     *                    attribute configured in api-tests-config.xml
     * @param environment Name of the API environment where the test needs to be run
     *                    (production | QA | stress...) <br/>
     *                    Environment URLs are configured in MySQL DB table
     *                    "aeng_prod_productdb.service_endpoints"<br/>
     *                    NAME column of the table is the end-point attribute configured in
     *                    the api-tests-config.xml
     * @param version     API or Service version needs to be executed
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName, String environment, String version) throws APITestException {
        return runTest(testName, environment, version, null);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param testName        Name of the test to be executed. testName is the test-name
     *                        attribute configured in api-tests-config.xml
     * @param environment     Name of the API environment where the test needs to be run
     *                        (production | QA | stress...) <br/>
     *                        Environment URLs are configured in MySQL DB table
     *                        "aeng_prod_productdb.service_endpoints"<br/>
     *                        NAME column of the table is the end-point attribute configured in
     *                        the api-tests-config.xml
     * @param invocationCount Number of times each request needs to be executed (repeatedly)
     * @param version         API or Service version needs to be executed
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(String testName, String environment, String version, Integer invocationCount) throws APITestException {
        APIBaseRequest request = getAPITestRequest(testName, environment, version);
        request.setInvocationCount(invocationCount);
        return runTest(request);
    }

    /**
     * Execute an API Test and return the list of results
     *
     * @param request Test Request Object with all required fields set
     * @return APIResponse containing the APITestResponseItems for each of the
     * scenarios executed by the test.
     * @throws APITestException
     */
    public APIBaseResponse runTest(APIBaseRequest request) throws APITestException {
        return execute(request);
    }

    /**
     * Execute the API Test Request and return the response. All the logic for a
     * standalone API test are executed through this method.
     *
     * @param request containing all the required information and configs to execute the
     *                test
     * @return Response containing all response items for the configured scenarios
     * @throws APITestException
     */
    private APIBaseResponse execute(APIBaseRequest request) throws APITestException {
        return getExecutor(request.getTestType()).execute(request);
    }

    private APITestExecutorBase getExecutor(TestType testType) {
        return testExecutorFactory.getAPITestExecutor(testType);
    }

    public APIRequest getAPITestRequest(String testName) throws APITestException {
        return apiTestFactory.getAPITestRequest(testName, null, null);
    }

    public APIRequest getAPITestRequest(String testName, String environment) throws APITestException {
        return apiTestFactory.getAPITestRequest(testName, environment, null);
    }

    public APIBaseRequest getAPITestRequest(String testName, String environment, String version) throws APITestException {
        return apiTestFactory.getAPITestRequest(testName, environment, version);
    }

    private static String getPackageName() {
        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        String testClassName = stackElements[2].getClassName();

        if (StringUtils.isEmpty(testClassName)) return "";
        String packageName = "";
        try {
            Package testPackage = Class.forName(testClassName).getPackage();
            packageName = testPackage.getName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

}
