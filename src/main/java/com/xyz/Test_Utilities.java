package com.xyz;

public class Test_Utilities {
    private static final String PASS_TEMPLATE = "%s: PASSED";
    private static final String FAIL_TEMPLATE = "%s: FAILED: %s";
    public static final float TOLERANCE = 0.001f;

    public static void pass(String testname) {
        System.out.println(String.format(PASS_TEMPLATE, testname));
    }

    public static void fail(String testname, String reason) {
        System.out.println(String.format(FAIL_TEMPLATE, testname, reason));
    }
}
