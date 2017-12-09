package com.xyz.pages_tests;

import com.xyz.DBInteraction;
import com.xyz.TimeManager;
import com.xyz.pages.CustomerInterfacePage;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;
import static com.xyz.pages.Page_Utilities.getStockPrice;

public class CustomerInterfacePageTests extends CustomerInterfacePage{
    public static void main(String[] args) {
        DepositSuccessTest();
        WithdrawSuccessTest();
        SellSuccessTest_ReduceStock();
        SellSuccessTest_ChangeMoney();
        BuySuccessTest_IncreaseStock();
        BuySuccessTest_ChangeMoney();
        getBalanceTest();
        getStockPriceTest();
        getStockTransactionsForBuyTest();
        getStockTransactionsForSellTest();
        ChargeCommissionTest_ReduceBalance();
        ChargeCommissionTest_IncreaseTotCommission();
        ListMovieInfoTest();
        ListTopMoviesTest_Success();
        ListTopMoviesTest_Fail();
        GetReviewsTest();
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        Initiate(UNAME_STUB, TAXID_STUB);
        TimeManager.Initialize(DATE_STUB);
    }

    private static void DepositSuccessTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 200f;
        try {
            Deposit_Helper(amt);
            ResultSet res = DBInteraction.getData("balance",
                    "Market_Accounts", String.format("WHERE aid = %s", M_AID));
            res.next();
            float expected = BALANCE_MARKET_ACCOUNT_STUB + amt;
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void WithdrawSuccessTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 200f;
        try {
            Withdraw_Helper(amt);
            ResultSet res = DBInteraction.getData("balance",
                    "Market_Accounts", String.format("WHERE aid = %s", M_AID));
            res.next();
            float expected = BALANCE_MARKET_ACCOUNT_STUB - amt;
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SellSuccessTest_ReduceStock() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            InsertStubIntoIn_Stock_Acc(null);
            Sell_Helper(amt, SYM_STUB, STOCK_BUYING_PRICE_STUB);
            ResultSet res = DBInteraction.getData("balance",
                    "In_Stock_Acc",
                    String.format("WHERE aid = %s AND sym = \"%s\"", S_AID, SYM_STUB));
            res.next();
            float expected = BALANCE_IN_STOCK_ACC_STUB - amt;
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void SellSuccessTest_ChangeMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            InsertStubIntoIn_Stock_Acc(null);
            Sell_Helper(amt, SYM_STUB, STOCK_BUYING_PRICE_STUB);
            ResultSet res = DBInteraction.getData("balance",
                    "Market_Accounts",
                    String.format("WHERE aid = %s", M_AID));
            res.next();
            float expected = BALANCE_MARKET_ACCOUNT_STUB + (amt * STOCK_PRICE_STUB);
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void BuySuccessTest_IncreaseStock() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            InsertStubIntoIn_Stock_Acc(null);
            Buy_Helper(amt, SYM_STUB, STOCK_BUYING_PRICE_STUB);
            ResultSet res = DBInteraction.getData("balance",
                    "In_Stock_Acc",
                    String.format("WHERE aid = %s AND sym = \"%s\" AND pps = %f", S_AID, SYM_STUB, STOCK_BUYING_PRICE_STUB));
            res.next();
            float expected = BALANCE_IN_STOCK_ACC_STUB + amt;
            float actual = res.getFloat("balance");
            if (isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void BuySuccessTest_ChangeMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            InsertStubIntoIn_Stock_Acc(null);
            Buy_Helper(amt, SYM_STUB, STOCK_PRICE_STUB);
            ResultSet res = DBInteraction.getData("balance",
                    "Market_Accounts",
                    String.format("WHERE aid = %s", M_AID));
            res.next();
            float expected = BALANCE_MARKET_ACCOUNT_STUB - (amt * STOCK_PRICE_STUB) - 20;
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getBalanceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            String balance = getBalance();
            float actual = Float.parseFloat(balance );
            float expected = BALANCE_MARKET_ACCOUNT_STUB;
            if(isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getStockPriceTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        float actual = getStockPrice(SYM_STUB);
        float expected = STOCK_PRICE_STUB;
        if(isEqual(expected, actual)) pass(test_name);
        else fail(test_name, "");

    }

    private static void getStockTransactionsForBuyTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            Buy_Helper(1, SYM_STUB, STOCK_PRICE_STUB);
            ResultSet res = getStockTransactions(true);
            res.next();
            res.getInt("tid");
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getStockTransactionsForSellTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            InsertStubIntoIn_Stock_Acc(null);
            Sell_Helper(1, SYM_STUB, STOCK_BUYING_PRICE_STUB);
            ResultSet res = getStockTransactions(false);
            res.next();
            res.getInt("tid");
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ChargeCommissionTest_ReduceBalance() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ChargeCommission(M_AID_STUB);
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            float actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB - 20;
            if(isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ListMovieInfoTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = ListMovieInfoHelper("Chicago");
            if (res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ChargeCommissionTest_IncreaseTotCommission() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ChargeCommission(M_AID_STUB);
            ResultSet res = DBInteraction.getData("tot_commission", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            float actual = res.getFloat("tot_commission");
            float expected = 20;
            if(isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ListTopMoviesTest_Success() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = ListTopMoviesHelper("2002", "2004");
            if(res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ListTopMoviesTest_Fail() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = ListTopMoviesHelper("1990", "2000");
            if(res.next()) fail(test_name, "");
            else pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void GetReviewsTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = GetReviewsHelper("Chicago");
            if (res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

}
