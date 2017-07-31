package net.funambolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fabio Mattei
 *
 * @version 1.3
 * @date 31/07/2017
 *
 * This class is meant to validate the input following the rules given by the software developer.
 * It gives an alarm if the input does not follow the rules and it gives back to the caller
 * the cleaned values.
 */
public class WashingMachine {
    private HashMap<String, String> values;
    private HashMap<String, String> rules;
    private HashMap<String, String> cleanValues = new HashMap<String, String>();

    public static final String RULE_BOOLEAN = "boolean";
    public static final String RULE_INTEGER = "integer";
    public static final String RULE_ONLYNUMERIC = "onlynumeric";
    public static final String RULE_ONLYALPHA = "onlyalpha";
    public static final String RULE_ALPHANUMERIC = "alphanumerical";
    public static final String RULE_EXACTLEN = "exactlen";
    public static final String RULE_MINLEN = "minlen";
    public static final String RULE_MAXLEN = "maxlen";
    public static final String RULE_MINNUMERIC = "minnumeric";
    public static final String RULE_MAXNUMERIC = "maxnumeric";
    public static final String RULE_CALENDARDATE = "calendardate";
    public static final String RULE_MYSQLDATE = "mysqldate";
    public static final String RULE_TIME = "time";
    public static final String RULE_REQUIRED = "required";
    public static final String RULE_CHECKBOX = "checkbox";
    public static final String RULE_DIV = "|";
    public static final String EMPTY_STRING = "";

    private static final String RE_CALENDARDATE = "^(0?[1-9]|[12][0-9]|3[01])[\\/\\-](0?[1-9]|1[012])[\\/\\-](\\d{4})$";
    private static final String RE_MYSQLDATE = "^(\\d{4})-\\d{2}-(\\d{2})$";
    private static final String RE_TIME = "\\d{2}:\\d{2}";
    private static final String RE_INTEGER = "(?=.*[^ ])[0-9]+";
    private static final String RE_ONLYNUMERIC = "(?=.*[^ ])[0-9\\., ]+";
    private static final String RE_ONLYALPHA = "(?=.*[^ ])[a-zA-ZÀ-ÿ\\?;\\.!@€£$&\\+=*\\{\\}\\[\\]\\(\\)\\-_\\r\\n\\t\\/,: ]+";
    private static final String RE_ALPHANUMERIC = "^[a-zA-ZÀ-ÿ0-9\\?;\\.!@€£$&\\+=*\\{\\}\\[\\]\\(\\)\\-_\\r\\n\\t\\/,: ]*$";

    private List<String> errors = new ArrayList<>();

    /**
     * This class check the content of a set o fields to see if they contina what they are ment to
     * possible filters are:
     * - boolean             can contain [true, false]
     * - integer             can contain an integer number
     * - onlynumeric         can contain a number integer or float (with comma or dots)
     * - onlyalpha           can contain only alphabetical characters (even with accents) and ? , : ; . ! @ € £ $ & + = * - _ \r \n \t
     * - alphanumerical      it is a combination of onlyalpha and onlynumeric
     * - exactlen            followed by a number is the exact allowed lenght of the passed parameter
     * - minlen              followed by a number is the minimum allowed lenght of the passed parameter
     * - maxlen              followed by a number is the maximum allowed lenght of the passed parameter
     * - minnumeric          followed by a number n the filled content need to be greater then n
     * - maxnumeric          followed by a number n the filled content need to be less then n
     * - calendardate        it has to have the format dd/mm/yyyy
     * - mysqldate           it has to have the format yyyy-mm-dd
     * - required            the field is mandatory
     * - checkbox            the field could be missing and that would not give an error, a check box must be followed by a type ex: integer or alphanumerical
     *
     * Example of usages
     * "required|integer"                      the parameter is a required integer number
     * "required|alphanumerical|maxlen250"     the parameter is required, alphanumerical and has a maximum allowed lenght of 250
     */
    public WashingMachine() {
    }

    public void setValues(HashMap<String, String> values) {
        // iterating through the values to set eventual null to empty String
        for (String k: values.keySet()) {
            if (values.get(k) == null) {
                values.put(k, "");
            }
        }
        this.values = values;
    }

    public void setRules(HashMap<String, String> rules) {
        this.rules = rules;
    }

    public List<String> getErrors() {
        return errors;
    }

    public HashMap<String, String> getCleanValues() {
        return cleanValues;
    }

    public String getCleanValue(String key) {
        if (cleanValues.containsKey(key)) {
            return cleanValues.get(key);
        } else {
            return "";
        }
    }

    public long getCleanLongValue(String key) {
        if (cleanValues.containsKey(key)) {
            try {
                return Long.parseLong(cleanValues.get(key));
            } catch (NumberFormatException ex) {
                return (long) 0;
            }
        } else {
            return (long) 0;
        }
    }

    /**
     * return all pair [key, value] with a key that contains a certain pattern
     *
     * Example if pattern is "checkbox" and key is "checkbox01" the pair key, value will be returned
     *
     * @param pattern
     * @return
     */
    public HashMap<String, String> getCleanValuesWithPattern(String pattern) {
        HashMap<String, String> out = new HashMap<>();
        for (String key : cleanValues.keySet()) {
            if (key.contains(pattern)) {
                out.put(key, cleanValues.get(key));
            }
        }
        return out;
    }

