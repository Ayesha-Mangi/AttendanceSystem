package ui;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

public class ManageUsers {

    public ManageUsers() {
        JFrame frame = new JFrame("Manage Users");
        frame.setSize(650, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setBounds(190, 30, 300, 40);

        JButton addStudentBtn = new JButton("Add Student");
        addStudentBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        addStudentBtn.setBounds(170, 110, 300, 50);

        JButton addTeacherBtn = new JButton("Add Teacher");
        addTeacherBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        addTeacherBtn.setBounds(170, 180, 300, 50);

        JButton viewStudentBtn = new JButton("View Students");
        viewStudentBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        viewStudentBtn.setBounds(170, 260, 300, 50);

        JButton viewTeacherBtn = new JButton("View Teachers");
        viewTeacherBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        viewTeacherBtn.setBounds(170, 330, 300, 50);

        frame.add(titleLabel);
        frame.add(addStudentBtn);
        frame.add(addTeacherBtn);
        frame.add(viewStudentBtn);
        frame.add(viewTeacherBtn);

        addStudentBtn.addActionListener(e -> openAddForm("student"));
        addTeacherBtn.addActionListener(e -> openAddForm("teacher"));
        viewStudentBtn.addActionListener(e -> viewUsers("student"));
        viewTeacherBtn.addActionListener(e -> viewUsers("teacher"));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void openAddForm(String role) {
    JFrame formFrame = new JFrame("Add " + role);
    formFrame.setSize(500, 450); 
    formFrame.setLayout(null);
    formFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Font labelFont = new Font("Arial", Font.PLAIN, 18); // Larger font
    Font fieldFont = new Font("Arial", Font.PLAIN, 16);

    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setBounds(50, 60, 120, 30);
    nameLabel.setFont(labelFont);
    JTextField nameField = new JTextField();
    nameField.setBounds(180, 60, 220, 30);
    nameField.setFont(fieldFont);

    JLabel passLabel = new JLabel("Password:");
    passLabel.setBounds(50, 110, 120, 30);
    passLabel.setFont(labelFont);
    JPasswordField passField = new JPasswordField();
    passField.setBounds(180, 110, 220, 30);
    passField.setFont(fieldFont);

    JLabel phoneLabel = new JLabel("Phone:");
    phoneLabel.setBounds(50, 160, 120, 30);
    phoneLabel.setFont(labelFont);
    JTextField phoneField = new JTextField();
    phoneField.setBounds(180, 160, 220, 30);
    phoneField.setFont(fieldFont);

    JButton submitBtn = new JButton("Submit");
    submitBtn.setBounds(120, 250, 110, 40);
    submitBtn.setFont(labelFont);

    JButton backBtn = new JButton("Back");
    backBtn.setBounds(260, 250, 110, 40);
    backBtn.setFont(labelFont);
 
        formFrame.add(nameLabel);
        formFrame.add(nameField);
        formFrame.add(passLabel);
        formFrame.add(passField);
        formFrame.add(phoneLabel);
        formFrame.add(phoneField);
        formFrame.add(submitBtn);
        formFrame.add(backBtn);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(formFrame, "Please fill all fields!");
            } else {
                if (insertIntoDatabase(role, name, password, phone)) {
                    JOptionPane.showMessageDialog(formFrame, role + " added successfully!");
                    formFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(formFrame, "Error adding " + role + " to database.");
                }
            }
        });

        backBtn.addActionListener(e -> formFrame.dispose());

        formFrame.setLocationRelativeTo(null);
        formFrame.setVisible(true);
    }

    private boolean insertIntoDatabase(String role, String name, String password, String phone) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = role.equals("student") ?
                    "INSERT INTO students (name, password, phone) VALUES (?, ?, ?)" :
                    "INSERT INTO teachers (name, password, phone) VALUES (?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setString(3, phone);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

   private void viewUsers(String role) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = DBConnection.getConnection();
        String sql = role.equals("student") ? "SELECT * FROM students" : "SELECT * FROM teachers";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Phone");

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            model.addRow(new Object[]{id, name, phone});
        }

        JTable table = new JTable(model);
        
        // Set larger font and row height
        Font tableFont = new Font("Arial", Font.PLAIN, 16);
        table.setFont(tableFont);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // header font

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 250)); // Bigger scroll pane

        JOptionPane.showMessageDialog(null, scrollPane, "View " + role + "s", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching " + role + " data.");
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public static void main(String[] args) {
        new ManageUsers();
    }
}