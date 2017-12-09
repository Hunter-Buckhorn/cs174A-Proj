package com.xyz.pages;

import com.xyz.DBInteraction;
import com.xyz.TimeManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xyz.App.*;
import static com.xyz.pages.LoginPage.InvalidateSession;
import static com.xyz.pages.Page_Utilities.*;

public class ManagerInterfacePages extends CustomerInterfacePage {
    // all vars imported from CustomerInterfacePage

    public static void Start(String uname, String taxid, String date) {
        Initiate(uname, taxid);
        Show_Menu_Prompt();
        UserInputLoop: while(true) {
            switch (getUserInt()) {
                case 0:
                    Show_Menu_Prompt();
                    break;
                case 1:
                    Add_Interest();
                    break;
                case 2:
                    Generate_Monthly_Statement_Prompt();
                    break;
                case 3:
                    List_Active_Customers();
                    break;
                case 4:
                    Generate_DTER();
                    break;
                case 5:
                    Generate_Customer_Report_Prompt();
                    break;
                case 6:
                    Delete_Transactions();
                    break;
                case 7:
                    InvalidateSession();
                    break UserInputLoop;
                case 8:
                    System.out.println("MANAGER INTERFACE");
                    MANAGER_INTERFACE = false;
                    CUSTOMER_INTERFACE = true;
                    break UserInputLoop;
                case 9:
                    System.out.println("Opening Time Manager...");
                    EnterTimeManagerPrompt();
                    System.out.println("MANAGER INTERFACE");
                    Show_Menu_Prompt();
                    break;
                case 10: // Exit
                    EXIT = true;
                    break UserInputLoop;
                default:
                    break;
            }
        }
    }


    protected static void Show_Menu_Prompt() {
        System.out.println("Choose 1 of the following instructions");
        System.out.println(
                "0: Redisplay Menu" + NEW_LINE +
                        "1: Add Interest" + NEW_LINE +
                        "2: Generate Monthly Statement" + NEW_LINE +
                        "3: List Active Customers" + NEW_LINE +
                        "4: Generate DTER" + NEW_LINE +
                        "5: Generate Customer Report" + NEW_LINE +
                        "6: Delete Transactions" + NEW_LINE +
                        "7: Log Out" + NEW_LINE +
                        "8: Switch Interface" + NEW_LINE +
                        "9: Enter Time Manager" + NEW_LINE +
                        "10: Exit"
        );
    }

    // Assumes it is only called at the end of the month, doesn't calculate by day
    private static void Add_Interest() {
        try {
            Add_Interest_Helper(TimeManager.DATE);
            success();
        } catch (SQLException e) {
            System.out.println("Error in AddInterest()");
            e.printStackTrace();
        }
    }

    protected static void Add_Interest_Helper(String date) throws SQLException {
        ResultSet res = DBInteraction.getData("aid", "Market_Accounts", "");
        while(res.next()) {
            DBInteraction.insertData("Accrue_Interest_Transactions", "(aid, date)", String.format("\"%s\", \"%s\"", res.getString("aid"), date));
        }
    }

    private static void EnterTimeManagerPrompt() {
        System.out.println("Time Manager:");
        TimeManager.Start();

    }

