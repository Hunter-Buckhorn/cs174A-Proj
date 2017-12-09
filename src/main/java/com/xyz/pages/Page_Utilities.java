package com.xyz.pages;

import com.xyz.DBInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Scanner;

public class Page_Utilities {
    public static Scanner in = new Scanner(System.in);
    public static final String NEW_LINE = System.getProperty("line.separator");

    public static void success() {
        System.out.println("SUCCESS!");
    }

    public static boolean AskUserYesNoQuestion(String question) {
        String user_in = "";
        while (!user_in.trim().toLowerCase().equals("y") && !user_in.trim().toLowerCase().equals("n")) {
            System.out.println(question);
            user_in = in.next();
        }
        return user_in.toLowerCase().equals("y");
    }

    public static float getUserFloat() {
        // tested and working for int/float types
        while (true) {
            try {
                return in.nextFloat();
            } catch (Exception e) {
                in = new Scanner(System.in);
                System.out.println("Please enter a numeric amount");
            }
        }
    }

    public static int getUserInt() {
        while (true) {
            try {
                return in.nextInt();
            } catch (Exception e) {
                in = new Scanner(System.in);
                System.out.println("Please enter an integer amount");
            }
        }
    }

    public static void printAllStocksAvailable(Collection<String> sym_collection, Collection<Float> price_collection) {
        try {
            ResultSet res = getAllStocksAvailable();
            while (res.next()) {
                if (sym_collection != null) sym_collection.add(res.getString("sym"));
                if (price_collection != null) price_collection.add(res.getFloat("price"));
                System.out.println(String.format("%3s: %10f", res.getString("sym"), res.getFloat("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static ResultSet getAllStocksAvailable() throws SQLException {
        return DBInteraction.getData("sym, price", "Stock_Profiles","");
    }

    public static float getStockPrice(String sym) {
        try {
            if (isValidSym(sym)) {
                ResultSet res = DBInteraction.getData("price", "Stock_Profiles", String.format("WHERE sym = \"%s\"", sym));
                res.next();
                return res.getFloat("price");
            }
        } catch (SQLException e) {
            System.out.println(String.format("Error in getStockPrice(%s)", sym));
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isValidSym(String sym) {
        try {
            ResultSet res = DBInteraction.getData("sym", "Stock_Profiles", String.format("WHERE sym = \"%s\"", sym));
            if(res.next()) return true;
            else {
                System.out.println(String.format("Invalid Stock: %s", sym));
                return false;
            }
        } catch(SQLException e) {
            System.out.println(String.format("Invalid Stock: %s", sym));
            return false;
        }
    }

    public static boolean isSymInAcc(String sym, String s_aid) {
        try {
            ResultSet res = DBInteraction.getData("sym", "In_Stock_Acc", String.format("WHERE aid = %s AND sym = \"%s\"", s_aid, sym));
            if (res.next()) return true;
            else {
                System.out.println(String.format("%s is not in account %s", sym, s_aid));
                return false;
            }
        } catch(SQLException e) {
            System.out.println(String.format("%s is not in account %s", sym, s_aid));
            return false;
        }
    }

}