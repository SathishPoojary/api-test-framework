/**
 *
 */
package com.shc.automation.api.test.framework.internal.response.printers;


import com.google.inject.Inject;
import com.shc.automation.api.test.framework.internal.APIUserProcessExecutor;
import com.shc.automation.api.test.framework.model.response.APIPrint;
import com.shc.automation.api.test.framework.model.request.APIRequest;
import com.shc.automation.api.test.framework.model.response.APIScenarioResponse;
import com.shc.automation.api.test.framework.utils.APITestUtils;

import java.util.Iterator;

/**
 * @author spoojar
 *
 */
public class APIScenarioResponsePrinter {
    private final APIUserProcessExecutor userProcessExecutor;

    @Inject
    public APIScenarioResponsePrinter(APIUserProcessExecutor userProcessExecutor) {
        this.userProcessExecutor = userProcessExecutor;
    }

    public void print(APIRequest request, APIScenarioResponse response) {
        Iterator<APIPrint> iter = request.getPrintFields().iterator();
        while (iter.hasNext()) {
            APIPrint printField = iter.next().getCopy();
            String path = printField.getResponsePath();

            Object object = APITestUtils.readFromJSON(response.getResponseContent(), path, true);

            if (object != null) {
                printField.setResponseValue(object.toString());
            }
            response.addPrinter(printField);
        }
        userProcessExecutor.runUserProcessPrint(request, response);

    }
}
