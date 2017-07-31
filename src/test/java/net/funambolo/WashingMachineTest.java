package net.funambolo;


import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by Fabio Mattei on 19/08/14.
 */
public class WashingMachineTest {

    @Test
    public void GivenNullValue_TestFails() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put(null, null);
        rules.put(null, null);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void GivenNullArguments_TestFails() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("hello", null);
        rules.put("hello", null);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenRequired_ValueMustBeNotEmpty() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", "required");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenRequired_CleanValueContainsSomething() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello");
        rules.put("name", "required");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenMaxLen_notGoodIfStringLong() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello this is a so long string");
        rules.put("name", "required|maxlen,11");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMaxLen_goodIfStringShort() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello this is a so long string");
        rules.put("name", "required|maxlen,30");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenMinLen_notGoodIfStringShort() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello this");
        rules.put("name", "required|minlen,11");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMinLen_goodIfStringLong() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello this is a so long string");
        rules.put("name", "required|minlen,11");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenExactLen_NotgoodIfStringLong() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello this is a so long string");
        rules.put("name", "required|exactlen,10");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenExactLen_GoodIfStringIsExact() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello");
        rules.put("name", "required|exactlen,5");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlphanumerical_GoodIfJustAlphanumerical() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello 123  \n   ?");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ALPHANUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlphanumericalUnrequired_GoodIfJustEmpty() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_ALPHANUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlphanumerical_GoodIfContainsFewSymbols() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello / {} () [] @ + = \n ?");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ALPHANUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlpha_GoodIfJustAlpha() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello fabio how are you good boy èàòù!");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ONLYALPHA);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlphaUnrequired_GoodIfJustEmpty() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_ONLYALPHA);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenAlpha_NotGoodIfNotJustAlpha() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello @  123");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ONLYALPHA);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenNumeric_GoodIfJustNumeric() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "1234.56");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ONLYNUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenNumericUnrequired_GoodIfJustEmpty() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_ONLYNUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenNumeric_NotGoodIfNotJustNumeric() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "Hello @  123");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_ONLYNUMERIC);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenInteger_GoodIfJustInteger() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "1234");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_INTEGER);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenIntegerUnrequired_GoodIfJustEmpty() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_INTEGER);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
        Assert.assertEquals(0, wm.getCleanLongValue("name"));
    }

    @Test
    public void givenInteger_NotGoodIfNotJustInteger() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "123.5");
        rules.put("name", "required|integer");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenBoolean_GoodIfJustBoolean() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "true");
        rules.put("name", "required|boolean");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenBoolean_NotGoodIfNotJustBoolean() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "true lalala");
        rules.put("name", "required|boolean");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMinnumeric_GoodIfValueIsMoreThenMin() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "50");
        rules.put("name", "required|minnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenMinnumeric_NotGoodIfValueIsLessThenMin() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "20");
        rules.put("name", "required|minnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMinnumericWithString_NotGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "aa");
        rules.put("name", "required|minnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMaxnumeric_GoodIfValueIsLessThenMin() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "20");
        rules.put("name", "required|maxnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenMaxnumeric_NotGoodIfValueIsMoreThenMin() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "50");
        rules.put("name", "required|maxnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenMaxnumericWithString_NotGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "aa");
        rules.put("name", "required|maxnumeric,32");
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenDate_ValidCalendarDateIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "22/05/2017");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_CALENDARDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_EmptyAndUnrequiredCalendarDateIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_CALENDARDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_ValidCalendarDateWithStringIsNotGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "22/05/2017a");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_CALENDARDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenDate_ValidMySqlDateIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "2014-04-02");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_MYSQLDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_EmptyAndUnrequiredMySqlDateIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_MYSQLDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_ValidMySqlDateWithStringIsNotGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "2014-04-02a");
        rules.put("name",  WashingMachine.RULE_REQUIRED + WashingMachine.RULE_MYSQLDATE);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

    @Test
    public void givenDate_ValidTimeIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "12:45");
        rules.put("name", WashingMachine.RULE_REQUIRED + WashingMachine.RULE_TIME);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_EmptyAndUnrequiredTimeIsGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "");
        rules.put("name", WashingMachine.RULE_TIME);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(true, wm.isGood());
    }

    @Test
    public void givenDate_ValidTimeWithStringIsNotGood() {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, String> rules = new HashMap<String, String>();
        values.put("name", "12:45a");
        rules.put("name",  WashingMachine.RULE_REQUIRED + WashingMachine.RULE_TIME);
        WashingMachine wm = new WashingMachine();
        wm.setValues(values);
        wm.setRules(rules);
        Assert.assertEquals(false, wm.isGood());
    }

}
