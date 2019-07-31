/**
 * 
 */
package com.shc.automation.api.test.framework.internal.process.source;

import java.util.Map;

import com.shc.automation.api.test.framework.entities.APITestInputSource;

/**
 * @author spoojar
 *
 */
public interface APIDataSourceMarker {
	public Map<String, Map<String, Object>> processRequestSource(APITestInputSource requestSource, Map<String, Object> contextRecords);

}
