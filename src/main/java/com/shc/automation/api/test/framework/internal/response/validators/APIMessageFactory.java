/**
 *
 */
package com.shc.automation.api.test.framework.internal.response.validators;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.shc.automation.api.test.framework.internal.config.APIPropertyQueryConfig;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author spoojar
 *
 */

@Singleton
public class APIMessageFactory {
    private Logger logger = Logger.getLogger("APIMessageFactory");
    private final APIPropertyQueryConfig propertyQueryConfig;

    @Inject
    public APIMessageFactory(APIPropertyQueryConfig propertyQueryConfig) {
        this.propertyQueryConfig = propertyQueryConfig;
    }

    public String getMessage(String messageKey, Boolean result) {
        if (BooleanUtils.isTrue(result)) {
            return getMessage(messageKey.trim(), "_Pass");
        } else {
            return getMessage(messageKey.trim(), "_Fail");
        }
    }

    private String getMessage(String messageKey, String type) {
        String message = propertyQueryConfig.getMessage(messageKey + (type == null ? "" : type.trim()));
        if (StringUtils.isNotBlank(message)) {
            return message;
        }

        message = propertyQueryConfig.getMessage(messageKey);
        if (StringUtils.isNotBlank(message)) {
            return message;
        }

        logger.info("Message not found for key :" + messageKey);

        return messageKey;
    }

}
