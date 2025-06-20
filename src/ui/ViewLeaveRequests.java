package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewLeaveRequests {

    private JTable table;
    private DefaultTableModel model;

    public ViewLeaveRequests(int teacherId) {
        JFrame frame = new JFrame("Leave Requests");
        frame.setSize(900, 500);  
        frame.setLayout(null);

        // Title
        JLabel title = new JLabel("Leave Requests");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(370, 10, 200, 30);
        frame.add(title);

        // Table model and setup
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Request ID", "Student ID", "Course", "Reason", "Status"});

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 15)); 
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 50, 820, 250);
        frame.add(scrollPane);

        // Approve Button
        JButton approveButton = new JButton("Approve");
        approveButton.setFont(new Font("Arial", Font.PLAIN, 16)); 
        approveButton.setBounds(200, 320, 120, 30);
        frame.add(approveButton);

        // Reject Button
        JButton rejectButton = new JButton("Reject");
        rejectButton.setFont(new Font("Arial", Font.PLAIN, 16));  
        rejectButton.setBounds(400, 320, 120, 30);
        frame.add(rejectButton);

        // Load leave requests
        loadRequests(teacherId);

        approveButton.addActionListener(e -> updateStatus("Approved"));
        rejectButton.addActionListener(e -> updateStatus("Rejected"));

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void loadRequests(int teacherId) {
        try (Connection conn = DBConnection.getConnection()) {
            // Load courses taught by the teacher
            String getCoursesQuery = "SELECT course_name FROM courses WHERE teacher_id = ?";
            PreparedStatement ps = conn.prepareStatement(getCoursesQuery);
            ps.setInt(1, teacherId);
            ResultSet courseRs = ps.executeQuery();

            // Collect course names into a string
            StringBuilder courseList = new StringBuilder();
            while (courseRs.next()) {
                if (courseList.length() > 0) courseList.append(",");
                courseList.append("'").append(courseRs.getString("course_name")).append("'");
            }

            if (courseList.length() == 0) return; // No courses assigned

            // Load leave requests for those courses
            String query = "SELECT * FROM leave_requests WHERE course_name IN (" + courseList + ")";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Clear existing rows and add new ones
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getInt("student_id"),
                        rs.getString("course_name"),
                        rs.getString("reason"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row first.");
            return;
        }

        int requestId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String updateQuery = "UPDATE leave_requests SET status = ? WHERE request_id = ?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Request " + status + " successfully!");
            model.setValueAt(status, selectedRow, 4); 

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ViewLeaveRequests(1);
    }
}

