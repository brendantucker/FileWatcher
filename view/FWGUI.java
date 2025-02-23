import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

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
    private JMenuItem myMenuStart;
    private JMenuItem myMenuStop;
    private double splitPaneResizeWeight = 0.2;
    private FWEventTable myEventTable;
    private JComboBox<String> myExtensionComboBox;
    private JTextField myDirectoryField, myDatabaseField, myExtensionField;
    private JButton myDirectoryStartButton, myDirectoryStopButton, myWriteDbButton, myDirectoryBrowseButton,
            myClearDirectoryButton;
    private JButton myImgStartButton, myImgStopButton, myImgDBButton, myImgClearButton;
    private FWPanel myMainPanel;
    private boolean myIsMonitoring;
    private DirectoryWatchService myDirectoryWatchService;
    private boolean myDatabaseActive;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
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

        myImgStartButton = myMainPanel.getMyImgStarButton();
        myImgStartButton.addActionListener(this);

        myImgStopButton = myMainPanel.getMyImgStopButton();
        myImgStopButton.addActionListener(this);

        myImgDBButton = myMainPanel.getMyImgDBButton();
        myImgDBButton.addActionListener(this);

        myImgClearButton = myMainPanel.getMyImgClearButton();
        myImgClearButton.addActionListener(this);
    }

    /*
     * This method will keep track of the time that the user has been monitoring
     * files.
     */
    private void timeKeeper() {
        myTimer = new Timer(1000, (ActionEvent e) -> {
            runningTime++;
            timerLabelExtended();
        });
        myMenuStart.addActionListener(this);
        myMenuStop.addActionListener(this);
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

    /*
     * This method will handle the actions of the user when they click on the menu
     * items, different actions will be taken depending on the menu item clicked.
     */
    public void actionPerformed(final ActionEvent theEvent) {
        if (theEvent.getSource().equals(myMenuStart) || theEvent.getSource().equals(myDirectoryStartButton)
                || theEvent.getSource().equals(myImgStartButton)) {
            myIsMonitoring = true; // Must be true for DirectoryWatchService to run

            // Create and start a new DirectoryWatchService for chosen directory
            try {
                // Throws if given invalid directory.
                myDirectoryWatchService = new DirectoryWatchService(myDirectoryField.getText(), this);
                myDirectoryWatchService.start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "\"" + myDirectoryField.getText() + "\" is not a valid directory",
                        "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null, "\"" + myDirectoryField.getText() + "\" is not a valid directory",
                        "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            runningTime = 0;
            myTimeLabel.setText("Time not started.");
            myTimer.start();
            buttonReverse(false);
        } else if (theEvent.getSource().equals(myMenuStop) || theEvent.getSource().equals(myDirectoryStopButton)
                || theEvent.getSource().equals(myImgStopButton)) {
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
                && !myExtensionField.getText().equals("")
                && !myExtensionComboBox.getSelectedItem().equals("Enter an extension")
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
        } else if (theEvent.getSource().equals(myClearDirectoryButton)
                || theEvent.getSource().equals(myImgClearButton)) {
            myDirectoryField.setText("");
            myExtensionComboBox.setSelectedItem("Enter an extension");
            myDatabaseField.setText("");
            myTimeLabel.setText("Time Not Started.");
            myMenuStop.setEnabled(false);
            myDirectoryStopButton.setEnabled(false);
            myImgStartButton.setEnabled(false);
            myImgStopButton.setEnabled(false);
            myDirectoryStartButton.setEnabled(false);
            myWriteDbButton.setEnabled(false);
            DatabaseConnection.disconnect();
            myDatabaseActive = false;
        } else if (theEvent.getSource().equals(myWriteDbButton) || theEvent.getSource().equals(myImgDBButton)) {
            myDatabaseActive = DatabaseConnection.connect();
            checkFields();
        }
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

    public FWEventTable getEventTable() {
        return myEventTable;
    }

    private void checkFields() {
        // If the user wants to only write to local directory.
        if (!myDirectoryField.getText().equals("")
                && !myExtensionField.getText().equals("Enter an extension")
                && !myExtensionField.getText().equals("")
                && !myDatabaseActive && myDatabaseField.getText().equals("")) {
            if (!myDirectoryStopButton.isEnabled()) {
                buttonReverse(true);
            }
        } else if (myDatabaseActive && !myDatabaseField.getText().equals("")) {
            if (!myDirectoryStopButton.isEnabled()) {
                buttonReverse(true);
            }
        } else {
            myDirectoryStartButton.setEnabled(false);
            myMenuStart.setEnabled(false);
            myImgStartButton.setEnabled(false);
        }
    }
}