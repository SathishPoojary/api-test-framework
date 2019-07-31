/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.util.Iterator;

import com.shc.automation.api.test.framework.entities.APIPrintField;
import com.shc.automation.api.test.framework.entities.APITestRequest;
import com.shc.automation.api.test.framework.entities.APITestResponseItem;
import com.shc.automation.api.test.framework.process.APIAfterResponseProcess;
import com.shc.automation.api.test.framework.utils.APITestUtils;

/**
 * @author spoojar
 * 
 */
public class APIResponsePrinter {

	public void print(APITestRequest request, APITestResponseItem response, APIAfterResponseProcess postTestProcess) {
		Iterator<APIPrintField> iter = request.getPrintFields().iterator();
		while (iter.hasNext()) {
			APIPrintField printField = iter.next().getCopy();
			String path = printField.getResponsePath();

			Object object = APITestUtils.readFromJSON(response.getResponseContent(), path, true);

			if (object != null) {
				printField.setResponseValue(object.toString());
			}
			response.addPrinter(printField);
		}
		if (postTestProcess != null) {
			postTestProcess.print();
		}

	}
}
