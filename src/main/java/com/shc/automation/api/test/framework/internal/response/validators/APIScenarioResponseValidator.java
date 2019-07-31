package com.shc.automation.api.test.framework.internal.response.validators;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.APIUserProcessExecutor;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.model.response.OnValidationFailureOption;
import com.shc.automation.api.test.framework.model.response.ResultType;
import com.shc.automation.api.test.framework.model.response.ValidationType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class APIScenarioResponseValidator {
    private final APIUserProcessExecutor userProcessExecutor;
    private final APIValidationFactory validationFactory;

    @Inject
    public APIScenarioResponseValidator(APIUserProcessExecutor userProcessExecutor, APIValidationFactory validationFactory) {
        this.userProcessExecutor = userProcessExecutor;
        this.validationFactory = validationFactory;
    }

    public void validate(APIRequest request, APIScenarioResponse scenarioResponse) {
        List<APIValidation> validators = scenarioResponse.getValidators();
        if (CollectionUtils.isEmpty(validators)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(validators)) {
            scenarioResponse.setValidators(validators.stream()
                    .filter(this::verifyPreCondition)
                    .map(this::evaluateValidationExpression)
                    .map(validationField -> setActualValue(scenarioResponse, validationField))
                    .map(this::validateWithActual)
                    .map(validationField -> setResult(scenarioResponse, validationField))
                    .collect(Collectors.toList()));
        }
        boolean validated = userProcessExecutor.runUserProcessValidation(request, scenarioResponse);
        scenarioResponse.setResult((validated ? ResultType.PASSED : ResultType.FAILED));
    }

    private Boolean verifyPreCondition(APIValidation validationField) {
        return APIExpressionEvaluator.validate(validationField.getValidationCondition());
    }

    private APIValidation evaluateValidationExpression(APIValidation validationField) {
        if (ValidationType.EXPRESSION == validationField.getValidationType()) {
            boolean result = APIExpressionEvaluator.validate(validationField.getExpression());
            validationField.setValidationResult(result);
            validationField.updateValidationMessage();
        }
        return validationField;
    }

    private APIValidation setActualValue(APIScenarioResponse response, APIValidation validationField) {
        if (StringUtils.isEmpty(validationField.getResponsePath())) {
            return validationField;
        }
        validationField.setActualResponseValue(response.getValueFromPath(validationField.getResponsePath()));

        return validationField;
    }

    private APIValidation setResult(APIScenarioResponse response, APIValidation validationField) {
        if (validationField.getValidationResult()) {
            return validationField;
        }
        if (OnValidationFailureOption.MARK_SCENARIO_FAILED.equals(validationField.getValidationFailureOption())) {
            response.setResult(ResultType.FAILED);
        }
        return validationField;
    }


    private APIValidation validateWithActual(APIValidation validationField) {
        return validationFactory.getValidator(validationField).validate(validationField);
    }
}
