/**
 *
 */
package com.shc.automation.api.test.framework.model.response;

/**
 * @author spoojar
 *
 */
public enum OnValidationFailureOption {
    MARK_VALIDATION_FAILED, MARK_SCENARIO_FAILED, MARK_TEST_FAILED, FAIL_AND_STOP_VALIDATIONS;

    public static OnValidationFailureOption getValidationFailureOption(String name) {
        for (OnValidationFailureOption option : values()) {
            if (option.name().equalsIgnoreCase(name)) {
                return option;
            }
        }
        return MARK_TEST_FAILED;
    }
}
