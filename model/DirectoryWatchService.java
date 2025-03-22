import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

/**
 * This class is responsible for watching a directory for file events. It uses
 * the WatchService class to monitor the directory
 * for file creation, deletion, and modification events. When an event occurs,
 * it will add the event to the event table in the GUI.
 */
public final class DirectoryWatchService {
    /** The directory to watch */
    private final Path myDirectory;
    /** The watch service to monitor the directory */
    private final WatchService myWatchService;
    /** Flag to check if watcher should be running. */
    private boolean myRunning = false;
    /** The key to the watch service */
    private WatchKey myKey;
    /** The event table to update with file events */
    private final FWEventTable myEventTable;
    /** The GUI watch service will work on; used to check the "myIsMonitoring" variable via a getter */
    private final FWGUI myGUI;
    /** The thread that will run the watch service */
    private Thread myWatchThread;

    /**
     * Constructor for DirectoryWatchService.
     * 
     * @param theDirectory The directory to watch
     * @param theGUI The GUI instance it will be connected to
     * @throws IOException Thrown when chosen directory is invalid
     */
    public DirectoryWatchService(final String theDirectory, final FWGUI theGUI) throws IOException {
        myDirectory = Path.of(theDirectory);
        if (!Files.isDirectory(myDirectory) || myDirectory.toString() == "") { // Guard against invalid directory
            throw new IOException();
        }

        // Initialize remaining DirectoryWatchService variables if valid
        myWatchService = FileSystems.getDefault().newWatchService();
        myGUI = theGUI;
        myEventTable = theGUI.getEventTable();
    }

    /**
     * Starts the watch service. This method will run in a separate thread to avoid
     * blocking the GUI.
     * It will watch for file creation, deletion, and modification events in the
     * specified directory.
     * 
     * @throws IOException                 Thrown when chosen directory is invalid
     * @throws ClosedWatchServiceException Thrown when the watch service is closed
     * @throws InterruptedException        Thrown when the thread is interrupted
     */
    public final void start() throws IOException {

        // Exit if watch service is already running
        if (myRunning) {
            return;
        }
        myRunning = true;

        /* Run the file watching service in a separate thread to avoid blocking the GUI, this is
        in addition to using the take method of myWatchService to block until an event is available.*/
        myWatchThread = new Thread(() -> {
            try {
                registerAllFolders(myDirectory); // Add all subfolders in the directory to the watch service
            } catch (final IOException e) {
                myRunning = false;
                return;
            }

            // Loop to watch for events until stop() is called
            while (myRunning && myGUI.isMonitoring()) {
                try {
                    final WatchKey key = myWatchService.take(); // Forces the thread to wait until new events occur
                    for (WatchEvent<?> event : key.pollEvents()) {
                        final Path eventPath = ((Path) key.watchable()).resolve((Path) event.context());
                        if (Files.isDirectory(eventPath)) { // Register the new folder with WatchService if it is a directory
                            try {
                                eventPath.register(myWatchService, StandardWatchEventKinds.ENTRY_CREATE,
                                        StandardWatchEventKinds.ENTRY_DELETE,
                                        StandardWatchEventKinds.ENTRY_MODIFY);
                            } catch (final IOException e) {
                                invalidDirectoryError();
                            }
                        }
                        // Filter out modify directory events -- this kind of event is created any time the contents of a directory change
                        if (!Files.isDirectory(eventPath) || event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                            myEventTable.addEvent(new FileEvent(event.context().toString(),
                                    eventPath.toString(),
                                    eventTypeFormatter(event.kind()),
                                    getExtension(event).toString(),
                                    createDateString(),
                                    createTimeString()));
                        }
                    }
                    key.reset(); // Reset the key to receive further events
                } catch (final InterruptedException e) {
                    stop();
                } catch (final ClosedWatchServiceException e) {
                    stop();
                }
            }
        });
        myWatchThread.start();

    }

    /**
     * Stops the watch service and closes it.
     */
    public final void stop() {
        if (myRunning == false) {
            return;
        }

        myRunning = false;

        try { // Close the watch service and key if they are not null
            if (myKey != null) {
                myKey.cancel();
            }
            if (myWatchService != null) {
                myWatchService.close();
            }
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "I/O Error: Could not close watch service!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (myWatchThread != null) {
            myWatchThread.interrupt(); // Stop the thread watching for file events
            try {
                myWatchThread.join(); // Wait for myWatchThread to finish shutting down
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }
        myWatchThread = null;
    }

    /**
     * Displays an error message when an invalid directory is chosen.
     */
    public final void invalidDirectoryError() {
        JOptionPane.showMessageDialog(null, "\"" + myDirectory.toString() + "\" is not a valid directory",
                "Invalid Directory Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Helper method which registers all subfolders in the directory with the watch
     * service.
     * 
     * @param theStartPath The starting path to register folders from
     * @throws IOException
     */
    private final void registerAllFolders(final Path theStartPath) throws IOException {
        Files.walkFileTree(theStartPath, new SimpleFileVisitor<Path>() {
            @Override
            // Contains logic for registering visited directories with the watch service. Catches exceptions for access denied and IO exceptions.
            public FileVisitResult preVisitDirectory(Path theDir, BasicFileAttributes theAttrs) throws IOException {
                try {
                    theDir.register(myWatchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (final AccessDeniedException e) {
                    // System.err.println("Access denied to directory: " + theDir);
                    return FileVisitResult.SKIP_SUBTREE; // Skip the directory
                } catch (final IOException e) {
                    // System.err.println("Error registering directory: " + theDir + " - " + e.getMessage());
                    return FileVisitResult.SKIP_SUBTREE; // Skip the directory
                }
                return FileVisitResult.CONTINUE;
            }
    
            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException theException) {
                if (theException instanceof AccessDeniedException) {
                    //System.err.println("Access denied to file: " + file);
                    return FileVisitResult.CONTINUE; // Skip the file
                }
                //System.err.println("Error visiting file: " + file + " - " + theException.getMessage());
                return FileVisitResult.CONTINUE; // Skip the file
            }
        });
    }

    /**
     * Gets the file extension from the event, including the dot. Returns "folder"
     * for directories and "none" if no extension.
     * 
     * @param theEvent
     * @return String representation of the file extension/type
     */
    private final String getExtension(final WatchEvent<?> theEvent) {

        if (Files.isDirectory(myDirectory.resolve((Path) theEvent.context()))) {
            return "folder";
        }

        final String fileName = theEvent.context().toString();
        final int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "none"; // No extension found or folder
        }
        return Path.of(fileName.substring(lastIndexOfDot)).toString();
    }

    /**
     * Creates a date string in the format of yyyy-MM-dd HH:mm:ss
     * 
     * @return String representation of the current date
     */
    private final String createDateString() {
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }

    /**
     * Creates a time string in the format of HH:mm:ss
     * 
     * @return String representation of the current time.
     */
    private final String createTimeString() {
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * Formats the event type to a more readable string.
     * 
     * @param theEvent One of the three StandardWatchEventKinds enum
     * @return Formatted event type string
     */
    private final String eventTypeFormatter(final WatchEvent.Kind<?>  theEvent) {
        final String eventType = theEvent.toString();
        if (eventType.equals("ENTRY_CREATE")) {
            return "CREATED";
        } else if (eventType.equals("ENTRY_DELETE")) {
            return "DELETED";
        } else if (eventType.equals("ENTRY_MODIFY")) {
            return "MODIFIED";
        }
        return null;
    }

    /**
     * Returns the directory being watched.
     * 
     * @return The directory being watched
     */
    public final boolean isRunning() {
        return myRunning;
    }

}