import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import java.time.LocalDateTime;

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



public class FWGUI implements ActionListener {

    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private int runningTime = 0;
    private Timer myTimer;
    private JLabel myTimeLabel;
    private JMenuItem myStartButton;
    private JMenuItem myStopButton;
    private String[] EXTENSION_TYPES = { "", "DOCX", "PDF", "TXT", "PNG", "JPG", "JPEG", "GIF", "MP3", "MP4", "WAV",
            "AVI", "MOV" };
    private JComboBox<String> myExtensionComboBox;
    private JTextField myDirectoryField;
    private JButton myDirectoryButton;
    private JButton myClearDirectoryButton;
    private JButton myImgStartButton;
    private JButton myImgStopButton;
    private FWEventTable myEventTable;
    private JPanel myTopPanel;
    private double splitPaneResizeWeight = 0.1;

    private Thread myFileWatcherThread;
    private FileWatcher myFileWatcher;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
        super();
        myFrame = new FWFrame().frameOutline();
        // Create the menu bar and start the timer when necessary.
        createMenuBar();

        // Create topPanel and add components to it.
        myTopPanel = new JPanel(new BorderLayout());
        JPanel imageButtons = imageButtons();
        JPanel dropDownMenus = dropDownMenus();

        myTopPanel.add(imageButtons, BorderLayout.NORTH);
        myTopPanel.add(dropDownMenus, BorderLayout.CENTER);

        timeKeeper();
        // Create a panel for the time label
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.add(myTimeLabel);
        // Add the time panel to the frame.
        myFrame.add(timePanel, BorderLayout.SOUTH);

