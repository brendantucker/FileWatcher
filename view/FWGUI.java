import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import java.util.List;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class is the main GUI for the FileWatcher application. It creates the
 * main frame and sets up the menu bar, buttons, and event table.
 */
public class FWGUI implements ActionListener {
    // Frame to hold all of the GUI portions.
    private JFrame myFrame;
    // Menu bar for the GUI.
    private JMenuBar myMenuBar;
    // Timer variable to keep track of the time the user has been monitoring files.
    private int runningTime = 0;
    // Timer to keep track of the time the user has been monitoring files.
    private Timer myTimer;
    // Label to display the time the user has been monitoring files.
    private JLabel myTimeLabel;
    // Menu item to start monitoring files.
    private JMenuItem myMenuStart;
    // Menu item to stop monitoring files.
    private JMenuItem myMenuStop;
    // The weight of the split pane.
    private double splitPaneResizeWeight = 0.2;
    // Table that houses all the logged events.
    private FWEventTable myEventTable;
    // Table that will house all the results from query window.
    private FWEventTable myQueryTable;
    // The types of extensions for users to monitor.
    private JComboBox<String> myExtensionComboBox;
    // The different queries to be ran in query window.
    private JComboBox<String> myQueryComboBox;
    // Text fields for the directory, database, and extensions.
    private JTextField myDirectoryField, myDatabaseField, myExtensionField;
    // Buttons for the GUI.
    private JButton myDirectoryStartButton, myDirectoryStopButton, myWriteDbButton, myDirectoryBrowseButton,
            myResetDirectoryButton, myQueryButton;
    // Buttons for the image icons.
    private JButton myImgStartButton, myImgStopButton, myImgDBButton, myImgClearButton;
    // The main panel for the GUI.
    private FWPanel myMainPanel;
    // Panel for the query window
    private FWPanel myQueryPanel;
    // Boolean value for if the service is being watched and recorded.
    private boolean myIsMonitoring;
    // The directory watch service to monitor the directory and files.
    private DirectoryWatchService myDirectoryWatchService;
    // Boolean value for if the database is active.
    private boolean myDatabaseActive;
    // Label to display whether or not the database is connected.
    private JLabel myDatabaseConnectionLabel;

    private JMenuItem add10Item;

    private JMenuItem add100Item;

    private static FWGUI myInstance;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
        myFrame = new FWFrame().frameOutline();
        myFrame.setLayout(new BorderLayout());

        // Create the main panel and event table
        myMainPanel = new FWPanel();
        myQueryPanel = new FWPanel();
        myEventTable = new FWEventTable();
        myQueryTable = new FWEventTable();
        myIsMonitoring = false;
        // Methods to help break up the constructor and make it easier to read.
        setUpButtons();
        createMenuBar();
        timeAndDbLabel();
        setUpDocumentListeners();
        setUpFileViewer();

        // Set up the exit listener
        setUpExitListener();

