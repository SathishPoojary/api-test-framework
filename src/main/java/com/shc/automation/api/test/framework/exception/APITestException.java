/**
 * 
 */
package com.shc.automation.api.test.framework.exception;

/**
 * @author spoojar
 *
 */
public class APITestException extends Exception {
	private static final long serialVersionUID = 602552959848467924L;
	
	/**
	   * 
	   */
	public APITestException() {

	}

	/**
	 * @param arg0
	 */
	public APITestException(String arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 */
	public APITestException(Throwable arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APITestException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

}
