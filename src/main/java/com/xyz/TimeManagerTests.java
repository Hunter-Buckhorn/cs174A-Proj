package com.xyz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.xyz.Test_Utilities.*;

public class TimeManagerTests extends TimeManager {
    public static void main(String[] args) {
        OpenDayTest();
        CloseDateTest_ChangeIsClosed();
        CloseDateTest_UpdateDayOfTheMonthByOne();
        CloseDateTest_UpdateRunningBalanceWithCurrentBalance();
        CloseDateTest_UpdateClosingPrice();
        UpdateStockClosingPriceTest();
        UpdateDayOfTheMonthTest();
        UpdateRunningBalanceTest();
        changeStockPriceTest();
        SetDayTest_OpenMarket();
        SetDayTest_UpdateDayOfTheMonth_OneDayApart_Opened();
        SetDayTest_RunningBalance_OneDayApart_Opened();
        SetDayTest_UpdateDayOfTheMonth_OneDayApart_Closed();
        SetDayTest_RunningBalance_OneDayApart_Closed();
        SetDayTest_UpdateDayOfTheMonth_ManyDayApart_Opened();
        SetDayTest_RunningBalance_ManyDayApart_Opened();
        SetDayTest_UpdateDayOfTheMonth_ManyDayApart_Closed();
        SetDayTest_RunningBalance_ManyDayApart_Closed();
        ClearDayofTheMonthTest();
        ClearRunningBalanceSumTest();
        ClearCommissionTest();
        setNewInitialBalanceTest();
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        Initialize(DATE_STUB);
    }

    private static void OpenDayTest() {
        String test_name = new Object() {}.getClass().getEnclosingMethod().getName();
        setUp();
        isClosed = true;
        openDay();
        if (isClosed == false) pass(test_name);
        else fail(test_name, "");
    }

    private static void CloseDateTest_ChangeIsClosed() {
        String test_name = new Object() {}.getClass().getEnclosingMethod().getName();
        setUp();
        isClosed = false;
        closeDay();
        if (isClosed == true) pass(test_name);
        else fail(test_name, "");
    }

    private static void CloseDateTest_UpdateDayOfTheMonthByOne() {
        String test_name = new Object() {}.getClass().getEnclosingMethod().getName();
        setUp();
        isClosed = false;
        closeDay();
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int actual = res.getInt(1);
            int expected = DAY_OF_THE_MONTH_STUB + 1;
            if (actual == expected) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void CloseDateTest_UpdateRunningBalanceWithCurrentBalance() {
        String test_name = new Object() {}.getClass().getEnclosingMethod().getName();
        setUp();
        isClosed = false;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            closeDay();
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance, newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void CloseDateTest_UpdateClosingPrice() {
        String test_name = new Object() {}.getClass().getEnclosingMethod().getName();
        setUp();
        isClosed = false;
        try {
            closeDay();
            ResultSet res = DBInteraction.getData("dcp, price", "Stock_Profiles", String.format("WHERE sym = \"%s\"", SYM_STUB));
            res.next();
            if(isEqual(res.getFloat("price"),res.getFloat("dcp"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
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
        int amt = 3;
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

    private static void changeStockPriceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float newPrice = 123456f;
        try {
            ChangeStockValHelper(newPrice, SYM_STUB);
            ResultSet res = DBInteraction.getData("price", "Stock_Profiles", String.format("WHERE sym = \"%s\"", SYM_STUB));
            res.next();
            float actual = res.getFloat(1);
            if (isEqual(actual, newPrice)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_OpenMarket() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 1;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = true;
        try {
            setDate(newDay.toString());
            if (!isClosed) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_UpdateDayOfTheMonth_OneDayApart_Opened() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 1;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = false;
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int oldDotM = res.getInt(1);
            setDate(newDay.toString());
            res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int newDotM = res.getInt(1);
            if(newDotM == (oldDotM + dayDiff)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_RunningBalance_OneDayApart_Opened() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 1;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = false;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            setDate(newDay.toString());
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance * dayDiff, newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_UpdateDayOfTheMonth_OneDayApart_Closed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 1;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = true;
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int oldDotM = res.getInt(1);
            setDate(newDay.toString());
            res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int newDotM = res.getInt(1);
            if(newDotM == (oldDotM + dayDiff - 1)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_RunningBalance_OneDayApart_Closed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 1;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = true;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            setDate(newDay.toString());
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance * (dayDiff - 1), newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_UpdateDayOfTheMonth_ManyDayApart_Opened() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 10;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = false;
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int oldDotM = res.getInt(1);
            setDate(newDay.toString());
            res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int newDotM = res.getInt(1);
            if(newDotM == (oldDotM + dayDiff)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_RunningBalance_ManyDayApart_Opened() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 10;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = false;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            setDate(newDay.toString());
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance * dayDiff, newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_UpdateDayOfTheMonth_ManyDayApart_Closed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 10;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = true;
        try {
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int oldDotM = res.getInt(1);
            setDate(newDay.toString());
            res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int newDotM = res.getInt(1);
            if(newDotM == (oldDotM + dayDiff - 1)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SetDayTest_RunningBalance_ManyDayApart_Closed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        LocalDate oldDay = LocalDate.parse(DATE_STUB);
        long dayDiff = 10;
        LocalDate newDay = oldDay.plusDays(dayDiff);
        isClosed = true;
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float oldRBS = res.getFloat(1);
            float balance = res.getFloat(2);
            setDate(newDay.toString());
            res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float newRBS = res.getFloat(1);
            if(isEqual(oldRBS + balance * (dayDiff - 1), newRBS)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ClearDayofTheMonthTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ClearDayofTheMonth();
            ResultSet res = DBInteraction.getData("day_of_the_month", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            int DotM = res.getInt(1);
            if(DotM == 0) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ClearRunningBalanceSumTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ClearRunningBalanceSum();
            ResultSet res = DBInteraction.getData("running_balance_sum", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float RBS = res.getFloat(1);
            if(isEqual(RBS, 0f)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ClearCommissionTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ClearCommission();
            ResultSet res = DBInteraction.getData("tot_commission", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float tot_commission = res.getFloat(1);
            if(isEqual(tot_commission, 0f)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setNewInitialBalanceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            setNewInitialBalance();
            ResultSet res = DBInteraction.getData("initial_balance, balance", "Market_Accounts", String.format("WHERE aid = \"%s\"", M_AID_STUB));
            res.next();
            float init_balance = res.getFloat("initial_balance");
            float balance = res.getFloat("balance");
            if(isEqual(init_balance, balance)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
