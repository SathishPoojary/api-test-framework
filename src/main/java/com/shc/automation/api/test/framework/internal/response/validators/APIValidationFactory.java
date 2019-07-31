package com.shc.automation.api.test.framework.internal.response.validators;

import com.google.common.primitives.Primitives;
import com.shc.automation.api.test.framework.model.response.APIValidation;
import com.shc.automation.api.test.framework.model.response.ValidationType;
import net.sf.json.JSONNull;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class APIValidationFactory {
    public APIValidator getValidator(APIValidation validationField) {
        Object actualResponseValue = validationField.getActualResponseValue();

        if (JSONNull.getInstance().equals(actualResponseValue) || StringUtils.isBlank(actualResponseValue.toString()) || isSimpleValue(actualResponseValue)) {
            return new APIPrimitiveValidator();
        }
        if (actualResponseValue instanceof List && allSimpleValues((List) actualResponseValue)) {
            if (((List) actualResponseValue).size() == 1) {
                return new APIPrimitiveValidator();
            }
            return new APIArrayValidator();
        }
        ValidationType validationType = validationField.getValidationType();
        if (ValidationType.EQUALS != validationType && ValidationType.NOT_EQUALS != validationType) {
            return new APIPrimitiveValidator();
        }
        if (JSONUtils.mayBeJSON(actualResponseValue.toString())) {
            return new APIJsonValidator();
        }

        return new APIPrimitiveValidator();
    }

    @SuppressWarnings("rawtypes")
    private static boolean allSimpleValues(List array) {
        for (int i = 0; i < array.size(); ++i) {
            if (!Primitives.isWrapperType(array.get(i).getClass()) && !(array.get(i) instanceof String)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSimpleValue(Object value) {
        if (!Primitives.isWrapperType(value.getClass()) && !(value instanceof String)) {
            return false;
        }
        return true;
    }
}
