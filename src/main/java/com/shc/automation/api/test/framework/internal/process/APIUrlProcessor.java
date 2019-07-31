/**
 *
 */
package com.shc.automation.api.test.framework.internal.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APIDataSourceType;
import com.shc.automation.api.test.framework.entities.APIRequestParameter;
import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestRequestItem;
import com.shc.automation.api.test.framework.exception.APIConfigException;
import com.shc.automation.api.test.framework.internal.connect.SQLDBManager;
import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author spoojar
 *
 */
public class APIUrlProcessor {
    private static final Logger log = Logger.getLogger("APIUrlUtils");

    public static String getServiceUrl(APITestRequest request) throws APIConfigException {
        System.out.println("Getting Endpoint Info for Test :" + request.getTestName());
        String serviceUrl = request.getBaseUrl();
        String serviceEndPointName = request.getServiceEndPointName();

        if (StringUtils.isBlank(serviceUrl)) {
            if (StringUtils.isNotBlank(serviceEndPointName)) {
                serviceUrl = SQLDBManager.INSTANCE.getServiceEP(serviceEndPointName, request.getEnvironment(), request.getEndpointVersion());
                log.info("serviceUrl {} " + serviceUrl + " for request serviceEndPointName: " + serviceEndPointName + " Env : " + request.getEnvironment()
                        + " and Version : " + request.getEndpointVersion());

            }
        }

        if (!isUrlsFromTxt(request.getUrlParamInputSource())) {
            serviceUrl = StringUtils.isBlank(serviceUrl) ? request.getServiceUrl() : serviceUrl;
            if (StringUtils.isBlank(serviceUrl)) {
                log.error("No URL found for the service End Point : {} " + serviceEndPointName + " Env : " + request.getEnvironment() + " and Version : "
                        + request.getEndpointVersion());
                throw new APIConfigException("No URL found for the service End Point : " + serviceEndPointName + " and Env : " + request.getEnvironment()
                        + " and Version : " + request.getEndpointVersion());
            }
            if (StringUtils.isNotBlank(request.getRequestPath()) && serviceUrl.indexOf("?") == -1) {
                serviceUrl = serviceUrl + request.getRequestPath();
            }
            log.info("serviceUrl with Path {} " + serviceUrl + " for request serviceEndPointName: " + serviceEndPointName + " Env : " + request.getEnvironment()
                    + " and Version : " + request.getEndpointVersion());
        }
        return serviceUrl;
    }

    private static boolean isUrlsFromTxt(APITestInputSource urlSource) {
        if (urlSource == null) {
            return false;
        }

        if (APIDataSourceType.file.toString().equalsIgnoreCase(urlSource.getSourceType())) {
            return true;
        }

        return false;
    }

    public void updateURLParams(APITestRequestItem requestItem, APITestRequest request, Map<String, Object> urlRecord) {
        List<APIRequestParameter> configParams = request.getUrlParameters();
        String sourceType = request.getUrlParamInputSource() == null ? null : request.getUrlParamInputSource().getSourceType();

        if (CollectionUtils.isNotEmpty(configParams)) {
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
            requestItem.setUrlParameters(urlParams);
        }

        String url = request.getServiceUrl();

        if (urlRecord.get(APITestConstants.API_URL_KEY) != null) {
            url = urlRecord.get(APITestConstants.API_URL_KEY).toString();
        }
        setURLFromTemplate(requestItem, url, urlRecord, sourceType);

    }

    public void updateURLParams(APITestRequestItem requestItem, Map<String, Object> urlRecord) {
        List<APIRequestParameter> configParams = requestItem.getUrlParameters();
        if (CollectionUtils.isEmpty(configParams)) {
            return;
        }

        for (APIRequestParameter configParam : configParams) {
            if (configParam.isOverride()) {
                continue;
            }

            String paramName = configParam.getParamName();
            String column = StringUtils.isEmpty(configParam.getInputColumnName()) ? paramName : configParam.getInputColumnName();
            Object paramValue = urlRecord.get(column);
            if (paramValue != null) {
                configParam.setParamValue(paramValue);
            }
        }
    }

    public void setURLFromTemplate(APITestRequestItem requestItem, String url, Map<String, Object> urlRecord, String sourceType) {
        StringBuilder urlStr = new StringBuilder(url);
        if (url.contains("{")) {
            Iterator<String> nameIter = urlRecord.keySet().iterator();
            while (nameIter.hasNext()) {
                String name = nameIter.next();
                String TemplateStr = "{" + name + "}";
                if (url.contains(TemplateStr)) {
                    int start = urlStr.indexOf(TemplateStr);
                    Object paramValue = APITestUtils.getValueFromRecord(name, urlRecord);
                    String value = paramValue == null ? null : paramValue.toString();
                    if (start > -1) {
                        int end = urlStr.indexOf(TemplateStr) + (TemplateStr).length();
                        urlStr.replace(start, end, value);
                    }
                    requestItem.addToContext(name, value);
                }
            }
        }
        requestItem.setUrl(urlStr.toString());
    }
}
