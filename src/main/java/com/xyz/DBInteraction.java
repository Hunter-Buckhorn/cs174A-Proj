package com.xyz;

import java.io.*;
import java.sql.*;

public class DBInteraction {
    private static final String CREATE_DB_STATEMENT = "CREATE DATABASE IF NOT EXISTS %s;";
    private static final String DROP_DB_STATEMENT = "DROP DATABASE %s;";
    private static final String USE_DB_STATEMENT = "USE %s;";
    private static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS %s;";
    private static final String SQL_FOLDER_REL_PATH = "./sql/";
    private static final String GET_DATA_TEMPLATE = "SELECT %s FROM %s %s;";
    private static final String INSERT_DATA_TEMPLATE = "INSERT INTO %s %s VALUES (%s)";
    private static final String UPDATE_DATA_TEMPLATE = "UPDATE %s SET %s %s";
    private static final String USER = "thienhoang";
    private static final String PASSWORD = "viSTALAM";
    public static final String TEST_DB = "thienhoangDB";
    public static final String MOVIES_DB = "Moviesdb";
    private static final String URI = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/";

    private static Connection con = null;

    public static void Start_up(String url, String user, String pwd) {
        try {
            if (con == null) {
                establishConnection(url, user, pwd);
            }
            Statement stmt = con.createStatement();
            stmt.execute(String.format(CREATE_DB_STATEMENT, TEST_DB));
            useDB(TEST_DB, stmt);
            executeAllStatementsInFile("load/dropOldTables.sql");
            establishSchema(stmt);
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    public static void useDB(String tablename, Statement stmt) {
        if (stmt == null) {
            try {
                stmt = con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            stmt.execute(String.format(USE_DB_STATEMENT, tablename));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void establishConnection(String url, String user, String pwd) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pwd);
    }

    private static void establishSchema(Statement stmt) {
        executeAllStatementFilesIn(stmt, "schema/level_0/");
        executeAllStatementFilesIn(stmt, "schema/level_0/level_1/");
        executeAllStatementFilesIn(stmt, "schema/level_0/level_1/level_2/");
        executeAllStatementFilesIn(stmt, "schema/level_0/level_1/level_2/level_3/");
        executeAllStatementFilesIn(stmt, "utilities/");
        executeAllStatementFilesIn(stmt, "triggers/");
    }

    public static void executeAllStatementFilesIn(Statement stmt, String subfolder_path) {
        if(stmt == null) {
            try {
                stmt = con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    public static ResultSet executeQuery(String querry_subfolder_path, String ... args) throws SQLException {
        File f = new File(SQL_FOLDER_REL_PATH + querry_subfolder_path);
        String sql_stmt = String.format(turnFileContentToString(f), args);
        Statement stmt = con.createStatement();
        return stmt.executeQuery(sql_stmt);
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
        establishConnection(URI, USER, PASSWORD);
        Statement stmt = con.createStatement();
        Start_up(URI, USER, PASSWORD);
        executeAllStatementsInFile(subFilePath);
    }

    private static void dropDB(Statement stmt, String dbname) {
        try {
            stmt.execute(String.format(DROP_DB_STATEMENT, dbname));
        }
        catch (SQLException e) {}
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
            establishConnection(URI, USER, PASSWORD);
            Statement stmt = con.createStatement();
            Start_up(URI, USER, PASSWORD);
            executeAllStatementFilesIn(stmt, "tests/setup/level0/");
            executeAllStatementFilesIn(stmt, "tests/setup/level0/level1/");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int updateData(String table, String colValPairs, String whereClause) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeUpdate(String.format(UPDATE_DATA_TEMPLATE, table, colValPairs, whereClause));
    }

    public static void DropTable(String table) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.execute(String.format(DROP_TABLE_STATEMENT, table));
    }
}
