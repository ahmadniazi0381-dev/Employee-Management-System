package ui;

import database.DepartmentDAO;
import database.EmployeeDAO;
import model.Department;
import model.Employee;
import util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Feature 2 (Add) + Feature 4 (Update) — shared employee add/edit form.
 * Supports both modes; call prepareForAdd() or prepareForEdit(employee).
 */
public class EmployeeFormPanel extends JPanel {

    private final DashboardFrame dashboard;
    private final EmployeeDAO    empDAO;
    private final DepartmentDAO  deptDAO;

    // Form fields
    private JTextField    fId, fName, fDesignation, fSalary, fHireDate, fEmail, fPhone;
    private JComboBox<Department> cbDepartment;

    private JLabel  lblMode;
    private JButton btnSave, btnCancel;

    private boolean editMode  = false;
    private int     editingId = -1;

    // Theme fields
    private JPanel      header;
    private JPanel      card;
    private JScrollPane scroll;
    private JPanel      btnBar;

    public EmployeeFormPanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        this.empDAO    = dashboard.getEmployeeDAO();
        this.deptDAO   = dashboard.getDepartmentDAO();
        setLayout(new BorderLayout());
        setBackground(Theme.getBgPanel());
        buildUI();
    }

    private void buildUI() {

        // ── Page header ──────────────────────────────────────────────────────
        header = new JPanel(new BorderLayout());
        header.setBackground(Theme.getBgPanel());
        header.setBorder(new EmptyBorder(20, 28, 8, 28));

        lblMode = new JLabel("Add New Employee");
        lblMode.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblMode.setForeground(Theme.getTxtPrimary());
        header.add(lblMode, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Form card ────────────────────────────────────────────────────────
        card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.getBgCard());
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 28, 0, 28),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.getBorderColor()),
                        new EmptyBorder(24, 28, 24, 28))));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor  = GridBagConstraints.WEST;
        lc.insets  = new Insets(9, 0, 9, 14);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = new Insets(9, 0, 9, 0);

        fId          = field(""); 
        fName        = field("");
        fDesignation = field("");
        fSalary      = field("");
        fHireDate    = field("YYYY-MM-DD");
        fEmail       = field("");
        fPhone       = field("11 digits");
        cbDepartment = new JComboBox<>();
        cbDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbDepartment.setBackground(Theme.getFieldBg());
        cbDepartment.setForeground(Theme.getTxtPrimary());

        // Row 0
        lc.gridx = 0; lc.gridy = 0; card.add(label("Employee ID *"),  lc);
        fc.gridx = 1; fc.gridy = 0; card.add(fId,                      fc);
        lc.gridx = 2; lc.gridy = 0; card.add(label("Full Name *"),     lc);
        fc.gridx = 3; fc.gridy = 0; card.add(fName,                    fc);

        // Row 1
        lc.gridx = 0; lc.gridy = 1; card.add(label("Department *"),   lc);
        fc.gridx = 1; fc.gridy = 1; card.add(cbDepartment,             fc);
        lc.gridx = 2; lc.gridy = 1; card.add(label("Designation *"),  lc);
        fc.gridx = 3; fc.gridy = 1; card.add(fDesignation,             fc);

        // Row 2
        lc.gridx = 0; lc.gridy = 2; card.add(label("Salary (PKR) *"), lc);
        fc.gridx = 1; fc.gridy = 2; card.add(fSalary,                  fc);
        lc.gridx = 2; lc.gridy = 2; card.add(label("Hire Date *"),    lc);
        fc.gridx = 3; fc.gridy = 2; card.add(fHireDate,                fc);

        // Row 3
        lc.gridx = 0; lc.gridy = 3; card.add(label("Email *"),        lc);
        fc.gridx = 1; fc.gridy = 3; card.add(fEmail,                   fc);
        lc.gridx = 2; lc.gridy = 3; card.add(label("Phone *"),        lc);
        fc.gridx = 3; fc.gridy = 3; card.add(fPhone,                   fc);

        // Column spacing
        GridBagConstraints spacer = new GridBagConstraints();
        spacer.gridx = 4; spacer.weightx = 0.01; card.add(new JLabel(), spacer);

        scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Theme.getBgPanel());
        scroll.getViewport().setBackground(Theme.getBgPanel());
        add(scroll, BorderLayout.CENTER);

        // ── Button bar ────────────────────────────────────────────────────────
        btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnBar.setBackground(Theme.getBgPanel());
        btnBar.setBorder(new EmptyBorder(0, 24, 4, 24));

        btnCancel = makeBtn("Cancel",       new Color(130, 140, 160));
        btnSave   = makeBtn("💾 Save",      new Color(60,  130, 210));
        btnBar.add(btnCancel);
        btnBar.add(btnSave);
        add(btnBar, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────────────────
        btnSave  .addActionListener(e -> save());
        btnCancel.addActionListener(e -> dashboard.showCard(DashboardFrame.CARD_EMP_TABLE));
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (header != null) header.setBackground(Theme.getBgPanel());
        if (lblMode != null) lblMode.setForeground(Theme.getTxtPrimary());
        if (card != null) {
            card.setBackground(Theme.getBgCard());
            card.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(0, 28, 0, 28),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.getBorderColor()),
                            new EmptyBorder(24, 28, 24, 28))));
            for (Component comp : card.getComponents()) {
                if (comp instanceof JLabel) {
                    comp.setForeground(Theme.getTxtPrimary());
                } else if (comp instanceof JTextField) {
                    JTextField f = (JTextField) comp;
                    f.setBackground(Theme.getFieldBg());
                    f.setForeground(Theme.getTxtPrimary());
                    f.setCaretColor(Theme.getTxtPrimary());
                    f.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                            new EmptyBorder(5, 8, 5, 8)));
                } else if (comp instanceof JComboBox) {
                    comp.setBackground(Theme.getFieldBg());
                    comp.setForeground(Theme.getTxtPrimary());
                }
            }
        }
        if (scroll != null) {
            scroll.setBackground(Theme.getBgPanel());
            scroll.getViewport().setBackground(Theme.getBgPanel());
        }
        if (btnBar != null) btnBar.setBackground(Theme.getBgPanel());
        
        // Specially handle read-only ID field color in edit mode
        if (editMode && fId != null) {
            fId.setBackground(Theme.isDarkMode ? new Color(35, 42, 58) : new Color(240, 242, 248));
        }
        revalidate();
        repaint();
    }

    // ── Mode setters ──────────────────────────────────────────────────────────

    public void prepareForAdd() {
        editMode  = false;
        editingId = -1;
        lblMode.setText("Add New Employee");
        btnSave.setText("💾 Save Employee");
        clearFields();
        loadDepartments();
        fId.setEditable(true);
        fId.setBackground(Color.WHITE);
    }

    public void prepareForEdit(Employee emp) {
        editMode  = true;
        editingId = emp.getId();
        lblMode.setText("Edit Employee — " + emp.getName());
        btnSave.setText("💾 Update Employee");
        loadDepartments();
        fId.setText(String.valueOf(emp.getId()));
        fId.setEditable(false);
        fId.setBackground(new Color(240, 242, 248));
        fName       .setText(emp.getName());
        fDesignation.setText(emp.getDesignation());
        fSalary     .setText(String.valueOf(emp.getSalary()));
        fHireDate   .setText(emp.getHireDate() != null ? emp.getHireDate().toString() : "");
        fEmail      .setText(emp.getEmail());
        fPhone      .setText(emp.getPhone());

        // Select matching department
        for (int i = 0; i < cbDepartment.getItemCount(); i++) {
            if (cbDepartment.getItemAt(i).getId() == emp.getDepartmentId()) {
                cbDepartment.setSelectedIndex(i);
                break;
            }
        }
    }

    // ── Save logic ────────────────────────────────────────────────────────────

    private void save() {
        // ── Validation ────────────────────────────────────────────────────────
        String idStr   = fId.getText().trim();
        String name    = fName.getText().trim();
        String desig   = fDesignation.getText().trim();
        String salStr  = fSalary.getText().trim();
        String dateStr = fHireDate.getText().trim();
        String email   = fEmail.getText().trim();
        String phone   = fPhone.getText().trim();

        if (!InputValidator.isNumeric(idStr)) {
            showError("Employee ID must be a positive integer.");
            fId.requestFocus(); return;
        }
        if (!InputValidator.isNotEmpty(name)) {
            showError("Name cannot be empty."); fName.requestFocus(); return;
        }
        if (!InputValidator.isNotEmpty(desig)) {
            showError("Designation cannot be empty."); fDesignation.requestFocus(); return;
        }
        if (!InputValidator.isPositiveDecimal(salStr)) {
            showError("Salary must be a positive number."); fSalary.requestFocus(); return;
        }
        if (!InputValidator.isValidEmail(email)) {
            showError("Please enter a valid email address (e.g. user@domain.com)."); fEmail.requestFocus(); return;
        }
        if (!InputValidator.isValidPhone(phone)) {
            showError("Phone must be exactly 11 digits (numbers only)."); fPhone.requestFocus(); return;
        }

        Date hireDate;
        try {
            hireDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            showError("Hire Date must be in YYYY-MM-DD format."); fHireDate.requestFocus(); return;
        }

        Department selectedDept = (Department) cbDepartment.getSelectedItem();
        if (selectedDept == null) {
            showError("No department selected. Please wait for departments to load, then try again.");
            return;
        }

        int empId = Integer.parseInt(idStr);

        // Build entity
        Employee emp = new Employee();
        emp.setId(empId);
        emp.setName(name);
        emp.setDepartmentId(selectedDept.getId());
        emp.setDesignation(desig);
        emp.setSalary(Double.parseDouble(salStr));
        emp.setHireDate(hireDate);
        emp.setEmail(email);
        emp.setPhone(phone);

        // Disable button during save
        btnSave.setEnabled(false);
        btnSave.setText("Saving…");

        boolean isEdit = editMode;
        int finalEmpId = empId;

        new SwingWorker<Boolean, Void>() {
            private String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    // Duplicate ID check (add mode only) — done in background
                    if (!isEdit && empDAO.employeeExists(finalEmpId)) {
                        errorMsg = "Employee with ID " + finalEmpId + " already exists. Choose a different ID.";
                        return false;
                    }
                    return isEdit ? empDAO.updateEmployee(emp) : empDAO.addEmployee(emp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorMsg = "Database error: " + ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                btnSave.setEnabled(true);
                btnSave.setText(isEdit ? "💾 Update Employee" : "💾 Save Employee");
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(EmployeeFormPanel.this,
                                isEdit ? "Employee updated successfully!" : "Employee added successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dashboard.navigateToEmployeeTable();
                        dashboard.refreshHome();
                        dashboard.setStatus(isEdit
                                ? "Employee " + finalEmpId + " updated."
                                : "Employee " + finalEmpId + " added.");
                    } else {
                        showError(errorMsg != null ? errorMsg
                                : "Operation failed. Check that the department exists and all fields are correct.");
                    }
                } catch (Exception ex) {
                    showError("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void loadDepartments() {
        cbDepartment.removeAllItems();
        new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() {
                return deptDAO.getAllDepartments();
            }
            @Override
            protected void done() {
                try {
                    List<Department> depts = get();
                    for (Department d : depts) {
                        cbDepartment.addItem(d);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void clearFields() {
        fId.setText(""); fName.setText(""); fDesignation.setText("");
        fSalary.setText(""); fHireDate.setText("YYYY-MM-DD");
        fEmail.setText(""); fPhone.setText("");
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField(18);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 208, 225)),
                new EmptyBorder(5, 8, 5, 8)));
        if (!placeholder.isEmpty()) f.setText(placeholder);
        return f;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(55, 65, 85));
        return l;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
