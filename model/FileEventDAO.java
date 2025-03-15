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

    public static FWEventTable fileEventsFromToday() {
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "SELECT * FROM file_events WHERE DATE(event_date) = DATE('now', 'localtime');";

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

    public static FWEventTable topFiveExtensions() {
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "SELECT * FROM file_events WHERE file_extension in(SELECT file_extension FROM file_events " +
                    "GROUP BY file_extension ORDER BY COUNT(*) DESC LIMIT 5);";

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

    public static FWEventTable querySpecificExtensions(List<String> theList) {
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            StringBuilder whereExtensionClause = new StringBuilder();
            for (String extension : theList) {
                whereExtensionClause.append("'.").append(extension).append("',");
            }
            // Remove the trailing comma
            if (whereExtensionClause.length() > 0) {
                whereExtensionClause.setLength(whereExtensionClause.length() - 1);
            }
            String sql = "SELECT * FROM file_events WHERE file_extension IN (" + whereExtensionClause + ")";

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

    public static FWEventTable mostCommonEventsPerExtension() {
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "SELECT file_extension, event_type, COUNT(*) as event_count " +
                    "FROM file_events " +
                    "GROUP BY file_extension, event_type " +
                    "ORDER BY event_count DESC;";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet resultElements = pstmt.executeQuery();
                while (resultElements.next()) {
                    FileEvent theEvent = new FileEvent(
                            "", // file_name is not necessary for this query
                            "", // file_path is not necessary for this query
                            resultElements.getString("event_type"),
                            resultElements.getString("file_extension"),
                            "", // event_date not necessary
                            ""); // event_time not necessary

                    theResultsTable.addEvent(theEvent);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying most common event types per extension.");
            }
        }
        return theResultsTable;
    }

    public static FWEventTable manualQueryResults(String theChoice, String theFilter){
        FWEventTable theResultsTable = new FWEventTable();
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "";
            if(theChoice.equals("file_name")){
                sql = "SELECT * FROM file_events WHERE " + theChoice + " LIKE ?";
                theFilter = "%" + theFilter + "%";
            } else{
                sql = "SELECT * FROM file_events WHERE " + theChoice + " = ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1,theFilter);
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

    public static void resetEntireDatabase() {
        Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }

        String sql = "DELETE FROM file_events";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " rows from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting rows from the database.");
        }
    }
}
