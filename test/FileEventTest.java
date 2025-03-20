import static org.junit.Assert.*;
import org.junit.*;

public class FileEventTest {

    private FileEvent fileEvent;

    @Before
    public void setUp() {
        fileEvent = new FileEvent("example.txt", "/path/to/example.txt", "CREATED", ".txt", "2024-03-07", "12:30:45");
    }

    @Test
    public void testFileNameWithoutExtension() {
        assertEquals("File name should not include extension", "example", fileEvent.getFileName());
    }

    @Test
    public void testFilePath() {
        assertEquals("File path should be correct", "/path/to/example.txt", fileEvent.getFilePath());
    }

    @Test
    public void testEventType() {
        assertEquals("Event type should be correct", "CREATED", fileEvent.getEventType());
    }

    @Test
    public void testFileExtension() {
        assertEquals("File extension should be correct", ".txt", fileEvent.getExtension());
    }

    @Test
    public void testEventDate() {
        assertEquals("Event date should be correct", "2024-03-07", fileEvent.getEventDate());
    }

    @Test
    public void testEventTime() {
        assertEquals("Event time should be correct", "12:30:45", fileEvent.getEventTime());
    }
}
