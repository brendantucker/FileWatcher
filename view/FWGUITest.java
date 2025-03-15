import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FWGUITest {

    private FWGUI fwgui;

    @Before
    public void setUp() {
        fwgui = new FWGUI();
    }

    @Test
    public void testGUIInitialization() {
        assertNotNull(fwgui);
        assertNotNull(fwgui.getEventTable());
        assertTrue(fwgui.getEventTable().getData().isEmpty());
        assertFalse(fwgui.isMonitoring()); // Should not be monitoring on startup
    }

    @Test
    public void testStartStopButtons() {
        JButton startButton = fwgui.getMainPanel().getStartButton();
        JButton stopButton = fwgui.getStopButton(); //  Use the new getter
    
        assertNotNull(startButton);
        assertNotNull(stopButton);
    
        // Start should be disabled initially
        assertFalse(startButton.isEnabled());
        assertFalse(stopButton.isEnabled());
    
        // Simulate enabling the buttons
        startButton.setEnabled(true);
        stopButton.setEnabled(true);
    
        assertTrue(startButton.isEnabled());
        assertTrue(stopButton.isEnabled());
    }
    
    // Does not set directory to valid field, shows a joptionpane error message
    @Test
    public void testActionPerformed_StartMonitoring() {
        JButton startButton = fwgui.getMainPanel().getStartButton();
        JTextField directoryField = fwgui.getMainPanel().getDirectoryField();
        directoryField.setText("C:\\"); // Set a valid directory for the test
        startButton.setEnabled(true); 

        ActionEvent event = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        fwgui.actionPerformed(event);

        assertTrue(fwgui.isMonitoring());
    }

    
    // Does not set directory to valid field, shows a joptionpane error message
    @Test
    public void testActionPerformed_StopMonitoring() {
        JButton startButton = fwgui.getMainPanel().getStartButton();
        JTextField directoryField = fwgui.getMainPanel().getDirectoryField();
        JButton stopButton = fwgui.getMainPanel().getStopButton();
        directoryField.setText("C:\\"); // Set a valid directory for the test
        startButton.setEnabled(true); 
        stopButton.setEnabled(true); 
        ActionEvent eventStart = new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, "Start");
        ActionEvent eventStop = new ActionEvent(stopButton, ActionEvent.ACTION_PERFORMED, "Stop");
        
        //fwgui.

        fwgui.actionPerformed(eventStart);
        fwgui.actionPerformed(eventStop); //Stop a running monitoring session

        assertFalse(fwgui.isMonitoring());
    }

    @Test
    public void testMenuItemsExist() {
        JMenuBar menuBar = fwgui.getFrame().getJMenuBar();
        assertNotNull(menuBar);
        
        JMenu fileMenu = menuBar.getMenu(0);
        JMenu debugMenu = menuBar.getMenu(1);
        JMenu databaseMenu = menuBar.getMenu(2);
        JMenu emailMenu = menuBar.getMenu(3);
        JMenu aboutMenu = menuBar.getMenu(4);
    
        assertNotNull(fileMenu);
        assertNotNull(debugMenu);
        assertNotNull(databaseMenu);
        assertNotNull(emailMenu);
        assertNotNull(aboutMenu);
    
        assertEquals("File", fileMenu.getText());
        assertEquals("Debug", debugMenu.getText());
        assertEquals("Database", databaseMenu.getText());
        assertEquals("Email", emailMenu.getText());
        assertEquals("About", aboutMenu.getText());
    }
    
    @Test
    public void testExportQueryResultsToCSV_NoData() {
        // Simulate clicking "Export Query Results to CSV" in the menu
        ActionEvent event = new ActionEvent(fwgui, ActionEvent.ACTION_PERFORMED, "Export Query Results to CSV");
        fwgui.actionPerformed(event);
    
        // Ensure no data is present in the table
        assertTrue(fwgui.getEventTable().getData().isEmpty());
    }
    

}
