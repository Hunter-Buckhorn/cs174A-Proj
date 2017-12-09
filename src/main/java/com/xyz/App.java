package com.xyz;

import com.xyz.pages.CustomerInterfacePage;
import com.xyz.pages.LoginPage;
import com.xyz.pages.ManagerInterfacePages;

import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static String Start_date = "2017-12-01";
    public static boolean CUSTOMER_INTERFACE = false;
    public static boolean MANAGER_INTERFACE = false;
    public static boolean EXIT = false;

    public static void main(String[] args) {
        printWelcomeMessage();
        TimeManager.Initialize(Start_date);
        try {
            //DBInteraction.populateDatabaseFromFile("load/TestingData.sql");
            DBInteraction.populateDatabaseFromFile("load/loadData.sql");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        loginLoop:
        while (true) {
            if (!LoginPage.isValidSession()) {
                LoginPage.Client_Login();
                CUSTOMER_INTERFACE = LoginPage.Is_Customer();
                MANAGER_INTERFACE = LoginPage.Is_Manager();
            }
            // debugging below
            //System.out.println(String.format("Admin: %b, Cust: %b, In_DB: %b", LoginPage.Is_Manager(), LoginPage.Is_Customer(), LoginPage.In_DB()));

            // check if uname, pwd valid
            if (LoginPage.In_DB()) {
                // check which interface to load

                if (MANAGER_INTERFACE) {
                    ManagerInterfacePages.Start(LoginPage.uname, LoginPage.taxid, Start_date);
                } else if (CUSTOMER_INTERFACE) {
                    CustomerInterfacePage.Start(LoginPage.uname, LoginPage.taxid);
                } else {
                    System.out.println("Error, user neither customer nor admin");
                }
            } else {
                System.out.println(String.format("Error: user \"%s\" doesn't exist or password: \"%s\" is incorrect", LoginPage.uname, LoginPage.pwd));
            }

            if (EXIT) return;
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("WELCOME TO THIEN AND HUNTER'S CS174A PROJECT");
    }
}