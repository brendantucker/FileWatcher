import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public class FileWatcher implements Runnable {
    private final Path directory;
    private volatile boolean running = true;

    public FileWatcher(String directoryPath) {
        this.directory = Paths.get(directoryPath);
    }

    public void stopWatching() {
        this.running = false;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, 
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("🔍 Watching directory: " + directory);

            while (running) {
                WatchKey key = watchService.take(); // Wait for an event
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path filePath = directory.resolve((Path) event.context());

                    System.out.println("📂 File Event Detected: " + filePath + " -> " + kind.name());

                    EventType eventType;
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        eventType = EventType.FILECREATED;
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        eventType = EventType.FILEDELETED;
                    } else {
                        eventType = EventType.FILEMODIFIED;
                    }

                    FileEvent fileEvent = new FileEvent(
                            filePath.getFileName().toString(),
                            filePath.toString(),
                            eventType,
                            getFileExtension(filePath.toString()),
                            LocalDateTime.now()
                    );

                    // Insert event into DB
                    if (DatabaseConnection.getConnection() != null) {
                        FileEventDAO.insertFileEvent(fileEvent);
                    } else {
                        System.out.println("Database is not connected. Event not logged.");
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex + 1);
    }
}
