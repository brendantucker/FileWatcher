import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class FileEventDAOTest {

    private Connection testConnection;

    @Before
    public void setUp() throws SQLException {
        // Use an in-memory SQLite database for testing
        testConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseConnection.setConnection(testConnection); // Override real DB connection

        // Initialize the table schema
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE file_events (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "file_name TEXT NOT NULL, " +
                    "file_path TEXT NOT NULL, " +
                    "event_type TEXT NOT NULL, " +
                    "file_extension TEXT, " +
                    "event_date TEXT NOT NULL, " +
                    "event_time TEXT NOT NULL)");
        }
    }

    @After
    public void tearDown() throws SQLException {
        DatabaseConnection.disconnect(); // Reset the connection after each test
    }

    @Test
    public void testInsertFileEvent_Success() {
        FileEvent event = new FileEvent("test.txt", "/path/to/test", "CREATED", ".txt", "2025-03-12", "10:00:00");

        FileEventDAO.insertFileEvent(event);

        assertEquals(1, getRowCount("file_events"));
    }

    @Test
    public void testInsertFileEvents_Batch() {
        List<FileEvent> events = Arrays.asList(
                new FileEvent("file1.txt", "/path/file1", "CREATED", ".txt", "2025-03-12", "10:00:00"),
                new FileEvent("file2.txt", "/path/file2", "DELETED", ".txt", "2025-03-12", "10:05:00")
        );

        FileEventDAO.insertFileEvents(events);

        assertEquals(2, getRowCount("file_events"));
    }

    @Test
    public void testFileEventsFromToday() {
        FileEvent event = new FileEvent("today.txt", "/path/today", "CREATED", ".txt", "2025-03-12", "10:00:00");
        FileEventDAO.insertFileEvent(event);

        FWEventTable result = FileEventDAO.fileEventsFromToday();
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testResetEntireDatabase() {
        FileEvent event = new FileEvent("delete.txt", "/path/delete", "DELETED", ".txt", "2025-03-12", "10:00:00");
        FileEventDAO.insertFileEvent(event);

        FileEventDAO.resetEntireDatabase();
        assertEquals(0, getRowCount("file_events"));
    }

    private int getRowCount(String tableName) {
        try (Statement stmt = testConnection.createStatement()) {
            return stmt.executeQuery("SELECT COUNT(*) FROM " + tableName).getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
