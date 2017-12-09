package com.xyz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.xyz.Test_Utilities.*;

public class TimeManagerTests extends TimeManager {
    public static void main(String[] args) {
        UpdateStockClosingPriceTest();
        UpdateDayOfTheMonthTest();
        UpdateRunningBalanceTest();
        SetDayHelper_CloseDay();
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        Initialize(DATE_STUB);
    }

    private static void UpdateStockClosingPriceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            UpdateStockClosingPrice();
            ResultSet res = DBInteraction.getData("dcp, price", "Stock_Profiles", String.format("WHERE sym = \"%s\"", SYM_STUB));
            res.next();
            if(isEqual(res.getFloat("price"),res.getFloat("dcp"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void UpdateDayOfTheMonthTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        int amt = 1;
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int oldDotM = res.getInt(1);
            UpdateDayInMarketAcct(amt);
            res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int newDotM = res.getInt(1);
            if(newDotM == (oldDotM + amt)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void UpdateRunningBalanceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        int mult = 4;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            UpdateRunningBalanceSum(mult);
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance * mult, newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayHelper_CloseDay() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 4;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        if(isClosed) pass(test_name);
        else fail(test_name, "");
    }
}
