package model;

import model.FileEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileEventDAO {
    public static void insertFileEvent(FileEvent event) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("Database is not connected!");
            return;
        }

        String sql = "INSERT INTO file_events (file_name, file_path, event_type, file_extension, event_time) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getFileName());
            pstmt.setString(2, event.getFilePath());
            pstmt.setString(3, event.getEventType().name());
            pstmt.setString(4, event.getExtension());
            pstmt.setString(5, event.getEventTime().toString());

            pstmt.executeUpdate();
            System.out.println("File event inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error inserting file event.");
        }
    }
}
