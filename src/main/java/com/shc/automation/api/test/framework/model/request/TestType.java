/**
 * 
 */
package com.shc.automation.api.test.framework.model.request;

/**
 * @author spoojar
 *
 */
public enum TestType {
	standalone("Standalone API Tests"), chain("Chained API Tests"), comparative("Comparative API Tests"), comparativechain("Comparative Chain API Tests");

	private final String name;

	private TestType(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		if (name == null)
			return false;
		return name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}

}
