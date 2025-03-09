import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is responsible for inserting file events into the database.
 */
public class FileEventDAO {
    public static void insertFileEvent(FileEvent theEvent) {
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }

        String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_date, event_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, theEvent.getFileName());
            pstmt.setString(2, theEvent.getFilePath());
            pstmt.setString(3, theEvent.getEventType());
            pstmt.setString(4, theEvent.getExtension());
            pstmt.setString(5, theEvent.getEventDate());
            pstmt.setString(6, theEvent.getEventTime());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(
                        "File event inserted: " + theEvent.getFileName() + " (" + theEvent.getEventType() + ")");
            } else {
                System.out.println("File event NOT inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting file event.");
        }
    }

    public static void insertFileEvents(List<FileEvent> events) {
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }

        String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_date, event_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (FileEvent event : events) {
                pstmt.setString(1, event.getFileName());
                pstmt.setString(2, event.getFilePath());
                pstmt.setString(3, event.getEventType());
                pstmt.setString(4, event.getExtension());
                pstmt.setString(5, event.getEventDate());
                pstmt.setString(6, event.getEventTime());
                pstmt.addBatch(); // Add to batch execution
            }
            int[] rowsInserted = pstmt.executeBatch();
            System.out.println("Inserted " + rowsInserted.length + " events into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting file events.");
        }
    }

    public static FWEventTable queryTxtFiles() {
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "SELECT * FROM file_events WHERE file_extension = '.txt'";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet resultElements = pstmt.executeQuery();
                while (resultElements.next()) {
                    FileEvent theEvent = new FileEvent(
                            resultElements.getString("file_name"),
                            resultElements.getString("file_path"),
                            resultElements.getString("event_type"),
                            resultElements.getString("file_extension"),
                            resultElements.getString("event_date"),
                            resultElements.getString("event_time"));

                    theResultsTable.addEvent(theEvent);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying txt files.");
            }
        }
        return theResultsTable;
    }


    
}
