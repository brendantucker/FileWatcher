import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) class to interact
 * with the database and perform CRUD operations.
 */
public final class FileEventDAO {
    /**
     * Inserts a single file event into the database.
     * @param theEvent The file event to insert.
     */
    public static final void insertFileEvent(final FileEvent theEvent) {
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }
        // SQL string that will insert the file event into the database.
        final String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_date, event_time) VALUES (?, ?, ?, ?, ?, ?)";
        // Try-with-resources block to automatically close the PreparedStatement.
        try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, theEvent.getFileName());
            pstmt.setString(2, theEvent.getFilePath());
            pstmt.setString(3, theEvent.getEventType());
            pstmt.setString(4, theEvent.getExtension());
            pstmt.setString(5, theEvent.getEventDate());
            pstmt.setString(6, theEvent.getEventTime());
            // Execute the update and store the number of rows inserted.
            if (pstmt.executeUpdate() > 0) {
                System.out.println(
                        "File event inserted: " + theEvent.getFileName() + " (" + theEvent.getEventType() + ")");
            } else {
                System.out.println("File event NOT inserted.");
            }
        } catch (final SQLException e) { // Catch any SQL exceptions that occur.
            e.printStackTrace();
            System.out.println("Error inserting file event.");
        }
    }

    /**
     * Inserts a list of file events into the database.
     * @param events The list of file events to insert.
     */
    public static final void insertFileEvents(final List<FileEvent> events) {
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }
        // SQL string that will insert the file events into the database.
        final String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_date, event_time) VALUES (?, ?, ?, ?, ?, ?)";
        // Try-with-resources block to automatically close the PreparedStatement.
        try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Loop through each file event in the list.
            for (final FileEvent event : events) {
                pstmt.setString(1, event.getFileName());
                pstmt.setString(2, event.getFilePath());
                pstmt.setString(3, event.getEventType());
                pstmt.setString(4, event.getExtension());
                pstmt.setString(5, event.getEventDate());
                pstmt.setString(6, event.getEventTime());
                pstmt.addBatch(); // Add to batch execution
            }
            // Execute the batch update and store the number of rows inserted.
            final int[] rowsInserted = pstmt.executeBatch();
            System.out.println("Inserted " + rowsInserted.length + " events into the database.");
        } catch (final SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting file events.");
        }
    }

    /**
     * Retrieves all file events from the database that occured today.
     * @return A table of file events that occured today.
     */
    public static final FWEventTable fileEventsFromToday() {
        final FWEventTable theResultsTable = new FWEventTable();
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            //SQL String that will grab all events from the users local time.
            String sql = "SELECT * FROM file_events WHERE DATE(event_date) = DATE('now', 'localtime');";

            try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
                final ResultSet resultElements = pstmt.executeQuery();
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
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying txt files.");
            }
        }
        return theResultsTable;
    }

    /**
     * Retrieves the top five extension types from the database.
     * @return A table of the top five extension types.
     */
    public static final FWEventTable topFiveExtensions() {
        final FWEventTable theResultsTable = new FWEventTable();
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            // SQL String that will grab the top five extensions from the database.
            final String sql = "SELECT * FROM file_events WHERE file_extension in(SELECT file_extension FROM file_events " +
                    "GROUP BY file_extension ORDER BY COUNT(*) DESC LIMIT 5);";
            // Try-with-resources block to automatically close the PreparedStatement.
            try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
                final ResultSet resultElements = pstmt.executeQuery();
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
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying txt files.");
            }
        }
        return theResultsTable;
    }

    /**
     * Retrieves the most common event types per extension from the database.
     * @return A table of the most common event types per extension.
     */
    public static final FWEventTable mostCommonEventsPerExtension() {
        final FWEventTable theResultsTable = new FWEventTable();
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            // SQL String that will grab the most common event types per extension.
            final String sql = "SELECT file_extension, event_type, COUNT(*) as event_count " +
                   "FROM file_events " +
                   "GROUP BY file_extension, event_type " +
                   "ORDER BY COUNT(event_type), file_extension ASC;";

            // Try-with-resources block to automatically close the PreparedStatement.
            try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
                final ResultSet resultElements = pstmt.executeQuery();
                // Loop through the results and add them to the table.
                while (resultElements.next()) {
                    FileEvent theEvent = new FileEvent(
                            "", // file_name is not necessary for this query
                            resultElements.getString("event_count"), // file_path is not necessary for this query
                            resultElements.getString("event_type"),
                            resultElements.getString("file_extension"),
                            "", // event_date not necessary
                            ""); // event_time not necessary

                    theResultsTable.addEvent(theEvent);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying most common event types per extension.");
            }
        }
        return theResultsTable;
    }

    /**
     * Retrieves information about specific extensions from the database.
     * @param theList The list of extensions to query.
     * @return A table of file events with the specified extensions.
     */
    public static final FWEventTable querySpecificExtensions(final List<String> theList) {
        final FWEventTable theResultsTable = new FWEventTable();
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            // Create a StringBuilder to loop through and build the WHERE clause.
            final StringBuilder whereExtensionClause = new StringBuilder();
            for (final String extension : theList) {
                whereExtensionClause.append("'.").append(extension).append("',");
            }
            // Remove the trailing comma
            if (whereExtensionClause.length() > 0) {
                whereExtensionClause.setLength(whereExtensionClause.length() - 1);
            }
            // SQL String that will grab all events with the specified extensions.
            final String sql = "SELECT * FROM file_events WHERE file_extension IN (" + whereExtensionClause + ")";

            // Try-with-resources block to automatically close the PreparedStatement.
            try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
                final ResultSet resultElements = pstmt.executeQuery();
                while (resultElements.next()) {
                    final FileEvent theEvent = new FileEvent(
                            resultElements.getString("file_name"),
                            resultElements.getString("file_path"),
                            resultElements.getString("event_type"),
                            resultElements.getString("file_extension"),
                            resultElements.getString("event_date"),
                            resultElements.getString("event_time"));

                    theResultsTable.addEvent(theEvent);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying txt files.");
            }
        }
        return theResultsTable;
    }

    /**
     * Retrieves variety of data based on what the user manually querys in the query window.
     * @param theChoice The choice of what to query.
     * @param theFilter The filter to apply to the query.
     * @return A table of file events based on the user's query.
     */
    public static final FWEventTable manualQueryResults(final String theChoice, String theFilter) {
        final FWEventTable theResultsTable = new FWEventTable();
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
        } else {
            String sql = "";
            String startDate = "";
            String endDate = "";
            // Build the SQL query based on the user's choice.
            if (theChoice.equals("file_name")) {
                sql = "SELECT * FROM file_events WHERE " + theChoice + " LIKE ?";
                theFilter = "%" + theFilter + "%";
            } else if (theChoice.equals("event_date")) {
                String[] dates = theFilter.split(" to");
                if(dates.length == 2) {
                    startDate = dates[0].trim();
                    endDate = dates[1].trim();
                }
                sql = "SELECT * FROM file_events WHERE event_date BETWEEN ? AND ?";
            } else {
                sql = "SELECT * FROM file_events WHERE " + theChoice + " = ?";
            }
            // Try-with-resources block to automatically close the PreparedStatement.
            try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // For the "event_date" case, set both date and time as parameters
                if (theChoice.equals("event_date")) {
                    pstmt.setString(1, startDate);
                    pstmt.setString(2, endDate);
                } else {
                    pstmt.setString(1, theFilter);
                }
                // Execute the query and loop through the results.
                final ResultSet resultElements = pstmt.executeQuery();
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
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println("Error querying event files.");
            }
        }
        return theResultsTable;
    }

    /**
     * Resets the entire database by deleting all entries.
     */
    public static final void resetEntireDatabase() {
        final Connection conn = DatabaseConnection.getMyConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }

        final String sql = "DELETE FROM file_events";

        try (final PreparedStatement pstmt = conn.prepareStatement(sql)) {
            final int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " rows from the database.");
        } catch (final SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting rows from the database.");
        }
    }
}
