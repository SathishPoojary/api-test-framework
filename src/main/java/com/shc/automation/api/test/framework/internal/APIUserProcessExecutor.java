package com.shc.automation.api.test.framework.internal;

import com.shc.automation.api.test.framework.client.process.APIAfterResponseProcess;
import com.shc.automation.api.test.framework.client.process.APIBeforeRequestProcess;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import org.apache.commons.lang3.StringUtils;

public class APIUserProcessExecutor {
    public void executeBeforeRequest(APIRequest apiRequest){
        String preProcess = apiRequest.getBeforeRequestProcess();
        if (StringUtils.isBlank(preProcess))
            return;
        try {
            ((APIBeforeRequestProcess) Class.forName(preProcess).getConstructor(APIRequest.class).newInstance(apiRequest)).process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean runUserProcessValidation(APIRequest request, APIScenarioResponse scenarioResponse) {
        String postProcess = request.getAfterResponseProcess();
        if (StringUtils.isBlank(postProcess)) {
            return true;
        }
        try {
            return ((APIAfterResponseProcess) Class.forName(postProcess).getConstructor(APIScenarioResponse.class).newInstance(scenarioResponse)).validate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void runUserProcessPrint(APIRequest request, APIScenarioResponse scenarioResponse){
        String postProcess = request.getAfterResponseProcess();
        if (StringUtils.isBlank(postProcess)) {
            return;
        }
        try {
            ((APIAfterResponseProcess) Class.forName(postProcess).getConstructor(APIScenarioResponse.class).newInstance(scenarioResponse)).print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
