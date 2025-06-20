package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import db.DBConnection;

public class ViewMyAttendance {
    private int studentId;

    public ViewMyAttendance(int studentId) {
        this.studentId = studentId;

        JFrame frame = new JFrame("My Attendance");
        frame.setSize(700, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setBounds(50, 30, 150, 30);
        courseLabel.setFont(new Font("Arial", Font.PLAIN, 18));
          frame.add(courseLabel);
          
        JComboBox<String> courseBox = new JComboBox<>();
        courseBox.setBounds(200, 30, 300, 35);
        courseBox.setFont(new Font("Arial", Font.PLAIN, 16));
        frame.add(courseBox);

        JButton loadBtn = new JButton("View Attendance");
        loadBtn.setBounds(250, 80, 200, 40);
        loadBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(loadBtn);

        JTable table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
         
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 140, 580, 280);
        frame.add(scrollPane);

        JLabel absenceLabel = new JLabel("Total Absences: 0");
        absenceLabel.setBounds(50, 440, 300, 30);
        absenceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(absenceLabel);

       
        loadCourses(courseBox);

        loadBtn.addActionListener(e -> {
            String course = (String) courseBox.getSelectedItem();
            if (course != null) {
                loadAttendance(course, table, absenceLabel);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a course.");
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadCourses(JComboBox<String> box) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT c.course_name FROM courses c " +
                 "JOIN enrollments e ON c.course_id = e.course_id " +
                 "WHERE e.student_id = ?")) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                box.addItem(rs.getString("course_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAttendance(String course, JTable table, JLabel absenceLabel) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Date");
        model.addColumn("Status");

        int absences = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.attendance_date, a.status FROM attendance a " +
                 "JOIN courses c ON a.course_id = c.course_id " +
                 "WHERE c.course_name = ? AND a.student_id = ?")) {
            ps.setString(1, course);
            ps.setInt(2, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String date = rs.getString("attendance_date");
                String status = rs.getString("status");
                model.addRow(new Object[]{date, status});

                if (status.equalsIgnoreCase("Absent")) {
                    absences++;
                }
            }

            table.setModel(model);
            absenceLabel.setText("Total Absences: " + absences);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public static void main(String[] args) {
        new ViewMyAttendance(4); 
    }
}
