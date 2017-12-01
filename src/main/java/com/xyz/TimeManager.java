package com.xyz;

import com.xyz.pages.CustomerInterfacePage;

import java.util.Scanner;

public class TimeManager {
    protected static final String NEW_LINE = System.getProperty("line.separator");
    public static String CUR_DATE;

    public static void Initialize(String date) {
        // Use When First Start
        setDateHelper(date);
    }

    public static void Start() {
        Scanner sc = new Scanner(System.in);
        userInputLoop: while(true) {
            printInstructions();
            switch (sc.nextInt()) {
                case 1:
                    System.out.println(String.format("Current Date is: %s", CUR_DATE));
                    break;
                case 2:
                    System.out.println("What date do you want to set it at? [format yyyy-mm-dd]");
                    setDate(sc.next());
                    break;
                case 3:
                    break userInputLoop;
                default:
                    break;
            }
        }
    }

    private static void printInstructions() {
        System.out.println(
                "1: Show Date" + NEW_LINE +
                "2: Set Date" + NEW_LINE +
                "3: Exit"
        );
    }

    public static void setDate(String new_date) {
        // Use To Set Date in The Future
        setDateHelper(new_date);
        handleSetDateEvent();
    }

    private static void setDateHelper(String new_date) {
        CUR_DATE = new_date;
        CustomerInterfacePage.setDate(CUR_DATE);
    }

    protected static void handleSetDateEvent() {}
}
