package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class HomePage {

    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Student Attendance System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Student Attendance System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Select Your Role : ");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalStrut(20));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(15));
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createVerticalStrut(40));
        titlePanel.add(roleLabel);
        titlePanel.add(Box.createVerticalStrut(10));

        frame.add(titlePanel, BorderLayout.NORTH);

     
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER , 70, 55));

        JButton adminBtn = new JButton("Admin");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 25));
        JButton teacherBtn = new JButton("Teacher");
        teacherBtn.setFont(new Font("Arial", Font.BOLD, 25));
        JButton studentBtn = new JButton("Student");
        studentBtn.setFont(new Font("Arial", Font.BOLD, 25));

        Dimension buttonSize = new Dimension(150, 80);
        adminBtn.setPreferredSize(buttonSize);
        teacherBtn.setPreferredSize(buttonSize);
        studentBtn.setPreferredSize(buttonSize);

        buttonPanel.add(adminBtn);
        buttonPanel.add(teacherBtn);
        buttonPanel.add(studentBtn);

        frame.add(buttonPanel, BorderLayout.CENTER);
      
        adminBtn.addActionListener(new ActionListener() {
       
        public void actionPerformed(ActionEvent e) {
          
             AdminLogin loginWindow = new AdminLogin();
            }
        });
        
    teacherBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
         TeacherLogin loginWindow2 = new TeacherLogin();
    }
    });

    studentBtn.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        StudentLogin loginWindow3 = new StudentLogin();
    }
    });

frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
    
}
