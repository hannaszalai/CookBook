package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Class for establishing a connection to the database.
 */
public class DatabaseConnection {    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cookbook", "root", "Gellert.123");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found", e);
        }
    }
}
