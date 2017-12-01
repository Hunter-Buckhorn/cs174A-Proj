package com.xyz;

import java.sql.SQLException;

public class Test_Utilities {
    private static final String PASS_TEMPLATE = "%s: PASSED";
    private static final String FAIL_TEMPLATE = "%s: FAILED: %s";
    public static final String SQL_CUSTOM_FAIL_STATE = "45000";
    public static final float TOLERANCE = 0.001f;
    public static final String M_AID_STUB = "1";
    public static final String S_AID_STUB = "1";
    public static final String SYM_STUB = "123";
    public static final String UNAME_STUB = "test";
    public static final String TAXID_STUB = "test";
    public static final String PWD_STUB = "test";
    public static final float BALANCE_IN_STOCK_ACC_STUB = 10f;
    public static final float BALANCE_MARKET_ACCOUNT_STUB = 1000f;
    public static final float STOCK_PRICE_STUB = 1f;

    public static void pass(String testname) {
        System.out.println(String.format(PASS_TEMPLATE, testname));
    }

    public static void fail(String testname, String reason) {
        System.out.println(String.format(FAIL_TEMPLATE, testname, reason));
    }

    public static boolean isEqual(float expected, float actual) {
        return Math.abs(expected - actual) < TOLERANCE;
    }

    public static void InsertStubIntoIn_Stock_Acc() throws SQLException {
        DBInteraction.insertData("In_Stock_Acc", "(sym, aid, balance)", String.format("%s,%s,%f", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
    }
}
