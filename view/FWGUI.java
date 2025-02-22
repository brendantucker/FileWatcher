import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class FWGUI implements ActionListener {
    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private int runningTime = 0;
    private Timer myTimer;
    private JLabel myTimeLabel;
    private JMenuItem myStartButton;
    private JMenuItem myStopButton;
    private double splitPaneResizeWeight = 0.2;
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
    private DirectoryWatchService myDirectoryWatchService;
    private static FWGUI myInstance; 
    
        /*
         * Constructor for the GUI. This will create the GUI and set up the menu bar.
         */
    public FWGUI() {
        myInstance = this;
        myFrame = new FWFrame().frameOutline();
        myFrame.setLayout(new BorderLayout());
    
        // Create the main panel and event table
        myMainPanel = new FWPanel();
        myEventTable = new FWEventTable();
        myIsMonitoring = false;
    
        createMenuBar();
        timeKeeper();
        setUpButtons();
        setUpDocumentListeners();
        setUpFileViewer();

        // Set up the exit listener
        setUpExitListener();
    
        myFrame.add(myMainPanel, BorderLayout.NORTH);
        myFrame.setVisible(true);
    }
    
    private void setUpFileViewer() {
    
        // Create a JSplitPane to divide the space between the main panel and the event table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myMainPanel, myEventTable);
        splitPane.setResizeWeight(splitPaneResizeWeight);
        splitPane.setDividerSize(0);
    
        // Add the JSplitPane to the frame
        myFrame.add(splitPane, BorderLayout.CENTER);
    }
    
    private void setUpButtons() {
        myExtensionComboBox = myMainPanel.getExtensionBox();
        myExtensionComboBox.setEditable(true);
        myExtensionComboBox.addActionListener(this);
    
        myDirectoryStartButton = myMainPanel.getStartButton();
        myDirectoryStartButton.addActionListener(this);
        myDirectoryStartButton.setEnabled(false); // Disable until directory is chosen
    
        myDirectoryStopButton = myMainPanel.getStopButton();
        myDirectoryStopButton.addActionListener(this);
    
        myDirectoryBrowseButton = myMainPanel.getBrowseButton();
        myDirectoryBrowseButton.addActionListener(this);
    
        myClearDirectoryButton = myMainPanel.getClearButton();
        myClearDirectoryButton.addActionListener(this);
    
        myWriteDbButton = myMainPanel.getMyWriteDBButton();
        myWriteDbButton.setEnabled(false); // Disable until database is connected
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
       // JMenu watcherMenu = new JMenu("File System Watcher"); Removed because it is duplicated
        JMenu databaseMenu = new JMenu("Database");
        JMenu aboutMenu = new JMenu("About");
        myTimeLabel = new JLabel("Time not started.");
        myStartButton = new JMenuItem("Start");
        myStopButton = new JMenuItem("Stop");
        JMenuItem queryItem = new JMenuItem("Query Database(file extension)");
        JMenuItem closeItem = new JMenuItem("Close");
        myStartButton.setEnabled(false);
        myStopButton.setEnabled(false);
        fileMenu.add(myStartButton);
        fileMenu.add(myStopButton);
        fileMenu.add(queryItem);
        fileMenu.add(closeItem);
        closeItem.addActionListener(this);
        //JMenuItem startWatcherItem = new JMenuItem("Start Watching");
        //JMenuItem stopWatcherItem = new JMenuItem("Stop Watching");
        //watcherMenu.add(startWatcherItem);
        //watcherMenu.add(stopWatcherItem);

        JMenuItem connectDbItem = new JMenuItem("Connect to Database");
        JMenuItem disconnectDbItem = new JMenuItem("Disconnect Database");
        databaseMenu.add(connectDbItem);
        databaseMenu.add(disconnectDbItem);
        connectDbItem.addActionListener(this);
        disconnectDbItem.addActionListener(this);


        JMenuItem aboutHelpItem = new JMenuItem("About");
        aboutHelpItem.addActionListener(this);
        aboutMenu.add(aboutHelpItem);
        myMenuBar.add(fileMenu);
        //myMenuBar.add(watcherMenu);
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
            myIsMonitoring = true; //Must be true for DirectoryWatchService to run

            //Create and start a new DirectoryWatchService for chosen directory
            try {
                myDirectoryWatchService = new DirectoryWatchService(myDirectoryField.getText(), this); //Throws if given invalid directory
                myDirectoryWatchService.start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,  "\"" + myDirectoryField.getText() + "\" is not a valid directory" , "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null,  "\"" + myDirectoryField.getText() + "\" is not a valid directory" , "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            runningTime = 0;
            myTimeLabel.setText("Time not started.");
            myTimer.start();
            buttonReverse(false);
            
        } else if (theEvent.getSource().equals(myStopButton) || theEvent.getSource().equals(myDirectoryStopButton)) {
            myTimer.stop();
            myIsMonitoring = false;
            buttonReverse(true);
            myDirectoryWatchService.stop();
        } else if (theEvent.getActionCommand().equals("Close")) {
            System.exit(0);
        }
        // Handle About button
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
            myTimeLabel.setText("Time Not Started.");
        } else if (theEvent.getSource().equals(myWriteDbButton)) {
            storeFilesToDatabase();
        }
        else if (theEvent.getActionCommand().equals("Connect to Database")) {
            boolean success = DatabaseConnection.connect();
            if (success) {
                JOptionPane.showMessageDialog(myFrame, "Connected to the database successfully!", "Database Connection", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(myFrame, "Failed to connect to the database.", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (theEvent.getActionCommand().equals("Disconnect Database")) {
            DatabaseConnection.disconnect();
            JOptionPane.showMessageDialog(myFrame, "Disconnected from the database.", "Database Disconnection", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /* Helper method - Flips state of start and stop buttons */
    private void buttonReverse(boolean theValue) {
        myStartButton.setEnabled(theValue);
        myDirectoryStartButton.setEnabled(theValue);
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
    /**
     * Returns true if GUI is monitoring a directory. Used by DirectoryWatchService
     * to check if it should continue running.
     * @return true if monitoring, false otherwise
     */
    public boolean isMonitoring() {
        return myIsMonitoring;
    }

     public FWEventTable getEventTable() {
        return myEventTable;
    }

    private void checkFields() {
        boolean hasDirectory = !myDirectoryField.getText().trim().isEmpty();

        // Enable the start button if the directory is provided.
        myDirectoryStartButton.setEnabled(hasDirectory);
        myStartButton.setEnabled(hasDirectory);

        // Stop button remains disabled until Start is clicked
        myDirectoryStopButton.setEnabled(false);
        myStopButton.setEnabled(false);

    }

    /**
     * Sets the database connection status in the GUI.
     * @param theValue true if connected, false otherwise
     */
    public void setDatabaseConnected(boolean theValue) {
        myWriteDbButton.setEnabled(theValue);
    }

    public static FWGUI getMyInstance() {
        return myInstance;
    }

    public void storeFilesToDatabase() {
        int rowsInserted = 0;
        for (FileEvent event : myEventTable.getData()) {
            FileEventDAO.insertFileEvent(event);
            rowsInserted++;
        }

        JOptionPane.showMessageDialog(myFrame, rowsInserted + " events written to the database.", "Database Write", JOptionPane.INFORMATION_MESSAGE);
    }

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
    
        // Check if there are unsaved events
        if (!unsavedEvents.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(
                    myFrame,
                    "You have unsaved file events. Would you like to save them to the database before exiting?",
                    "Unsaved Data",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
    
            if (choice == JOptionPane.CANCEL_OPTION) {
                return; // Do nothing, let user stay
            }
    
            if (choice == JOptionPane.YES_OPTION) {
                // Check if the database is connected
                if (DatabaseConnection.getMyConnection() == null) {
                    int dbChoice = JOptionPane.showConfirmDialog(
                            myFrame,
                            "Database is not connected. Would you like to connect now?",
                            "Database Not Connected",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
    
                    if (dbChoice == JOptionPane.CANCEL_OPTION) {
                        return; // Stop exit, let user stay
                    }
    
                    if (dbChoice == JOptionPane.YES_OPTION) {
                        if (!DatabaseConnection.connect()) {
                            JOptionPane.showMessageDialog(
                                    myFrame,
                                    "Failed to connect to the database. Events will not be saved.",
                                    "Database Connection Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            System.exit(0); // Exit without saving
                        }
                    } else {
                        System.exit(0); // User chose not to connect, exit without saving
                    }
                }
    
                // If the database is connected, insert the events
                FileEventDAO.insertFileEvents(unsavedEvents);
            }
        }
    
        // Proceed with exit
        System.exit(0);
    }
    

    

}