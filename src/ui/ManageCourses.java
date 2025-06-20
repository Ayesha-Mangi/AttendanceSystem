package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import javax.swing.table.DefaultTableModel;

public class ManageCourses {
    
    public ManageCourses() {
    JFrame frame = new JFrame("Manage Courses");
    frame.setSize(500, 300); 
    frame.setLayout(null);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

   
    Font titleFont = new Font("Arial", Font.BOLD, 28);
    Font buttonFont = new Font("Arial", Font.PLAIN, 18);

   
    JLabel titleLabel = new JLabel("Manage Courses");
    titleLabel.setFont(titleFont);
    titleLabel.setBounds(130, 30, 250, 30);

    
    JButton addCourseBtn = new JButton("Add Course");
    addCourseBtn.setFont(buttonFont);
    addCourseBtn.setBounds(130, 100, 250, 40);

    JButton viewCoursesBtn = new JButton("View Courses");
    viewCoursesBtn.setFont(buttonFont);
    viewCoursesBtn.setBounds(130, 170, 250, 40);

    
    frame.add(titleLabel);
    frame.add(addCourseBtn);
    frame.add(viewCoursesBtn);

  
    addCourseBtn.addActionListener(e -> openAddCourseForm());
    viewCoursesBtn.addActionListener(e -> viewCourses());

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}

    private void openAddCourseForm() {
    JFrame formFrame = new JFrame("Add Course");
    formFrame.setSize(500, 450);
    formFrame.setLayout(null);
    formFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Font labelFont = new Font("Arial", Font.BOLD, 16);
    Font fieldFont = new Font("Arial", Font.PLAIN, 16);

    JLabel courseNameLabel = new JLabel("Course Name:");
    courseNameLabel.setFont(labelFont);
    courseNameLabel.setBounds(50, 50, 150, 30);
    JTextField courseNameField = new JTextField();
    courseNameField.setFont(fieldFont);
    courseNameField.setBounds(200, 50, 220, 30);

    JLabel courseCodeLabel = new JLabel("Course Code:");
    courseCodeLabel.setFont(labelFont);
    courseCodeLabel.setBounds(50, 100, 150, 30);
    JTextField courseCodeField = new JTextField();
    courseCodeField.setFont(fieldFont);
    courseCodeField.setBounds(200, 100, 220, 30);

    JLabel teacherIdLabel = new JLabel("Teacher ID:");
    teacherIdLabel.setFont(labelFont);
    teacherIdLabel.setBounds(50, 150, 150, 30);
    JTextField teacherIdField = new JTextField();
    teacherIdField.setFont(fieldFont);
    teacherIdField.setBounds(200, 150, 220, 30);

    JButton submitBtn = new JButton("Submit");
    submitBtn.setFont(labelFont);
    submitBtn.setBounds(120, 250, 110, 35);

    JButton backBtn = new JButton("Back");
    backBtn.setFont(labelFont);
    backBtn.setBounds(250, 250, 110, 35);

    formFrame.add(courseNameLabel);
    formFrame.add(courseNameField);
    formFrame.add(courseCodeLabel);
    formFrame.add(courseCodeField);
    formFrame.add(teacherIdLabel);
    formFrame.add(teacherIdField);
    formFrame.add(submitBtn);
    formFrame.add(backBtn);

        submitBtn.addActionListener(e -> {
            String courseName = courseNameField.getText().trim();
            String courseCode = courseCodeField.getText().trim();
            String teacherId = teacherIdField.getText().trim();

            if (courseName.isEmpty() || courseCode.isEmpty() || teacherId.isEmpty()) {
                JOptionPane.showMessageDialog(formFrame, "Please fill all fields!");
            } else {
                if (insertCourseToDatabase(courseName, courseCode, teacherId)) {
                    JOptionPane.showMessageDialog(formFrame, "Course added successfully!");
                    formFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(formFrame, "Error adding course to database.");
                }
            }
        });

        backBtn.addActionListener(e -> formFrame.dispose());

        formFrame.setLocationRelativeTo(null);
        formFrame.setVisible(true);
    }

    private boolean insertCourseToDatabase(String courseName, String courseCode, String teacherId) {
        String sql = "INSERT INTO courses (course_name, course_code, teacher_id) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
        
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, courseName);
            stmt.setString(2, courseCode);
            stmt.setString(3, teacherId);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
    private void viewCourses() {
    JFrame viewFrame = new JFrame("View Courses");
    viewFrame.setSize(700, 400); // increased size

    String[] columnNames = {"Course ID", "Course Name", "Course Code", "Teacher ID"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    JTable courseTable = new JTable(model);
    
    courseTable.setFont(new Font("Arial", Font.PLAIN, 16));
    courseTable.setRowHeight(25);
    
    JScrollPane scrollPane = new JScrollPane(courseTable);

    viewFrame.add(scrollPane);

    
    loadCoursesToTable(model);
    viewFrame.setLocationRelativeTo(null);
    viewFrame.setVisible(true);
}
private void loadCoursesToTable(DefaultTableModel model) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = DBConnection.getConnection();
        String sql = "SELECT * FROM courses";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

        while (rs.next()) {
            int courseId = rs.getInt("course_id");
            String courseName = rs.getString("course_name");
            String courseCode = rs.getString("course_code");
            int teacherId = rs.getInt("teacher_id");

            model.addRow(new Object[]{courseId, courseName, courseCode, teacherId});
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    public static void main(String[] args) {
        new ManageCourses();
    }
}
