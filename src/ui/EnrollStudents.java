package ui;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

public class EnrollStudents {

    JComboBox<String> studentComboBox;
    JComboBox<String> courseComboBox;

    public EnrollStudents() {
    JFrame frame = new JFrame("Enroll Students");
    frame.setSize(550, 400); 
    frame.setLayout(null);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Font labelFont = new Font("Arial", Font.BOLD, 16);
    Font fieldFont = new Font("Arial", Font.PLAIN, 16);

    JLabel titleLabel = new JLabel("Enroll Students");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setBounds(190, 20, 200, 30);

    JLabel studentLabel = new JLabel("Select Student:");
    studentLabel.setFont(labelFont);
    studentLabel.setBounds(60, 80, 130, 30);

    studentComboBox = new JComboBox<>();
    studentComboBox.setFont(fieldFont);
    studentComboBox.setBounds(200, 80, 250, 30);

    JLabel courseLabel = new JLabel("Select Course:");
    courseLabel.setFont(labelFont);
    courseLabel.setBounds(60, 130, 130, 30);

    courseComboBox = new JComboBox<>();
    courseComboBox.setFont(fieldFont);
    courseComboBox.setBounds(200, 130, 250, 30);

    JButton enrollBtn = new JButton("Enroll");
    enrollBtn.setFont(labelFont);
    enrollBtn.setBounds(60, 200, 120, 35);

    JButton viewBtn = new JButton("View Enrollments");
    viewBtn.setFont(labelFont);
    viewBtn.setBounds(200, 200, 200, 35);

    JButton backBtn = new JButton("Back");
    backBtn.setFont(labelFont);
    backBtn.setBounds(420, 200, 90, 35);

    frame.add(titleLabel);
    frame.add(studentLabel);
    frame.add(studentComboBox);
    frame.add(courseLabel);
    frame.add(courseComboBox);
    frame.add(enrollBtn);
    frame.add(viewBtn);
    frame.add(backBtn);

        loadStudents();
        loadCourses();

        enrollBtn.addActionListener(e -> {
            String student = (String) studentComboBox.getSelectedItem();
            String course = (String) courseComboBox.getSelectedItem();
            if (student != null && course != null) {
                enrollStudent(student, course);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select both student and course.");
            }
        });

        viewBtn.addActionListener(e -> viewEnrollments());

        backBtn.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void loadStudents() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement("SELECT name FROM students");
            rs = stmt.executeQuery();
            while (rs.next()) {
                studentComboBox.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { 
                if (rs != null) 
                    rs.close(); 
                if (stmt != null) 
                    stmt.close(); 
                if (conn != null) 
                    conn.close(); 
            } catch (Exception ex) 
            {
            }
        }
    }

    void loadCourses() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement("SELECT course_name FROM courses");
            rs = stmt.executeQuery();
            while (rs.next()) {
                courseComboBox.addItem(rs.getString("course_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) 
                    rs.close(); 
                if (stmt != null) 
                    stmt.close(); 
                if (conn != null) 
                    conn.close(); 
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        }
    }

    void enrollStudent(String studentName, String courseName) {
        Connection conn = null;
        PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
        ResultSet rs1 = null, rs2 = null;

        try {
            conn = DBConnection.getConnection();

            stmt1 = conn.prepareStatement("SELECT id FROM students WHERE name = ?");
            stmt1.setString(1, studentName);
            rs1 = stmt1.executeQuery();
            if (!rs1.next()) return;
            int studentId = rs1.getInt("id");

            stmt2 = conn.prepareStatement("SELECT course_id FROM courses WHERE course_name = ?");
            stmt2.setString(1, courseName);
            rs2 = stmt2.executeQuery();
            if (!rs2.next()) return;
            int courseId = rs2.getInt("course_id");

            stmt3 = conn.prepareStatement("INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)");
            stmt3.setInt(1, studentId);
            stmt3.setInt(2, courseId);
            int rows = stmt3.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Student enrolled successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to enroll student.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs1 != null) rs1.close();
                if (rs2 != null) rs2.close();
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (stmt3 != null) stmt3.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {}
        }
    }
    
    void viewEnrollments() 
{
    JFrame tableFrame = new JFrame("View Enrollments");
    tableFrame.setSize(600, 400);  
    tableFrame.setLayout(null);
    tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

   
    String[] columns = {"Student Name", "Course Name"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = DBConnection.getConnection();
        stmt = conn.prepareStatement(
            "SELECT s.name AS student_name, c.course_name AS course_name " +
            "FROM enrollments e " +
            "JOIN students s ON e.student_id = s.id " +
            "JOIN courses c ON e.course_id = c.course_id"
        );
        rs = stmt.executeQuery();

        while (rs.next()) {
            String studentName = rs.getString("student_name");
            String courseName = rs.getString("course_name");
            model.addRow(new Object[]{studentName, courseName});
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    JTable table = new JTable(model);
    table.setRowHeight(25); 
    table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    table.setFont( new Font("Arial", Font.PLAIN, 13));

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBounds(30, 30, 520, 280); 

    tableFrame.add(scrollPane);
    tableFrame.setLocationRelativeTo(null);
    tableFrame.setVisible(true);
}
    
    public static void main(String[] args) {
        new EnrollStudents();
    }
}