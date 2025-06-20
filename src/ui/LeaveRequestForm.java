package ui;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

import db.DBConnection;
import java.awt.Font;

public class LeaveRequestForm {
    public LeaveRequestForm(int studentId) {
        JFrame frame = new JFrame("Submit Leave Request");
        frame.setSize(550, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setBounds(40, 30, 150, 30);
          courseLabel.setFont(labelFont);
        
        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setBounds(200, 30, 280, 30);
        courseDropdown.setFont(fieldFont);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT c.course_name FROM courses c " +
                           "JOIN enrollments e ON c.course_id = e.course_id " +
                           "WHERE e.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                courseDropdown.addItem(rs.getString("course_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setBounds(40, 80, 150, 30);
        reasonLabel.setFont(labelFont);
        
        JTextArea reasonArea = new JTextArea();
        reasonArea.setBounds(200, 80, 280, 120);
        reasonArea.setFont(fieldFont);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(reasonArea);
        scrollPane.setBounds(200, 80, 280, 120);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(200, 220, 150, 40);
        submitButton.setFont(labelFont);
        

        frame.add(courseLabel);
        frame.add(courseDropdown);
        frame.add(reasonLabel);
        frame.add(reasonArea);
        frame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String courseName = (String) courseDropdown.getSelectedItem();
                String reason = reasonArea.getText();

                if (courseName == null || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String insert = "INSERT INTO leave_requests (student_id, course_name, reason, status) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insert);
                    pstmt.setInt(1, studentId);
                    pstmt.setString(2, courseName);
                    pstmt.setString(3, reason);
                    pstmt.setString(4, "Pending");

                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(frame, "Leave request submitted!");
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to submit leave request.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}