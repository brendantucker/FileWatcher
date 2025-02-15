package model;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import view.FWEventTable;
import view.FWGUI;

public class DirectoryWatchService {

    private Path myDirectory;
    private WatchService myWatchService;
    private boolean myRunning = false;
    private WatchKey myKey;
    private FWEventTable myEventTable;
    private FWGUI myGUI;

    public DirectoryWatchService(String directory, FWGUI theGUI) throws IOException {
        myDirectory = Path.of(directory);
        myWatchService = FileSystems.getDefault().newWatchService();
        myGUI = theGUI;
        myEventTable = theGUI.getEventTable();
    }

    public void start() throws IOException {

        //Check if watch service is already running
        if (myRunning) {
            return;
        }
        myRunning = true;

        //Run the file watching service in a separate thread to avoid blocking the GUI
        new Thread(() -> {
            try {
                myKey = myDirectory.register(myWatchService, StandardWatchEventKinds.ENTRY_CREATE,
                                                             StandardWatchEventKinds.ENTRY_DELETE, 
                                                             StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                System.out.println("Error registering directory: " + e.getMessage());
                e.printStackTrace();
                myRunning = false;
                return;
            }
            //Loop to watch for events
            while(myRunning && myGUI.isMonitoring()) {
                for (WatchEvent<?> event : myKey.pollEvents()) {
                    myEventTable.addEvent(new FileEvent(event.context().toString(), myDirectory.toString() + "\\" + event.context().toString(), event.kind().toString(), getExtension(event).toString(), createDateString()));
                }
    
            }
        }).start();





    }

    public void stop() {
        myRunning = false;
        try {
            myKey.cancel();
            myWatchService.close();
        } catch (IOException e) {
            System.out.println("Error closing watch service: " + e.getMessage());
        }
    }
    
    private Path getExtension(WatchEvent<?> theEvent) {
        String fileName = theEvent.context().toString();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return null; // No extension found or folder
        }
        return Path.of(fileName.substring(lastIndexOfDot));
    }

    private String createDateString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }


}
