package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import db.DBConnection;

public class ViewCourses {
    private int teacherId;

    public ViewCourses(int teacherId) {
        this.teacherId = teacherId;

        JFrame frame = new JFrame("Your Courses");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        JLabel titleLabel = new JLabel("Your Courses", SwingConstants.CENTER);
        frame.add(titleLabel, BorderLayout.NORTH);

      
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);


        loadCourses(table);

        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

   
    private void loadCourses(JTable table) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            
            conn = DBConnection.getConnection();
            String query = "SELECT course_name, course_code FROM courses WHERE teacher_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, teacherId);
            rs = pstmt.executeQuery();

            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Course Name");
            model.addColumn("Course Code");

          
            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String courseCode = rs.getString("course_code");
                model.addRow(new Object[]{courseName, courseCode});
            }
            
            table.setModel(model);

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
    }
}