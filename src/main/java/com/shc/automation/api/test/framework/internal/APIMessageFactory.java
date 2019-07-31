/**
 * 
 */
package com.shc.automation.api.test.framework.internal;

import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.shc.automation.api.test.framework.entities.APITestConstants;
import com.shc.automation.api.test.framework.internal.config.APIConfigManager;

/**
 * @author spoojar
 *
 */
public enum APIMessageFactory {
	INSTANCE;

	private static final Logger log = Logger.getLogger("APIMessageFactory");
	private final Properties messages = APIConfigManager.getProperties(APITestConstants.MESSAGE_FILE);

	public static APIMessageFactory getInstance() {
		return INSTANCE;
	}

	public String getMessage(String messageKey, Boolean result) {
		if (BooleanUtils.isTrue(result)) {
			return getMessage(messageKey.trim(), "_Pass");
		} else {
			return getMessage(messageKey.trim(), "_Fail");
		}
	}

	private String getMessage(String messageKey, String type) {
		String message = messages.getProperty(messageKey + (type == null ? "" : type.trim()));
		if (StringUtils.isNotBlank(message)) {
			return message;
		}

		message = messages.getProperty(messageKey);
		if (StringUtils.isNotBlank(message)) {
			return message;
		}

		log.info("Message not found for key :" + messageKey);

		return messageKey;
	}

}
