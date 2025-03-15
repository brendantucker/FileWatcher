import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.junit.*;


public class DirectoryWatchServiceTest {

    private DirectoryWatchService myWatchService;
    private FWGUI myGUI;
    private static Path testDirectory;

    @Before
    public void setup() throws IOException{
        testDirectory = Files.createTempDirectory("testDir");
        myGUI = new FWGUI();
        myWatchService = new DirectoryWatchService(testDirectory.toString(), myGUI);
    }

    @After
    public void teardown() throws IOException, InterruptedException {
        myWatchService.stop();
        Files.walk(testDirectory)
            .map(Path::toFile)
            .forEach(file -> file.delete());
        Files.deleteIfExists(testDirectory);
    }

    @Test
    public void testInitialization() {
        assertNotNull(myWatchService);
        assertNotNull(myGUI);
        assertFalse(myGUI.isMonitoring()); 
    }

    @Test
    public void testStartMonitoring() throws IOException {
        myWatchService.start();
        assertTrue(myWatchService.isRunning());
    }

    @Test
    public void testStopMonitoring() throws IOException {
        myWatchService.start();
        myWatchService.stop();
        assertFalse(myWatchService.isRunning());
    }

    @Test
    public void testFileEvents() throws IOException, InterruptedException {
        
        JButton startButton = myGUI.getMainPanel().getStartButton();
        JTextField directoryField = myGUI.getMainPanel().getDirectoryField();
        directoryField.setText(testDirectory.toString()); // Set a valid directory for the test. NOTE: This is not the actual directory being watched.
        startButton.setEnabled(true); 

        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        myGUI.actionPerformed(event); // Calls myWatchService.start() and sets FWGUI's myIsMonitoring field to true.

        // Create a new file in the test directory
        Path testFile = Files.createFile(testDirectory.resolve("testFile.txt"));
        System.out.println("File created: " + testFile);
        assertNotNull(Files.getFileStore(testFile));

        // Wait for the watch service to process the event in separate thread
        Thread.sleep(500);
        FileEvent fileEvent = myGUI.getEventTable().getData().get(0);
        // Verify that the event was added to the event table
        assertEquals(1, myGUI.getEventTable().getData().size());
        
        assertEquals("testFile", fileEvent.getFileName());
        assertEquals(testFile.toString(), fileEvent.getFilePath());
        assertEquals("CREATED", fileEvent.getEventType());

        myWatchService.stop();
    }
}
