package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class FWGUI implements ActionListener {

    private JFrame myFrame;
    private JMenuBar myMenuBar;

    public FWGUI() {
        super();
        myFrame = new FWFrame().frameOutline();
        createMenuBar();

        myFrame.setVisible(true);
    }

    private void createMenuBar() {
        // Create the menu bar
        myMenuBar = new JMenuBar();

        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu watcherMenu = new JMenu("File System Watcher");
        JMenu databaseMenu = new JMenu("Database");
        JMenu aboutMenu = new JMenu("About");

        // Add menu items for "File"
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem stopItem = new JMenuItem("Stop");
        JMenuItem queryItem = new JMenuItem("Query Database(file extension)");
        JMenuItem closeItem = new JMenuItem("Close");
        startItem.setEnabled(false); // Disable startItem by default
        stopItem.setEnabled(false); // Disable stopItem by default
        fileMenu.add(startItem);
        fileMenu.add(stopItem);
        fileMenu.add(queryItem);
        fileMenu.add(closeItem);


        // Add action listener to closeItem
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });











        

        // Add menu items to "File System Watcher"
        JMenuItem startWatcherItem = new JMenuItem("Start Watching");
        JMenuItem stopWatcherItem = new JMenuItem("Stop Watching");
        watcherMenu.add(startWatcherItem);
        watcherMenu.add(stopWatcherItem);

        // Add menu items to "Database"
// IDK what would go under this menu so i added random stuff
        JMenuItem connectDbItem = new JMenuItem("Connect to Database");
        JMenuItem disconnectDbItem = new JMenuItem("Disconnect Database");
        databaseMenu.add(connectDbItem);
        databaseMenu.add(disconnectDbItem);

       // Add Help menu
        
        JMenuItem aboutHelpItem = new JMenuItem("About");
        aboutHelpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(myFrame,
                        "Program Usage: This application watches file system changes.\n" +
                        "Version: 1.0\n" +
                        "Developer: Manjinder Ghuman, Ryder Deback",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        aboutMenu.add(aboutHelpItem);

        // Add menus to the menu bar
        myMenuBar.add(fileMenu);
        myMenuBar.add(watcherMenu);
        myMenuBar.add(databaseMenu);
        myMenuBar.add(aboutMenu);

        // Attach menu bar to the frame
        myFrame.setJMenuBar(myMenuBar);
    }

    public void actionPerformed(final ActionEvent theEvent) {
        // TODO Auto-generated method stub
    }
}
