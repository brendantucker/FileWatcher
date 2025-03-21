import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

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
            .sorted(Comparator.reverseOrder()) // Ensures directories are deleted AFTER their contents, otherwise exception is thrown.
            // Convert each path into a file object
            .map(Path::toFile)
            //Delete each file/directory
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
    public void testFileEventInsertion() throws IOException, InterruptedException {
        
        JButton startButton = myGUI.getMainPanel().getStartButton();
        JTextField directoryField = myGUI.getMainPanel().getMyDirectoryField();
        directoryField.setText(testDirectory.toString()); // Set a valid directory for the test. NOTE: This is not the actual directory being watched.
        startButton.setEnabled(true); 

        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        myGUI.actionPerformed(event); // Calls myWatchService.start() and sets FWGUI's myIsMonitoring field to true.

        // Create a new file in the test directory
        Path testFile = Files.createFile(testDirectory.resolve("testFile.txt"));

        // Wait for the watch service to process the event in separate thread
        Thread.sleep(2000);
        FileEvent fileEvent = myGUI.getEventTable().getData().get(0);
        // Verify that the event was added to the event table
        assertEquals(1, myGUI.getEventTable().getData().size());
        
        assertEquals("testFile", fileEvent.getFileName());
        assertEquals(testFile.toString(), fileEvent.getFilePath());
        assertEquals("CREATED", fileEvent.getEventType());

        myWatchService.stop();
    }

    @Test
    public void testAddFolder() throws IOException, InterruptedException {
        JButton startButton = myGUI.getMainPanel().getStartButton();
        JTextField directoryField = myGUI.getMainPanel().getMyDirectoryField();
        directoryField.setText(testDirectory.toString()); // Set a valid directory for the test. NOTE: This is not the actual directory being watched.
        startButton.setEnabled(true); 

        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        myGUI.actionPerformed(event); // Calls myWatchService.start() and sets FWGUI's myIsMonitoring field to true.

        // Create a new folder in the test directory
        Path testFolder = Files.createDirectory(testDirectory.resolve("NewTestDir"));
        Path testFile = Files.createFile(testFolder.resolve("testFile.txt"));
        // Increased time to lower test failing chances.
        Thread.sleep(5000);

        for (FileEvent e : myGUI.getEventTable().getData()) {
            System.out.println(e.getFileName());
        }

        assertNotNull(Files.getFileStore(testFile));
        assertEquals(2, myGUI.getEventTable().getData().size());

        // Wait for the watch service to process the event in separate thread
        
        FileEvent folderEvent = myGUI.getEventTable().getData().get(0); //Folder creation event
        FileEvent fileEvent = myGUI.getEventTable().getData().get(1);   //File creation event

        // Verify that the event was added to the event table

        
        assertEquals("NewTestDir", folderEvent.getFileName());
        assertEquals(testFolder.toString(), folderEvent.getFilePath());
        assertEquals("CREATED", folderEvent.getEventType());

        assertEquals("testFile", fileEvent.getFileName());
        assertEquals(testFile.toString(), fileEvent.getFilePath());
        assertEquals("CREATED", fileEvent.getEventType());

        myWatchService.stop();
    }


    @Test
    public void testFileDeletion() throws IOException, InterruptedException {
        
        JButton startButton = myGUI.getMainPanel().getStartButton();
        JTextField directoryField = myGUI.getMainPanel().getMyDirectoryField();
        directoryField.setText(testDirectory.toString()); // Set a valid directory for the test. NOTE: This is not the actual directory being watched.
        startButton.setEnabled(true); 

        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        myGUI.actionPerformed(event); // Calls myWatchService.start() and sets FWGUI's myIsMonitoring field to true.

        // Create a new file in the test directory
        Path testFile = Files.createFile(testDirectory.resolve("testFile.txt"));
        assertNotNull(Files.getFileStore(testFile));
        Thread.sleep(500);
        Files.delete(testFile);

        // Wait for the watch service to process the event in separate thread
        Thread.sleep(500);
        assertEquals(2, myGUI.getEventTable().getData().size());
        FileEvent fileDeleteEvent = myGUI.getEventTable().getData().get(1);
        
        assertEquals("testFile", fileDeleteEvent.getFileName());
        assertEquals(testFile.toString(), fileDeleteEvent.getFilePath());
        assertEquals("DELETED", fileDeleteEvent.getEventType());

        myWatchService.stop();
    }

}