        // Setup and add FileEvent table to GUI
        myEventTable = new FWEventTable();
        // myFrame.add(myEventTable, BorderLayout.CENTER); //Set to CENTER to prevent
        // the table covering other GUI elements

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myTopPanel, myEventTable);
        splitPane.setDividerSize(0); // Hide the divider
        splitPane.setResizeWeight(splitPaneResizeWeight);
        myFrame.add(splitPane, BorderLayout.CENTER);

        // Add 100 test events to the table to test scrolling
        for (int i = 0; i < 100; i++) {
            myEventTable.addEvent(new FileEvent("TestFile.txt", "C:/path/to/TestFile.txt", EventType.FILECREATED, "txt",
                    LocalDateTime.of(2025, 2, 2, 12, 27)));
        }

        myFrame.setVisible(true);
    }

    private JPanel imageButtons() {
        // Create a panel for the image buttons
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));

        // Load the images
        ImageIcon startImgIcon = new ImageIcon("files/startWatching.png");
        myImgStartButton = new JButton(startImgIcon);
        myImgStartButton.addActionListener(this);
        ImageIcon stopImageIcon = new ImageIcon("files/stopWatching.png");
        myImgStopButton = new JButton(stopImageIcon);
        myImgStopButton.addActionListener(this);

        // Add the image buttons to the panel
        imagePanel.add(myImgStartButton);
        imagePanel.add(myImgStopButton);

        // Add the image panel to the frame
        return imagePanel;

    }

    private JPanel dropDownMenus() {
        myExtensionComboBox = new JComboBox<>(EXTENSION_TYPES);
        myExtensionComboBox.setEditable(true);
        myExtensionComboBox.addActionListener(this);

        JLabel extensionLable = new JLabel(
                "Select a file extension, a directory, and click Watch to begin File System Monitor.");
        JPanel dropDownPanels = new JPanel();
        dropDownPanels.setLayout(new BoxLayout(dropDownPanels, BoxLayout.Y_AXIS));

        JLabel directLabel = new JLabel("Directory to monitor:");
        myDirectoryField = new JTextField();
        myDirectoryButton = new JButton("Browse Directory");
        myDirectoryButton.addActionListener(this);
        myClearDirectoryButton = new JButton("Clear Directory");
        myClearDirectoryButton.addActionListener(this);

        JPanel directoryPanel = new JPanel();
        directoryPanel.add(myDirectoryButton);
        directoryPanel.add(myClearDirectoryButton);

        dropDownPanels.add(extensionLable);
        dropDownPanels.add(myExtensionComboBox);
        dropDownPanels.add(directLabel);
        dropDownPanels.add(myDirectoryField);
        dropDownPanels.add(directoryPanel);

        JPanel containPanel = new JPanel();
        containPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        containPanel.add(dropDownPanels);

        return containPanel;
    }

    /*
     * This method will keep track of the time that the user has been monitoring
     * files.
     */
    private void timeKeeper() {
        // Create a timer to keep track of time (VSCode is extremely helpful.)
        myTimer = new Timer(1000, (ActionEvent e) -> {
            runningTime++;
            timerLabelExtended();
        });
        // Add action listeners to the start and stop buttons.
        myStartButton.addActionListener(this);
        myStopButton.addActionListener(this);
    }

    /*
     * This method will create the menu bar for the GUI.
     */
    private void createMenuBar() {
        // Create the menu bar
        myMenuBar = new JMenuBar();

        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu watcherMenu = new JMenu("File System Watcher");
        JMenu databaseMenu = new JMenu("Database");
        JMenu aboutMenu = new JMenu("About");

        // Create a label for the time.
        myTimeLabel = new JLabel("Time not started.");

        // Add menu items for "File"
        myStartButton = new JMenuItem("Start");
        myStopButton = new JMenuItem("Stop");
        JMenuItem queryItem = new JMenuItem("Query Database(file extension)");
        JMenuItem closeItem = new JMenuItem("Close");
        myStartButton.setEnabled(true); // Disable startItem by default
        myStopButton.setEnabled(false); // Disable stopItem by default
        fileMenu.add(myStartButton);
        fileMenu.add(myStopButton);
        fileMenu.add(queryItem);
        fileMenu.add(closeItem);

        // Add action listener to closeItem
        closeItem.addActionListener(this);

        // Add menu items to "File System Watcher"
        JMenuItem startWatcherItem = new JMenuItem("Start Watching");
        JMenuItem stopWatcherItem = new JMenuItem("Stop Watching");
        watcherMenu.add(startWatcherItem);
        watcherMenu.add(stopWatcherItem);

        // Add menu items to "Database"
        JMenuItem connectDbItem = new JMenuItem("Connect to Database");
        JMenuItem disconnectDbItem = new JMenuItem("Disconnect Database");
        databaseMenu.add(connectDbItem);
        databaseMenu.add(disconnectDbItem);

        connectDbItem.addActionListener(this);
        disconnectDbItem.addActionListener(this);

        databaseMenu.add(connectDbItem);
        databaseMenu.add(disconnectDbItem);

        // Add Help menu
        JMenuItem aboutHelpItem = new JMenuItem("About");
        // Add action listener to aboutHelpItem.
        aboutHelpItem.addActionListener(this);
        aboutMenu.add(aboutHelpItem);

        // Add menus to the menu bar
        myMenuBar.add(fileMenu);
        myMenuBar.add(watcherMenu);
        myMenuBar.add(databaseMenu);
        myMenuBar.add(aboutMenu);

        // Attach menu bar to the frame
        myFrame.setJMenuBar(myMenuBar);
    }

    /*
     * This method will handle the actions of the user when they click on the menu
     * items,
     * different actions will be taken depending on the menu item clicked.
     */
    public void actionPerformed(final ActionEvent theEvent) {
        if (theEvent.getSource().equals(myStartButton) || theEvent.getSource().equals(myImgStartButton)) {
            runningTime = 0;
            myTimeLabel.setText("Time not started.");
            myTimer.start();
            myStartButton.setEnabled(false);
            myStopButton.setEnabled(true);
        } else if (theEvent.getSource().equals(myStopButton) || theEvent.getSource().equals(myImgStopButton)) {
            myTimer.stop();
            myStartButton.setEnabled(true);
            myStopButton.setEnabled(false);
        } 
        // Handle closing
        else if (theEvent.getActionCommand().equals("Close")) {
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
        } 
        // Handle File Extension Selection
        else if (theEvent.getSource().equals(myExtensionComboBox) && myExtensionComboBox.getSelectedItem() != "") {
            JOptionPane.showMessageDialog(myFrame, (String) myExtensionComboBox.getSelectedItem());
        } 
        // Handle Directory Selection
        else if (theEvent.getSource().equals(myDirectoryButton)) {
            JFileChooser direcChooser = new JFileChooser();
            direcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            direcChooser.setAcceptAllFileFilterUsed(false);
    
            int directoryValue = direcChooser.showOpenDialog(null);
            if (directoryValue == JFileChooser.APPROVE_OPTION) {
                myDirectoryField.setText(direcChooser.getSelectedFile().getAbsolutePath());
            }
        } 
        // Handle Clearing Directory
        else if (theEvent.getSource().equals(myClearDirectoryButton)) {
            myDirectoryField.setText("");
        } 
        // Handle Database Connection
        else if (theEvent.getActionCommand().equals("Connect to Database")) {
            if (DatabaseConnection.connect()) {
                JOptionPane.showMessageDialog(myFrame, "Connected to SQLite database successfully!");
            } else {
                JOptionPane.showMessageDialog(myFrame, "Failed to connect to database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } 
        else if (theEvent.getActionCommand().equals("Disconnect Database")) {
            DatabaseConnection.disconnect();
            JOptionPane.showMessageDialog(myFrame, "Disconnected from SQLite database.");
        } 
        // Handle Start Watching Directory
        else if (theEvent.getActionCommand().equals("Start Watching")) {
            String directoryToWatch = myDirectoryField.getText();
            if (directoryToWatch.isEmpty()) {
                JOptionPane.showMessageDialog(myFrame, "Please select a directory to monitor!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            myFileWatcher = new FileWatcher(directoryToWatch);
            myFileWatcherThread = new Thread(myFileWatcher);
            myFileWatcherThread.start();
    
            JOptionPane.showMessageDialog(myFrame, "Started watching directory: " + directoryToWatch);
        } 
        // Handle Stop Watching Directory
        else if (theEvent.getActionCommand().equals("Stop Watching")) {
            if (myFileWatcher != null) {
                myFileWatcher.stopWatching();
            }
            JOptionPane.showMessageDialog(myFrame, "Stopped watching.");
        }
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
}
