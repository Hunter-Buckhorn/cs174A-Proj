package com.xyz;

import com.xyz.pages.CustomerInterfacePage;
import com.xyz.pages.LoginPage;
import com.xyz.pages.ManagerInterfacePages;

import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static final String Start_date = "2017-12-01";

    public static void main(String[] args) {
        printWelcomeMessage();
        TimeManager.Initialize(Start_date);
        try {
            DBInteraction.populateDatabaseFromFile("load/loadData.sql");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        loginLoop: while(true) {
            switch (LoginPage.Client_Login()) {
                case LoginPage.ADMIN_RET_CODE:
                    ManagerInterfacePages.Start();
                    break loginLoop;
                case LoginPage.CUSTOMER_RET_CODE:
                    CustomerInterfacePage.Start(LoginPage.uname, LoginPage.taxid, "2017-12-01");
                    break loginLoop;
                default:
                    break;
            }
        }
        // If they are here, they are logged in
    }

    private static void printWelcomeMessage() {
        System.out.println("WELCOME TO THIEN AND HUNTER'S CS174A PROJECT");
    }
}