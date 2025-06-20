package ui;

import javax.swing.*;
import java.sql.*;
import db.DBConnection;
import java.awt.Font;

public class ViewMyCourses {

    public ViewMyCourses(int studentId) {
        JFrame frame = new JFrame("My Courses");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null); 

        JTextArea courseArea = new JTextArea();
        courseArea.setFont(new Font("Arial", Font.BOLD, 18)); 
        courseArea.setEditable(false); // Make the text area non-editable
        JScrollPane scroll = new JScrollPane(courseArea);
        frame.add(scroll); 

        // Fetching courses for the student from the database
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT c.course_name FROM courses c " +
                             "JOIN enrollments e ON c.course_id = e.course_id " +
                             "WHERE e.student_id = ?")) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courseArea.append(rs.getString("course_name") + "\n"); // Append each course name
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ViewMyCourses(3); 
    }
}
