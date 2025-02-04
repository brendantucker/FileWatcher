package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class FWGUI implements ActionListener {
    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private int runningTime = 0;
    private Timer myTimer;
    private JLabel myTimeLabel;
    private JMenuItem myStartButton;
    private JMenuItem myStopButton;

    /*
     * Constructor for the GUI. This will create the GUI and set up the menu bar.
     */
    public FWGUI() {
        myFrame = new FWFrame().frameOutline();
        myFrame.setLayout(new BorderLayout());
        createMenuBar();
        timeKeeper();
        JPanel mainPanel = new FWPanel();
        myFrame.add(mainPanel, BorderLayout.CENTER);
        myFrame.setVisible(true);
    }

    /*
     * This method will keep track of the time that the user has been monitoring files.
     */
    private void timeKeeper() {
        myTimer = new Timer(1000, (ActionEvent e) -> {
            runningTime++;
            timerLabelExtended();
        });
        myStartButton.addActionListener(this);
        myStopButton.addActionListener(this);
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
        }
    }

    /*
     * This method will extend the timer label to show the time in days, hours, minutes, and seconds.
     */
    private void timerLabelExtended() {
        int days = runningTime / 86400;
        int hours = (runningTime % 86400) / 3600;
        int minutes = (runningTime % 3600) / 60;
        int seconds = runningTime % 60;
        String timeFormatted = String.format("Time Running: %02d Days: %02d Hours: %02d Minutes: %02d Seconds", days, hours, minutes, seconds);
        myTimeLabel.setText(timeFormatted);
    }
}
