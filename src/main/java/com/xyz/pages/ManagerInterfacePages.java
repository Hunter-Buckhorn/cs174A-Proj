package com.xyz.pages;

import com.xyz.DBInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class ManagerInterfacePages extends CustomerInterfacePage {
    protected static final String NEW_LINE = System.getProperty("line.separator");
    protected static void success() {
        System.out.println("SUCCESS!");
    }
    private static Scanner in;
    private static String DATE;


    public static void Start() {
        in = new Scanner(System.in);
        UserInputLoop: while(true) {
            printInstructions();
            switch (in.nextInt()) {
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
                    //Log_Out();
                    break;
                case 8:
                    //Switch_Interface();
                    break;
                case 9: //Exit
                    break UserInputLoop;
                default:
                    break;
            }
        }
    }

    private static void printInstructions() {
        System.out.println(
                "1: Add Interest" + NEW_LINE +
                "2: Generate Monthly Statement" + NEW_LINE +
                "3: List Active Customers" + NEW_LINE +
                "4: Generate DTER" + NEW_LINE +
                "5: Generate Customer Report" + NEW_LINE +
                "6: Delete Transactions" + NEW_LINE +
                "7: Log Out" + NEW_LINE +
                "8: Switch Interface" + NEW_LINE +
                "9: Exit"
        );
    }

    public static void setDate(String date) {
        DATE = date;
    }


    // Assumes it is only called at the end of the month, doesn't calculate by day
    private static void Add_Interest() {
        try {
            Add_Interest_Helper(DATE);
            success();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void Add_Interest_Helper(String date) throws SQLException {
        ResultSet res = DBInteraction.getData("aid", "Market_Accounts", "");
        while(res.next()) {
            DBInteraction.insertData("Accrue_Interest_Transactions", "(aid, date)", String.format("%s, \"%s\"", res.getString("aid"), date));
        }
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
            System.out.println(String.format("Market Account (%s) balance: %f, initial balance: %f", marketAid, marketBal, marketInitBal));
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
            System.out.println(String.format("%10s %10s %10s", "(tid)", "(amount)", "(date)"));
            while(Trans.next()) {
                int tid = Trans.getInt("tid");
                float amount = Trans.getFloat("amount");
                String date = Trans.getString("date");

                System.out.println(String.format("%10d %10f %10s", tid, amount, date));
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

    protected static ResultSet Generate_Monthly_Statement_Earnings(String m_aid) throws SQLException {
        return DBInteraction.executeQuery("/queries/MonthlyStatementEarnings.sql", m_aid, m_aid);
    }

    private static void Print_Earnings(ResultSet earningsRes) {
        try {
            earningsRes.next();
            System.out.println(String.format("Earnings: %f", earningsRes.getFloat(1)));
        } catch (SQLException e) {
            System.out.println("Error in Printing Earnings");
            e.printStackTrace();
        }
    }

    // helpers used in Generate_Monthly_Statement_Helper
    private static void Print_Buy_Transaction(ResultSet BuyTransRS) {
        try {
            System.out.println("Buy Transactions:");
            System.out.println(String.format("%10s %10s %10s %10s %10s", "(tid)", "(sym)", "(amount)", "(price per share)", "(date)"));
            while (BuyTransRS.next()) {
                int tid = BuyTransRS.getInt("tid");
                String sym = BuyTransRS.getString("sym");
                float amt = BuyTransRS.getFloat("amount");
                float pps = BuyTransRS.getFloat("pps");
                String date = BuyTransRS.getDate("date").toString();
                System.out.println(String.format("%10d %10s %10f %10f %10s", tid, sym, amt, pps, date));
            }
        } catch(SQLException e) {
            System.out.println(String.format("Error in Print_Buy_Transaction, error msg: ", e.getMessage()));
        }
    }
    private static void Print_Sell_Transaction(ResultSet SellTransRS) {
        try {
            System.out.println("Sell Transactions:");
            System.out.println(String.format("%10s %10s %10s %10s %10s %10s %10s", "(tid)", "(sym)", "(amount)", "(bought price per share)", "(sell price per share)", "(earnings)", "(date)"));
            while (SellTransRS.next()) {
                int tid = SellTransRS.getInt("tid");
                String sym = SellTransRS.getString("sym");
                float amt = SellTransRS.getFloat("amount");
                float s_pps = SellTransRS.getFloat("s_pps");
                float b_pps = SellTransRS.getFloat("b_pps");
                float e = SellTransRS.getFloat("earnings");
                String earnings = e > 0 ? String.format("+%f", e) : String.format("%f", e); // if earnings is positive, prepend "+"
                String date = SellTransRS.getDate("date").toString();
                System.out.println(String.format("%10d %10s %10f %10f %10f %10s %10s", tid, sym, amt, b_pps, s_pps, earnings, date));
            }
        } catch(SQLException e) {
            System.out.println(String.format("Error in Print_Buy_Transaction, error msg: ", e.getMessage()));
        }
    }

    private static void Generate_DTER() {
        // each customer who earns more than 10k each month
        // earnings are stored in sell transactions
        // interest is calculated on the market account total
        try {
            ResultSet res = Generate_DTER_Helper();
            while (res.next()) {
                String uname = res.getString("uname");
                String state = res.getString("state");
                System.out.println(String.format("%s, %s", uname, state));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet Generate_DTER_Helper() throws SQLException {
        return DBInteraction.executeQuery("queries/DTER.sql");
    }

    //            ResultSet MarketAccounts = DBInteraction.getData("aid, taxid", "Market_Accounts", "");
//        try {
//
//            System.out.println(String.format("%30s %20s %5s", "(name)", "(username)", "(state)"));
//
//            while (MarketAccounts.next()) {
//                int marketAid = MarketAccounts.getInt("aid");
//                int CustomerTaxid = MarketAccounts.getInt("taxid");
//                // get all sell trans and sum earnings
//                // todo: is this the right date var ?
//                ResultSet SellTrans = DBInteraction.getData("earnings", "Sell_Transactions", String.format("WHERE m_aid = %d AND month(date) = \"%s\"", marketAid, DATE.substring(5,7)));
//                double totalEarnings = 0;
//                while(SellTrans.next()) {
//                    totalEarnings += SellTrans.getFloat("earnings");
//                }
//
//                // if the interest has been calculated already?
//                ResultSet AccrIntr = DBInteraction.getData("amount", "Accrue_Interest_Transactions", String.format("WHERE aid = %d AND month(date) = \"%s\"", marketAid, DATE.substring(5,7)));
//
//                if (AccrIntr.next()) {
//                    totalEarnings += AccrIntr.getFloat("amount");
//                }
//
//                if (totalEarnings > 10000) {
//                    // get the customer info with taxid
//                    ResultSet Customer = DBInteraction.getData("uname, name, state", "Customers", String.format("WHERE taxid = \"%d\"", CustomerTaxid));
//                    String Cuname = Customer.getString("uname");
//                    String Cname = Customer.getString("name");
//                    String Cstate = Customer.getString("state");
//
//                    System.out.println(String.format("%30s %20s %5s", Cname, Cuname, Cstate));
//                }
//
//
//            }
//        } catch (SQLException e) {
//            System.out.print(String.format("Error in Generate_DTER, error msg: ", e.getMessage()));
//        }
//    }



    private static void List_Active_Customers() {
        try {
            ResultSet res = List_Active_Customers_Helper(DATE);
            while (res.next()) {
                System.out.println(String.format("%s: %s", res.getString("uname"), res.getString("tot")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet List_Active_Customers_Helper(String date) throws SQLException {
        return DBInteraction.executeQuery("queries/ListActiveUsers.sql", date, date);
    }

    private static void Delete_Transactions() {
        try {
            Delete_Transactions_Helper();
            success();
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
