package com.xyz;

import java.io.*;
import java.sql.*;

public class DBInteraction {
    private static final String CREATE_DB_STATEMENT = "CREATE DATABASE IF NOT EXISTS testdb;";
    private static final String DROP_DB_STATEMENT = "DROP DATABASE testdb;";
    private static final String USE_DB_STATEMENT = "USE testDB;";
    private static final String SQL_FOLDER_REL_PATH = "./sql/";
    private static final String GET_DATA_TEMPLATE = "SELECT %s FROM %s %s;";
    private static final String INSERT_DATA_TEMPLATE = "INSERT INTO %s %s VALUES (%s)";
    private static final String UPDATE_DATA_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    private static Connection con = null;

    public static void Start_up() {
        try {
            if (con == null) {
                establishConnection();
            }
            Statement stmt = con.createStatement();
            stmt.execute(CREATE_DB_STATEMENT);
            stmt.execute(USE_DB_STATEMENT);
            establishSchema(stmt);
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    private static void establishConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
    }

    private static void establishSchema(Statement stmt) {
        executeAllStatementFilesIn(stmt, "schema/level_0/");
        executeAllStatementFilesIn(stmt, "schema/level_0/level_1/");
        executeAllStatementFilesIn(stmt, "schema/level_0/level_1/level_2/");
        executeAllStatementFilesIn(stmt, "utilities/");
        executeAllStatementFilesIn(stmt, "triggers/");
    }

    private static void executeAllStatementFilesIn(Statement stmt, String subfolder_path) {
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
                str.append(line + " ");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(str.toString());
        return str.toString();
    }

    public static void populateDatabaseFromFile (String subFilePath) throws IOException, SQLException, ClassNotFoundException {
        establishConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute(String.format(DROP_DB_STATEMENT));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Start_up();
            executeAllStatementsInFile(subFilePath);
        }
    }

    private static void executeAllStatementsInFile(String subFilePath) throws SQLException, IOException {
        File f = new File(SQL_FOLDER_REL_PATH + subFilePath);
        Statement stmt = con.createStatement();
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        while((line = reader.readLine()) != null) {
            stmt.execute(line);
        }
        reader.close();
    }

    public static ResultSet getData(String data, String table, String whereClause) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeQuery(String.format(GET_DATA_TEMPLATE, data, table, whereClause));
    }

    public static int insertData(String table, String cols, String vals) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeUpdate(String.format(INSERT_DATA_TEMPLATE, table, cols, vals));
    }

    public static void setupTestDB() {
        try{
            establishConnection();
            Statement stmt = con.createStatement();
            try {
                stmt.execute(String.format(DROP_DB_STATEMENT));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                Start_up();
                executeAllStatementFilesIn(stmt, "tests/setup/level0/");
                executeAllStatementFilesIn(stmt, "tests/setup/level0/level1/");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
