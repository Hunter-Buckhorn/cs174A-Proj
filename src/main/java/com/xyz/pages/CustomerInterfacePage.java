package com.xyz.pages;

import com.xyz.DBInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerInterfacePage {
    protected static final String NEW_LINE = System.getProperty("line.separator");
    protected static String UNAME;
    protected static String M_AID;
    protected static String S_AID;
    protected static String DATE;
    protected static boolean IS_ADMIN;
    protected static Scanner in;

    protected static void success() {
        System.out.println("SUCCESS!");
    }

    public static void Start(String uname, String date) {
        Initiate(uname);
        setDate(date);
        in = new Scanner(System.in);
        userInputLoop: while(true) {
            PrintInstructions();
            switch (in.nextInt()) {
                case 1:
                    Deposit_Prompt();
                    break;
                case 2:
                    Withdraw_Prompt();
                    break;
                case 3:
                    Buy_Prompt();
                    break;
                case 4:
                    Sell_Prompt();
                    break;
                case 5:
                    ShowBalance();
                    break;
                case 6:
                    ShowTransactionHistoryOnStock();
                    break;
                case 7:
                    ListStockPricePrompt();
                    break;
                case 8:
                    break;
                case 9:
                    break userInputLoop;
                case 10:
                    break;
                default:
                    break;
            }
        }
    }

    protected static void PrintInstructions() {
        System.out.println("Choose 1 of the following instructions");
        System.out.println(
                "1: Deposit" + NEW_LINE +
                "2: Withdraw" + NEW_LINE +
                "3: Buy" + NEW_LINE +
                "4: Sell" + NEW_LINE +
                "5: Show Balance" + NEW_LINE +
                "6: Show Transactions" + NEW_LINE +
                "7: List Current Price of Stock" + NEW_LINE +
                "8: List Movie information" + NEW_LINE +
                "9: Exit"
         );
        if (IS_ADMIN) {
            System.out.println("10: Switch Interface");
        }
    }

    protected static void Initiate(String uname) {
        UNAME = uname;
        setM_AID();
        setS_AID();
        setIS_ADMIN();
    }

    protected static void setM_AID() {
        try {
            ResultSet res = DBInteraction.getData("aid", "Market_Accounts", String.format("WHERE uname = \"%s\"", UNAME));
            res.next();
            M_AID = res.getString("aid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void setS_AID() {
        try {
            ResultSet res = DBInteraction.getData("aid", "Stock_Accounts", String.format("WHERE uname = \"%s\"", UNAME));
            res.next();
            S_AID = res.getString("aid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void setIS_ADMIN() {
        try {
            ResultSet res = DBInteraction.getData("is_admin", "Customers", String.format("WHERE uname = \"%s\"", UNAME));
            res.next();
            IS_ADMIN = res.getBoolean("is_admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDate(String date) {
        DATE = date;
    }

    private static void Deposit_Prompt() {
        System.out.println("How much do you want to deposit?");
        Deposit(in.nextFloat());
    }

    private static void Deposit(float amt) {
        try {
            Deposit_Helper(amt);
            success();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static void Deposit_Helper(float amt) throws SQLException {
        DBInteraction.insertData("Deposit_Transactions", "", String.format("0, %s, \'%s\', %f", M_AID, DATE, amt));
    }

    private static void Withdraw_Prompt() {
        System.out.println("How much do you want to withdraw?");
        Withdraw(in.nextFloat());
    }

    private static void Withdraw(float amt) {
        try {
            Withdraw_Helper(amt);
            success();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void Buy_Prompt() {
        try {
            System.out.println("What do you want to buy?");
            String sym = in.next();
            System.out.println(String.format("Current Price is %s", getStockPrice(sym)));
            System.out.println("How many do you want to buy?");
            float amt = in.nextFloat();
            Buy(amt, sym);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void Withdraw_Helper(float amt) throws SQLException {
        DBInteraction.insertData("Withdraw_Transactions", "", String.format("0, %s, \'%s\', %f", M_AID, DATE, amt));
    }

    private static void Buy(float amt, String sym) {
        try {
            Buy_Helper(amt, sym);
            success();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static void Buy_Helper(float amt, String sym) throws SQLException {
        float pps = getStockPrice(sym);
        DBInteraction.insertData("Buy_Transactions", "", String.format("0, %s, %s, %s, %.3f, %f, \'%s\'", M_AID, S_AID, sym, amt, pps, DATE));
    }

    private static void Sell_Prompt() {
        try {
            System.out.println("What do you want to sell?");
            String sym = in.next();
            System.out.println(String.format("Current Price is %s", getStockPrice(sym)));
            System.out.println("How many do you want to sell?");
            float amt = in.nextFloat();
            Sell(amt, sym);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void Sell(float amt, String sym) {
        try {
            Sell_Helper(amt, sym);
            success();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static void Sell_Helper(float amt, String sym) throws SQLException {
        float pps = getStockPrice(sym);
        DBInteraction.insertData("Sell_Transactions", "(tid, m_aid, s_aid, sym, amount, pps, date)", String.format("NULL, %s, %s, %s, %.3f, %f, \'%s\'", M_AID, S_AID, sym, amt, pps, DATE));
    }

    protected static float getStockPrice(String sym) throws SQLException {
        ResultSet res = DBInteraction.getData("price", "Stock_Profiles", String.format("WHERE sym = %s", sym));
        res.next();
        return res.getFloat("price");
    }

    private static void ShowBalance() {
        try {
            System.out.println(String.format("Your Market Account Balance is: %s", getBalance()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static String getBalance() throws SQLException {
        ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE uname = \"%s\"", UNAME));
        res.next();
        return res.getString("balance");
    }

    private static void ShowTransactionHistoryOnStock() {
        ShowTransactionHistoryOnStockHelper(true);
        ShowTransactionHistoryOnStockHelper(false);
    }

    private static void ShowTransactionHistoryOnStockHelper(boolean isBuy) {
        try {
            ResultSet res = getStockTransactions(isBuy);
            while(res.next()) {
                String action = isBuy ? "Buy" : "Sell";
                String amount = res.getString("amount");
                String sym = res.getString("sym");
                String date = res.getString("date");
                String pps = res.getString("pps");
                System.out.println(String.format("%s: %s %s of %s as %s per share", date, action, amount, sym, pps));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet getStockTransactions(boolean isBuy) throws SQLException {
        return DBInteraction.getData("*", isBuy ? "Buy_Transactions" : "Sell_Transactions", String.format("WHERE s_aid = %s", S_AID));
    }

    private static void ListStockPricePrompt() {
        System.out.println("What stock do you want to check?");
        listPriceOf(in.next());
    }

    protected static void listPriceOf(String sym) {
        try {
            System.out.println(String.format("Price of %s: %s", sym, getStockPrice(sym)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
