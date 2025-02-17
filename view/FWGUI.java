import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FWGUI implements ActionListener {
    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private int runningTime = 0;
    private Timer myTimer;
    private JLabel myTimeLabel;
    private JMenuItem myStartButton;
    private JMenuItem myStopButton;
    private double splitPaneResizeWeight = 0.2;
    // New field
    private FWEventTable myEventTable;
    private JComboBox<String> myExtensionComboBox;
    private JTextField myDirectoryField;
    private JTextField myDatabaseField;
    private JTextField myExtensionField;
    private JButton myClearDirectoryButton;
    private JButton myDirectoryBrowseButton;
    private JButton myDirectoryStartButton;
    private JButton myDirectoryStopButton;
    private JButton myWriteDbButton;
    private FWPanel myMainPanel;
    private boolean myIsMonitoring;
    
        /*
         * Constructor for the GUI. This will create the GUI and set up the menu bar.
         */
        public FWGUI() {
            myFrame = new FWFrame().frameOutline();
            myFrame.setLayout(new BorderLayout());
    
            // Create the main panel and event table
            myMainPanel = new FWPanel();
            myEventTable = new FWEventTable();
    
            createMenuBar();
            timeKeeper();
            setUpButtons();
            setUpDocumentListeners();
            setUpFileViewer();
    
            myFrame.add(myMainPanel, BorderLayout.NORTH);
            myFrame.setVisible(true);
        }
    
        private void setUpFileViewer() {
    
            // Create a JSplitPane to divide the space between the main panel and the event
            // table
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myMainPanel, myEventTable);
            splitPane.setResizeWeight(splitPaneResizeWeight);
            splitPane.setDividerSize(0);
    
            // Add the JSplitPane to the frame
            myFrame.add(splitPane, BorderLayout.CENTER);
    
            // Add 100 test events to the table to test scrolling
            for (int i = 0; i < 100; i++) {
                myEventTable.addEvent(new FileEvent("Test.txt", "C:/path/to/TestFile.txt", EventType.FILECREATED, "txt",
                        LocalDateTime.of(2025, 2, 2, 12, 27)));
            }
        }
    
        private void setUpButtons() {
            myExtensionComboBox = myMainPanel.getExtensionBox();
            myExtensionComboBox.setEditable(true);
            myExtensionComboBox.addActionListener(this);
    
            myDirectoryStartButton = myMainPanel.getStartButton();
            myDirectoryStartButton.addActionListener(this);
    
            myDirectoryStopButton = myMainPanel.getStopButton();
            myDirectoryStopButton.addActionListener(this);
    
            myDirectoryBrowseButton = myMainPanel.getBrowseButton();
            myDirectoryBrowseButton.addActionListener(this);
    
            myClearDirectoryButton = myMainPanel.getClearButton();
            myClearDirectoryButton.addActionListener(this);
    
            myWriteDbButton = myMainPanel.getMyWriteDBButton();
            myWriteDbButton.addActionListener(this);
        }
    
        /*
         * This method will keep track of the time that the user has been monitoring
         * files.
         * This method will keep track of the time that the user has been monitoring
         * files.
         */
        private void timeKeeper() {
            myTimer = new Timer(1000, (ActionEvent e) -> {
                runningTime++;
                timerLabelExtended();
            });
            myStartButton.addActionListener(this);
            myStopButton.addActionListener(this);
            // Create a panel for the time label
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            timePanel.add(myTimeLabel);
            myFrame.add(timePanel, BorderLayout.SOUTH);
        }
    
        /*
         * This method will extend the timer label to show the time in days, hours,
         * minutes, and seconds.
         */
        private void timerLabelExtended() {
            int days = runningTime / 86400;
            int hours = (runningTime % 86400) / 3600;
            int minutes = (runningTime % 3600) / 60;
            int seconds = runningTime % 60;
            String timeFormatted = String.format("Time Running: %02d Days: %02d Hours: %02d Minutes: %02d Seconds", days,
                    hours, minutes, seconds);
            myTimeLabel.setText(timeFormatted);
        }
    
        /*
         * This method will create the menu bar for the GUI.
         */
        private void createMenuBar() {
            myMenuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenu watcherMenu = new JMenu("File System Watcher");
            JMenu databaseMenu = new JMenu("Database");
            JMenu aboutMenu = new JMenu("About");
            myTimeLabel = new JLabel("Time not started.");
            myStartButton = new JMenuItem("Start");
            myStopButton = new JMenuItem("Stop");
            JMenuItem queryItem = new JMenuItem("Query Database(file extension)");
            JMenuItem closeItem = new JMenuItem("Close");
            myStartButton.setEnabled(true);
            myStopButton.setEnabled(false);
            fileMenu.add(myStartButton);
            fileMenu.add(myStopButton);
            fileMenu.add(queryItem);
            fileMenu.add(closeItem);
            closeItem.addActionListener(this);
            JMenuItem startWatcherItem = new JMenuItem("Start Watching");
            JMenuItem stopWatcherItem = new JMenuItem("Stop Watching");
            watcherMenu.add(startWatcherItem);
            watcherMenu.add(stopWatcherItem);
            JMenuItem connectDbItem = new JMenuItem("Connect to Database");
            JMenuItem disconnectDbItem = new JMenuItem("Disconnect Database");
            databaseMenu.add(connectDbItem);
            databaseMenu.add(disconnectDbItem);
            JMenuItem aboutHelpItem = new JMenuItem("About");
            aboutHelpItem.addActionListener(this);
            aboutMenu.add(aboutHelpItem);
            myMenuBar.add(fileMenu);
            myMenuBar.add(watcherMenu);
            myMenuBar.add(databaseMenu);
            myMenuBar.add(aboutMenu);
            myFrame.setJMenuBar(myMenuBar);
        }
    
        /*
         * This method will handle the actions of the user when they click on the menu
         * items,
         * This method will handle the actions of the user when they click on the menu
         * items,
         * different actions will be taken depending on the menu item clicked.
         */
        public void actionPerformed(final ActionEvent theEvent) {
            if (theEvent.getSource().equals(myStartButton) || theEvent.getSource().equals(myDirectoryStartButton)) {
                runningTime = 0;
                myTimeLabel.setText("Time not started.");
                myTimer.start();
                buttonReverse(false);
            } else if (theEvent.getSource().equals(myStopButton) || theEvent.getSource().equals(myDirectoryStopButton)) {
                myTimer.stop();
                buttonReverse(true);
            } else if (theEvent.getActionCommand().equals("Close")) {
                System.exit(0);
            }
            // Handle About
            else if (theEvent.getActionCommand().equals("About")) {
                JOptionPane.showMessageDialog(myFrame,
                        "Program Usage: This application watches file system changes.\n" +
                                "Version: 1.0\n" +
                                "Developers: Manjinder Ghuman, Ryder Deback, Brendan Tucker",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (theEvent.getSource().equals(myExtensionComboBox)
                    && !myExtensionComboBox.getSelectedItem().equals("")
                    && myExtensionComboBox.getEditor().getEditorComponent().hasFocus()) {
                checkFields();
                JOptionPane.showMessageDialog(myFrame, (String) myExtensionComboBox.getSelectedItem());
            } else if (theEvent.getSource().equals(myDirectoryBrowseButton)) {
                JFileChooser direcChooser = new JFileChooser();
                direcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                direcChooser.setAcceptAllFileFilterUsed(false); // Disabling the ability to select all files.
    
                int directoryValue = direcChooser.showOpenDialog(null);
                if (directoryValue == JFileChooser.APPROVE_OPTION) {
                    myDirectoryField.setText(direcChooser.getSelectedFile().getAbsolutePath());
                }
            } else if (theEvent.getSource().equals(myClearDirectoryButton)) {
                myDirectoryField.setText("");
                myExtensionComboBox.setSelectedItem("");
                myDatabaseField.setText("");
            } else if (theEvent.getSource().equals(myWriteDbButton)) {
                DatabaseConnection.connect();
            }
        }
    
        /* Helper method to clean up repeated lines. */
        private void buttonReverse(boolean theValue) {
            myStartButton.setEnabled(theValue);
            myDirectoryStartButton.setEnabled(theValue);
            myIsMonitoring = !theValue;
            myStopButton.setEnabled(!theValue);
            myDirectoryStopButton.setEnabled(!theValue);
        }
    
        private void setUpDocumentListeners() {
            DocumentListener theListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkFields();
                }
    
                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkFields();
                }
    
                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkFields();
                }
            };
    
            myDirectoryField = myMainPanel.getDirectoryField();
            myDirectoryField.getDocument().addDocumentListener(theListener);
            myDatabaseField = myMainPanel.getMyDatabaseField();
            myDatabaseField.getDocument().addDocumentListener(theListener);
            myExtensionField = (JTextField) myExtensionComboBox.getEditor().getEditorComponent();
            myExtensionField.getDocument().addDocumentListener(theListener);
        }
    
        public boolean isMonitoring() {
            return myIsMonitoring;
    }

    private void checkFields() {
        if (!myExtensionField.getText().equals("") && !myDirectoryField.getText().equals("")
                && !myDatabaseField.getText().equals("")) {
            if (!myDirectoryStopButton.isEnabled()) {
                myDirectoryStartButton.setEnabled(true);
                myDirectoryStopButton.setEnabled(false);
            }
        } else {
            myDirectoryStartButton.setEnabled(false);
        }
    }

}