package com.shc.automation.api.test.framework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.shc.automation.api.test.framework.entities.APIResponse;
import com.shc.automation.api.test.framework.entities.APITestConfig;

/**
 * @author spoojar
 *
 */
public class APITestContext {
	private static final String CONFIG_PROPERTY_TEST_EXECUTION_START_TIME = "test-execution-start-time";
	private static final Locale DEFAULT_LOCALE = new Locale("");

	/**
	 * Date format as reports folder.
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("d_MMM_y_HH_mm");

	/**
	 * Time when the tests started running, this is captured from Ant's
	 * build.xml, or defaulted to current time if time is not available from
	 * Ant.
	 */
	private static Date testExecutionStartTime = null;

	// capture the test execution start time.
	static {
		try {
			testExecutionStartTime = sdf.parse(System.getProperty(CONFIG_PROPERTY_TEST_EXECUTION_START_TIME, sdf.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static ThreadLocal<APITestContext> thread = new InheritableThreadLocal<APITestContext>() {
		protected APITestContext initialValue() {
			return new APITestContext();
		}
	};
	private Locale locale;
	private Map<String, Object> data = new HashMap<String, Object>();
	private String methodName = null;
	private Map<String, Object> testObject = new HashMap<String, Object>();
	private APIResponse testResponse;
	private List<APIResponse> testResponses;
	private APITestConfig testConfig = null;

	public APIResponse getTestResponse() {
		return this.testResponse;
	}

	public void setTestResponse(APIResponse testResponse) {
		this.testResponse = testResponse;
	}

	/**
	 * Get the method name.
	 * 
	 * @return
	 */
	public String getMethodName() {
		return methodName;

	}

	/**
	 * Set the Method name.
	 * 
	 * @param methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Constructor of TestContext. webdriver object gets created here
	 */
	public APITestContext() {

	}

	public static APITestContext get() {
		return thread.get();
	}

	public Date getTestExecutionStartTime() {
		return testExecutionStartTime;
	}

	public Locale locale() {
		if (locale == null)
			return DEFAULT_LOCALE;
		return locale;
	}

	public APITestContext locale(Locale locale) {
		this.locale = locale;
		return this;
	}

	public APITestContext put(String key, Object value) {
		data.put(key, value);
		return this;
	}

	public Object get(String key) {
		return data.get(key);
	}

	public APITestContext update(String key, Object newValue) {
		delete(key);
		put(key, newValue);
		return this;
	}

	public void delete(String key) {
		data.remove(key);
	}

	public Object getTestObject(String key) {
		return testObject.get(key);
	}

	public APITestContext putTestObject(String key, Object value) {
		testObject.put(key, value);
		return this;
	}

	public List<APIResponse> getTestResponses() {
		return testResponses;
	}

	public void addAllTestResponses(List<APIResponse> testResponses) {
		this.testResponses = testResponses;
	}

	public APITestConfig getTestConfig() {
		if (testConfig == null)
			testConfig = new APITestConfig();
		return testConfig;
	}

	public void setTestConfig(APITestConfig testConfig) {
		this.testConfig = testConfig;
	}

}
