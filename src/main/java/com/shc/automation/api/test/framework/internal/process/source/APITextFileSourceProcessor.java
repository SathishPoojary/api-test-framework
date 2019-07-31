package com.shc.automation.api.test.framework.internal.process.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.entities.APITestInputSource;
import com.shc.automation.api.test.framework.internal.config.QueryProperty;
import com.shc.automation.api.test.framework.internal.process.APITestScenarioProcessor;

public class APITextFileSourceProcessor implements APIDataSourceMarker {
	private static final Logger log = Logger.getLogger("APITextFileSourceProcessor");

	public Map<String, Map<String, Object>> processRequestSource(APITestInputSource requestSource, Map<String, Object> contextRecords) {
		if (requestSource == null) {
			log.error("Please check the CONFIG : TEXT File Source is NULL");
			return null;
		}
		String fileSrcName = requestSource.getSourceName();
		if (StringUtils.isEmpty(fileSrcName)) {
			log.error("Please check the CONFIG : TXT File Source Name is NULL or Empty");
			return null;
		}
		System.out.println("Starting processing of TEXT file :" + fileSrcName);

		BufferedReader lines = getFileContents(fileSrcName);
		int fromIndex = requestSource.getFromIndex();
		int toIndex = requestSource.getToIndex();

		Map<String, Map<String, Object>> records = null;
		Map<String, Object> record = null;
		if (lines != null) {
			try {

				String url = lines.readLine();
				if (StringUtils.isNotBlank(url) && isBinary(url.substring(0, 3))) {
					log.error("Invalid Input File > " + fileSrcName + ". File is not in TXT format. Appears to be Binary");
				}
				int numOfRecs = 0;
				if (fromIndex > 0 && toIndex > 0) {
					numOfRecs = Math.abs(fromIndex - toIndex) + 1;
					while (fromIndex >= 0) {
						log.info("Skipped Line:" + url);
						fromIndex--;
						url = lines.readLine();
					}
				} else if (fromIndex > 0) {
					numOfRecs = fromIndex;
				} else if (toIndex > 0) {
					numOfRecs = toIndex;
				}
				if (numOfRecs <= 0) {
					numOfRecs = Integer.MAX_VALUE;
				}

				records = new HashMap<String, Map<String, Object>>();
				for (int i = 0; url != null && i < numOfRecs; i++) {
					record = new HashMap<String, Object>(1);
					if (StringUtils.isNotBlank(url)) {
						String scenario = APITestScenarioProcessor.generateScenarioNameFromURLQueryString(requestSource.getScenarioFields(), url, (i + 1));
						record.put(APITestConstants.API_URL_KEY, url);
						records.put(scenario, record);
					}
					url = lines.readLine();
				}
			} catch (IOException e) {
				log.error("Error in TXT file processing " + fileSrcName + " : " + e);
			} catch (Exception e) {
				log.error("Error in TXT file processing " + fileSrcName + " : " + e);
			} finally {
				try {
					if (lines != null)
						lines.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (MapUtils.isEmpty(records)) {
			log.error("!!!! No records found in EXCEL :" + requestSource.getSourceName() + " [ " + requestSource.getFromIndex() + " - "
					+ requestSource.getToIndex() + " ] ");
		}
		return records;
	}

	public BufferedReader getFileContents(String requestFileSource) {
		if (StringUtils.isEmpty(requestFileSource)) {
			log.error(" Request Text File Source is NULL or Empty for test ");
		}
		String requestFileName = QueryProperty.INSTANCE.getFileName(requestFileSource);
		if (StringUtils.isEmpty(requestFileName)) {
			log.error("Request File Source is NULL or Empty. No entry found in properties for :" + requestFileSource);
		}
		try {
			return new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(requestFileName)));
		} catch (Exception e) {
			log.error("Error in Reading the text File :" + requestFileName, e);
			throw e;
		}
	}

	public boolean isBinary(String type) throws IOException {
		CharsetEncoder encoder = Charset.forName("ISO-8859-1").newEncoder();
		if (encoder.canEncode(type)) {
			return false;
		}
		return true;

	}
}
