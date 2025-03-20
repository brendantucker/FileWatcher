import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to handle the connection to the SQLite database.
 */
public final class DatabaseConnection {
    private static final String MY_URL = "jdbc:sqlite:filewatcher.db";
    private static Connection myConnection = null;

    /**
     * Connects to the SQLite database.
     * 
     * @return true if the connection is successful, false otherwise.
     */
    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            myConnection = DriverManager.getConnection(MY_URL);
            System.out.println(" Connected to SQLite database!");

            // Ensure the table exists
            initializeDatabase();

            // Set the GUI to show that the database is connected
            // Enable "Write to Database" button
            FWGUI gui = FWGUI.getMyFWGUIInstance();
            if (gui != null) {
                gui.setDatabaseConnected(true);
            }

            return true;
        } catch (final ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (final SQLException e) {
            System.out.println("Failed to connect to SQLite database.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Initializes the SQLite database and creates the table if it does not exist.
     */
    private static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS file_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT NOT NULL, " +
                "file_path TEXT NOT NULL, " +
                "event_type TEXT NOT NULL, " +
                "file_extension TEXT, " +
                "event_date TEXT NOT NULL, " +
                "event_time TEXT NOT NULL);";

        try (final Statement stmt = getMyConnection().createStatement()) {
            stmt.execute(sql);
            System.out.println(" Database initialized. Table 'file_events' is ready.");
        } catch (final SQLException e) {
            System.out.println(" ERROR: Failed to initialize the database.");
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the SQLite database.
     */
    public static void disconnect() {
        try {
            if (myConnection != null && !myConnection.isClosed()) {
                myConnection.close();
                myConnection = null; // 🔹 Ensure the connection is set to null

                final FWGUI gui = FWGUI.getMyFWGUIInstance();
                if (gui != null) {
                    gui.setDatabaseConnected(false);
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the connection to the SQLite database.
     * @return Connection to the SQLite database.
     */
    public static final Connection getMyConnection() {
        return myConnection;
    }

    /**
     * Sets the connection to the SQLite database.
     * @param connection Connection to the SQLite database.
     */
    public static void setConnection(final Connection connection) {
        myConnection = connection;
    }

}
