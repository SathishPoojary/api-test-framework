package com.shc.automation.api.test.framework.internal.executors;

import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.response.APIBaseResponse;

public interface APITestExecutorBase {
    APIBaseResponse execute(APIBaseRequest request);
}
