package net.funambolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fabio Mattei on 19/08/14.
 */
public class WashingMachine {
    private HashMap<String, String> values;
    private HashMap<String, String> rules;
    private HashMap<String, String> cleanValues = new HashMap<String, String>();

    private List<String> errors = new ArrayList<String>();

    public WashingMachine() {
    }

    public void setValues(HashMap<String, String> values) {
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
            errors.add("The field " + field + " has not been defined");
            return false;
        }
        if (rule == null) {
            errors.add("No check defined for the field "+ field);
            return false;
        }
        if (rule.contains("required")) {
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

        if (rule.contains("alphanumerical")) {
            if (!value.matches("(?=.*[^ ])[a-zA-Z0-9\\?;\\.!@\\-_\\n\\t\\r,: ]+")) {
                errors.add("The " + field + " field may only contain alpha-numeric characters");
                return false;
            }
        }

        if (rule.contains("onlyalpha")) {
            if (!value.matches("(?=.*[^ ])[a-zA-Z\\?;\\.!,: ]+")) {
                errors.add("The " + field + " field may only contain alpha characters");
                return false;
            }
        }

        if (rule.contains("onlynumeric")) {
            if (!value.matches("(?=.*[^ ])[0-9\\., ]+")) {
                errors.add("The " + field + " field may only contain numeric characters");
                return false;
            }
        }

        if (rule.contains("integer")) {
            if (!value.matches("(?=.*[^ ])[0-9]+")) {
                errors.add("The " + field + " field may only contain integer number");
                return false;
            }
        }

        if (rule.contains("boolean")) {
            if (!(value.matches("true") ||value.matches("false"))) {
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
                errors.add("The " + field + " field needs to be exactly " + value.length() + " character in length");
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
                errors.add("The " + field + " field needs to be exactly " + value.length() + " character in length");
                return false;
            }
        }

        if (rule.contains("date")) {
            if (!value.matches("\\d{4}-\\d{2}-\\d{2}") ) {
                errors.add("The " + field + " field needs to be a valid date");
                return false;
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

}

