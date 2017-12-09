package com.xyz.pages;

import com.xyz.DBInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static com.xyz.pages.Page_Utilities.in;

public class LoginPage {
    private static boolean IN_DB = false;
    public static final int ADMIN_RET_CODE = 0;
    public static final int CUSTOMER_RET_CODE = 1;

    // flags for App control flow
    private static boolean IS_CUSTOMER = false;
    private static boolean IS_MANAGER = false;
    private static boolean VALID_SESSION = false;

    public static String uname;
    public static String pwd;
    public static String taxid;

    public static boolean Is_Customer() {
        // Assumes Client_Login already called to init flags
        return IS_CUSTOMER;
    }
    public static boolean Is_Manager() {
        // Assumes Client_Login already called to init flags
        return IS_MANAGER;
    }
    public static boolean In_DB() {
        return IN_DB;
    }
    public static boolean isValidSession() { return VALID_SESSION; }
    public static void InvalidateSession() {
        System.out.println("Logging out...");
        VALID_SESSION = false;
    }

    public static void Client_Login() {
        // if already logged in with valid user, VALID_SESSION == true
        //System.out.println(String.format("ValidSession: %b", VALID_SESSION));
        if (VALID_SESSION) {
            return;
        }

        // The only way to get rid of the awkward
        in = new Scanner(System.in);

        System.out.println("Please Log In:");
        System.out.print("username:");
        uname= in.nextLine();
        System.out.print("password:");
        pwd = in.nextLine();
        try {
            ResultSet res = DBInteraction.getData("is_admin, uname, taxid", "Customers", String.format("WHERE uname = \"%s\" AND pwd = \"%s\"", uname, pwd));

            // 2 cases: admin, customer
            // admin -> customer, but not necessarily with a market_account
            res.next();
            boolean isAdmin = res.getBoolean("is_admin");

            String unname = res.getString("uname");

            if (isAdmin) {
                //System.out.println("ADMIN w mAcc");
                System.out.println("ADMIN INTERFACE:");
                taxid = res.getString("taxid");
                IS_MANAGER = true;
                IS_CUSTOMER = true;
                IN_DB = true;
                //return ADMIN_RET_CODE;
            }
            else {
                //System.out.println("CUSTOMER");
                System.out.println("CUSTOMER INTERFACE:");
                taxid = res.getString("taxid");
                IS_MANAGER = false;
                IS_CUSTOMER = true;
                IN_DB = true;
                //return CUSTOMER_RET_CODE;
            }
            System.out.println(String.format("Welcome %s", uname));
            VALID_SESSION = true;
        } catch (SQLException e) {
            IN_DB = false;
            IS_CUSTOMER = false;
            IS_MANAGER = false;
        }
    }
}
