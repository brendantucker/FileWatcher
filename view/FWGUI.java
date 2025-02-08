package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
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
    private String[] EXTENSION_TYPES = {"","DOCX", "PDF", "TXT", "PNG", "JPG", "JPEG", "GIF", "MP3", "MP4", "WAV", "AVI", "MOV"};
    private JComboBox<String> myExtensionComboBox;
    private JTextField myDirectoryField;
    private JButton myDirectoryButton;
    private JButton myClearDirectoryButton;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
        super();
        myFrame = new FWFrame().frameOutline();
        // Create the menu bar and start the timer when necessary.
        createMenuBar();
        dropDownMenus();
        timeKeeper();
        // Create a panel for the time label
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.add(myTimeLabel);
        // Add the time panel to the frame.
        myFrame.add(timePanel, BorderLayout.SOUTH);
        myFrame.setVisible(true);
    }

    private void dropDownMenus(){
        myExtensionComboBox= new JComboBox<>(EXTENSION_TYPES);
        myExtensionComboBox.setEditable(true);
        myExtensionComboBox.addActionListener(this);

        JLabel extensionLable = new JLabel("Select a file extension, a directory, and click Watch to begin File System Monitor.");
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
        
        myFrame.add(containPanel, BorderLayout.NORTH);
    }

    /*
     * This method will keep track of the time that the user has been monitoring files.
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
     * This method will handle the actions of the user when they click on the menu items,
     * different actions will be taken depending on the menu item clicked.
     */
    public void actionPerformed(final ActionEvent theEvent) {
        if (theEvent.getSource().equals(myStartButton)) {
            runningTime = 0;
            myTimeLabel.setText("Time not started.");
            myTimer.start();
            myStartButton.setEnabled(false);
            myStopButton.setEnabled(true);
        } else if (theEvent.getSource().equals(myStopButton)) {
            myTimer.stop();
            myStartButton.setEnabled(true);
            myStopButton.setEnabled(false);
        } else if (theEvent.getActionCommand().equals("Close")) {
            System.exit(0);
        } else if (theEvent.getActionCommand().equals("About")) {
            JOptionPane.showMessageDialog(myFrame,
                    "Program Usage: This application watches file system changes.\n" +
                            "Version: 1.0\n" +
                            "Developer: Manjinder Ghuman, Ryder Deback",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (theEvent.getSource().equals(myExtensionComboBox) && myExtensionComboBox.getSelectedItem() != "") {
            JOptionPane.showMessageDialog(myFrame, (String) myExtensionComboBox.getSelectedItem());
        } else if (theEvent.getSource().equals(myDirectoryButton)) {
            JFileChooser direcChooser = new JFileChooser();
            direcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            direcChooser.setAcceptAllFileFilterUsed(false); //Disabling the ability to select all files.

            int directoryValue = direcChooser.showOpenDialog(null);
            if(directoryValue == JFileChooser.APPROVE_OPTION){
                myDirectoryField.setText(direcChooser.getSelectedFile().getAbsolutePath());
            }
        } else if(theEvent.getSource().equals(myClearDirectoryButton)){
            myDirectoryField.setText("");
        }
    }

    /*
     * This method will extend the timer label to show the time in days, hours, minutes, and seconds.
     */
    private void timerLabelExtended(){
        int days = runningTime / 86400;
        int hours = (runningTime % 86400) / 3600;
        int minutes = (runningTime % 3600) / 60;
        int seconds = runningTime % 60;

        String timeFormatted = String.format("Time Running: %02d Days: %02d Hours: %02d Minutes: %02d Seconds",days,hours,minutes,seconds);
        myTimeLabel.setText(timeFormatted);
    }
}
