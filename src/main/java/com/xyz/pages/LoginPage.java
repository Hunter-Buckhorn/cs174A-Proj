package com.xyz.pages;

import com.xyz.DBInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginPage {
    public static final int NOT_IN_DB = -1;
    public static final int ROOT_RET_CODE = 0;
    public static final int ADMIN_RET_CODE = 1;
    public static final int CUSTOMER_RET_CODE = 2;

    public static int Client_Login() {
        Scanner in = new Scanner(System.in);
        System.out.print("username:");
        String uname= in.nextLine();
        System.out.print("password:");
        String pwd = in.nextLine();
        if (uname.equals("root") && pwd.equals("root")) {
            return ROOT_RET_CODE;
        }
        try {
            ResultSet res = DBInteraction.getData("is_admin", "Customers", String.format("WHERE uname = %s AND pwd = %s", uname, pwd));
            res.next();
            if (res.getBoolean("is_admin")) {
                return ADMIN_RET_CODE;
            }
            else {
                return CUSTOMER_RET_CODE;
            }
        } catch (SQLException e) {
            return NOT_IN_DB;
        }
    }
}
