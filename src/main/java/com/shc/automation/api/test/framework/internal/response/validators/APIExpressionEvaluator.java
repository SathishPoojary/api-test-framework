/**
 *
 */
package com.shc.automation.api.test.framework.internal.response.validators;

import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author spoojar
 *
 */
public class APIExpressionEvaluator {
    private static ScriptEngine scriptEvaluationEngine = null;

    static {
        ScriptEngineManager mgr = new ScriptEngineManager();
        scriptEvaluationEngine = mgr.getEngineByName("JavaScript");
    }

    /**
     * @param expression
     * @return
     * @throws ScriptException
     */
    public static Object evaluateExpression(String expression) throws ScriptException {
        if (StringUtils.isBlank(expression)) {
            return expression;
        }
        expression = expression.trim();
        Pattern regex = Pattern.compile("[&|!=+-/*]");
        Matcher matcher = regex.matcher(expression);
        if (!matcher.find()) {
            return expression;
        }
        if (JSONUtils.mayBeJSON(expression)) {
            return expression;
        }

        return scriptEvaluationEngine.eval(expression.toString());

    }

    public static boolean validate(String boolExpression) {
        if (StringUtils.isEmpty(boolExpression)) {
            return true;
        }
        Object eval = null;
        try {
            eval = evaluateExpression(boolExpression);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        if (eval == null) {
            return false;
        }
        return BooleanUtils.toBoolean(eval.toString());
    }

}
