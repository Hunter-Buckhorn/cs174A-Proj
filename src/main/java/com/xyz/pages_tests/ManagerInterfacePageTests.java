package com.xyz.pages_tests;

import com.xyz.DBInteraction;
import com.xyz.Test_Utilities;
import com.xyz.TimeManager;
import com.xyz.pages.ManagerInterfacePages;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;

public class  ManagerInterfacePageTests extends ManagerInterfacePages {

    public static void main(String[] args) {
        Add_Interest_Test();
        List_Active_Customers_Success_Test_Buy_Only();
        List_Active_Customers_Success_Test_Sell_Only();
        List_Active_Customers_Success_Test_Mixed();
        List_Active_Customers_Fail_Test();
        Generate_DTER_Test_Interest_Only();
        Generate_DTER_Test_Sell_Only();
        Generate_DTER_Test_Mixed();
        Generate_DTER_Test_Fail_Empty();
        Generate_DTER_Test_Fail_Due_To_Commission();
        Generate_Customer_Report_MA_Test();
        Generate_Customer_Report_SA_Test();
        Delete_Transactions_Test();
        Generate_Monthly_Statement_Earnings_Test_Only_Interest();
        Generate_Monthly_Statement_Earnings_Test_Only_Sell();
        Generate_Monthly_Statement_Earnings_Test_Mixed();
        Generate_Monthly_Statement_Test_Deposit();
        Generate_Monthly_Statement_Test_Withdraw();
        Generate_Monthly_Statement_Test_Accrue_Interest();
        Generate_Monthly_Statement_Test_Buy();
        Generate_Monthly_Statement_Test_Sell();
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        Initiate(UNAME_STUB, TAXID_STUB);
        TimeManager.Initialize(DATE_STUB);
    }

