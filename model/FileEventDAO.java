import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileEventDAO {
    public static void insertFileEvent(FileEvent event) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println(" Database is not connected!");
            return;
        }

        String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_time) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getFileName());
            pstmt.setString(2, event.getFilePath());
            pstmt.setString(3, event.getEventType().name());
            pstmt.setString(4, event.getExtension());
            pstmt.setString(5, event.getEventTime().toString());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(" File event inserted: " + event.getFileName() + " (" + event.getEventType() + ")");
            } else {
                System.out.println("File event NOT inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" Error inserting file event.");
        }
    }
}
