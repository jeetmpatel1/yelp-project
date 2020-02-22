import java.sql.*;

public class DBConnection {
    public static Connection getDBConnection() {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XE", "system", "oracle");
            return connection;
        } catch (SQLException e) {
            System.out.println("Not connected to database ! : " + e.getMessage());
        }
        return connection;
    }
}
