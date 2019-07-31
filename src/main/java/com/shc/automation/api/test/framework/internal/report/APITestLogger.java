/**
 * 
 */
package com.shc.automation.api.test.framework.internal.report;

import org.testng.Reporter;

import com.shc.automation.api.test.framework.entities.TestStepType;

/**
 * @author spoojar
 *
 */
@Deprecated
public class APITestLogger {
	@Deprecated
    public static void log(String message) {

        log(message, TestStepType.STEP);

    }
	
	@Deprecated
    public static void log(String message, TestStepType typeOfStep) {

        switch (typeOfStep) {
            case STEP:
                message = makeStringAppearBold(message);
                break;
            case VERIFICATION_STEP:
                message = verificationStep(message);
                break;
            case SUBSTEP:
                message = subStep(message);
                break;
            case VERIFICATION_RESULT:
                message = verificationResult(message);
                break;
            case VERIFICATION_SUBSTEP:
                message = verificationSubStep(message);
                break;
            case DATA_CAPTURE:
                message = dataCapture(message);
                break;
            case INNER_SUBSTEP:
                message = innerSubStep(message);
                break;
            case ERRORMESSAGE:
                message = errorMessageStep(message);
                break;
            case ERRORMESSAGEDETAILS:
                message = errorMessageDetailsStep(message);
                break;
            case EXCEPTION:
                message = exceptionStep(message);
                break;
            default:
                break;
        }
        message = removeNonUtf8CompliantCharacters(message);
        Reporter.log(message);
    }

    public static String subStep(String message) {
        //make the message appear in bold
        message = makeStringAppearBold(message);
        return indentWithHTMLCharacter(message, 1, "&bull;");

    }

    public static String innerSubStep(String message) {
        return indentWithHTMLCharacter(message, 2, "&bull;");

    }

    public static String dataCapture(String message) {
        return indentWithHTMLCharacter(message, 2, "&ordm;");

    }

    public static String verificationStep(String message) {
        return indentWithHTMLCharacter(message, 1, "&diams;");

    }

    public static String verificationSubStep(String message) {
        return indentWithHTMLCharacter(message, 2, "&diams;");

    }

    public static String verificationResult(String message) {
        return indentWithHTMLCharacterAndChangeColor(message, 3, "&radic;", "GREEN");

    }

    public static String errorMessageStep(String message) {
        return indentWithHTMLCharacterAndChangeColor(message, 2, "&diams;", "RED");

    }

    public static String errorMessageDetailsStep(String message) {
        return indentWithHTMLCharacterAndChangeColor(message, 4, "&loz;", "RED");
    }

    public static String exceptionStep(String message) {
        return indentWithHTMLCharacterAndChangeColor(message, 0, "", "RED");
    }


    /**
     * @param stringToIndent
     * @param numberOfIndents
     * @param indentationHTMLEncode
     * @return
     */
    private static String indentWithHTMLCharacter(String stringToIndent, Integer numberOfIndents, String indentationHTMLEncode) {
        String spaceIndent = "";
        for (int i = 0; i < numberOfIndents; i++) {
            spaceIndent += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        return spaceIndent + indentationHTMLEncode + "&nbsp;&nbsp;" + stringToIndent;

    }

    /**
     * Change the color of the string to the color specified in parameter <b>color</b> after indenting the string.
     *
     * @param stringToIndent
     * @param numberOfIndents
     * @param indentationHTMLEncode
     * @param color
     * @return String
     */
    private static String indentWithHTMLCharacterAndChangeColor(String stringToIndent, Integer numberOfIndents, String indentationHTMLEncode, String color) {
        String indentedString, coloredString;
        indentedString = indentWithHTMLCharacter(stringToIndent, numberOfIndents, indentationHTMLEncode);
        coloredString = changeStringColor(color, indentedString);
        return coloredString;
    }

    private static String removeNonUtf8CompliantCharacters(final String inString) {
        if (null == inString)
            return null;
        byte[] byteArr = inString.getBytes();
        for (int i = 0; i < byteArr.length; i++) {
            byte ch = byteArr[i];
            if (!((ch > 31 && ch < 253) || ch == '\t')) {
                byteArr[i] = ' ';
            }
        }
        return new String(byteArr);
    }

    private static String makeStringAppearBold(String message) {
        return "<b>" + message + "</b>";
    }

    /**
     * @param color   - color to which the indented string should be changed. This can be in HEX or String
     * @param message - Message for which color should be changed.
     * @return String
     */
    private static String changeStringColor(String color, String message) {
        return "<FONT COLOR=\"" + color + "\">" + message + "</FONT>";
    }


}