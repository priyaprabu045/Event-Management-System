package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager for PostgreSQL.
 * Provides a single access point to the database connection across the application.
 */
public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/event_management_system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Root@123";

    private static DBConnection instance;
    private Connection connection;

    // Private constructor prevents direct instantiation
    private DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found in classpath.");
        }
    }

    /**
     * Thread-safe Singleton accessor.
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Retrieves an active database Connection. Re-establishes connection if closed.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Gracefully closes the database connection on application shutdown.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Standalone main method to test database connectivity directly.
     */
    public static void main(String[] args) {
        System.out.println("Testing PostgreSQL Database Connection...");
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection successful!");
                System.out.println("Database: event_management_system");
                System.out.println("User: " + USER);
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
        } finally {
            DBConnection.getInstance().closeConnection();
        }
    }
}
