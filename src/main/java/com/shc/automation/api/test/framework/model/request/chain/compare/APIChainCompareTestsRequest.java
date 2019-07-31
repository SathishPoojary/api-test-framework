package com.shc.automation.api.test.framework.model.request.chain.compare;

import com.shc.automation.api.test.framework.model.request.APIBaseRequest;
import com.shc.automation.api.test.framework.model.request.TestType;
import com.shc.automation.api.test.framework.model.request.chain.APIChainTestRequest;
import com.shc.automation.utils.json.JsonCompareOption;

import java.util.List;
import java.util.Map;

public class APIChainCompareTestsRequest extends APIBaseRequest {
    private final TestType testType = TestType.comparativechain;
    private Map<String, APIChainTestRequest> chainsToCompare;
    private List<String> pathsToExcludeFromCompare;
    private List<String> arrayPathListToIgnoreOrder;
    private JsonCompareOption compareOption;

    public Map<String, APIChainTestRequest> getChainsToCompare() {
        return chainsToCompare;
    }

    public void setChainsToCompare(Map<String, APIChainTestRequest> chainsToCompare) {
        this.chainsToCompare = chainsToCompare;
    }

    public List<String> getPathsToExcludeFromCompare() {
        return pathsToExcludeFromCompare;
    }

    public void setPathsToExcludeFromCompare(List<String> pathsToExcludeFromCompare) {
        this.pathsToExcludeFromCompare = pathsToExcludeFromCompare;
    }

    public List<String> getArrayPathListToIgnoreOrder() {
        return arrayPathListToIgnoreOrder;
    }

    public void setArrayPathListToIgnoreOrder(List<String> arrayPathListToIgnoreOrder) {
        this.arrayPathListToIgnoreOrder = arrayPathListToIgnoreOrder;
    }

    public JsonCompareOption getCompareOption() {
        return compareOption;
    }

    public void setCompareOption(JsonCompareOption compareOption) {
        this.compareOption = compareOption;
    }

    public TestType getTestType() {
        return testType;
    }
}
