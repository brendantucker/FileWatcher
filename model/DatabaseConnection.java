import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String myURL = "jdbc:sqlite:filewatcher.db";
    private static Connection myConnection = null;
    /**
     * Connects to the SQLite database.
     * @return
     */
    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            myConnection = DriverManager.getConnection(myURL);
            System.out.println(" Connected to SQLite database!");

            // Ensure the table exists
            initializeDatabase();

            // Set the GUI to show that the database is connected
            // Enable "Write to Database" button
            FWGUI gui = FWGUI.getMyInstance();
            if (gui != null) {
                gui.setDatabaseConnected(true);
            }


            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to SQLite database.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Initializes the database by creating the necessary table if it doesn't exist.
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

        try (Statement stmt = getMyConnection().createStatement()) {
            stmt.execute(sql);
            System.out.println(" Database initialized. Table 'file_events' is ready.");
        } catch (SQLException e) {
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
                System.out.println(" Disconnected from SQLite database.");

                // Set the GUI to show that the database is disconnected
                FWGUI gui = FWGUI.getMyInstance();
                if (gui != null) {
                    gui.setDatabaseConnected(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" Error disconnecting from database.");
        }
    }

    /**
     * Returns the current database connection.
     *
     * @return the current database connection
     */
    public static Connection getMyConnection() {
        return myConnection;
    }
}