    private static void Generate_Monthly_Statement_Prompt() {
        System.out.println("Enter Customer Username:");
        String uname = in.next();
        try {
            // Get Customer
            ResultSet CustomersRes = Generate_Monthly_Statement_Customers(uname);
            CustomersRes.next();
            // name, email address
            String cName = CustomersRes.getString("name");
            String cEmail = CustomersRes.getString("email");
            String cTaxId = CustomersRes.getString("taxid");
            System.out.println(String.format("Customer: %s, email: %s", cName, cEmail));

            // Get Market Account with taxid
            // Print its current balance
            ResultSet MarketRes = Generate_Monthly_Statement_MA(cTaxId);
            MarketRes.next();
            String marketAid = MarketRes.getString("aid");
            float marketBal = MarketRes.getFloat("balance");
            float marketInitBal = MarketRes.getFloat("initial_balance");
            float tot_commission = MarketRes.getFloat("tot_commission");
            System.out.println(String.format("Market Account: tid: %s, balance: %f, initial balance: %f", marketAid, marketBal, marketInitBal));
            System.out.println(String.format("Total Commission: %f", tot_commission));

            // Print Deposit Transactions
            System.out.println("Deposits Transactions:");
            Print_Simple_Transactions(Generate_Monthly_Statement_Simple_Trans(marketAid, "Deposit_Transactions"), "Deposit");

            // Print Withdraw Transactions
            System.out.println("Withdraw Transactions:");
            Print_Simple_Transactions(Generate_Monthly_Statement_Simple_Trans(marketAid, "Withdraw_Transactions"), "Withdraw");

            // Print Accrue Interest Transactions
            System.out.println("Accrue Interest Transactions:");
            Print_Simple_Transactions(Generate_Monthly_Statement_Simple_Trans(marketAid, "Accrue_Interest_Transactions"), "Accrue_Interest");

            // Print Buy Transactions
            Print_Buy_Transaction(Generate_Monthly_Statement_Comp_Trans(marketAid, "Buy_Transactions"));

            // Print Sell Transactions
            Print_Sell_Transaction(Generate_Monthly_Statement_Comp_Trans(marketAid, "Sell_Transactions"));

            // Print Earnings
            Print_Earnings(Generate_Monthly_Statement_Earnings(marketAid));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet Generate_Monthly_Statement_Customers(String uname) throws SQLException {
        return DBInteraction.getData("name, email, taxid", "Customers", String.format("WHERE uname = \"%s\"", uname));
    }
    protected static ResultSet Generate_Monthly_Statement_MA(String taxid) throws SQLException {
        return DBInteraction.getData("aid, balance, initial_balance, tot_commission","Market_Accounts", String.format("WHERE taxid = %s", taxid));
    }
    protected static ResultSet Generate_Monthly_Statement_Simple_Trans(String m_aid, String table_name) throws SQLException {
        return DBInteraction.getData("tid, amount, date",
                table_name,
                String.format("WHERE aid = %s ORDER BY date", m_aid));
    }
    private static void Print_Simple_Transactions(ResultSet Trans, String kind_of_transaction) {
        if (!kind_of_transaction.toLowerCase().equals("deposit") &&
                !kind_of_transaction.toLowerCase().equals("withdraw") &&
                !kind_of_transaction.toLowerCase().equals("accrue_interest"))
        {
            System.out.println(String.format("Error in Print_%s_Transactions", kind_of_transaction));
            return;
        }
        try {
            System.out.println(String.format("%15s %15s %15s", "(tid)", "(amount)", "(date)"));
            while(Trans.next()) {
                int tid = Trans.getInt("tid");
                float amount = Trans.getFloat("amount");
                String date = Trans.getString("date");

                System.out.println(String.format("%15d %15f %15s", tid, amount, date));
            }

        } catch(SQLException e) {
            System.out.println(String.format("Error in Print_%s_Transaction, error msg: %s", kind_of_transaction, e.getMessage()));
        }
    }

    protected static ResultSet Generate_Monthly_Statement_Comp_Trans(String m_aid, String table_name) throws SQLException {
        return DBInteraction.getData("*",
                table_name,
                String.format("WHERE m_aid = %s ORDER BY date", m_aid));
    }

    protected static float Generate_Monthly_Statement_Earnings(String m_aid) {
        try {
            ResultSet r1 = Generate_Monthly_Statement_Earnings_From_Sell(m_aid);
            float sellEarnings = 0.0f;
            if (r1.next()) {
                sellEarnings = r1.getFloat(1);
            }
            ResultSet r2 = Generate_Monthly_Statement_Earnings_From_Interest(m_aid);
            float intEarnings = 0.0f;
            if (r2.next()) {
                intEarnings = r1.getFloat(1);
            }
            ResultSet r3 = Generate_Monthly_Statement_Earnings_Commission(m_aid);
            float comm = 0.0f;
            if (r3.next()) {
                comm = r3.getFloat(1);
            }
            return sellEarnings + intEarnings - comm;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0f;
        }
    }

    protected static ResultSet Generate_Monthly_Statement_Earnings_From_Sell(String m_aid) throws SQLException {
        return DBInteraction.executeQuery("/queries/GetTotSellEarnings.sql", m_aid);
    }

    protected static ResultSet Generate_Monthly_Statement_Earnings_From_Interest(String m_aid) throws SQLException {
        return DBInteraction.executeQuery("/queries/GetTotInterestEarnings.sql", m_aid);
    }

    protected static ResultSet Generate_Monthly_Statement_Earnings_Commission(String m_aid) throws SQLException {
        return DBInteraction.getData("tot_commission", "Market_Accounts", String.format("WHERE aid = %s", m_aid));
    }


    private static void Print_Earnings(float earningsRes) {
        System.out.println(String.format("Earnings: %.3f", earningsRes));
    }
    // helpers used in Generate_Monthly_Statement_Helper
    private static void Print_Buy_Transaction(ResultSet BuyTransRS) {
        try {
            System.out.println("Buy Transactions:");
            System.out.println(String.format("%15s %15s %15s %15s %15s", "(tid)", "(sym)", "(amount)", "(pps)", "(date)"));
            while (BuyTransRS.next()) {
                int tid = BuyTransRS.getInt("tid");
                String sym = BuyTransRS.getString("sym");
                float amt = BuyTransRS.getFloat("amount");
                float pps = BuyTransRS.getFloat("pps");
                String date = BuyTransRS.getDate("date").toString();
                System.out.println(String.format("%15d %15s %15f %15f %15s", tid, sym, amt, pps, date));
            }
        } catch(SQLException e) {
            System.out.println(String.format("Error in Print_Buy_Transaction, error msg: ", e.getMessage()));
        }
    }
    private static void Print_Sell_Transaction(ResultSet SellTransRS) {
        try {
            System.out.println("Sell Transactions:");
            System.out.println(String.format("%15s %15s %15s %15s %15s %15s %15s", "(tid)", "(sym)", "(amount)", "(bought pps)", "(sell pps)", "(earnings)", "(date)"));
            while (SellTransRS.next()) {
                int tid = SellTransRS.getInt("tid");
                String sym = SellTransRS.getString("sym");
                float amt = SellTransRS.getFloat("amount");
                float s_pps = SellTransRS.getFloat("s_pps");
                float b_pps = SellTransRS.getFloat("b_pps");
                float e = SellTransRS.getFloat("earnings");
                String earnings = e > 0 ? String.format("+%f", e) : String.format("%f", e); // if earnings is positive, prepend "+"
                String date = SellTransRS.getDate("date").toString();
                System.out.println(String.format("%15d %15s %15f %15f %15f %15s %15s", tid, sym, amt, b_pps, s_pps, earnings, date));
            }
        } catch(SQLException e) {
            System.out.println(String.format("Error in Print_Buy_Transaction, error msg: ", e.getMessage()));
        }
    }


    private static void Generate_DTER() {
        // each customer who earns more than 10k each month
        // earnings are stored in sell transactions
        // interest is calculated on the market account total
        System.out.println("Generating DTER:");
        try {
            boolean isEmpty = true;
            ResultSet res = Generate_DTER_Helper();
            while (res.next()) {
                isEmpty = false;
                String uname = res.getString("uname");
                String state = res.getString("state");
                System.out.println(String.format("%s, %s", uname, state));
            }
            if (isEmpty) {
                System.out.println("No customers have made more than $10,000 this month");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet Generate_DTER_Helper() throws SQLException {
        return DBInteraction.executeQuery("queries/DTER.sql");
    }



    private static void List_Active_Customers() {
        try {
            //System.out.println("In active customers");
            ResultSet res = List_Active_Customers_Helper(TimeManager.DATE);
            boolean isEmpty = true;
            while (res.next()) {
                isEmpty = false;
                System.out.println(String.format("%s: %s", res.getString("uname"), res.getString("totSharesDealt")));
            }
            if(isEmpty == true) {
                System.out.println("No active users");
            }
        } catch (SQLException e) {
            System.out.println(String.format("Error in List_Active_Customers: %s", e.getMessage()));
        }
    }

    protected static ResultSet List_Active_Customers_Helper(String date) throws SQLException {
        return DBInteraction.executeQuery("queries/ListActiveUsers.sql");
    }

    public static void Delete_Transactions() {
        try {
            System.out.println("Deleting Transactions");
            Delete_Transactions_Helper();
            System.out.println("Transactions Deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void Delete_Transactions_Helper() throws SQLException{
        DBInteraction.DropTable("Accrue_Interest_Transactions");
        DBInteraction.DropTable("Buy_Transactions");
        DBInteraction.DropTable("Sell_Transactions");
        DBInteraction.DropTable("Deposit_Transactions");
        DBInteraction.DropTable("Withdraw_Transactions");
        DBInteraction.executeAllStatementFilesIn(null, "schema/level_0/");
        DBInteraction.executeAllStatementFilesIn(null, "schema/level_0/level_1/");
        DBInteraction.executeAllStatementFilesIn(null, "schema/level_0/level_1/level_2/");
        DBInteraction.executeAllStatementFilesIn(null, "schema/level_0/level_1/level_2/level_3/");
        DBInteraction.executeAllStatementFilesIn(null, "triggers/");
    }

    // todo: add error checking for valid customer uname
    private static void Generate_Customer_Report_Prompt() {
        System.out.println("Customer username:");
        String uname = in.next();
        try {
            ResultSet res1 = Generate_Customer_Report_Helper_MA(uname);
            while (res1.next()) {
                System.out.println(String.format("Market_Acount_id: %s, balance: %f", res1.getString("aid"), res1.getFloat("balance")));
            }
            ResultSet res2 = Generate_Customer_Report_Helper_SA(uname);
            while (res2.next()) {
                System.out.println(String.format("Stock_Acount_id: %s, stock_id: %s: %f", res2.getString("aid"), res2.getString("sym"), res2.getFloat("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet Generate_Customer_Report_Helper_MA(String uname) throws SQLException {
        return DBInteraction.executeQuery("queries/CustomerReportMarketAccount.sql",uname);
    }

    protected static ResultSet Generate_Customer_Report_Helper_SA(String uname) throws SQLException {
        return DBInteraction.executeQuery("queries/CustomerReportStockAccount.sql",uname);
    }
}
