/*
 *  Villena, John Kenneth M. - 23-0380-161
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ClassRecords extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField studentIdField, firstNameField, lastNameField,
            lab1Field, lab2Field, lab3Field, prelimField, attendanceField;
    private JButton addButton, deleteButton, editButton;

    public ClassRecords() {
        this.setTitle("Records - Villena, John Kenneth M. 23-0380-161");
        this.setSize(1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"StudentID", "First Name", "Last Name",
                "LAB WORK 1", "LAB WORK 2", "LAB WORK 3",
                "PRELIM EXAM", "ATTENDANCE GRADE"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 8, 5, 5));
        studentIdField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        lab1Field = new JTextField();
        lab2Field = new JTextField();
        lab3Field = new JTextField();
        prelimField = new JTextField();
        attendanceField = new JTextField();

        // Restrictions
        studentIdField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-') e.consume();
            }
        });
        firstNameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isLetter(e.getKeyChar())) e.consume();
            }
        });
        lastNameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isLetter(e.getKeyChar())) e.consume();
            }
        });
        KeyAdapter numericOnly = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        };
        lab1Field.addKeyListener(numericOnly);
        lab2Field.addKeyListener(numericOnly);
        lab3Field.addKeyListener(numericOnly);
        prelimField.addKeyListener(numericOnly);
        attendanceField.addKeyListener(numericOnly);

        // Add labels + fields
        inputPanel.add(new JLabel("StudentID:")); inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("First Name:")); inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:")); inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Lab1:")); inputPanel.add(lab1Field);
        inputPanel.add(new JLabel("Lab2:")); inputPanel.add(lab2Field);
        inputPanel.add(new JLabel("Lab3:")); inputPanel.add(lab3Field);
        inputPanel.add(new JLabel("Prelim:")); inputPanel.add(prelimField);
        inputPanel.add(new JLabel("Attendance:")); inputPanel.add(attendanceField);

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");

        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(editButton);

        this.add(inputPanel, BorderLayout.SOUTH);

        // Load CSV
        loadCSV();

        // Add row
        addButton.addActionListener(e -> {
            if (fieldsFilled()) {
                model.addRow(new Object[]{
                        studentIdField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        lab1Field.getText().trim(),
                        lab2Field.getText().trim(),
                        lab3Field.getText().trim(),
                        prelimField.getText().trim(),
                        attendanceField.getText().trim()
                });
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
            }
        });

        // Delete row
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) model.removeRow(row);
            else JOptionPane.showMessageDialog(this, "Select a row to delete!");
        });

        // Edit row
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && fieldsFilled()) {
                model.setValueAt(studentIdField.getText().trim(), row, 0);
                model.setValueAt(firstNameField.getText().trim(), row, 1);
                model.setValueAt(lastNameField.getText().trim(), row, 2);
                model.setValueAt(lab1Field.getText().trim(), row, 3);
                model.setValueAt(lab2Field.getText().trim(), row, 4);
                model.setValueAt(lab3Field.getText().trim(), row, 5);
                model.setValueAt(prelimField.getText().trim(), row, 6);
                model.setValueAt(attendanceField.getText().trim(), row, 7);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Select a row and fill all fields!");
            }
        });
    }

    private void loadCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Prelim-Exam\\Prelim-Exam\\JAVA\\MOCK_DATA.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                StringTokenizer st = new StringTokenizer(line, ",");
                Object[] row = new Object[8];
                for (int i = 0; i < 8; i++) row[i] = st.nextToken();
                model.addRow(row);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private boolean fieldsFilled() {
        return !studentIdField.getText().trim().isEmpty() &&
!firstNameField.getText().trim().isEmpty() &&
!lastNameField.getText().trim().isEmpty() &&
!lab1Field.getText().trim().isEmpty() &&
!lab2Field.getText().trim().isEmpty() &&
!lab3Field.getText().trim().isEmpty() &&
!prelimField.getText().trim().isEmpty() &&
!attendanceField.getText().trim().isEmpty();
    }

    private void clearFields() {
        studentIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        lab1Field.setText("");
        lab2Field.setText("");
        lab3Field.setText("");
        prelimField.setText("");
        attendanceField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassRecords().setVisible(true));
    }
}
