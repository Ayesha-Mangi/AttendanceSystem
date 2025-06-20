package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AttendanceReport {
    public AttendanceReport() {
        JFrame frame = new JFrame("Attendance Report");
        frame.setSize(800, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("All Attendance Records");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(280, 10, 300, 30);
        frame.add(title);

        String[] columns = {"Student ID", "Course", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 60, 720, 350);
        frame.add(scrollPane);

        // Load all attendance records
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT a.student_id, c.course_name, a.attendance_date, a.status " +
                           "FROM attendance a " +
                           "JOIN courses c ON a.course_id = c.course_id";
            // String query = "select * from attendance";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("course_name"),
                        rs.getString("attendance_date"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main (String ar[])
    {
       new AttendanceReport();
    }

}

