package com.shc.automation.api.test.framework.entities;

public interface APITestConstants {
	public static final String METADATA = "lib_iteration_metaData";
	public static final String PARAM_TREE = "Parameter_List";
	public static final String CHAIN_LOG = "Chain_Log";
	public final String API_PERF_COLLECION = "api_test_response_store";

	public static final Integer API_RETRY_COUNT = 2;

	public static final String API_DATA_DB_QUERIES = "api/api-data-db.properties";
	public static final String API_DATA_FILES = "api/api-data-file.properties";
	public static final String API_PROJECT_PROPERTIES = "api/project.properties";
	public final static String MESSAGE_FILE = "api/messages.properties";

	public static final String API_TESTS_CONFIG_FILE = "api-tests-config.xml";
	public static final String API_CONFIG_FILE = "api-config.xml";
	public static final String API_INTERNAL_SQL_CONFIG = "sql-data-source.xml";
	public static final String API_INTERNAL_NoSQL_CONFIG = "nosql-data-source.xml";

	public static final String DEFAULT_DOC_DB_NAME = "carsApiDb";

	public static final Integer DEFAULT_THREAD_POOL_SIZE = 100;
	public static final Integer CON_TIME_OUT = 10000;
	public static final Integer HTTP_SOCKET_TIME_OUT = 5000;
	public static final Integer TEST_REPORT_LENGTH_LIMIT = 60000;

	public static final String TURN_OFF_VALIDATION = "TurnOffValidation";
	public static final String TURN_OFF_RESPONSE_PARSING = "TurnOffResponseParsing";
	public static final String TURN_OFF_SAVE_RESPONSE_TO_MONGO = "saveResponseToMongo";
	public static final String REPORT_ONLY_FAILURES = "reportOnlyFailures";
	public static final String PRINT_API_JSON_RESPONSE = "logApiJSONResponse";
	public static final String PERSIST_SESSION_IN_RESPONSE = "saveSessionToResponse";

	public static final String COMPARE_TWO_RESPONSES_BASE_PATH = "$";

	public static final String API_RESPONSE_MAP_KEY = "APITestResponse";

	public static final int TEST_FAIL = -1;
	public static final String NO_STACKTRACE = "No StackTrace Available";
	public static final String NO_SCREENSHOT = "NO_SCREENSHOT";
	public static final String NO_COOKIE_URL = "NO_COOKIE_URL";

	public static final int LOG_THRESHOLD_COUNT = 100;

	public static final String API_PROJECT_CONFIG = "APIProjectConfig";

	public final static String API_TEST_RESULT_CLASS = "APITestResultClass";
	public final static String API_TEST_RESULT_OBJECT = "APITestResultObject";

	public static final String API_JSON_BASE_PATH_IDENTIFIER = "APIPayload";
	public static final String TEMPLATE = "template";
	public static final String API_URL_KEY = "APIURL";
	public static final String INPUT_TYPE_PAYLOAD = "PAYLOAD";
	public static final String INPUT_TYPE_URL = "URL";

	public static final String PROP_API_DATA_ENV = "api.data.environment";

	public static final String DAFAULT_API_ENDPOINT_VERSION = "1.0";
	public static String ENVIRONMENT_VARIABLE = "ENV";

	public static final String API_INPUT_SOURCE = "input";
	public static final String API_RESPONSE_SOURCE = "response";
	public static final String API_HEADER_SOURCE = "header";
	public static final String API_CONTEXT_IDENTIFIER_PREFIX = "$";

}
