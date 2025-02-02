package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.FileEvent;
import model.EventType;

public class FWGUI implements ActionListener {

    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private int runningTime = 0;
    private Timer myTimer;
    private JLabel myTimeLabel;
    private JMenuItem myStartButton;
    private JMenuItem myStopButton;
    //New field
    private FWEventTable myEventTable;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
        super();
        myFrame = new FWFrame().frameOutline();
        // Create the menu bar and start the timer when necessary.
        createMenuBar();
        timeKeeper();
        // Create a panel for the time label
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.add(myTimeLabel);
        // Add the time panel to the frame.
        myFrame.add(timePanel, BorderLayout.SOUTH);

        //Setup and add FileEvent table to GUI
        myEventTable = new FWEventTable();
        myFrame.add(myEventTable, BorderLayout.CENTER); //Set to CENTER to prevent the table covering other GUI elements

        //Add 100 test events to the table to test scrolling
        for (int i = 0; i < 100; i++) {
            myEventTable.addEvent(new FileEvent("TestFile.txt", "C:/path/to/TestFile.txt", EventType.FILECREATED, "txt", LocalDateTime.of(2025, 2, 2, 12, 27)));
        }

        myFrame.setVisible(true);
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
                            "Developers: Manjinder Ghuman, Ryder Deback, Brendan Tucker",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
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
