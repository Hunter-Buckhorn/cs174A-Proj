package com.xyz.pages_tests;

import com.xyz.DBInteraction;
import com.xyz.Test_Utilities;
import com.xyz.pages.ManagerInterfacePages;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.*;

public class  ManagerInterfacePageTests extends ManagerInterfacePages {

    public static void main(String[] args) {
        Add_Interest_Test();
        //Generate_Monthly_Statement_SuccessTest();
        List_Active_Customers_Success_Test();
        List_Active_Customers_Fail_Test();
        Generate_DTER_Test();
        Generate_Customer_Report_MA_Test();
        Generate_Customer_Report_SA_Test();
        Delete_Transactions_Test();
    }

    private static void setUp() {
        DBInteraction.setupTestDB();
        Initiate(UNAME_STUB, TAXID_STUB);
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

    private static void List_Active_Customers_Success_Test() {
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
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void Generate_DTER_Test() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        setUp();
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
        try {
            ResultSet res = Generate_DTER_Helper();
            if(res.next()) pass(test_name);
            else fail(test_name, "");
        } catch (SQLException e) {
            fail(test_name,e.getMessage());
            e.printStackTrace();
        }
    }
}
