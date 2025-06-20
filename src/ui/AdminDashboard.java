package ui;

import java.awt.Font;
import javax.swing.*;
import java.awt.event.*;

public class AdminDashboard {
    public AdminDashboard() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(650, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Admin Dashboard!!");
        titleLabel.setBounds(160, 30, 350, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        frame.add(titleLabel);

        JButton manageUsersBtn = new JButton("Manage Users");
        manageUsersBtn.setBounds(170, 110, 300, 50);
         manageUsersBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(manageUsersBtn);

        JButton manageCoursesBtn = new JButton("Manage Courses");
        manageCoursesBtn.setBounds(170, 180, 300, 50);
        manageCoursesBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(manageCoursesBtn);

        JButton enrollBtn = new JButton("Enroll Students in Courses");
        enrollBtn.setBounds(170, 260, 300, 50);
        enrollBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(enrollBtn);

        JButton attendanceReportBtn = new JButton("View Attendance Reports");
        attendanceReportBtn.setBounds(170, 330, 300, 50);
         attendanceReportBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(attendanceReportBtn);

       
        manageUsersBtn.addActionListener(e -> new ManageUsers());
        manageCoursesBtn.addActionListener(e -> new ManageCourses());
        enrollBtn.addActionListener(e -> new EnrollStudents());
        attendanceReportBtn.addActionListener(e -> new AttendanceReport());

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
    
   public static void main (String ar[])
    {
        AdminDashboard ad = new AdminDashboard();
    }
}