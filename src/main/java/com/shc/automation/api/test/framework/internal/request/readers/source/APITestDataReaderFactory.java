package com.shc.automation.api.test.framework.internal.request.readers.source;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.model.request.APIDataSourceType;

import java.util.Map;

@Singleton
public class APITestDataReaderFactory {
    private final Map<APIDataSourceType, Provider<APITestDataReader>> dataSourceReaderMap;

    @Inject
    public APITestDataReaderFactory(Map<APIDataSourceType, Provider<APITestDataReader>> dataSourceReaderMap) {
        this.dataSourceReaderMap = dataSourceReaderMap;
    }

    public APITestDataReader getTestDataSourceReader(APIDataSourceType dataSourceType){
        return dataSourceReaderMap.get(dataSourceType).get();
    }
}
