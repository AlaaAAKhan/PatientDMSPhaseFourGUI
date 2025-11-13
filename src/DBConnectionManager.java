/**
 * CEN 3024C - 13950 - Software Development 1
 * November 12, 2025
 * DBConnectionManager.java
 *
 * This class is created specifically to establish the connection between the GUI and the mySQL server.
 * It sets empty values for the URL, USER, and PASSWORD, and then later receives them from the user through the GUI.
 * */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    public static String URL = "";
    public static String USER = "";
    public static String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found");
            throw new SQLException(e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void setConnectionParams(String newURL, String newUser, String newPassword) {
        URL = newURL;
        USER = newUser;
        PASSWORD = newPassword;

    }

}
