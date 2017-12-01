package com.xyz;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;

public class TriggerTests {
   public static void main(String[] args) {
       NotEnoughStockToSellTest();
       NotEnoughStockToSellTest_NoStockInAccounts();
       AfterStockSaleTest_AddMoney();
       AfterStockSaleTest_SubtractStock();
       DepositTransactionTest();
       WithdrawWithoutEnoughMoney();
       WithdrawWithEnoughMoney();
       NewMarketAccUnder1000Test();
       NotEnoughMoneyToBuyTest();
       AfterPurchaseTest_CreateNewInStockAcc();
       AfterPurchaseTest_AddTheStock();
       AfterPurchaseTest_ReduceTheMoney();
    }

    private static void NotEnoughStockToSellTest_NoStockInAccounts() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", "1,1,123,10");
        } catch (SQLException e) {
            if(e.getSQLState().equals(SQL_CUSTOM_FAIL_STATE)) pass(test_name);
            else {
                fail(test_name, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void NotEnoughStockToSellTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", "1,1,123,11");
        } catch (SQLException e) {
            if(e.getSQLState().equals(SQL_CUSTOM_FAIL_STATE)) pass(test_name);
            else {
                fail(test_name, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void AfterStockSaleTest_SubtractStock() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, pps)", "1,1,123,1,1");
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
            float expected = 981.000f;
            res.next();
            float actual = res.getFloat("balance");
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AfterStockSaleTest_AddMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
           DBInteraction.insertData("In_Stock_Acc", "", "123,1,10");
           DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, pps)", "1,1,123,1,1");
           ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
           float expected = 9.000f;
           res.next();
           float actual = res.getFloat("balance");
           if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
           else fail(test_name, "");
       }
       catch (SQLException e) {
           fail(test_name, e.getMessage());
           e.printStackTrace();
       }
    }


        private static void DepositTransactionTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Deposit_Transactions", "(aid, amount)", "1, 100");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
            return;
        }
        float actual = 0.000f;
        try {
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
            res.next();
            actual = res.getFloat("balance");
            float expected = 1100f;
            if ( Math.abs(actual - expected) < TOLERANCE )pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void WithdrawWithoutEnoughMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", "1, 11000");
        } catch (SQLException e) {
            if (e.getSQLState().equals(SQL_CUSTOM_FAIL_STATE)) pass(test_name);
            else {
                fail(test_name, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void WithdrawWithEnoughMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", "1, 100");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
            return;
        }
        float actual = 0.000f;
        try {
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
            res.next();
            actual = res.getFloat("balance");
            float expected = 900f;
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void NewMarketAccUnder1000Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Market_Accounts","(aid, uname, balance)", "2, \"test\", 10");
        } catch (SQLException e) {
            if (e.getSQLState().equals(SQL_CUSTOM_FAIL_STATE)) pass(test_name);
            else {
                fail(test_name, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void NotEnoughMoneyToBuyTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Buy_Transactions","(m_aid, s_aid, sym, amount, pps)", "1,1,123,1,999");
        } catch (SQLException e) {
            if(e.getSQLState().equals(SQL_CUSTOM_FAIL_STATE)) pass(test_name);
            else {
                fail(test_name, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void AfterPurchaseTest_CreateNewInStockAcc() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        try {
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
            if (res.next() == true) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AfterPurchaseTest_AddTheStock() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        float actual = 0.0f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", "WHERE aid = 1 AND sym = 123");
            res.next();
            actual = res.getFloat("balance");
            float expected = 11f;
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AfterPurchaseTest_ReduceTheMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        float actual = 0.0f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "","123,1,10");
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", "1,1,123,1,1");
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", "WHERE aid = 1");
            res.next();
            actual = res.getFloat("balance");
            float expected = 979f;
            if (Math.abs(expected - actual) < TOLERANCE) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
