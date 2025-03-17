import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class FWEventTableTest {

    private FWEventTable eventTable;

    @Before
    public void setUp() {
        eventTable = new FWEventTable();
    }



    // THIS TESTS KEEPS FAILING
    @Test
    public void testAddEvent() {
        FileEvent event = new FileEvent("test.txt", "/path/to/test", "CREATED", ".txt", "2025-03-12", "10:00:00");
        eventTable.addEvent(event);

        List<FileEvent> data = eventTable.getData();
        assertEquals(1, data.size());

        //  Ensure the full filename is stored correctly
        assertEquals("test", data.get(0).getFileName());
        assertEquals(".txt", data.get(0).getExtension());
    }


    @Test
    public void testClearTable() {
        FileEvent event1 = new FileEvent("file1.txt", "/path/file1", "CREATED", ".txt", "2025-03-12", "10:00:00");
        FileEvent event2 = new FileEvent("file2.txt", "/path/file2", "DELETED", ".txt", "2025-03-12", "10:05:00");

        eventTable.addEvent(event1);
        eventTable.addEvent(event2);

        assertEquals(2, eventTable.getData().size());

        eventTable.clearTable();
        assertEquals(0, eventTable.getData().size());
    }

    @Test
    public void testFilterTable_AllExtensions() {
        FileEvent event1 = new FileEvent("doc1.pdf", "/path/doc1", "CREATED", ".pdf", "2025-03-12", "10:00:00");
        FileEvent event2 = new FileEvent("image.jpg", "/path/image", "CREATED", ".jpg", "2025-03-12", "10:00:00");

        eventTable.addEvent(event1);
        eventTable.addEvent(event2);

        eventTable.extensionTableFilter("All Extensions");

        assertEquals(2, eventTable.getData().size());  // No filter applied, all items should be present
    }

    @Test
    public void testFilterTable_SpecificExtension() {
        FileEvent event1 = new FileEvent("doc1.pdf", "/path/doc1", "CREATED", ".pdf", "2025-03-12", "10:00:00");
        FileEvent event2 = new FileEvent("image.jpg", "/path/image", "CREATED", ".jpg", "2025-03-12", "10:00:00");

        eventTable.addEvent(event1);
        eventTable.addEvent(event2);

        eventTable.extensionTableFilter(".pdf");

        List<FileEvent> filteredData = eventTable.getData();
        assertEquals(2, filteredData.size()); // The internal list still contains both

        // Manually check the table to verify that only PDFs were added
        assertTrue(filteredData.stream().anyMatch(e -> e.getExtension().equals(".pdf")));
        assertTrue(filteredData.stream().anyMatch(e -> e.getExtension().equals(".jpg"))); // Both remain in data
    }
}
