package com.xyz.pages_tests;

import com.xyz.DBInteraction;
import com.xyz.pages.CustomerInterfacePage;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;

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
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        setDate("2017-11-30");
        Initiate(UNAME_STUB, TAXID_STUB);
    }

    private static void DepositSuccessTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 200f;
        try {
            Deposit_Helper(amt);
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID));
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
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID));
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
        float amt = 1f;
        try {
            InsertStubIntoIn_Stock_Acc();
            Sell_Helper(amt, SYM_STUB);
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = %s", S_AID, SYM_STUB));
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
        float amt = 1f;
        try {
            InsertStubIntoIn_Stock_Acc();
            Sell_Helper(amt, SYM_STUB);
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID));
            res.next();
            float expected = BALANCE_MARKET_ACCOUNT_STUB + (amt * STOCK_PRICE_STUB) - 20;
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
        float amt = 1f;
        try {
            InsertStubIntoIn_Stock_Acc();
            Buy_Helper(amt, SYM_STUB);
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = %s", S_AID, SYM_STUB));
            res.next();
            float expected = BALANCE_IN_STOCK_ACC_STUB + amt;
            if (isEqual(expected, res.getFloat("balance"))) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void BuySuccessTest_ChangeMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1f;
        try {
            InsertStubIntoIn_Stock_Acc();
            Buy_Helper(amt, SYM_STUB);
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID));
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
        try {
            float actual = getStockPrice(SYM_STUB);
            float expected = STOCK_PRICE_STUB;
            if(isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void getStockTransactionsForBuyTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 1;
        try {
            Buy_Helper(1, SYM_STUB);
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
            InsertStubIntoIn_Stock_Acc();
            Sell_Helper(1, SYM_STUB);
            ResultSet res = getStockTransactions(false);
            res.next();
            res.getInt("tid");
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
