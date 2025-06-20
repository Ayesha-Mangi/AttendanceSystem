package ui;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import java.awt.Font;

public class StudentDashboard {
    private int studentId;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;

        JFrame frame = new JFrame("Student Dashboard");
        frame.setSize(650, 500);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome Student!");
        welcomeLabel.setBounds(180, 30, 350, 40);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 35));
        frame.add(welcomeLabel);

        JButton viewCoursesBtn = new JButton("View My Courses");
        viewCoursesBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        viewCoursesBtn.setBounds(170, 110, 300, 50);
       
        frame.add(viewCoursesBtn);

        JButton viewAttendanceBtn = new JButton("View My Attendance");
        viewAttendanceBtn.setBounds(170, 180, 300, 50);
        viewAttendanceBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(viewAttendanceBtn);

        JButton leaveButton = new JButton("Request Leave");
        leaveButton.setBounds(170, 260, 300, 50);
        leaveButton.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(leaveButton);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(250, 340, 130, 40);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(logoutBtn);
        
        

        leaveButton.addActionListener(e -> new LeaveRequestForm(studentId));
        viewCoursesBtn.addActionListener(e -> new ViewMyCourses(studentId));
        viewAttendanceBtn.addActionListener(e -> new ViewMyAttendance(studentId));
        logoutBtn.addActionListener(e -> frame.dispose());
       
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        checkStudentAbsences(studentId);
    }
    
    private void checkStudentAbsences(int studentId) {
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT c.course_name, COUNT(*) AS absence_count " +
                       "FROM attendance a " +
                       "JOIN courses c ON a.course_id = c.course_id " +
                       "WHERE a.student_id = ? AND a.status = 'Absent' " +
                       "GROUP BY c.course_name";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();

        StringBuilder warningMsg = new StringBuilder();

        while (rs.next()) {
            String courseName = rs.getString("course_name");
            int count = rs.getInt("absence_count");

            if (count >= 5) {
                warningMsg.append(" You have ").append(count)
                          .append(" absences in ").append(courseName)
                          .append("\n");
            }
        }

        if (warningMsg.length() > 0) {
            JOptionPane.showMessageDialog(null, warningMsg.toString(), "Attendance Warning", JOptionPane.WARNING_MESSAGE);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    public static void main (String ar[])
    {
      StudentDashboard s = new StudentDashboard(1);
    }        
}         
            
    
            
            
