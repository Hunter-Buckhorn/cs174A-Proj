package com.xyz;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInteraction {
    private static final String CREATE_DB_STATEMENT = "CREATE DATABASE IF NOT EXISTS testdb;";
    private static final String USE_DB_STATEMENT = "USE testDB;";
    private static final String SQL_FOLDER_REL_PATH = "./sql/";

    public static void Start_up() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projdb", "root", "root");
            Statement stmt = con.createStatement();
            stmt.execute(CREATE_DB_STATEMENT);
            stmt.execute(USE_DB_STATEMENT);
            establishSchema(stmt);
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    private static void establishSchema(Statement stmt) {
        executeAllStatementOf(stmt, "schema/level_0/");
        executeAllStatementOf(stmt, "schema/level_0/level_1/");
        executeAllStatementOf(stmt, "schema/level_0/level_1/level_2/");
    }

    private static void executeAllStatementOf(Statement stmt, String subfolder_path) {
        File folder = new File(SQL_FOLDER_REL_PATH + subfolder_path);
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                continue;
            }
            String sql_stmt = turnFileContentToString(f);
            try {
                stmt.execute(sql_stmt);
            } catch (java.sql.SQLException e) {
                System.out.println("Error: " + f.getName() + ": " + e);
            }
        }
    }

    private static String turnFileContentToString(File f) {
        StringBuilder str = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while((line = reader.readLine()) != null) {
                str.append(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
