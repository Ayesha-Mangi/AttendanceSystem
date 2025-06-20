
package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import db.DBConnection;

public class TeacherDashboard {
    private int teacherId;

    public TeacherDashboard(int teacherId) {
        this.teacherId = teacherId;
        JFrame frame = new JFrame("Teacher Dashboard");
        frame.setSize(650, 500); 
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Teacher Dashboard");
        titleLabel.setBounds(160, 30, 350, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        frame.add(titleLabel);

        JButton viewCoursesBtn = new JButton("View Your Courses");
        viewCoursesBtn.setBounds(170, 110, 300, 50); 
        viewCoursesBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(viewCoursesBtn);
 
        JButton markAttendanceBtn = new JButton("Mark Attendance");
        markAttendanceBtn.setBounds(170, 180, 300, 50); 
        markAttendanceBtn.setFont(new Font("Arial", Font.PLAIN, 18)); 
        frame.add(markAttendanceBtn);

        
        JButton viewAttendanceBtn = new JButton("View Attendance");
        viewAttendanceBtn.setBounds(170, 260, 300, 50); // Adjusted position and size
        viewAttendanceBtn.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font
        frame.add(viewAttendanceBtn);

       
        JButton viewleaveBtn = new JButton("View Leave Requests");
        viewleaveBtn.setBounds(170, 330, 300, 50); // Adjusted position and size
        viewleaveBtn.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font
        frame.add(viewleaveBtn);

        viewCoursesBtn.addActionListener(e -> new ViewCourses(teacherId));
        markAttendanceBtn.addActionListener(e -> new MarkAttendance(teacherId));
        viewAttendanceBtn.addActionListener(e -> new ViewAttendance(teacherId));
        viewleaveBtn.addActionListener(e -> new ViewLeaveRequests(teacherId));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        checkStudentWarnings();
        
        
    }
  
    private void checkStudentWarnings() {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT s.name AS student_name, c.course_name, COUNT(*) AS absences " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.status = 'Absent' AND c.teacher_id = ? " +
                     "GROUP BY a.student_id, a.course_id " +
                     "HAVING absences >= 5";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, teacherId);

        ResultSet rs = stmt.executeQuery();
        StringBuilder warningMsg = new StringBuilder();

        while (rs.next()) {
            String studentName = rs.getString("student_name");
            String courseName = rs.getString("course_name");
            int absences = rs.getInt("absences");

            warningMsg.append("⚠️ ")
                      .append(studentName)
                      .append(" has ")
                      .append(absences)
                      .append(" absences in ")
                      .append(courseName)
                      .append("\n");
        }

        if (warningMsg.length() > 0) {
            JOptionPane.showMessageDialog(null, warningMsg.toString());
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error checking student absences.");
    }
}
    public static void main (String ar[])
    {
        
        new TeacherDashboard(1);
        
    }
}
