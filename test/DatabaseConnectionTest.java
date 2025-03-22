import static org.junit.Assert.*;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    @Before
    public void setup() {
        DatabaseConnection.connect();
    }

    @After
    public void teardown() {
        DatabaseConnection.disconnect();
    }

    @Test
    public void testConnect() {
        assertTrue(DatabaseConnection.connect());
        assertNotNull(DatabaseConnection.getMyConnection());
    }

    @Test
    public void testDisconnect() throws SQLException {
        DatabaseConnection.disconnect();
        Connection conn = DatabaseConnection.getMyConnection();
        
        // Ensure the connection is closed properly
        assertTrue(conn == null || conn.isClosed());
    }
}
