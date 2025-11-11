import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBSConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/phasefour";
    private static final String USER = "root";
    private static final String PASSWORD = "123password";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found");
            throw new SQLException(e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
