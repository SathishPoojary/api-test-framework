package com.shc.automation.api.test.framework.model.response;

public enum ValidationType {
    EQUALS, NOT_EQUALS, GREATER_THAN, LESSER_THAN, CONTAINS_NODE, NOT_CONTAINS_NODE, EMPTY, NOT_EMPTY, CONTAINS_VALUE, NOT_CONTAINS_VALUE, EXPRESSION;

    public static ValidationType getValidationType(String name) {
        for (ValidationType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return NOT_EMPTY;
    }
}
