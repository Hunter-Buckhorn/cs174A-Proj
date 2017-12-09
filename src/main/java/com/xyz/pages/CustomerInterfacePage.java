package com.xyz.pages;

import com.xyz.DBInteraction;
import com.xyz.TimeManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static com.xyz.App.*;
import static com.xyz.DBInteraction.MOVIES_DB;
import static com.xyz.DBInteraction.TEST_DB;
import static com.xyz.TimeManager.isMarketClosed;
import static com.xyz.pages.LoginPage.InvalidateSession;
import static com.xyz.pages.Page_Utilities.*;

public class CustomerInterfacePage {
    protected static String UNAME = null;
    protected static String TAXID = null;
    protected static String M_AID = null;
    protected static String S_AID = null;
    protected static boolean IS_ADMIN;

    public static void Start(String uname, String taxid) {
        Initiate(uname, taxid);
        Show_Menu_Prompt();
        userInputLoop: while(true) {
            switch (getUserInt()) {
                case 0:
                    Show_Menu_Prompt();
                    break;
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
                    ListMovieInfoPrompt();
                    break;
                case 9:
                    ListTopMoviesPrompt();
                    break;
                case 10:
                    GetReviewsPrompt();
                    break;
                case 11:
                    System.out.println("Signed Out");
                    InvalidateSession();
                    CUSTOMER_INTERFACE = false;
                    MANAGER_INTERFACE = false;
                    break userInputLoop;
                case 12: // Exit
                    EXIT = true;
                    break userInputLoop;
                case 13:
                    if (IS_ADMIN) {
                        CUSTOMER_INTERFACE = false;
                        MANAGER_INTERFACE = true;
                        break userInputLoop;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected static void Show_Menu_Prompt() {
        System.out.println("Choose 1 of the following instructions");
        System.out.println(
                "0: Redisplay Menu" + NEW_LINE +
                        "1: Deposit" + NEW_LINE +
                        "2: Withdraw" + NEW_LINE +
                        "3: Buy" + NEW_LINE +
                        "4: Sell" + NEW_LINE +
                        "5: Show Balance" + NEW_LINE +
                        "6: Show Transactions" + NEW_LINE +
                        "7: List Current Price of Stock" + NEW_LINE +
                        "8: List Movie information" + NEW_LINE +
                        "9: List Top Movies" + NEW_LINE +
                        "10: Get Reviews" + NEW_LINE +
                        "11: Sign Out" + NEW_LINE +
                        "12: Exit"
        );
        if (IS_ADMIN) {
            System.out.println("13: Switch Interface");
        }
    }




    protected static void Initiate(String uname, String taxid) {
        UNAME = uname;
        TAXID = taxid;
        setM_AID();
        setS_AID();
        setIS_ADMIN();
    }

    protected static void setM_AID() {
        try {
            ResultSet res = DBInteraction.getData("aid", "Market_Accounts", String.format("WHERE taxid = \"%s\"", TAXID));
            if (res.next()) {
                M_AID = res.getString("aid");
            } else {
                CreateMarketAccountPrompt();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static boolean CreateMarketAccountPrompt() {
        String question = "You have no market account, would you like to create one? y/n";

        if (AskUserYesNoQuestion(question)) {
            float deposit = 0;
            while(true) {
                System.out.println("Choose initial deposit of at least $1000: ");
                deposit = getUserFloat();
                try {
                    M_AID = CreateMarketAccount(deposit, TAXID, TimeManager.DATE);
                    return true;
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
            }
        }
        M_AID = null;
        return false;
    }

    protected static String CreateMarketAccount(float balance, String taxid, String date) throws SQLException {
        DBInteraction.insertData("Market_Accounts", "(balance,initial_balance,taxid,day_of_the_month)", String.format("%f, %f, \"%s\", \"%s\"", balance, balance, taxid, date.split("-")[2]));
        ResultSet mAid = DBInteraction.getData("aid", "Market_Accounts", String.format("WHERE taxid = \"%s\"", TAXID));
        mAid.next();
        String aid =  mAid.getString("aid");
        DBInteraction.insertData("Deposit_Transactions", "(aid, date, amount)", String.format("\"%s\", \"%s\", %f", aid, date, balance));
        return aid;
    }

    protected static void setS_AID() {
        try {
            ResultSet res = DBInteraction.getData("aid", "Stock_Accounts", String.format("WHERE taxid = \"%s\"", TAXID));
            if(res.next()) {
                S_AID = res.getString("aid");
            } else {
                CreateStockAccountPrompt();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static boolean CreateStockAccountPrompt() {
        if (isMarketClosed()) {
            System.out.println("Cannot create stock account, market is closed");
            return false;
        } else {

            String question = "You have no stock account, would you like to create one? y/n";
            if (AskUserYesNoQuestion(question)) {
                while (true) {
                    try {
                        S_AID = CreateStockAccount(TAXID);
                        return true;
                    } catch (SQLException e) {
                        System.out.println(String.format("Error in CreateStockAccountPrompt(%s)", TAXID));
                        e.printStackTrace();
                        S_AID = null;
                        return false;
                    }
                }
            }
            S_AID = null;
            return false;
        }
    }

    protected static String CreateStockAccount(String taxid) throws SQLException {
        DBInteraction.insertData("Stock_Accounts", "(taxid)", TAXID);
        ResultSet sAid = DBInteraction.getData("aid", "Stock_Accounts", String.format("WHERE taxid = %s", TAXID));
        sAid.next();
        return sAid.getString("aid");
    }

    protected static void setIS_ADMIN() {
        try {
            ResultSet res = DBInteraction.getData("is_admin", "Customers", String.format("WHERE uname = \"%s\"", UNAME));
            if (res.next()) {
                IS_ADMIN = res.getBoolean("is_admin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static boolean hasMarketAccount() {
        return M_AID != null;
    }

    protected static boolean hasStockAccount() {
        return S_AID != null;
    }

    protected static void Deposit_Prompt() {
        if (isMarketClosed()) {
            System.out.println("Cannot deposit, market is closed");
            return;
        }
        // check if market account exists
        if (!hasMarketAccount()) {
            if(!CreateMarketAccountPrompt()) {
                System.out.println("You need a market account to deposit... exiting");
                return;
            }
        }
        // now has market account
        System.out.println("How much do you want to deposit?");
        float val = getUserFloat();
        if (val > 0) {
            Deposit(val);
        } else {
            System.out.println("Cannot deposit a negative value... exiting");
        }
    }
    protected static void Deposit(float amt) {
        // assumes is called by Deposit Prompt, adheres to assumptions
        try {
            Deposit_Helper(amt);
            success();
        } catch (SQLException e) {
            System.out.println(String.format("Error in Deposit(%f)", amt));
            e.printStackTrace();
        }
    }
    protected static void Deposit_Helper(float amt) throws SQLException {
        DBInteraction.insertData("Deposit_Transactions", "(aid, date, amount)", String.format("%s, \'%s\', %f", M_AID, TimeManager.DATE, amt));
    }

    protected static void Withdraw_Prompt() {
        if (isMarketClosed()) {
            System.out.println("Cannot Withdraw, market is closed");
            return;
        }
        if (!hasMarketAccount()) {
            if (!CreateMarketAccountPrompt()) {
                System.out.println("You need a market account to withdraw... exiting");
                return;
            }
        }
        // now has market account
        System.out.println("How much do you want to withdraw?");
        float val = getUserFloat();
        if (val > 0) {
            Withdraw(val);
        } else {
            System.out.println("Cannot withdraw a negative amount... exiting");
        }
    }
    protected static void Withdraw(float amt) {
        // assumes is called by Withdraw Prompt, adheres to assumptions
        try {
            Withdraw_Helper(amt);
            success();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    protected static void Withdraw_Helper(float amt) throws SQLException {
        DBInteraction.insertData("Withdraw_Transactions", "", String.format("0, %s, \'%s\', %f", M_AID, TimeManager.DATE, amt));
    }

    protected static void Buy_Prompt() {
        if (isMarketClosed()) {
            System.out.println("Cannot Buy, market is closed");
            return;
        }
        if (!hasMarketAccount()) {
            if (!CreateMarketAccountPrompt()) {
                System.out.println("Need Market Account to Buy Stocks... exiting");
                return;
            }
        }
        if (!hasStockAccount()) {
            if (!CreateStockAccountPrompt()) {
                System.out.println("Need Stock Account to Buy Stocks... exiting");
                return;
            }
        }
        // end check assumptions
        while (true) {
            System.out.println("What stock do you want to buy?");
            printAllStocksAvailable(null, null);
            String sym = in.next();
            if (isValidSym(sym)) {
                System.out.println(String.format("Current Price of \"%s\": $%s", sym, getStockPrice(sym)));
                System.out.println("How many shares do you want to buy?");
                float amt = getUserFloat();
                if (amt <= 0) {
                    System.out.println(String.format("Cannot buy %f number of shares, enter a valid number", amt));
                } else {
                    Buy(amt, sym);
                }
                if (AskUserYesNoQuestion("Done? y/n")) {
                    break;
                }
            }
        }
    }
    protected static void Buy(float amt, String sym) {
        try {
            float pps = getStockPrice(sym); // valid sym check built into getStockPrice
            Buy_Helper(amt, sym, pps);
            success();
        } catch (SQLException e) {
            System.out.println(String.format("Error in Buy(%f, %s)", amt, sym));
            e.printStackTrace();
        }
    }
    protected static void Buy_Helper(float amt, String sym, float pps) throws SQLException {
        DBInteraction.insertData("Buy_Transactions", "(m_aid, s_aid, sym, amount, pps, date)",
                String.format("%s, %s, \"%s\", %f, %f, \'%s\'", M_AID, S_AID, sym, amt, pps, TimeManager.DATE));
    }

    protected static void Sell_Prompt() {
        if (isMarketClosed()) {
            System.out.println("Cannot Sell, market is closed");
            return;
        }

        // need to have a market acct
        // need to have a stock acct
        // need to have enough shares of right stock in stock acct

        // Assumptions Check
        if (!hasMarketAccount()) {
            if (!CreateMarketAccountPrompt()) {
                System.out.println("Cannot Sell stocks without Market Account... exiting");
                return;
            }
        }
        if (!hasStockAccount()) {
            if (CreateStockAccountPrompt()) {
                System.out.println("Cannot Sell stocks without Stocks Account... exiting");
                return;
            }
        }
        // End Assumptions Check
        // now there is a valid market and stock account, continue

        while (true) {
            System.out.println("What stock do you want to sell?");
            String sym = in.next();
            if (isValidSym(sym) && isSymInAcc(sym, S_AID)) {
                // stock in account, continue
                System.out.println(String.format("Current Price of \"%s\" is $%s", sym, getStockPrice(sym)));
                Collection<Float> pps_had = new ArrayList<Float>();
                showCurrentStockOwned(sym, pps_had);

                float b_pps = -1f;

                System.out.println("Choose the buying price per share you would like to sell:");
                while(true) {
                    b_pps = getUserFloat();
                    if(pps_had.contains(b_pps)) break;
                    else System.out.println("You don't have this b_pps");
                }

                System.out.println("How many do you want to sell?");
                float numShares = getUserFloat();
                if (numShares <= 0) {
                    System.out.println(String.format("Cannot sell %f number of shares, enter a valid number", numShares));
                } else {
                    Sell(numShares, sym, b_pps);
                }

                if (AskUserYesNoQuestion("Done? y/n")) {
                    try {
                        ChargeCommission(M_AID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    protected static void Sell(float amt, String sym, float b_pps) {
        // assumes is being called from SellPrompt
        try {
            Sell_Helper(amt, sym, b_pps);
            success();
        } catch (SQLException e) {
            System.out.println(String.format("Error in Sell(%f, %s, %f): %s", amt, sym, b_pps, e.getMessage()));
            //e.printStackTrace();
        }
    }
    protected static void Sell_Helper(float amt, String sym, float b_pps) throws SQLException {
        float s_pps = getStockPrice(sym);
        DBInteraction.insertData("Sell_Transactions", "(tid, m_aid, s_aid, sym, amount, s_pps, date, b_pps)",
                String.format("NULL, %s, %s, \"%s\", %f, %f, \'%s\', %f", M_AID, S_AID, sym, amt, s_pps, TimeManager.DATE , b_pps));
    }


    protected static void showCurrentStockOwned(String sym, Collection<Float> collection) {
        try {
            ResultSet res = getCurrentStockOwned(sym, S_AID);
            System.out.println(String.format("pps: balance"));
            while(res.next()) {
                if (collection != null) collection.add(res.getFloat("pps"));
                System.out.println(String.format("%.3f: %.3f", res.getFloat("pps"), res.getFloat("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet getCurrentStockOwned(String sym, String s_aid) throws SQLException {
        return DBInteraction.getData("balance, pps", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = \"%s\"", s_aid, sym));
    }

    protected static void ChargeCommission(String m_AID) throws SQLException {
        DBInteraction.updateData("Market_Accounts", "balance = balance - 20", String.format("WHERE aid = %s", m_AID));
        DBInteraction.updateData("Market_Accounts", "tot_commission = tot_commission + 20", String.format("WHERE aid = %s", m_AID));
    }

    protected static void ShowBalance() {
        try {
            System.out.println(String.format("Your Market Account Balance is: %s", getBalance()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static String getBalance() throws SQLException {
        ResultSet res = DBInteraction.getData("balance", "Market_Accounts", String.format("WHERE taxid = \"%s\"", TAXID));
        res.next();
        return res.getString("balance");
    }

    protected static void ShowTransactionHistoryOnStock() {
        ShowTransactionHistoryOnStockHelper(true);
        ShowTransactionHistoryOnStockHelper(false);
    }

    protected static void ShowTransactionHistoryOnStockHelper(boolean isBuy) {
        try {
            ResultSet res = getStockTransactions(isBuy);
            boolean isEmpty = true;
            while(res.next()) {
                isEmpty = true;
                String action = isBuy ? "Buy" : "Sell";
                String amount = res.getString("amount");
                String sym = res.getString("sym");
                String date = res.getString("date");
                String pps = isBuy ? res.getString("pps") : res.getString("s_pps");
                System.out.println(String.format("%s: %s %s of %s as %s per share", date, action, amount, sym, pps));
            }
            if (isEmpty) {
                if (isBuy) {
                    System.out.println("No Buy Transactions");
                } else {
                    System.out.println("No Sell Transactions");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet getStockTransactions(boolean isBuy) throws SQLException {
        return DBInteraction.getData("*", isBuy ? "Buy_Transactions" : "Sell_Transactions", String.format("WHERE s_aid = %s", S_AID));
    }

    protected static void ListStockPricePrompt() {
        System.out.println("What stock do you want to check?");
        String sym = in.next();
        if (isValidSym(sym)) {
            listPriceOf(sym);
        } else {
            System.out.println(String.format("Sym \"%s\" not found", sym));
        }
    }

    protected static void listPriceOf(String sym) {
        System.out.println(String.format("Price of \"%s\": $%s", sym, getStockPrice(sym)));
    }

    protected static void ListMovieInfoPrompt() {
        System.out.println("Movie name: ");
        String movie_name = in.next();
        try {
            ResultSet res = ListMovieInfoHelper(movie_name);
            while (res.next()) {
                System.out.println(
                        String.format("Name: %s", movie_name) + NEW_LINE +
                        String.format("Rating: %s", res.getString("rating")) + NEW_LINE +
                        String.format("Production Year: %s", res.getString("production_year"))
                );
            }
        } catch (SQLException e) {
            System.out.println(String.format("%s not in the system", movie_name));
            e.printStackTrace();
        }
        DBInteraction.useDB(TEST_DB, null);
    }

    protected static ResultSet ListMovieInfoHelper(String movie_name) throws SQLException {
        DBInteraction.useDB(MOVIES_DB, null);
        return DBInteraction.getData("*", "Movies", String.format("WHERE title = \"%s\"", movie_name));
    }


    protected static void ListTopMoviesPrompt() {
        System.out.println("What time period are you looking at? [format: yyyy-yyyy]");
        String year_start, year_end;
        try {
            String[] period = in.next().trim().split("-");
            year_start = period[0]; year_end = period[1];
        } catch (Exception e) {
            System.out.println("Wrong format");
            in = new Scanner(System.in);
            return;
        }
        if(Integer.parseInt(year_end) < Integer.parseInt(year_start)) {
            System.out.println("Year end > Year Start");
            return;
        }
        try {
            ResultSet res = ListTopMoviesHelper(year_start, year_end);
            while (res.next()) {
                System.out.println(res.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBInteraction.useDB(TEST_DB, null);
    }

    protected static ResultSet ListTopMoviesHelper(String year_begin, String year_end) throws SQLException {
        DBInteraction.useDB(MOVIES_DB, null);
        return DBInteraction.getData("title", "movies", String.format("WHERE rating >= 5.0 AND production_year BETWEEN %s AND %s", year_begin, year_end));
    }

    protected static void GetReviewsPrompt() {
        System.out.println("Give me the Movie Title:");
        String title;
        try {
            title = in.next().trim();
        } catch (Exception e) {
            System.out.println("Shit happens");
            in = new Scanner(System.in);
            return;
        }
        try {
            ResultSet res = GetReviewsHelper(title);
            while (res.next()) {
                System.out.println(res.getString("review"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBInteraction.useDB(TEST_DB, null);
    }

    protected static ResultSet GetReviewsHelper(String title) throws SQLException {
        DBInteraction.useDB(MOVIES_DB, null);
        ResultSet res = DBInteraction.getData("id", "movies", String.format("WHERE title = \"%s\"", title));
        res.next();
        int movieid = res.getInt("id");
        return DBInteraction.getData("review","reviews", String.format("WHERE movie_id = %d", movieid));
    }
}
