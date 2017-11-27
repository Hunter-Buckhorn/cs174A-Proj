package com.xyz;
        import java.sql.*;


public class App2 {
    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projdb", "root", "root");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO testtable (column_1, column_2) VALUES (1, 'hunter')");


        } catch(Exception e) {
            System.out.println("Error:" + e);
        }

        System.out.println("HELLO MOTHERFUCKERS!");
    }
}