    private static void Add_Interest_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            Add_Interest_Helper(DATE_STUB);
            ResultSet res = DBInteraction.getData("balance","Market_Accounts", String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            float actual = res.getFloat("balance");
            float expected = BALANCE_MARKET_ACCOUNT_STUB + ((RUNNING_BALANCE_SUM_STUB / DAY_OF_THE_MONTH_STUB) * 0.03f);
            if (isEqual(expected, actual)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_SuccessTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // insert data into the database





    }

    private static void List_Active_Customers_Success_Test_Buy_Only() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 600f, 0.001f, DATE_STUB)
            );
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 400f, 0.001f, DATE_STUB)
            );
        } catch (SQLException e) {
            fail(test_name, "SET UP Problem");
            e.printStackTrace();
            return;
        }
        try {
            ResultSet res = List_Active_Customers_Helper(DATE_STUB);
            if (res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void List_Active_Customers_Success_Test_Sell_Only() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            InsertStubIntoIn_Stock_Acc(1500f);
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, s_pps, date, b_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\",%f", M_AID_STUB, S_AID_STUB, SYM_STUB, 1000f, 0.001f, DATE_STUB, STOCK_BUYING_PRICE_STUB));
        } catch (SQLException e) {
            fail(test_name, "SET UP Problem");
            e.printStackTrace();
            return;
        }
        try {
            ResultSet res = List_Active_Customers_Helper(DATE_STUB);
            if (res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void List_Active_Customers_Success_Test_Mixed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 200f, 0.001f, DATE_STUB)
            );
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 300f, 0.001f, DATE_STUB)
            );
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, s_pps, date, b_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\",%f", M_AID_STUB, S_AID_STUB, SYM_STUB, 500f, 0.001f, DATE_STUB, 0.001f));
        } catch (SQLException e) {
            fail(test_name, "SET UP Problem");
            e.printStackTrace();
            return;
        }
        try {
            ResultSet res = List_Active_Customers_Helper(DATE_STUB);
            if (res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static  void List_Active_Customers_Fail_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = List_Active_Customers_Helper(DATE_STUB);
            if (res.next()) fail(test_name, "");
            else pass(test_name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void Generate_Customer_Report_MA_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = Generate_Customer_Report_Helper_MA(UNAME_STUB);
            res.next();
            if(res.getString("aid").equals(M_AID_STUB) &&
                    isEqual(res.getFloat("balance"), BALANCE_MARKET_ACCOUNT_STUB)) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Customer_Report_SA_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            Test_Utilities.InsertStubIntoIn_Stock_Acc(null);
            ResultSet res = Generate_Customer_Report_Helper_SA(UNAME_STUB);
            res.next();
            if(res.getString("aid").equals(S_AID_STUB) &&
                    res.getString("sym").equals(SYM_STUB) &&
                    isEqual(res.getFloat("balance"), BALANCE_IN_STOCK_ACC_STUB)) pass(test_name);
            else fail(test_name, "");
         } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }

    }

    private static void Delete_Transactions_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            Delete_Transactions_Helper();
            // NO ERRORS WHEN DROP TABLES
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_DTER_Test_Interest_Only() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // SET UP
        try {
            DBInteraction.updateData("Market_Accounts", "running_balance_sum = 10000001", String.format("WHERE aid = %s", M_AID_STUB));
            Add_Interest_Helper(DATE_STUB);
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_DTER_Test_Sell_Only() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // SET UP
        try {
            float amt = 1;
            InsertStubIntoIn_Stock_Acc(null);
            DBInteraction.insertData("Sell_Transactions",
                    "(m_aid, s_aid, sym, b_pps, amount, s_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, %f",
                            M_AID_STUB, S_AID_STUB,
                            SYM_STUB, STOCK_BUYING_PRICE_STUB,
                            amt, STOCK_BUYING_PRICE_STUB + 10001f));
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }


    private static void Generate_DTER_Test_Mixed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // SET UP
        try {
            ResultSet res = DBInteraction.getData("running_balance_sum, day_of_the_month",
                    "Market_Accounts",
                    String.format("WHERE aid = %s", M_AID_STUB));
            res.next();
            float amt = (10001f - res.getFloat(1) / res.getFloat(2) * 0.03f) / (STOCK_PRICE_STUB - STOCK_BUYING_PRICE_STUB);
            Add_Interest_Helper(DATE_STUB);
            InsertStubIntoIn_Stock_Acc(1000000f);
            DBInteraction.insertData("Sell_Transactions",
                    "(m_aid, s_aid, sym, b_pps, amount, s_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, %f",
                            M_AID_STUB, S_AID_STUB,
                            SYM_STUB, STOCK_BUYING_PRICE_STUB,
                            amt, STOCK_PRICE_STUB));
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_DTER_Test_Fail_Empty() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) fail(test_name, "");
            else pass(test_name);
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_DTER_Test_Fail_Due_To_Commission() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // SET UP
        try {
            float amt = 1;
            InsertStubIntoIn_Stock_Acc(null);
            DBInteraction.insertData("Sell_Transactions",
                    "(m_aid, s_aid, sym, b_pps, amount, s_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, %f",
                            M_AID_STUB, S_AID_STUB,
                            SYM_STUB, STOCK_BUYING_PRICE_STUB,
                            amt, STOCK_BUYING_PRICE_STUB + 10019f));
            ChargeCommission(M_AID); // Simulate Commision From Sell
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) fail(test_name,"");
            else pass(test_name);
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_Earnings_Test_Only_Interest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();

        // SET UP
        try {
            Add_Interest_Helper(DATE_STUB);
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        float earnings = Generate_Monthly_Statement_Earnings(M_AID_STUB);
        float expected = RUNNING_BALANCE_SUM_STUB / (float) DAY_OF_THE_MONTH_STUB * 0.03f;
        if (isEqual(earnings, expected)) pass(test_name);
        else fail(test_name, "");
    }

    private static void Generate_Monthly_Statement_Earnings_Test_Only_Sell() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 6f;

        // SET UP
        try {
            InsertStubIntoIn_Stock_Acc(null);
            DBInteraction.insertData("Sell_Transactions",
                    "(m_aid, s_aid, sym, b_pps, amount, s_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, %f",
                            M_AID_STUB, S_AID_STUB,
                            SYM_STUB, STOCK_BUYING_PRICE_STUB,
                            amt, STOCK_PRICE_STUB));
            ChargeCommission(M_AID); // Simulate Commision From Sell
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        float earnings = Generate_Monthly_Statement_Earnings(M_AID_STUB);
        float expected = (STOCK_PRICE_STUB - STOCK_BUYING_PRICE_STUB) * amt - 20;
        if (isEqual(earnings, expected)) pass(test_name);
        else fail(test_name, "");
    }

    private static void Generate_Monthly_Statement_Earnings_Test_Mixed() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 6f;

        // SET UP
        try {
            Add_Interest_Helper(DATE_STUB);
            InsertStubIntoIn_Stock_Acc(null);
            DBInteraction.insertData("Sell_Transactions",
                    "(m_aid, s_aid, sym, b_pps, amount, s_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, %f",
                            M_AID_STUB, S_AID_STUB,
                            SYM_STUB, STOCK_BUYING_PRICE_STUB,
                            amt, STOCK_PRICE_STUB));
            ChargeCommission(M_AID); // Simulate Commision From Sell
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        float earnings = Generate_Monthly_Statement_Earnings(M_AID_STUB);
        float expected_from_sell = (STOCK_PRICE_STUB - STOCK_BUYING_PRICE_STUB) * amt - 20;
        float expected_from_Interest = RUNNING_BALANCE_SUM_STUB / (float) DAY_OF_THE_MONTH_STUB * 0.03f;
        if (isEqual(earnings, expected_from_sell + expected_from_Interest)) pass(test_name);
        else fail(test_name, "");
    }

    private static void Generate_Monthly_Statement_Test_Deposit() {
        // SET UP
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 200f;
        try {
            DBInteraction.insertData("Deposit_Transactions", "(aid, date, amount)", String.format("%s, \'%s\', %f", M_AID_STUB, DATE_STUB, amt));
            DBInteraction.insertData("Deposit_Transactions", "(aid, date, amount)", String.format("%s, \'%s\', %f", M_AID_STUB, DATE_STUB, amt));
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_Monthly_Statement_Simple_Trans(M_AID_STUB, "Deposit_Transactions");
            res.next(); int tid1 = res.getInt("tid"); int exp1 = 1;
            res.next(); int tid2 = res.getInt("tid"); int exp2 = 2;
            if (tid1 == exp1 && tid2 == exp2) pass(test_name);
            if (tid1 != exp1 && tid2 == exp2) fail(test_name, "First Transaction Returned False");
            if (tid1 == exp1 && tid2 != exp2) fail(test_name, "Second Transaction Returned False");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_Test_Withdraw() {
        // SET UP
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        float amt = 0.001f;
        try {
            DBInteraction.insertData("Withdraw_Transactions", "(aid, date, amount)", String.format("%s, \'%s\', %f", M_AID_STUB, DATE_STUB, amt));
            DBInteraction.insertData("Withdraw_Transactions", "(aid, date, amount)", String.format("%s, \'%s\', %f", M_AID_STUB, DATE_STUB, amt));
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_Monthly_Statement_Simple_Trans(M_AID_STUB, "Withdraw_Transactions");
            res.next(); int tid1 = res.getInt("tid"); int exp1 = 1;
            res.next(); int tid2 = res.getInt("tid"); int exp2 = 2;
            if (tid1 == exp1 && tid2 == exp2) pass(test_name);
            if (tid1 != exp1 && tid2 == exp2) fail(test_name, "First Transaction Returned False");
            if (tid1 == exp1 && tid2 != exp2) fail(test_name, "Second Transaction Returned False");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_Test_Accrue_Interest() {
        // SET UP
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            Add_Interest_Helper(DATE_STUB);
        } catch (SQLException e) {
            fail(test_name, "SET UP ERROR");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_Monthly_Statement_Simple_Trans(M_AID_STUB, "Accrue_Interest_Transactions");
            res.next();
            int tid1 = res.getInt("tid");
            int exp1 = 1;
            if (tid1 == exp1) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_Test_Buy() {
        // SET UP
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 600f, 0.001f, DATE_STUB)
            );
            DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\"", M_AID_STUB, S_AID_STUB, SYM_STUB, 400f, 0.001f, DATE_STUB)
            );
        } catch (SQLException e) {
            fail(test_name, "SET UP Problem");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_Monthly_Statement_Comp_Trans(M_AID_STUB, "Buy_Transactions");
            res.next(); int tid1 = res.getInt("tid"); int exp1 = 1;
            res.next(); int tid2 = res.getInt("tid"); int exp2 = 2;
            if (tid1 == exp1 && tid2 == exp2) pass(test_name);
            if (tid1 != exp1 && tid2 == exp2) fail(test_name, "First Transaction Returned False");
            if (tid1 == exp1 && tid2 != exp2) fail(test_name, "Second Transaction Returned False");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_Monthly_Statement_Test_Sell() {
        // SET UP
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
        try {
            InsertStubIntoIn_Stock_Acc(1500f);
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, s_pps, date, b_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\",%f", M_AID_STUB, S_AID_STUB, SYM_STUB, 200f, 0.001f, DATE_STUB, STOCK_BUYING_PRICE_STUB));
            DBInteraction.insertData("Sell_Transactions", "(m_aid, s_aid, sym, amount, s_pps, date, b_pps)",
                    String.format("%s, %s, \"%s\", %f, %f, \"%s\",%f", M_AID_STUB, S_AID_STUB, SYM_STUB, 300f, 0.001f, DATE_STUB, STOCK_BUYING_PRICE_STUB));

        } catch (SQLException e) {
            fail(test_name, "SET UP Problem");
            e.printStackTrace();
            return;
        }

        // EXECUTION
        try {
            ResultSet res = Generate_Monthly_Statement_Comp_Trans(M_AID_STUB, "Sell_Transactions");
            res.next(); int tid1 = res.getInt("tid"); int exp1 = 1;
            res.next(); int tid2 = res.getInt("tid"); int exp2 = 2;
            if (tid1 == exp1 && tid2 == exp2) pass(test_name);
            if (tid1 != exp1 && tid2 == exp2) fail(test_name, "First Transaction Returned False");
            if (tid1 == exp1 && tid2 != exp2) fail(test_name, "Second Transaction Returned False");
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
