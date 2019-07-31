package com.shc.automation.api.test.framework.internal.request.readers;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.APIUserProcessExecutor;
import com.shc.automation.api.test.framework.internal.request.readers.source.APIExternalDataReader;
import com.shc.automation.api.test.framework.internal.request.readers.source.APITestDataReader;
import com.shc.automation.api.test.framework.internal.request.readers.source.APITestDataReaderFactory;
import com.shc.automation.api.test.framework.model.request.APIDataSourceType;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APIScenarioRequest;
import com.shc.automation.api.test.framework.model.request.APITestDataSource;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class APIRequestReader {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final APITestDataReaderFactory testDataProcessorFactory;
    private final APIScenarioReader scenarioReader;
    private final APIUserProcessExecutor userProcessExecutor;
    private final APIExternalDataReader externalDataReader;

    @Inject
    public APIRequestReader(APITestDataReaderFactory testDataProcessorFactory, APIScenarioReader scenarioReader, APIUserProcessExecutor userProcessExecutor, APIExternalDataReader externalDataReader) {
        this.testDataProcessorFactory = testDataProcessorFactory;
        this.scenarioReader = scenarioReader;
        this.userProcessExecutor = userProcessExecutor;
        this.externalDataReader = externalDataReader;
    }

    public List<APIScenarioRequest> read(APIRequest request) {
        userProcessExecutor.executeBeforeRequest(request);

        Map<String, Map<String, Object>> urlParamRecords = processSource(request.getUrlParamInputSource(), null);
        Map<String, Map<String, Object>> payloadRecords = processSource(request.getPayloadInputSource(), null);
        Map<String, Map<String, Object>> validationDataRecords = processSource(request.getValidationInputSource(), null);

        List<APIScenarioRequest> scenarios = readTestData(request, urlParamRecords, payloadRecords, validationDataRecords);
        logger.info(scenarios.size() + " Scenarios created for test " + request.getTestName() + " Successfully");

        return getRequestItemsWithInvocationCount(request.getInvocationCount(), scenarios);
    }

    private List<APIScenarioRequest> readTestData(APIRequest request,
                                                  Map<String, Map<String, Object>> urlParamRecords,
                                                  Map<String, Map<String, Object>> payloadRecords,
                                                  Map<String, Map<String, Object>> validationDataRecords) {

        scenarioReader.setRequest(request);

        if (testDataNotFound(request, urlParamRecords, payloadRecords)) {
            logger.info("Test Data not found. Executing Default Request from Config");
            return readDefault();
        }

        Set<String> scenarios = getScenarioNames(urlParamRecords, payloadRecords);

        return scenarios.stream()
                .map(scenario ->
                        scenarioReader.createRequestItem(scenario,
                                getTestDataRecord(payloadRecords, scenario),
                                getTestDataRecord(urlParamRecords, scenario),
                                getTestDataRecord(validationDataRecords, scenario)
                        ))
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Object>> processSource(APITestDataSource source, Map<String, Object> contextRecords) {
        if (source == null) {
            return null;
        }

        APITestDataReader dataProcessor = testDataProcessorFactory.getTestDataSourceReader(APIDataSourceType.valueOf(source.getSourceType()));
        return dataProcessor.processRequestSource(source, contextRecords);
    }

    private Map<String, Object> getTestDataRecord(Map<String, Map<String, Object>> records, String scenario) {
        int size = records == null ? 0 : records.size();
        return size == 0 ? null
                : (size == 1 ? records.values().iterator().next() : records.get(scenario));
    }

    private List<APIScenarioRequest> readDefault() {
        List<APIScenarioRequest> requests = new ArrayList<APIScenarioRequest>(1);
        APIScenarioRequest requestItem = scenarioReader.createRequestItem("Default Scenario", null, null, null);
        requests.add(requestItem);

        return requests;
    }

    private boolean testDataNotFound(APIRequest request,
                                     Map<String, Map<String, Object>> urlParamRecords,
                                     Map<String, Map<String, Object>> payloadRecords) {
        return ((request.getPayloadInputSource() == null && request.getUrlParamInputSource() == null)
                || (MapUtils.isEmpty(urlParamRecords) && MapUtils.isEmpty(payloadRecords)));
    }

    private Set<String> getScenarioNames(Map<String, Map<String, Object>> urlParamRecords,
                                         Map<String, Map<String, Object>> payloadRecords) {
        int urlParamSize = urlParamRecords == null ? 0 : urlParamRecords.size();
        int payloadSize = payloadRecords == null ? 0 : payloadRecords.size();

        Set<String> scenarios;
        if (urlParamSize >= payloadSize) {
            scenarios = urlParamRecords.keySet();
        } else {
            scenarios = payloadRecords.keySet();
        }
        return scenarios;
    }

    private List<APIScenarioRequest> getRequestItemsWithInvocationCount(Integer invocation, List<APIScenarioRequest> requests) {
        if (invocation == null || invocation == 1) {
            return requests;
        }

        List<APIScenarioRequest> repeatRequestsList = new ArrayList<>(requests.size() * invocation);
        requests.stream()
                .forEach(requestItem ->
                        IntStream.range(0, invocation)
                                .forEach(invCount -> repeatRequestsList.add(requestItem.getCopy(invCount + 1))));

        return repeatRequestsList;
    }

}
