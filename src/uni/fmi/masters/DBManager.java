package uni.fmi.masters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    private static final String DB_URL = "jdbc:sqlite:aquarium.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password_hash TEXT NOT NULL," +
                    "salt TEXT NOT NULL" +
                    ");";
            stmt.execute(createUsersTableSQL);

            String createSearchHistoryTableSQL = "CREATE TABLE IF NOT EXISTS search_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "search_params_json TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ");";
            stmt.execute(createSearchHistoryTableSQL);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}