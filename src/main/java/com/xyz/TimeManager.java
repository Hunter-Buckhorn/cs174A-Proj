package com.xyz;

import com.xyz.pages.ManagerInterfacePages;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.xyz.pages.Page_Utilities.*;

public class TimeManager {
    public static String DATE;
    public static boolean isClosed;
    public static int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};

    public static boolean isNewMonth = false;

    public static void Initialize(String date) {
        // Use When First Start
        DATE = date;
        isClosed = false;
    }

    public static boolean isMarketClosed()  {
        return isClosed;
    }

    public static void Start() {
        // Use to kick off the interactive session
        userInputLoop: while(true) {
            printInstructions();
            switch (getUserInt()) {
                case 1:
                    System.out.println(isClosed ? "Opening Day" : "Day Already Opened");
                    openDay();
                    break;
                case 2:
                    if (!isClosed) {
                        System.out.println("Closing Day");
                        closeDay();
                    } else System.out.println("Day Already Closed");
                    break;
                case 3:
                    System.out.println(String.format("Current Date is: %s", DATE));
                    break;
                case 4:
                    setDate_Prompt();
                    break;
                case 5:
                    System.out.println("Change the stock values");
                    ChangeStockValPrompt();
                    break;
                case 6:
                    System.out.println("Reseting for new month");
                    ResetForNewMonth();
                case 7:
                    System.out.println("Leaving Time Manager...");
                    break userInputLoop;
                default:
                    break;
            }
        }
    }

    private static void printInstructions() {
        System.out.println(
                "1: Open Day" + NEW_LINE +
                "2: Close Day" + NEW_LINE +
                "3: Show Date" + NEW_LINE +
                "4: Set Date" + NEW_LINE +
                "5: Change the stock values" +  NEW_LINE +
                "6: Reset for New Month" +  NEW_LINE +
                "7: Exit"
        );
    }

    protected static void openDay() {
        isClosed = false;
    }

    protected static void closeDay() {
        // need to record the daily closing price
        try {
            isClosed = true;
            UpdateStockClosingPrice();
            UpdateDayInMarketAcct(1);
            UpdateRunningBalanceSum(1);
        } catch (SQLException e) {
            System.out.println(String.format("Error in closeDay, error msg: %s", e.getMessage()));
        }
    }

    protected static void UpdateStockClosingPrice() throws SQLException {
        DBInteraction.updateData("Stock_Profiles", String.format("dcp = price"), "");
    }

    protected static void UpdateDayInMarketAcct(int amt) throws SQLException {
        DBInteraction.updateData("Market_Accounts",
                String.format(String.format("day_of_the_month = day_of_the_month + %d", amt)), "");
    }

    protected static void UpdateRunningBalanceSum(int multiplier) throws SQLException {
        DBInteraction.updateData("Market_Accounts",
                String.format(String.format("running_balance_sum = running_balance_sum + balance * CAST(%d AS DECIMAL(18,3)) ", multiplier)), "");
    }

    public static void setDate_Prompt() {
        // Use To Set Date in The Future

        // Get User Input
        String newDate = null;
        while(true) {
            System.out.println("What date do you want to set it at? [format yyyy-mm-dd]");
            newDate = in.next();
            if(newDate.split("-").length == 3) break;
        }
        try {
            setDate(newDate);
        } catch (SQLException e) {
            System.out.println(String.format("Error in setDate(%s)", newDate));
            e.printStackTrace();
        }
    }

    protected static void setDate(String new_date) throws SQLException {
        // Make sure the day is closed before set to new day
        if(isClosed == false) closeDay();
        setDateHelper(new_date);
        openDay();
    }

    protected static void setDateHelper(String new_date) throws SQLException {
        LocalDate newDate = LocalDate.parse(new_date);
        LocalDate currDate = LocalDate.parse(DATE);
        if (newDate.isAfter(currDate)) {
            int numDays = (int) ChronoUnit.DAYS.between(currDate, newDate);
            UpdateDayInMarketAcct(numDays - 1); // Because when we close, we already take into account 1 day
            UpdateRunningBalanceSum(numDays - 1); //Also because when we close, we already update running balance once
            DATE = new_date;
        } else {
            System.out.println("Cannot set date before current date");
        }
    }

    private static void ChangeStockValPrompt() {
        try{
            List<String> symArr = new ArrayList<>();
            printAllStocksAvailable(symArr,null);
            String input = "";
            while(true) {
                System.out.println("Enter a stock sym to update, or enter \"Cancel\" to exit");
                input = in.next();
                if (input.toLowerCase().equals("cancel")) {
                    return;
                }
                float newSymVal;
                if (symArr.contains(input)) {
                    while(true) {
                        System.out.println(String.format("Enter a new value for %s: ", input));
                        newSymVal = getUserFloat();
                        if (newSymVal > 0) {
                            break;
                        } else {
                            System.out.println("Cannot set stock value to a negative number");
                        }
                    }
                } else {
                    System.out.println("Error, sym not found");
                    continue;
                }
                // update the current price
                ChangeStockValHelper(newSymVal, input);
                System.out.println(String.format("\"%s\" value updated to $%f",input, newSymVal));
            }
        } catch (SQLException e) {
            System.out.println(String.format("Error in ChangeStockValPrompt, error msg: %s", e.getMessage()));
            e.printStackTrace();
        }
    }

    protected static void ChangeStockValHelper(float newSymVal, String sym) throws SQLException {
        DBInteraction.updateData("Stock_Profiles", String.format("price = %f", newSymVal), String.format("WHERE sym = \"%s\"", sym));
    }

    private static void ResetForNewMonth() {
        try {
            if (AskUserYesNoQuestion("Clear Day of The Month? y/n")) {
                ClearDayofTheMonth();
                success();
            }
            if (AskUserYesNoQuestion("Clear Running Balance Sum? y/n")) {
                ClearRunningBalanceSum();
                success();
            }
            if (AskUserYesNoQuestion("Clear Total Commission? y/n")) {
                ClearCommission();
                success();
            }
            if (AskUserYesNoQuestion("Delete Trasaction? y/n")) {
                ManagerInterfacePages.Delete_Transactions();
            }
            if (AskUserYesNoQuestion("Set New Initial Balance? y/n")) {
                setNewInitialBalance();
                success();
            }
        } catch (SQLException e) {
            System.out.println("Error in ResetForMonth()");
            e.printStackTrace();
        }
    }

    protected static void ClearDayofTheMonth() throws SQLException {
        DBInteraction.updateData("Market_Accounts", "day_of_the_month = 0", "");
    }

    protected static void ClearRunningBalanceSum() throws SQLException {
        DBInteraction.updateData("Market_Accounts", "running_balance_sum = 0", "");
    }

    protected static void ClearCommission() throws SQLException {
        DBInteraction.updateData("Market_Accounts", "tot_commission = 0", "");
    }

    protected static void setNewInitialBalance() throws SQLException {
        DBInteraction.updateData("Market_Accounts", "initial_balance = balance", "");
    }

    public static void updateDate_days(String currDateStr, int daysForward) {
        if (daysForward < 0) {
            System.out.println("Error in updateDate_days, can't move negative days forward");
            return;
        }
        String[] dateArr = currDateStr.split("-");
        String y,m,d;

        y = currDateStr.split("-")[0];
        m = currDateStr.split("-")[1];
        d = currDateStr.split("-")[2];

        int currDay = Integer.parseInt(d);
        int currMonth = Integer.parseInt(m);
        int currYear = Integer.parseInt(y);

        int nextDay = currDay + daysForward;
        int nextMonth = currMonth;
        int nextYear = currYear;

        String newDay = "";
        String newMonth = "";
        String newYear = "";

        int i = currMonth - 1; // dates from 1 to 12 -> indices 0 to 11

        // update the months
        while (nextDay > daysInMonth[i%12]) {
            nextDay -= daysInMonth[i%12];
            nextMonth++;
            i++;
        }

        // update the year
        while (nextMonth > 12) {
            nextYear++;
            nextMonth -= 12;
        }

        if (nextDay < 10) {
            newDay = String.format("0%d", nextDay);
        } else {
            newDay = String.format("%d", nextDay);
        }

        if (nextMonth < 10) {
            newMonth = String.format("0%d", nextMonth);
        } else {
            newMonth = String.format("%d", nextMonth);
        }

        newYear = String.format("%d", nextYear);
        currDateStr = String.format("%s-%s-%s", newYear, newMonth, newDay);
    }


}