    public boolean isGood() {
        boolean out = true;
        for (String key : values.keySet()) {
            if (!singleValueIsGood(key, values.get(key), rules.get(key))) {
                cleanValues.put(key, "");
                out = false;
            } else {
                cleanValues.put(key, stripXSS(values.get(key).trim()));
            }
        }
        return out;
    }

    private boolean singleValueIsGood(String field, String value, String rule) {
        if (value == null) {
            // a checkbox could send back a null field
            if (rule.contains(RULE_CHECKBOX)) {
                if (rule.contains(RULE_INTEGER) || rule.contains(RULE_ONLYNUMERIC)) {
                    values.put(field, "0");
                    value = "0";
                } else {
                    values.put(field, "");
                    value = "";
                }
            } else {
                errors.add("The field " + field + " has not been defined");
                return false;
            }
        }
        if (rule == null) {
            errors.add("No check defined for the field " + field);
            return false;
        }
        if (rule.contains(RULE_REQUIRED)) {
            if (value.trim().length() == 0) {
                errors.add("The " + field + " field is required");
                return false;
            }
        }
        Pattern p = Pattern.compile(".*maxlen,([0-9]+).*");
        Matcher m = p.matcher(rule);
        if (m.find()) {
            int mx = Integer.parseInt(m.group(1));
            if (value.length() > mx) {
                errors.add("The " + field + " field needs to be shorter than " + value.length() + " character");
                return false;
            }
        }

        p = Pattern.compile(".*minlen,([0-9]+).*");
        m = p.matcher(rule);
        if (m.find()) {
            int mx = Integer.parseInt(m.group(1));
            if (value.length() < mx) {
                errors.add("The " + field + " field needs to be longer than " + value.length() + " character");
                return false;
            }
        }
        p = Pattern.compile(".*exactlen,([0-9]+).*");
        m = p.matcher(rule);
        if (m.find()) {
            int mx = Integer.parseInt(m.group(1));
            if (value.length() != mx) {
                errors.add("The " + field + " field needs to be exactly " + value.length() + " character in length");
                return false;
            }
        }

        if (rule.contains(RULE_ALPHANUMERIC)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_ALPHANUMERIC)) {
                    errors.add("The " + field + " field may only contain alpha-numeric characters");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_ALPHANUMERIC)) {
                    errors.add("The " + field + " field may only contain alpha-numeric characters");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_ONLYALPHA)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_ONLYALPHA)) {
                    errors.add("The " + field + " field may only contain alpha characters");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_ONLYALPHA)) {
                    errors.add("The " + field + " field may only contain alpha characters");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_ONLYNUMERIC)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_ONLYNUMERIC)) {
                    errors.add("The " + field + " field may only contain numeric characters");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_ONLYNUMERIC)) {
                    errors.add("The " + field + " field may only contain numeric characters");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_INTEGER)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_INTEGER)) {
                    errors.add("The " + field + " field may only contain integer number");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_INTEGER)) {
                    errors.add("The " + field + " field may only contain integer number");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_BOOLEAN)) {
            if (!(value.matches("true") || value.matches("false"))) {
                errors.add("The " + field + " field may only contain a true or false value");
                return false;
            }
        }

        p = Pattern.compile(".*minnumeric,([0-9]+).*");
        m = p.matcher(rule);
        if (m.find()) {
            long mx = Long.parseLong(m.group(1));
            long localvalue = 0;
            try {
                localvalue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                errors.add("The " + field + " field needs to be numeric");
                return false;
            }
            if (localvalue < mx) {
                errors.add("The " + field + " field needs to greater then " + mx );
                return false;
            }
        }

        p = Pattern.compile(".*maxnumeric,([0-9]+).*");
        m = p.matcher(rule);
        if (m.find()) {
            long mx = Long.parseLong(m.group(1));
            long localvalue = 0;
            try {
                localvalue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                errors.add("The " + field + " field needs to be numeric");
                return false;
            }
            if (localvalue > mx) {
                errors.add("The " + field + " field needs to less then " + mx);
                return false;
            }
        }

        if (rule.contains(RULE_CALENDARDATE)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_CALENDARDATE)) {
                    errors.add("The " + field + " field needs to be a valid date");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_CALENDARDATE)) {
                    errors.add("The " + field + " field needs to be a valid date");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_MYSQLDATE)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_MYSQLDATE)) {
                    errors.add("The " + field + " field needs to be a valid date");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_MYSQLDATE)) {
                    errors.add("The " + field + " field needs to be a valid date");
                    return false;
                }
            }
        }

        if (rule.contains(RULE_TIME)) {
            if (rule.contains(RULE_REQUIRED)) {
                if (!value.matches(RE_TIME)) {
                    errors.add("The " + field + " field needs to be a valid time");
                    return false;
                }
            } else {
                if (!EMPTY_STRING.equals(value) && !value.matches(RE_TIME)) {
                    errors.add("The " + field + " field needs to be a valid time");
                    return false;
                }
            }
        }

        return true;
    }

    private String stripXSS(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }

    public String getAllErrors() {
        String out = "";
        for (String er : errors) {
            out += er + " ";
        }
        return out;
    }

    public String getAllErrorsWithBr() {
        String out = "";
        for (String er : errors) {
            out += er + "</br>";
        }
        return out;
    }

}
