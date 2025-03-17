import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;

public class FWPanelTest {

    private FWPanel panel;

    @Before
    public void setUp() {
        panel = new FWPanel();
    }

    @Test
    public void testPanelInitialization() {
        assertNotNull(panel);
    }

    @Test
    public void testHasStartAndStopButtons() {
        JButton startButton = panel.getStartButton();
        JButton stopButton = panel.getStopButton();

        assertNotNull(startButton);
        assertNotNull(stopButton);

        // Start and Stop should be disabled initially
        assertFalse(startButton.isEnabled());
        assertFalse(stopButton.isEnabled());
    }

    @Test
    public void testHasDirectoryField() {
        JTextField directoryField = panel.getMyDirectoryField();
        assertNotNull(directoryField);
        assertEquals("", directoryField.getText()); // Should start empty
    }

    @Test
    public void testHasExtensionDropdown() {
        JComboBox<String> extensionBox = panel.getExtensionBox();
        assertNotNull(extensionBox);
        assertEquals("All extensions", extensionBox.getSelectedItem()); // Default should be "All extensions"
    }



    @Test
    public void testEnableStartButton() {
        JButton startButton = panel.getStartButton();
        assertFalse(startButton.isEnabled());

        startButton.setEnabled(true);
        assertTrue(startButton.isEnabled());
    }

    @Test
    public void testEnableQueryButton() {
        JButton queryButton = panel.getQueryButton();
        assertFalse(queryButton.isEnabled());

        queryButton.setEnabled(true);
        assertTrue(queryButton.isEnabled());
    }
}
