
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:filewatcher.db"; // SQLite database file
    private static Connection connection = null;

    public static boolean connect() {
        try {
            // **LOAD THE DRIVER EXPLICITLY**
            Class.forName("org.sqlite.JDBC");

            if (connection == null || connection.isClosed()) {
                System.out.println("Attempting to connect to database...");
                connection = DriverManager.getConnection(URL);
                System.out.println("Connected to SQLite database successfully!");
                initializeDatabase();
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found. Make sure sqlite.jar is in the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to SQLite database. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error disconnecting from database.");
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    private static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS file_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT NOT NULL, " +
                "file_path TEXT NOT NULL, " +
                "event_type TEXT NOT NULL, " +
                "file_extension TEXT, " +
                "event_time TEXT NOT NULL);";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
