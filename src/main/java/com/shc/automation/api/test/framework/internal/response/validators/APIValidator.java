package com.shc.automation.api.test.framework.internal.response.validators;

import com.shc.automation.api.test.framework.model.response.APIValidation;

public interface APIValidator {
    APIValidation validate(APIValidation validator);
}
