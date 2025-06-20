package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import javax.swing.table.DefaultTableModel;

public class ViewAttendance {
    private int teacherId;

    public ViewAttendance(int teacherId) {
        this.teacherId = teacherId;

       
        JFrame frame = new JFrame("View Attendance");
        frame.setSize(800, 500);  
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      
        JLabel title = new JLabel("Attendance Records");
        title.setFont(new Font("Arial", Font.BOLD, 18)); 
        title.setBounds(300, 10, 250, 30);
        frame.add(title);

       
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Arial", Font.PLAIN, 16)); 
        courseLabel.setBounds(50, 50, 80, 30);
        frame.add(courseLabel);

        JComboBox<String> courseBox = new JComboBox<>();
        courseBox.setFont(new Font("Arial", Font.PLAIN, 16));  
        courseBox.setBounds(130, 50, 250, 30); 
        frame.add(courseBox);

        JButton loadBtn = new JButton("Search");
        loadBtn.setFont(new Font("Arial", Font.PLAIN, 16));  
        loadBtn.setBounds(400, 50, 100, 30);  
        frame.add(loadBtn);

        
        JLabel dateLabel = new JLabel("Date (optional):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));  
        dateLabel.setBounds(50, 90, 150, 30);
        frame.add(dateLabel);

        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 16));  
        dateField.setBounds(180, 90, 250, 30); 
        frame.add(dateField);

    
        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 14)); 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(30, 130, 730, 250); 
        frame.add(scroll);

        
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));  
        backBtn.setBounds(350, 400, 100, 30);  
        frame.add(backBtn);

      
        loadCourses(courseBox);

        
        loadBtn.addActionListener(e -> {
            String selectedCourse = (String) courseBox.getSelectedItem();
            String date = dateField.getText().trim(); 

            if (selectedCourse != null) {
                if (date.isEmpty()) {
                
                    showAttendance(table, selectedCourse, null);
                } else {
                    
                    showAttendance(table, selectedCourse, date);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a course.");
            }
        });

       
        backBtn.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadCourses(JComboBox<String> courseBox) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT course_name FROM courses WHERE teacher_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courseBox.addItem(rs.getString("course_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load courses.");
        }
    }

    private void showAttendance(JTable table, String courseName, String date) {
        try (Connection conn = DBConnection.getConnection()) {
            String query;
            if (date == null || date.isEmpty()) {
                
                query = """
                    SELECT s.name AS Student, c.course_name AS Course,
                           a.attendance_date AS Date, a.status AS Status
                    FROM attendance a
                    JOIN students s ON a.student_id = s.id
                    JOIN courses c ON a.course_id = c.course_id
                    WHERE c.teacher_id = ? AND c.course_name = ?
                """;
            } else {
                // If date is provided, filter by both course and date
                query = """
                    SELECT s.name AS Student, c.course_name AS Course,
                           a.attendance_date AS Date, a.status AS Status
                    FROM attendance a
                    JOIN students s ON a.student_id = s.id
                    JOIN courses c ON a.course_id = c.course_id
                    WHERE c.teacher_id = ? AND c.course_name = ? AND a.attendance_date = ?
                """;
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, teacherId);
            ps.setString(2, courseName);
            if (date != null && !date.isEmpty()) {
                ps.setString(3, date); // Use the entered date if provided
            }
            ResultSet rs = ps.executeQuery();

            String[] columns = {"Student", "Course", "Date", "Status"};

            // Create DefaultTableModel with no rows initially (0 rows)
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            // Loop through the ResultSet and add rows to the table model
            while (rs.next()) {
                Object[] row = {
                    rs.getString("Student"),
                    rs.getString("Course"),
                    rs.getDate("Date"),
                    rs.getString("Status")
                };
                model.addRow(row); // Add the row to the model
            }

            
            table.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load attendance.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ViewAttendance(1);
    }
}
