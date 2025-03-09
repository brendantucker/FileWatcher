import static org.junit.Assert.*;
import org.junit.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class FileEventDAOTest {

    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        // ✅ Initialize connection properly
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseConnection.setConnection(connection);

        // ✅ Ensure connection is used for table creation
        String createTableSQL = "CREATE TABLE file_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT NOT NULL, " +
                "file_path TEXT NOT NULL, " +
                "event_type TEXT NOT NULL, " +
                "file_extension TEXT, " +
                "event_date TEXT NOT NULL, " +
                "event_time TEXT NOT NULL)";
        connection.createStatement().execute(createTableSQL);
    }

    @After
    public void tearDown() throws SQLException {
        // ✅ Prevent NullPointerException if setUp() fails
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testInsertFileEvent() {
        FileEvent event = new FileEvent("testFile.txt", "/test/path", "CREATED", ".txt", "2024-03-07", "12:00:00");
        FileEventDAO.insertFileEvent(event);

        // Verify insertion
        FWEventTable resultTable = FileEventDAO.queryTxtFiles();
        assertEquals("One event should be inserted", 1, resultTable.getData().size());
        assertEquals("Inserted file name should match", "testFile", resultTable.getData().get(0).getFileName());
    }

    @Test
    public void testInsertFileEventsBatch() {
        FileEvent event1 = new FileEvent("file1.txt", "/path1", "MODIFIED", ".txt", "2024-03-07", "12:10:00");
        FileEvent event2 = new FileEvent("file2.txt", "/path2", "DELETED", ".txt", "2024-03-07", "12:15:00");
        FileEventDAO.insertFileEvents(Arrays.asList(event1, event2));

        // Verify batch insertion
        FWEventTable resultTable = FileEventDAO.queryTxtFiles();
        assertEquals("Two events should be inserted", 2, resultTable.getData().size());
    }

    @Test
    public void testQueryTxtFiles() {
        // Insert test data
        FileEvent event1 = new FileEvent("text1.txt", "/test/path1", "CREATED", ".txt", "2024-03-07", "10:30:00");
        FileEvent event2 = new FileEvent("text2.pdf", "/test/path2", "CREATED", ".pdf", "2024-03-07", "11:00:00");
        FileEventDAO.insertFileEvents(Arrays.asList(event1, event2));

        // Query and validate
        FWEventTable resultTable = FileEventDAO.queryTxtFiles();
        assertEquals("Only one .txt file should be returned", 1, resultTable.getData().size());
        assertEquals("Correct file should be retrieved", "text1", resultTable.getData().get(0).getFileName());
    }

    @Test
    public void testInsertWithNoConnection() {
        // Close the connection to simulate database disconnection
        DatabaseConnection.disconnect();

        FileEvent event = new FileEvent("errorTest.txt", "/path/error", "CREATED", ".txt", "2024-03-07", "12:45:00");
        FileEventDAO.insertFileEvent(event);

        // Since the database is disconnected, no data should be inserted
        FWEventTable resultTable = FileEventDAO.queryTxtFiles();
        assertEquals("No data should be inserted if DB is disconnected", 0, resultTable.getData().size());
    }
}
