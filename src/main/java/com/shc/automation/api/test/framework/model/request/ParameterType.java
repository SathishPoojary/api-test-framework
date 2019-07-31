package com.shc.automation.api.test.framework.model.request;

public enum ParameterType {
    path, noname, colon, query, form;

    public static ParameterType getParameterType(String name) {
        for (ParameterType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return ParameterType.query;
    }
}
