import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;

public class FWEventTableTest {
    
    private FWEventTable eventTable;

    @Before
    public void setUp() {
        eventTable = new FWEventTable();
    }

    @Test
    public void testAddEvent() {
        FileEvent event = new FileEvent("testFile.txt", "/test/path", "CREATED", ".txt", "2024-03-07", "12:00:00");
        eventTable.addEvent(event);

        ArrayList<FileEvent> data = eventTable.getData();
        assertEquals("One event should be added", 1, data.size());
        assertEquals("Event file name should match", "testFile", data.get(0).getFileName());
    }

    @Test
    public void testUpdateTable() {
        FileEvent event1 = new FileEvent("file1.txt", "/path1", "MODIFIED", ".txt", "2024-03-07", "12:10:00");
        FileEvent event2 = new FileEvent("file2.pdf", "/path2", "DELETED", ".pdf", "2024-03-07", "12:15:00");
        eventTable.addEvent(event1);
        eventTable.addEvent(event2);

        eventTable.updateTable();
        assertEquals("Table should contain 2 events", 2, eventTable.getData().size());
    }

    @Test
    public void testClearTable() {
        eventTable.addEvent(new FileEvent("file1.txt", "/path1", "CREATED", ".txt", "2024-03-07", "12:10:00"));
        eventTable.clearTable();

        assertEquals("Table should be empty after clearing", 0, eventTable.getData().size());
    }

    @Test
    public void testFilterTable() {
        FileEvent event1 = new FileEvent("file1.txt", "/path1", "CREATED", ".txt", "2024-03-07", "12:10:00");
        FileEvent event2 = new FileEvent("file2.pdf", "/path2", "CREATED", ".pdf", "2024-03-07", "12:15:00");
        FileEvent event3 = new FileEvent("file3.txt", "/path3", "DELETED", ".txt", "2024-03-07", "12:20:00");

        eventTable.addEvent(event1);
        eventTable.addEvent(event2);
        eventTable.addEvent(event3);

        eventTable.extensionTableFilter(".txt");

        assertEquals("Only 2 events should be shown for .txt filter", 2, eventTable.getData().size());
    }
}
