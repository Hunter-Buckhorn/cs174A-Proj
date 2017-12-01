package com.xyz;

import java.io.IOException;
import java.sql.SQLException;

import static com.xyz.Test_Utilities.fail;
import static com.xyz.Test_Utilities.pass;

public class LoadDataTests {
    public static void main(String[] args) {
        populateDBFromLoadDataTest();
    }

    private static void populateDBFromLoadDataTest() {
        String test_name = new Object(){}.getClass().getEnclosingMethod().getName();
        try {
            DBInteraction.populateDatabaseFromFile("load/loadData.sql");
            pass(test_name);
        } catch (SQLException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            fail(test_name, e.getMessage());
            e.printStackTrace();
        }
    }
}