        myFrame.add(myMainPanel, BorderLayout.NORTH);
        myFrame.setVisible(true);
    }

    private void setUpButtons() {
        myExtensionComboBox = myMainPanel.getExtensionBox();
        myExtensionComboBox.setEditable(true);
        myExtensionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object theDropdowObject = myExtensionComboBox.getSelectedItem();
                if (theDropdowObject.equals("Enter an extension")) {
                    myExtensionComboBox.setEditable(true);
                } else {
                    myExtensionComboBox.setEditable(false);
                }
            }
        });
        myExtensionComboBox.addActionListener(this);

        myDirectoryStartButton = addButtonActionListener(myMainPanel.getStartButton());
        myDirectoryStopButton = addButtonActionListener(myMainPanel.getStopButton());
        myDirectoryBrowseButton = addButtonActionListener(myMainPanel.getBrowseButton());
        myResetDirectoryButton = addButtonActionListener(myMainPanel.getResetButton());
        myQueryButton = addButtonActionListener(myMainPanel.getQueryButton());

        myWriteDbButton = addButtonActionListener(myMainPanel.getMyWriteDBButton());
        myWriteDbButton.setEnabled(false); // 🔹 Ensure "Write to Database" starts disabled

        myImgStartButton = addButtonActionListener(myMainPanel.getMyImgStarButton());
        myImgStopButton = addButtonActionListener(myMainPanel.getMyImgStopButton());
        myImgDBButton = addButtonActionListener(myMainPanel.getMyImgDBButton());
        myImgClearButton = addButtonActionListener(myMainPanel.getMyImgClearButton());
    }

    /**
     * Helper method for the setUpButtons method. This will add an action listener
     * to the button passed in and return the button.
     */
    private JButton addButtonActionListener(JButton theButton) {
        theButton.addActionListener(this);
        return theButton;
    }

    /**
     * This method will create the menu bar for the GUI. Cleaned up original version
     * to make it more readable.
     */
    private void createMenuBar() {
        myMenuBar = new JMenuBar();
        createFileMenu();
        createWatcherMenu();
        createDatabaseMenu();
        createAboutMenu();
        createImgButtons();
        myFrame.setJMenuBar(myMenuBar);
    }

    /**
     * Creates the first drop down menu choice for the GUI.
     */
    private void createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        myTimeLabel = new JLabel("Time not started.");
        myDatabaseConnectionLabel = new JLabel("Database not connected.");
        myMenuStart = new JMenuItem("Start");
        myMenuStop = new JMenuItem("Stop");
        JMenuItem queryItem = new JMenuItem("Query Database(file extension)");
        JMenuItem closeItem = new JMenuItem("Close");
        myMenuStart.setEnabled(false);
        myMenuStop.setEnabled(false);
        fileMenu.add(myMenuStart);
        fileMenu.add(myMenuStop);
        fileMenu.add(queryItem);
        fileMenu.add(closeItem);
        closeItem.addActionListener(this);
        myMenuBar.add(fileMenu);
    }

    /**
     * Creates the second drop down menu choice for the GUI.
     */
    private void createWatcherMenu() {
        JMenu watcherMenu = new JMenu("Debug");
        add10Item = new JMenuItem("Add 10 Events");
        add100Item = new JMenuItem("Add 100 Events");
        watcherMenu.add(add10Item);
        watcherMenu.add(add100Item);
        add10Item.addActionListener(this);
        add100Item.addActionListener(this);
        watcherMenu.add(add10Item);
        watcherMenu.add(add100Item);
        myMenuBar.add(watcherMenu);
    }

    /**
     * Creates the third drop down menu choice for the GUI.
     */
    private void createDatabaseMenu() {
        JMenu databaseMenu = new JMenu("Database");

        JMenuItem connectDbItem = new JMenuItem("Connect to Database");
        JMenuItem disconnectDbItem = new JMenuItem("Disconnect Database");
        databaseMenu.add(connectDbItem);
        databaseMenu.add(disconnectDbItem);
        connectDbItem.addActionListener(this);
        disconnectDbItem.addActionListener(this);

        myMenuBar.add(databaseMenu);
    }

    /**
     * Creates the fourth drop down menu choice for the GUI.
     */
    private void createAboutMenu() {
        JMenu aboutMenu = new JMenu("About");
        JMenuItem aboutHelpItem = new JMenuItem("About");
        aboutHelpItem.addActionListener(this);
        aboutMenu.add(aboutHelpItem);
        myMenuBar.add(aboutMenu);
    }

    /**
     * Creates the image buttons for the GUI and pushes them all the way to the end
     * of the menu bar.
     */
    private void createImgButtons() {
        myMenuBar.add(Box.createHorizontalGlue());
        myMenuBar.add(myImgStartButton);
        myMenuBar.add(myImgStopButton);
        myMenuBar.add(myImgDBButton);
        myMenuBar.add(myImgClearButton);
    }

    /**
     * This method will keep track of the time that the user has been monitoring
     * files.
     */
    private void timeAndDbLabel() {
        myTimer = new Timer(1000, (ActionEvent e) -> {
            runningTime++;
            timerLabelExtended();
        });
        myMenuStart.addActionListener(this);
        myMenuStop.addActionListener(this);
        // Create a panel for the time label
        JPanel bottomGuiPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(myDatabaseConnectionLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(myTimeLabel);

        bottomGuiPanel.add(leftPanel, BorderLayout.WEST);
        bottomGuiPanel.add(rightPanel, BorderLayout.EAST);
        myFrame.add(bottomGuiPanel, BorderLayout.SOUTH);
    }

    /**
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

    /**
     * Adds document listeners for the directory, database, and extension fields.
     */
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

    /**
     * This method will set up the file viewer for the GUI.
     */
    private void setUpFileViewer() {

        // Create a JSplitPane to divide the space between the main panel and the event
        // table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myMainPanel, myEventTable);
        splitPane.setResizeWeight(splitPaneResizeWeight);
        splitPane.setDividerSize(0);

        // Add the JSplitPane to the frame
        myFrame.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Sets up the exit listener for the GUI.
     */
    private void setUpExitListener() {
        myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        myFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void handleExit() {
        List<FileEvent> unsavedEvents = myEventTable.getData();

        if (!unsavedEvents.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(
                    myFrame,
                    "You have unsaved file events. Would you like to save them to the database before exiting?",
                    "Unsaved Data",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.CANCEL_OPTION) {
                return; // Stop exit, let user stay
            }

            if (choice == JOptionPane.YES_OPTION) {
                if (DatabaseConnection.getMyConnection() == null) {
                    int dbChoice = JOptionPane.showConfirmDialog(
                            myFrame,
                            "Database is not connected. Would you like to connect now?",
                            "Database Not Connected",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (dbChoice == JOptionPane.CANCEL_OPTION) {
                        return; // Stop exit
                    }

                    if (dbChoice == JOptionPane.YES_OPTION) {
                        if (!DatabaseConnection.connect()) {
                            JOptionPane.showMessageDialog(
                                    myFrame,
                                    "Failed to connect to the database. Events will not be saved.",
                                    "Database Connection Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                    } else {
                        System.exit(0);
                    }
                }

                FileEventDAO.insertFileEvents(unsavedEvents);
            }
        }
        System.exit(0);
    }

    /**
     * This method will handle the actions of the user when they click on the menu
     * items, different actions will be taken depending on the menu item clicked.
     */
    @Override
    public void actionPerformed(final ActionEvent theEvent) {
        Object source = theEvent.getSource();
        String command = theEvent.getActionCommand();

        // Start Monitoring
        if (source.equals(myMenuStart) || source.equals(myDirectoryStartButton) || source.equals(myImgStartButton)) {
            startMonitoring();
        }
        // Stop Monitoring
        else if (source.equals(myMenuStop) || source.equals(myDirectoryStopButton) || source.equals(myImgStopButton)) {
            stopMonitoring();
        }
        // Close Application
        else if (command.equals("Close")) {
            System.exit(0);
        }
        // Show About Dialog
        else if (command.equals("About")) {
            showAboutDialog();
        }
        // Connect to Database
        else if (command.equals("Connect to Database")) {
            boolean success = DatabaseConnection.connect();
            if (success) {
                setDatabaseConnected(true);
                JOptionPane.showMessageDialog(myFrame, "Connected to the database successfully!",
                        "Database Connection", JOptionPane.INFORMATION_MESSAGE);
                myDatabaseConnectionLabel.setText("Database connected.");
            } else {
                JOptionPane.showMessageDialog(myFrame, "Failed to connect to the database.",
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Disconnect from Database
        else if (command.equals("Disconnect Database")) {
            DatabaseConnection.disconnect();
            setDatabaseConnected(false);
            JOptionPane.showMessageDialog(myFrame, "Disconnected from the database.",
                    "Database Disconnection", JOptionPane.INFORMATION_MESSAGE);
            myDatabaseConnectionLabel.setText("Database not connected.");
        }
        // Extension Selection
        else if (source.equals(myExtensionComboBox) && !myExtensionField.getText().isEmpty()
                && !myExtensionComboBox.getSelectedItem().equals("Enter an extension")
                && myExtensionComboBox.getEditor().getEditorComponent().hasFocus()) {
            checkFields();
            JOptionPane.showMessageDialog(myFrame, (String) myExtensionComboBox.getSelectedItem());
        }
        // Browse Directory
        else if (source.equals(myDirectoryBrowseButton)) {
            browseDirectory();
        }
        // Clear Fields
        else if (source.equals(myResetDirectoryButton) || source.equals(myImgClearButton)) {
            clearFields();
        }
        // Write to Database
        else if (source.equals(myWriteDbButton) || source.equals(myImgDBButton)) {
            if (DatabaseConnection.getMyConnection() == null) {
                int choice = JOptionPane.showConfirmDialog(
                        myFrame,
                        "Database is not connected. Would you like to connect now?",
                        "Database Not Connected",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.CANCEL_OPTION) {
                    return; // Stop execution if the user cancels
                }

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DatabaseConnection.connect();
                    if (!success) {
                        JOptionPane.showMessageDialog(
                                myFrame,
                                "Failed to connect to the database. Events will not be saved.",
                                "Database Connection Error",
                                JOptionPane.ERROR_MESSAGE);
                        return; // Stop execution if connection fails
                    }
                    setDatabaseConnected(true);
                } else {
                    return; // Stop execution if the user chooses "No"
                }
            }
            // Write all stored events to the database
            int rowsInserted = 0;
            for (FileEvent event : myEventTable.getData()) {
                FileEventDAO.insertFileEvent(event);
                rowsInserted++;
            }

            JOptionPane.showMessageDialog(myFrame, rowsInserted + " events written to the database.",
                    "Database Write", JOptionPane.INFORMATION_MESSAGE);
        } else if (source.equals(add10Item)) {
            // Add 10 events to the event table for testing
            for (int i = 0; i < 10; i++) {
                myEventTable.addEvent(new FileEvent("DebugTestFile.test", "C:\\Users\\test\\subfolder\\subfolder",
                        "TESTEVENT", ".test", createDateString(), createTimeString()));
            }
        } else if (source.equals(myQueryButton)) {
            guiWindow();
            myQueryTable.clearTable();
            myQueryTable.revalidate();
            myQueryTable.repaint();
        } else if (source.equals(myQueryComboBox)) {
            myQueryTable.clearTable();
            if(myQueryComboBox.getSelectedItem().equals("Query 1")){
                FWEventTable queryResults = FileEventDAO.queryTxtFiles();

                for(FileEvent event : queryResults.getData()){
                    myQueryTable.addEvent(event);
                }
                myQueryTable.updateTable();
            } else if(myQueryComboBox.getSelectedItem().equals("Query 2")){
                System.out.println("Query2");
            } else if(myQueryComboBox.getSelectedItem().equals("Query 3")){
                System.out.println("Query3");
            }
            // Adding in the new table values.
            myQueryTable.revalidate();
            myQueryTable.repaint();
        } else if (source.equals(add100Item)) {
            // Add 10 events to the event table for testing
            for (int i = 0; i < 100; i++) {
                myEventTable.addEvent(new FileEvent("DebugTestFile.test", "C:\\Users\\test\\subfolder\\subfolder",
                        "TESTEVENT", ".test", createDateString(), createTimeString()));
            }
        }
    }

    private void guiWindow() {
        FWFrame queryFrame = new FWFrame();
        queryFrame.queryFrameSize(.8, .3);
        queryFrame.setLocationRelativeTo(null);
        queryFrame.setTitle("Query Window");
        queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        queryFrame.setLayout(new BorderLayout());

        JPanel queryGUI = myQueryPanel.FWQueryPanel();
        queryFrame.add(queryGUI, BorderLayout.NORTH);

        //FWEventTable queryEventTable = new FWEventTable();
        // for (FileEvent event : myEventTable.getData()) {
        //     queryEventTable.addEvent(event);
        // }

        JSplitPane queryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryGUI, myQueryTable);
        queryPane.setResizeWeight(splitPaneResizeWeight);
        queryPane.setDividerSize(0);

        // Add the JSplitPane to the frame
        queryFrame.add(queryPane, BorderLayout.CENTER);

        myQueryComboBox = myQueryPanel.getQueryPopupSelection();
        myQueryComboBox.addActionListener(this);

        queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        queryFrame.setVisible(true);
    }

    /**
     * Creates a date string in the format of yyyy-MM-dd HH:mm:ss
     * 
     * @return String representation of the current date
     */
    private String createDateString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }

    /**
     * Creates a time string in the format of HH:mm:ss
     * 
     * @return String representation of the current time.
     */
    private String createTimeString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * Method for if the user has hit the start button and all the correct fields
     * are filled.
     */
    private void startMonitoring() {
        myIsMonitoring = true;
        try {
            myDirectoryWatchService = new DirectoryWatchService(myDirectoryField.getText(), this);
            myDirectoryWatchService.start();
        } catch (IOException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "\"" + myDirectoryField.getText() + "\" is not a valid directory",
                    "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        runningTime = 0;
        myTimeLabel.setText("Time not started.");
        myTimer.start();
        buttonReverse(false);
    }

    /**
     * Method for if the user has hit the stop button.
     */
    private void stopMonitoring() {
        myTimer.stop();
        myIsMonitoring = false;
        buttonReverse(true);
        myDirectoryWatchService.stop();
    }

    /**
     * Method to show the about dialog box.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(myFrame,
                "Program Usage: This application watches file system changes.\n" +
                        "Version: 1.0\n" +
                        "Developers: Manjinder Ghuman, Ryder Deback, Brendan Tucker",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method to browse the directory and set the text field to the directory
     * chosen.
     */
    private void browseDirectory() {
        JFileChooser direcChooser = new JFileChooser();
        direcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        direcChooser.setAcceptAllFileFilterUsed(false);

        int directoryValue = direcChooser.showOpenDialog(null);
        if (directoryValue == JFileChooser.APPROVE_OPTION) {
            myDirectoryField.setText(direcChooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Method to reset the timer and set the label to "Time Not Started."
     */
    private void resetTimer() {
        if (myTimer != null) {
            myTimer.stop();
        }
        runningTime = 0;
        myTimeLabel.setText("Time Not Started.");
    }

    /**
     * Method to clear all the fields in the GUI and reset the buttons.
     */
    private void clearFields() {
        myDirectoryField.setText("");
        myExtensionComboBox.setSelectedItem("Enter an extension");
        myDatabaseField.setText("");
        resetTimer();
        DatabaseConnection.disconnect();
        myDatabaseActive = false;
        if (myDirectoryWatchService != null)
            myDirectoryWatchService.stop();
        myIsMonitoring = false;
        myEventTable.clearTable();
        checkFields();
    }

    /* Helper method - Flips state of start and stop buttons */
    private void buttonReverse(boolean theValue) {
        myMenuStart.setEnabled(theValue);
        myDirectoryStartButton.setEnabled(theValue);
        myImgStartButton.setEnabled(theValue);
        myMenuStop.setEnabled(!theValue);
        myDirectoryStopButton.setEnabled(!theValue);
        myImgStopButton.setEnabled(!theValue);
    }

    /**
     * Returns true if GUI is monitoring a directory. Used by DirectoryWatchService
     * to check if it should continue running.
     * 
     * @return true if monitoring, false otherwise
     */
    public boolean isMonitoring() {
        return myIsMonitoring;
    }

    /**
     * Sets the database connection status in the GUI.
     * 
     * @param theValue true if connected, false otherwise
     */
    public void setDatabaseConnected(boolean theValue) {
        myWriteDbButton.setEnabled(theValue);
        myQueryButton.setEnabled(theValue);
    }

    /**
     * Returns the event table for the GUI.
     * 
     * @return The event table for the GUI
     */
    public FWEventTable getEventTable() {
        return myEventTable;
    }

    /**
     * Returns the instance of the GUI.
     * 
     * @return The instance of the GUI
     */
    public static FWGUI getMyInstance() {
        return myInstance;
    }

    /**
     * Checks if the required fields are filled out correctly and enables/disables
     * Start buttons accordingly.
     */
    private void checkFields() {
        // Prevent the pressing of the stop buttons if DirectoryWatchService isnt
        // monitoring
        if (!myIsMonitoring) {
            myMenuStop.setEnabled(false);
            myDirectoryStopButton.setEnabled(false);
            myImgStopButton.setEnabled(false);
        }
        boolean hasDirectory = !myDirectoryField.getText().trim().isEmpty();
        boolean hasExtension = !myExtensionField.getText().trim().isEmpty()
                && !myExtensionField.getText().equals("Enter an extension");
        boolean hasDatabase = myDatabaseActive && !myDatabaseField.getText().trim().isEmpty();

        boolean enableStart = (hasDirectory && hasExtension) || hasDatabase;

        myDirectoryStartButton.setEnabled(enableStart);
        myMenuStart.setEnabled(enableStart);
        myImgStartButton.setEnabled(enableStart);

        // If conditions are not met, disable the start buttons
        if (!enableStart) {
            myDirectoryStartButton.setEnabled(false);
            myMenuStart.setEnabled(false);
            myImgStartButton.setEnabled(false);
        }
    }
}
