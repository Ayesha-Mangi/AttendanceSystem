package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;
import db.DBConnection;

public class StudentLogin {
    public StudentLogin() {
        JFrame frame = new JFrame("Student Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Student Login");
        titleLabel.setBounds(150, 20, 100, 30);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 70, 100, 25);
        JTextField userField = new JTextField();
        userField.setBounds(150, 70, 180, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 110, 100, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 110, 180, 25);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 160, 100, 30);

        frame.add(titleLabel);
        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                int studentId = authenticateStudent(username, password);
                if (studentId != -1) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                    frame.dispose();
                    new StudentDashboard(studentId);  // Open Student Dashboard
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials!");
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    
    public int authenticateStudent(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String query = "SELECT id FROM students WHERE name = ? AND password = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); 
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return -1;
    }
}

