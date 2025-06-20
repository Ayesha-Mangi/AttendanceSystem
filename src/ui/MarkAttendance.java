package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import db.DBConnection;

public class MarkAttendance {
    private int teacherId;
    private List<JCheckBox> studentCheckboxes = new ArrayList<>();

    public MarkAttendance(int teacherId) {
        this.teacherId = teacherId;

        JFrame frame = new JFrame("Mark Attendance");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel courseLabel = new JLabel("Select Course:");
        JComboBox<String> courseBox = new JComboBox<>();
        JLabel dateLabel = new JLabel("Date (yyyy-mm-dd):");
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
      
        JButton loadBtn = new JButton("Load Students");

        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(courseLabel);
        formPanel.add(courseBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(new JLabel()); 
        formPanel.add(loadBtn);

        
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(studentPanel);
        JButton markAllPresentBtn = new JButton("Mark All Present");
        JButton markAllAbsentBtn = new JButton("Mark All Absent");
        
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton markBtn = new JButton("Mark Attendance");
        JButton backBtn = new JButton("Back");
        bottomPanel.add(markBtn);
        bottomPanel.add(backBtn);
        bottomPanel.add(markAllPresentBtn);
        bottomPanel.add(markAllAbsentBtn);

        
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadCourses(courseBox);

        loadBtn.addActionListener(e -> {
            String course = (String) courseBox.getSelectedItem();
            loadStudents(course, studentPanel);
            });

       markBtn.addActionListener(e -> {
        String course = (String) courseBox.getSelectedItem();
        String dateText = dateField.getText();
        if (course != null && !dateText.isEmpty()) {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to mark attendance?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            mark(course, dateText, studentPanel);
        }
        } 
        else {
        JOptionPane.showMessageDialog(null, "Please select course and enter date.");
        }
            });
    
        markAllPresentBtn.addActionListener(e -> {
        for (JCheckBox cb : studentCheckboxes) {
            cb.setSelected(true);  
        }
        });
        
        markAllAbsentBtn.addActionListener(e -> {
        for (JCheckBox cb : studentCheckboxes) {
            cb.setSelected(false);  
        }
        });
        
        backBtn.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

       private void loadCourses(JComboBox<String> box) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT course_name FROM courses WHERE teacher_id = ?")) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                box.addItem(rs.getString("course_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStudents(String course, JPanel panel) {
    panel.removeAll();
    studentCheckboxes.clear();
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 "SELECT s.id, s.name FROM students s " +
                 "JOIN enrollments e ON s.id = e.student_id " +
                 "JOIN courses c ON c.course_id = e.course_id " +
                 "WHERE c.course_name = ? AND (e.is_dropped = FALSE OR e.is_dropped IS NULL)")) {
        ps.setString(1, course);
        ResultSet rs = ps.executeQuery();
        boolean found = false;
        while (rs.next()) {
            int studentId = rs.getInt("id");  
            String studentName = rs.getString("name");  
            String displayText = "ID: " + studentId + " - " + studentName;  

            JCheckBox check = new JCheckBox(displayText); 
            check.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(check);
            studentCheckboxes.add(check);
            found = true;
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "No students found for this course.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    panel.revalidate();
    panel.repaint();
}
    
    
    private void mark(String course, String dateText, JPanel panel) {
        int count = 0;

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement checkPs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM attendance a " +
                    "JOIN courses c ON a.course_id = c.course_id " +
                    "WHERE c.course_name = ? AND a.attendance_date = ?");
            checkPs.setString(1, course);
            checkPs.setDate(2, java.sql.Date.valueOf(dateText));
            ResultSet checkRs = checkPs.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Attendance already marked for this date and course.");
                return;
            }
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) comp;
                    String label = cb.getText(); // e.g., "ID: 101 - Ali"
                    String[] parts = label.split(" - ");
                    int studentId = Integer.parseInt(parts[0].replace("ID: ", "").trim());
                    String status = cb.isSelected() ? "Present" : "Absent";

                   // Insert attendance directly using student ID
                PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO attendance (student_id, course_id, attendance_date, status) " +
                "SELECT ?, c.course_id, ?, ? FROM courses c WHERE c.course_name = ?"
                );
                    ps.setInt(1, studentId);
                    ps.setDate(2, java.sql.Date.valueOf(dateText));
                    ps.setString(3, status);
                    ps.setString(4, course);
                    
                    int rows = ps.executeUpdate();
                    if (rows > 0) count++;
                    ps.close();

                    if (status.equals("Absent")) {
                        PreparedStatement countAbsent = conn.prepareStatement(
                        "SELECT COUNT(*) FROM attendance a " +
                        "JOIN courses c ON a.course_id = c.course_id " +
                        "WHERE a.student_id = ? AND c.course_name = ? AND a.status = 'Absent'");
                        countAbsent.setInt(1, studentId);
                        countAbsent.setString(2, course);
                        
                        ResultSet absentRs = countAbsent.executeQuery();
                        if (absentRs.next() && absentRs.getInt(1) >= 8) {
                           PreparedStatement dropStmt = conn.prepareStatement(
                            "UPDATE enrollments e " +
                            "JOIN courses c ON e.course_id = c.course_id " +
                            "SET e.is_dropped = TRUE " +
                            "WHERE e.student_id = ? AND c.course_name = ?");
                            dropStmt.setInt(1, studentId);
                            dropStmt.setString(2, course);
                           
                            dropStmt.executeUpdate();
                            dropStmt.close();
                            JOptionPane.showMessageDialog(null,"ID : "+ studentId + " has been dropped from " + course + " due to 8 absences.");
                        }
                        countAbsent.close();
                    }
                }
            }

            if (count > 0) {
                JOptionPane.showMessageDialog(null, "Attendance Marked Successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "No students to mark attendance.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while marking attendance.");
        }
    }

    public static void main(String[] args) 
    {
        new MarkAttendance(1);
    }
    
}