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
       AccrueTriggerTest();
    }

    private static void NotEnoughStockToSellTest_NoStockInAccounts() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();

        float amt = 1.325f;
        try {
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", String.format("%s,%s,%s,%f", M_AID_STUB, S_AID_STUB, SYM_STUB, amt));
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
        float amt = BALANCE_IN_STOCK_ACC_STUB + 1;
        try {
            DBInteraction.insertData("In_Stock_Acc", "", String.format("%s,%s,%s", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount)", String.format("%s,%s,%s,%f", M_AID_STUB, S_AID_STUB, SYM_STUB, amt));
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
        float amt = 1.125f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "", String.format("%s,%s,%f", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f", M_AID_STUB, S_AID_STUB, SYM_STUB, amt, STOCK_PRICE_STUB));
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = %s", S_AID_STUB, SYM_STUB));
            float expected = BALANCE_IN_STOCK_ACC_STUB - amt;
            res.next();
            float actual = res.getFloat("balance");
            if (isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AfterStockSaleTest_AddMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        float amt = 1.125f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "", String.format("%s,%s,%f", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f", M_AID_STUB, S_AID_STUB, SYM_STUB, amt, STOCK_PRICE_STUB));
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            float expected = BALANCE_MARKET_ACCOUNT_STUB + STOCK_PRICE_STUB * amt - 20;
            res.next();
            float actual = res.getFloat("balance");
            if (isEqual(expected, actual)) pass(test_name);
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
        float amt = 124.542f;
        try {
            DBInteraction.insertData("Deposit_Transactions", "(aid, amount)", String.format("%s,%f", M_AID_STUB, amt));
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
            return;
        }
        float actual = 0.000f;
        try {
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB + amt;
            if ( isEqual(expected, actual) ) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void WithdrawWithoutEnoughMoney() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        float amt = BALANCE_MARKET_ACCOUNT_STUB + 1000;
        try {
            DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", String.format("%s,%s", M_AID_STUB, amt));
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
        float amt = 100.125f;
        try {
            DBInteraction.insertData("Withdraw_Transactions", "(aid, amount)", String.format("%s, %f", M_AID_STUB, amt));
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
            return;
        }
        float actual = 0.000f;
        try {
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB - amt;
            if (isEqual(expected, actual)) pass(test_name);
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
            DBInteraction.insertData("Market_Accounts","(aid, taxid, balance)", String.format("%d, \"%s\", 10", Integer.parseInt(M_AID_STUB) + 1, UNAME_STUB));
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
            DBInteraction.insertData("Buy_Transactions","(m_aid, s_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f", M_AID_STUB, S_AID_STUB, SYM_STUB, 10f, BALANCE_MARKET_ACCOUNT_STUB + 1));
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
        float amt = 1.243f;
        try {
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f",S_AID_STUB, M_AID_STUB, SYM_STUB, amt, STOCK_PRICE_STUB));
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = %s", S_AID_STUB, SYM_STUB));
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
        float amt = 1.243f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "", String.format("%s,%s,%f", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f",S_AID_STUB, M_AID_STUB, SYM_STUB, amt, STOCK_PRICE_STUB));
            ResultSet res = DBInteraction.getData("balance", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = %s", S_AID_STUB, SYM_STUB));
            res.next();
            actual = res.getFloat("balance");
            float expected = BALANCE_IN_STOCK_ACC_STUB + amt;
            if (isEqual(expected, actual)) pass(test_name);
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
        float amt = 1.243f;
        try {
            DBInteraction.insertData("In_Stock_Acc", "", String.format("%s,%s,%f", SYM_STUB, S_AID_STUB, BALANCE_IN_STOCK_ACC_STUB));
            DBInteraction.insertData("Buy_Transactions", "(s_aid, m_aid, sym, amount, pps)", String.format("%s,%s,%s,%f,%f",S_AID_STUB, M_AID_STUB, SYM_STUB, amt, STOCK_PRICE_STUB));
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB - amt * STOCK_PRICE_STUB - 20;
            if (isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AccrueTriggerTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        DBInteraction.setupTestDB();
        float actual = 0.0f;
        try {
            DBInteraction.insertData("Accrue_Interest_Transactions", "(aid)", M_AID_STUB);
            ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB + ((RUNNING_BALANCE_SUM_STUB / DAY_OF_THE_MONTH_STUB) * 0.03f);
            if (isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
