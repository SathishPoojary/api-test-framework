/**
 *
 */
package com.shc.automation.api.test.framework.internal.request.readers;

import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.connect.APISQLDataManager;
import com.shc.automation.api.test.framework.model.APIEndpointInfo;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.request.APIRequestParameter;
import com.shc.automation.api.test.framework.utils.APITestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author spoojar
 *
 */
public class APIUrlReader {
    private static final Logger log = Logger.getLogger("APIUrlReader");

    @Inject
    private APISQLDataManager sqlDataManager;

    public List<APIRequestParameter> create(APIRequest requestItem, Map<String, Object> urlRecord) {

        List<APIRequestParameter> configParams = requestItem.getUrlParameters();
        if (CollectionUtils.isEmpty(configParams)) {
            return configParams;
        }

        List<APIRequestParameter> urlParams = new ArrayList<APIRequestParameter>();
        APIRequestParameter urlParameter = null;
        for (APIRequestParameter configParam : configParams) {
            if (configParam.isOverride()) {
                urlParams.add(configParam);
                continue;
            }

            String paramName = configParam.getParamName();
            String column = StringUtils.isEmpty(configParam.getInputColumnName()) ? paramName : configParam.getInputColumnName();
            Object paramValue = APITestUtils.getValueFromRecord(column, urlRecord);

            urlParameter = configParam.copy();
            if (paramValue != null) {
                urlParameter.setParamValue(paramValue.toString());
            }
            urlParams.add(urlParameter);
        }

        return urlParams;
    }

    public String getServiceUrl(APIRequest request) {
        System.out.println("Getting Endpoint Info for Test :" + request.getTestName());
        String serviceUrl = request.getBaseUrl();
        String serviceEndPointName = request.getServiceEndPointName();

        if (StringUtils.isBlank(serviceUrl) && StringUtils.isNotBlank(serviceEndPointName)) {
            APIEndpointInfo endPoint = sqlDataManager.getAPIEndPoint(serviceEndPointName, request.getEnvironment(), request.getEndpointVersion());
            if (endPoint != null) {
                serviceUrl = endPoint.getBaseUrl();
            }
        }

        if (StringUtils.isNotBlank(request.getRequestPath()) && serviceUrl.indexOf("?") == -1) {
            serviceUrl = serviceUrl + request.getRequestPath();
        }

        return serviceUrl;
    }


}
