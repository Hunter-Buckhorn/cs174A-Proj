package com.xyz;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;

public class TriggerTests {
   public static void main(String[] args) {
        NotEnoughStockToSellTest1();
        NotEnoughStockToSellTest2();
        try {
            AfterStockSaleTest();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DepositTransactionTest();
        WithdrawWithoutEnoughMoney();
        WithdrawWithEnoughMoney();
        NewMarketAccUnder1000Test();
        NotEnoughMoneyToBuyTest();
        AfterPurchaseTest_CreateNewInStockAcc();
        AfterPurchaseTest_AddTheStock();
        AfterPurchaseTest_ReduceTheMoney();
    }

    private static void NotEnoughStockToSellTest1() {
        // WHEN THERE IS NO ASSOCIATION BETWEEN STOCK AND STOCK_ACCOUNTS
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        int num_row_affected = DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", "1,1,123,10");
        if (num_row_affected == 0) {
            pass(test_name);
        }
        else {
            fail(test_name, "");
        }
    }

    private static void NotEnoughStockToSellTest2() {
        // HAS THE STOCK BUT NOT THE AMOUNT
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
        int num_row_affected = DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", "1,1,123,11");
        if (num_row_affected == 0) {
            pass(test_name);
        }
        else {
            fail(test_name, "");
        }
    }

    private static void AfterStockSaleTest() throws SQLException {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
        DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, pps)", "1,1,123,1,1");
        ResultSet res1 = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
        ResultSet res2 = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
        float expected1 = 981.000f;
        res1.next();
        float actual1 = res1.getFloat("balance");
        float expected2 = 9.000f;
        res2.next();
        float actual2 = res2.getFloat("balance");
        if (Math.abs(expected1 - actual1) < TOLERANCE &&
                Math.abs(expected2 - actual2) < TOLERANCE) {
            pass(test_name);
        }
        else if (Math.abs(expected1 - actual1) > TOLERANCE) {
            fail(test_name, "Not Adding the correct amount of money");
        }
        else {
            fail(test_name, "Not Subtracting the correct amount of stock");
        }
    }

    private static void DepositTransactionTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("Deposit_Transactions", "(aid, amount)", "1, 100");
        ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
        float actual = 0.000f;
        try {
            res.next();
            actual = res.getFloat("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            float expected = 1100f;
            if ( Math.abs(actual - expected) < TOLERANCE ){
                pass(test_name);
            } else {
                fail(test_name, "");
            }
        }
    }

    private static void WithdrawWithoutEnoughMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        int num_row_affected = DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", "1, 11000");
        if (num_row_affected == 0 ){
            pass(test_name);
        }
        else {
            fail(test_name, "");
        }
    }

    private static void WithdrawWithEnoughMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", "1, 100");
        ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
        float actual = 0.000f;
        try {
            res.next();
            actual = res.getFloat("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            float expected = 900f;
            if (Math.abs(expected - actual) < TOLERANCE) {
                pass(test_name);
            }
            else {
                fail(test_name, "");
            }
        }
    }

    private static void NewMarketAccUnder1000Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        int num_row_affected = DBInteraction.insertData("Market_Accounts","(aid, uname, balance)", "2, \"test\", 10");
        if (num_row_affected == 0) pass(test_name);
        else fail(test_name, "");
    }

    private static void NotEnoughMoneyToBuyTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        int num_row_affected = DBInteraction.insertData("Buy_Transactions","(m_aid, s_aid, sym, amount, pps)", "1,1,123,1,999");
        if(num_row_affected == 0) pass(test_name);
        else fail(test_name, "");
    }

    private static void AfterPurchaseTest_CreateNewInStockAcc() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
        ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
        try {
            if (res.next() == true) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            e.printStackTrace();
            fail(test_name,"");
        }
    }

    private static void AfterPurchaseTest_AddTheStock() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
        DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
        ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
        float actual = 0.0f;
        try {
            res.next();
            actual = res.getFloat("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            float expected = 11f;
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        }
    }

    private static void AfterPurchaseTest_ReduceTheMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
        DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
        ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
        float actual = 0.0f;
        try {
            res.next();
            actual = res.getFloat("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            float expected = 979f;
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        }
    }
}
