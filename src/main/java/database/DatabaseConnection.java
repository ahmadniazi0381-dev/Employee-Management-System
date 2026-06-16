package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;

    private static final String URL =
            "jdbc:mysql://localhost:3306/employee_management_system";

    private static final String USER = "root";

    private static final String PASSWORD = "root";

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {

        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}