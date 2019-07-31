package com.shc.automation.api.test.framework;

public interface APITestConstants {
    String API_TESTS_CONFIG_FILE = "/api-tests-config.xml";
    String API_CONFIG_FILE = "/api-config.xml";
    String API_RESOURCE_BASE_DIR ="api";

    String API_JSON_BASE_PATH_IDENTIFIER = "APIPayload";
    String API_URL_KEY = "APIURL";

    String API_INPUT_SOURCE = "input";
    String API_RESPONSE_SOURCE = "response";
    String API_HEADER_SOURCE = "header";
    String API_CONTEXT_IDENTIFIER_PREFIX = "$";

    String API_RESPONSE_MAP_KEY = "APIResponse";

    String PROP_API_DATA_ENV = "api.data.environment";

    String DAFAULT_API_ENDPOINT_VERSION = "1.0";
    Integer DEFAULT_THREAD_POOL_SIZE = 100;
    String COMPARE_TWO_RESPONSES_BASE_PATH = "$";

    String API_TEST_RESULT_CLASS = "APITestResultClass";
    String API_TEST_RESULT_OBJECT = "APITestResultObject";

    String ENVIRONMENT_VARIABLE = "ENV";
    String API_PERF_COLLECTION = "api_test_response_store";
    String DEFAULT_DOC_DB_NAME = "carsApiDb";
}
