import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class is the main GUI for the FileWatcher application. It creates the
 * main frame and sets up the menu bar, buttons, and event table.
 */
public class FWGUI implements ActionListener {
    // Frame to hold all of the GUI portions, and the query popup.
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
    private JTextField myDirectoryField, myExtensionField;
    // Buttons for the GUI.
    private JButton myDirectoryStartButton, myDirectoryStopButton, myWriteDbButton, myDirectoryBrowseButton,
            myResetDirectoryButton, myQueryButton, myDatabaseResetButton;
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
    // Debug buttons created to make fake data for quicker testing.
    private JMenuItem add10Item, add100Item, myAdd1OfEachItem;
    // Connect and disconnect from the database buttons.
    private JMenuItem myConnectDbItem, myDisconnectDbItem;
    // Checkboxes for querying the database for specific extensions.
    private JPanel myQueryCheckBoxPanel;
    // Frame for the query panel pop up window.
    private FWFrame myQueryFrame;
    // Array for holding all the checkbox options
    private JCheckBox[] myExtensionCheckBox;

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
        createEmailMenu();
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

        JMenuItem exportCsvItem = new JMenuItem("Export Query Results to CSV");
        fileMenu.add(exportCsvItem);
        exportCsvItem.addActionListener(this);

    }

    /**
     * Creates the second drop down menu choice for the GUI.
     */
    private void createWatcherMenu() {
        JMenu watcherMenu = new JMenu("Debug");
        add10Item = new JMenuItem("Add 10 Events");
        add100Item = new JMenuItem("Add 100 Events");
        watcherMenu.add(add100Item);
        add10Item.addActionListener(this);
        watcherMenu.add(add10Item);
        add100Item = new JMenuItem("Add 100 Events");
        watcherMenu.add(add100Item);
        add100Item.addActionListener(this);
        myAdd1OfEachItem = new JMenuItem("Add one of each file type");
        watcherMenu.add(myAdd1OfEachItem);
        myAdd1OfEachItem.addActionListener(this);
        watcherMenu.add(myAdd1OfEachItem);
        myMenuBar.add(watcherMenu);
    }

    /**
     * Creates the third drop down menu choice for the GUI.
     */
    private void createDatabaseMenu() {
        JMenu databaseMenu = new JMenu("Database");

        myConnectDbItem = new JMenuItem("Connect to Database");
        myDisconnectDbItem = new JMenuItem("Disconnect Database");
        // Disabling until database connection is established.
        myDisconnectDbItem.setEnabled(false);
        databaseMenu.add(myConnectDbItem);
        databaseMenu.add(myDisconnectDbItem);
        myConnectDbItem.addActionListener(this);
        myDisconnectDbItem.addActionListener(this);

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

    private void createEmailMenu() {
        JMenu emailMenu = new JMenu("Email");
        JMenuItem sendEmailItem = new JMenuItem("Send File via Email");
        sendEmailItem.addActionListener(this);
        emailMenu.add(sendEmailItem);
        myMenuBar.add(emailMenu);

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
        myExtensionField = (JTextField) myExtensionComboBox.getEditor().getEditorComponent();
        // Was starting out blank for some reason.
        // Was starting out blank for some reason.
        myExtensionField.setText(("All extensions"));
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
                myConnectDbItem.setEnabled(false);
                myDisconnectDbItem.setEnabled(true);
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
            myConnectDbItem.setEnabled(true);
            myDisconnectDbItem.setEnabled(false);
            DatabaseConnection.disconnect();
            setDatabaseConnected(false);
            JOptionPane.showMessageDialog(myFrame, "Disconnected from the database.",
                    "Database Disconnection", JOptionPane.INFORMATION_MESSAGE);
            myDatabaseConnectionLabel.setText("Database not connected.");
        }
        // Extension Selection
        else if (source.equals(myExtensionComboBox) && !myExtensionField.getText().isEmpty()
                && !myExtensionComboBox.getSelectedItem().equals("Enter an extension")
        // && myExtensionComboBox.getEditor().getEditorComponent().hasFocus() Possibly unnecessary?
        ) {
            checkFields();
            myEventTable.filterTable('.' + myExtensionComboBox.getSelectedItem().toString());
            if (myExtensionComboBox.getSelectedItem().equals("All extensions")) {
                myEventTable.updateTable();
            }
        }
        // Send File via Email
        else if (command.equals("Send File via Email")) {
            String recipient = JOptionPane.showInputDialog(myFrame, "Enter recipient email:", "Send Email",
                    JOptionPane.QUESTION_MESSAGE);
            if (recipient != null && !recipient.isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    EmailSender.sendEmailWithAttachment(recipient, "File Watcher Report",
                            "Please find the attached file.", filePath);
                    JOptionPane.showMessageDialog(myFrame, "Email Sent Successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        // Browse Directory
        else if (source.equals(myDirectoryBrowseButton)) {
            browseDirectory();
        }
        // Clear Fields
        else if (source.equals(myResetDirectoryButton) || source.equals(myImgClearButton)) {
            clearFields();
        }
        // Export Query Results to CSV
        else if (command.equals("Export to CSV")) {
            exportQueryResultsToCSV();
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
        } else if (source.equals(myQueryButton)) {
            guiWindow();
        } else if (source.equals(myQueryComboBox)) {
            myQueryTable.clearTable();
            FWEventTable queryResults = new FWEventTable();
            myQueryCheckBoxPanel.setVisible(false);
            myQueryFrame.revalidate(); 
            myQueryFrame.repaint(); 
            //Erasing the selected checkboxes as a precaution
            for(JCheckBox checkBox: myExtensionCheckBox){
                checkBox.setSelected(false);
            }
            if (myQueryComboBox.getSelectedItem().equals("Query 1 - All events from today")) {
                queryResults = FileEventDAO.fileEventsFromToday();
            } else if (myQueryComboBox.getSelectedItem().equals("Query 2 - Top 5 frequently modified file types")) {
                queryResults = FileEventDAO.topFiveExtensions();
            } else if (myQueryComboBox.getSelectedItem().equals("Query 3 - Most Common Events for Each Extension")) {
                queryResults = FileEventDAO.mostCommonEventsPerExtension();
            } else if (myQueryComboBox.getSelectedItem().equals("Select specific extensions")) {
                myQueryCheckBoxPanel.setVisible(true);
                myQueryFrame.revalidate();   // Revalidate all components within the frame
                myQueryFrame.repaint();      // Repaint the frame to update the UI
                myQueryFrame.pack();         // Resizes the frame to fit the components
                myQueryFrame.queryFrameSize(.8, .3);
            }
            // Adding in the new table values.
            if (queryResults.getData().size() != 0) {
                for (FileEvent event : queryResults.getData()) {
                    myQueryTable.addEvent(event);
                }
                myQueryTable.updateTable();
            } else if (myQueryComboBox.getSelectedItem().equals("Query 2")) {
                System.out.println("Query2");
            } else if (myQueryComboBox.getSelectedItem().equals("Query 3")) {
                System.out.println("Query3");
            }
        } else if (source.equals(add10Item)) {
            // Add 10 events to the event table for testing
            for (int i = 0; i < 10; i++) {
                myEventTable.addEvent(new FileEvent("DebugTestFile.test", "C:\\Users\\test\\subfolder\\subfolder",
                        "TESTEVENT", ".test", createDateString(), createTimeString()));
            }
        } else if (source.equals(add100Item)) {
            // Add 10 events to the event table for testing
            for (int i = 0; i < 100; i++) {
                myEventTable.addEvent(new FileEvent("DebugTestFile.test", "C:\\Users\\test\\subfolder\\subfolder",
                        "TESTEVENT", ".test", createDateString(), createTimeString()));
            }
        } else if (source.equals(myAdd1OfEachItem)) {
            runDummyInsertion();
        }
    }

    private void guiWindow() {
        myQueryFrame = new FWFrame();
        myQueryFrame.queryFrameSize(.8, .3);
        myQueryFrame.setLocationRelativeTo(null);
        myQueryFrame.setTitle("Query Window");
        myQueryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myQueryFrame.setLayout(new BorderLayout());

        JPanel queryGUI = myQueryPanel.FWQueryPanel();
        myQueryFrame.add(queryGUI, BorderLayout.NORTH);

        myQueryCheckBoxPanel = setUpQueryCheckBoxes();

        myQueryCheckBoxPanel.setVisible(false);

        JSplitPane middleQueryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myQueryCheckBoxPanel, myQueryTable);
        middleQueryPane.setResizeWeight(splitPaneResizeWeight);
        middleQueryPane.setDividerSize(2);

        JSplitPane queryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryGUI, middleQueryPane);
        queryFrame.add(queryGUI, BorderLayout.NORTH);

         // Create Export Button
        JButton exportCsvButton = new JButton("Export to CSV");
        exportCsvButton.addActionListener(this);
        queryGUI.add(exportCsvButton); // Add button to the panel

        JSplitPane queryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryGUI, myQueryTable);
        queryPane.setResizeWeight(splitPaneResizeWeight);
        queryPane.setDividerSize(0);

        // Add the JSplitPane to the frame
        myQueryFrame.add(queryPane, BorderLayout.CENTER);

        myQueryComboBox = myQueryPanel.getQueryPopupSelection();
        myQueryComboBox.addActionListener(this);

        myDatabaseResetButton = myQueryPanel.getDatabaseResetButton();
        myDatabaseResetButton.addActionListener(this);

        myQueryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        myQueryFrame.setVisible(true);
    }

    private JPanel setUpQueryCheckBoxes() {
        JPanel queryCheckboxesPanel = new JPanel();
        String[] extensions = { "test", "docx", "pdf", "txt", "png", "jpg", "jpeg", "gif", "mp3", "mp4", "wav", "avi",
                "mov", "csv" };
        List<String> extensionList = new ArrayList<>();

        myExtensionCheckBox = new JCheckBox[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            myExtensionCheckBox[i] = new JCheckBox(extensions[i]);

            myExtensionCheckBox[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox curCheckBox = (JCheckBox) e.getSource();
                    String curExtension = curCheckBox.getText();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if(!extensionList.contains(curExtension)) {
                            extensionList.add(curExtension);
                        }
                    } else {
                        extensionList.remove(curExtension);
                    }
                    FWEventTable tempQueryResults = FileEventDAO.querySpecificExtensions(extensionList);
                    myQueryTable.clearTable();
                    for(FileEvent event: tempQueryResults.getData()){
                        myQueryTable.addEvent(event);
                    }
                    myQueryTable.updateTable();
                    myQueryTable.revalidate();
                    myQueryTable.repaint();
                }
            });
            queryCheckboxesPanel.add(myExtensionCheckBox[i]);
        }
        return queryCheckboxesPanel;
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
        // Disable ability to modify directory and extension fields while monitoring
        // Disable ability to modify directory and extension fields while monitoring
        myDirectoryField.setEditable(false);
        myExtensionField.setEditable(false);
        myExtensionComboBox.setEditable(false);

    }

    /**
     * Method for if the user has hit the stop button.
     */
    private void stopMonitoring() {
        myTimer.stop();
        myIsMonitoring = false;
        buttonReverse(true);
        myDirectoryWatchService.stop();
        // Enable ability to modify directory and extension fields while not monitoring
        // Enable ability to modify directory and extension fields while not monitoring
        myDirectoryField.setEditable(true);
        myExtensionField.setEditable(true);
        myExtensionComboBox.setEditable(true);
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

    private void runDummyInsertion() {
        String dummyDate = LocalDate.now().toString();
        String dummyDatePlus3 = LocalDate.now().plusDays(3).toString();
        String dummyDatePlus10 = LocalDate.now().plusDays(10).toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dummyTime = LocalTime.now().format(formatter).toString();
        String dummyTimePlus6 = LocalTime.now().plusHours(6).format(formatter).toString();
        String dummyTimeMinus6 = LocalTime.now().plusHours(-6).format(formatter).toString();
        addDummyData("Dummy DUMB", "C:\\Users\\test\\subfolder\\subfolder", "TRASH", ".dumb", dummyDate, dummyTime);
        addDummyData("Dummy TEST", "C:\\Users\\test\\subfolder\\subfolder", "CREATED", ".test", dummyDate,
                dummyTimePlus6);
        addDummyData("Dummy DOCX", "C:\\Users\\test\\subfolder\\subfolder", "CREATED", ".docx", dummyDatePlus3,
                dummyTime);
        addDummyData("Dummy PDF", "C:\\Users\\test\\subfolder\\subfolder", "CREATED", ".pdf", dummyDate,
                dummyTimePlus6);
        addDummyData("Dummy TXT", "C:\\Users\\test\\subfolder\\subfolder", "DELETED", ".txt", dummyDatePlus10,
                dummyTime);
        addDummyData("Dummy PNG", "C:\\Users\\test\\subfolder\\subfolder", "DELETED", ".png", dummyDatePlus10,
                dummyTimePlus6);
        addDummyData("Dummy JPG", "C:\\Users\\test\\subfolder\\subfolder", "DELETED", ".jpg", dummyDatePlus3,
                dummyTimeMinus6);
        addDummyData("Dummy JPEG", "C:\\Users\\test\\subfolder\\subfolder", "DELETED", ".jpeg", dummyDatePlus3,
                dummyTime);
        addDummyData("Dummy GIF", "C:\\Users\\test\\subfolder\\subfolder", "MODIFIED", ".gif", dummyDatePlus10,
                dummyTime);
        addDummyData("Dummy MP3", "C:\\Users\\test\\subfolder\\subfolder", "MODIFIED", ".mp3", dummyDatePlus10,
                dummyTimePlus6);
        addDummyData("Dummy MP4", "C:\\Users\\test\\subfolder\\subfolder", "CREATED", ".mp4", dummyDate,
                dummyTimePlus6);
        addDummyData("Dummy WAV", "C:\\Users\\test\\subfolder\\subfolder", "CREATED", ".wav", dummyDatePlus3,
                dummyTimeMinus6);
        addDummyData("Dummy AVI", "C:\\Users\\test\\subfolder\\subfolder", "DELETED", ".avi", dummyDate,
                dummyTimeMinus6);
        addDummyData("Dummy MOV", "C:\\Users\\test\\subfolder\\subfolder", "MODIFIED", ".mov", dummyDatePlus3,
                dummyTimeMinus6);
        addDummyData("Dummy CSV", "C:\\Users\\test\\subfolder\\subfolder", "MODIFIED", ".csv", dummyDatePlus10,
                dummyTime);
    }

    private void addDummyData(String theFileName, String theFilePath, String theEventType, String theExtension,
            String theDate, String theTime) {
        // Adding one of every item into the menu to help show off the filtering
        // functions.
        myEventTable.addEvent(new FileEvent(theFileName, theFilePath, theEventType, theExtension, theDate, theTime));
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
        boolean hasDatabase = myDatabaseActive;

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
        // Do not enable start buttons if already monitoring
        if (myIsMonitoring) {
            myDirectoryStartButton.setEnabled(false);
            myMenuStart.setEnabled(false);
            myImgStartButton.setEnabled(false);
        }
    }
    private void exportQueryResultsToCSV() {
        List<FileEvent> events = myQueryTable.getData();
    
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(myFrame, "No query results available for export. Run a query first.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
    
        int userSelection = fileChooser.showSaveDialog(myFrame);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }
    
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("File Watcher Query Results");
            writer.println("Export Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            writer.println("File Name,File Path,Event Type,Extension,Event Time");
    
            for (FileEvent event : events) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        event.getFileName(),
                        event.getFilePath(),
                        event.getEventType(),
                        event.getExtension(),
                        event.getEventTime());
            }
    
            JOptionPane.showMessageDialog(myFrame, "Export successful: " + filePath, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(myFrame, "Error writing to file.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    
    

    

}
