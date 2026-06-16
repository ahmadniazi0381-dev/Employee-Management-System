package ui;

import database.DepartmentDAO;
import database.EmployeeDAO;
import database.UserDAO;
import model.Department;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Feature 3 – View All Employees.
 * Displays all employees in a sortable JTable with Edit / Delete / Add buttons.
 */
public class EmployeeTablePanel extends JPanel {

    private final DashboardFrame dashboard;
    private final EmployeeDAO    empDAO;
    private final DepartmentDAO  deptDAO;

    private JTable            table;
    private DefaultTableModel model;
    private JLabel            lblCount;

    // Theme fields
    private JPanel      header;
    private JLabel      title;
    private JScrollPane scroll;
    private JPanel      btnBar;

    private static final String[] COLUMNS = {
            "ID", "Name", "Department", "Designation", "Salary", "Hire Date", "Email", "Phone"
    };

    public EmployeeTablePanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        this.empDAO    = dashboard.getEmployeeDAO();
        this.deptDAO   = dashboard.getDepartmentDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.getBgPanel());
        buildUI();
    }

    private void buildUI() {

        // ── Header ───────────────────────────────────────────────────────────
        header = new JPanel(new BorderLayout());
        header.setBackground(Theme.getBgPanel());
        header.setBorder(new EmptyBorder(20, 24, 10, 24));

        title = new JLabel("All Employees");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.getTxtPrimary());
        header.add(title, BorderLayout.WEST);

        lblCount = new JLabel();
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(Theme.getTxtSecondary());
        header.add(lblCount, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Integer.class;
                if (c == 4) return Double.class;
                return String.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Theme.getTableHeaderBg());
        table.getTableHeader().setForeground(Theme.getTableHeaderFg());
        table.setSelectionBackground(Theme.getTableSelectionBg());
        table.setSelectionForeground(Theme.getTableSelectionFg());
        table.setGridColor(Theme.getTableGrid());
        table.setShowVerticalLines(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Make columns sortable
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Column widths
        int[] widths = {55, 150, 140, 140, 90, 100, 190, 115};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Alternating row colours via renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Theme.getBgCard() : Theme.getAltRowColor());
                    setForeground(Theme.getTxtPrimary());
                } else {
                    setBackground(Theme.getTableSelectionBg());
                    setForeground(Theme.getTableSelectionFg());
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 24, 0, 24),
                BorderFactory.createLineBorder(Theme.getBorderColor())));
        add(scroll, BorderLayout.CENTER);

        // ── Button bar ────────────────────────────────────────────────────────
        btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(Theme.getBgPanel());
        btnBar.setBorder(new EmptyBorder(0, 24, 6, 24));

        JButton btnAdd    = makeBtn("➕ Add Employee",   new Color(60, 130, 210));
        JButton btnEdit   = makeBtn("✏ Edit",            new Color(50, 160, 110));
        JButton btnDelete = makeBtn("🗑 Delete",          new Color(200, 60, 60));
        JButton btnRefresh= makeBtn("⟳ Refresh",         new Color(100, 110, 140));

        btnBar.add(btnAdd);
        btnBar.add(btnEdit);
        btnBar.add(btnDelete);
        btnBar.add(btnRefresh);
        add(btnBar, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────────────────
        btnAdd.addActionListener(e -> {
            dashboard.getEmployeeFormPanel().prepareForAdd();
            dashboard.showCard(DashboardFrame.CARD_EMP_FORM);
        });

        btnEdit.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) { warn("Please select an employee to edit."); return; }
            int modelRow = table.convertRowIndexToModel(viewRow);
            int id = (int) model.getValueAt(modelRow, 0);
            Employee emp = empDAO.getById(id);
            if (emp == null) { error("Could not load employee record."); return; }
            dashboard.showEmployeeForm(emp);
        });

        btnDelete.addActionListener(e -> deleteSelected());

        btnRefresh.addActionListener(e -> refresh());

        // Double-click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent ev) {
                if (ev.getClickCount() == 2) btnEdit.doClick();
            }
        });
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (header != null) header.setBackground(Theme.getBgPanel());
        if (title != null) title.setForeground(Theme.getTxtPrimary());
        if (lblCount != null) lblCount.setForeground(Theme.getTxtSecondary());
        if (btnBar != null) btnBar.setBackground(Theme.getBgPanel());
        
        if (table != null) {
            table.getTableHeader().setBackground(Theme.getTableHeaderBg());
            table.getTableHeader().setForeground(Theme.getTableHeaderFg());
            table.setSelectionBackground(Theme.getTableSelectionBg());
            table.setSelectionForeground(Theme.getTableSelectionFg());
            table.setGridColor(Theme.getTableGrid());
            table.setBackground(Theme.getBgCard());
            table.setForeground(Theme.getTxtPrimary());
        }
        
        if (scroll != null) {
            scroll.setBackground(Theme.getBgPanel());
            scroll.getViewport().setBackground(Theme.getBgCard());
            scroll.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(0, 24, 0, 24),
                    BorderFactory.createLineBorder(Theme.getBorderColor())));
        }
        revalidate();
        repaint();
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    public void refresh() {
        SwingWorker<List<Employee>, Void> worker = new SwingWorker<>() {
            @Override protected List<Employee> doInBackground() {
                return empDAO.getAllEmployees();
            }
            @Override protected void done() {
                try {
                    populate(get());
                    dashboard.setStatus("Employee list loaded.");
                } catch (Exception ex) {
                    dashboard.setStatus("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void populate(List<Employee> employees) {
        model.setRowCount(0);
        List<Department> depts = deptDAO.getAllDepartments();
        for (Employee e : employees) {
            String deptName = depts.stream()
                    .filter(d -> d.getId() == e.getDepartmentId())
                    .map(Department::getName)
                    .findFirst().orElse("ID:" + e.getDepartmentId());
            model.addRow(new Object[]{
                    e.getId(), e.getName(), deptName,
                    e.getDesignation(), e.getSalary(),
                    e.getHireDate(), e.getEmail(), e.getPhone()
            });
        }
        lblCount.setText(employees.size() + " record(s)");
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) { warn("Please select an employee to delete."); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int empId    = (int) model.getValueAt(modelRow, 0);
        String name  = (String) model.getValueAt(modelRow, 1);

        // ── Password Confirmation Dialog ──────────────────────────────────────
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this) instanceof java.awt.Frame
                ? (java.awt.Frame) SwingUtilities.getWindowAncestor(this) : null,
                "Confirm Delete", true);
        dlg.setSize(420, 240);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(Theme.getBgCard());
        outer.setBorder(new EmptyBorder(24, 28, 20, 28));

        // Warning message
        JLabel lblMsg = new JLabel(
                "<html>You are about to delete:<br><b>" + name + " (ID: " + empId + ")</b><br>"
                + "<span style='color:#cc4444;'>This cannot be undone.</span><br><br>"
                + "Enter your password to confirm:</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(Theme.getTxtPrimary());
        outer.add(lblMsg, BorderLayout.NORTH);

        // Password field
        JPasswordField pwdField = new JPasswordField();
        pwdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pwdField.setPreferredSize(new Dimension(0, 34));
        pwdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                new EmptyBorder(4, 8, 4, 8)));
        pwdField.setBackground(Theme.getFieldBg());
        pwdField.setForeground(Theme.getTxtPrimary());
        pwdField.setCaretColor(Theme.getTxtPrimary());
        JPanel pwdWrap = new JPanel(new BorderLayout());
        pwdWrap.setBackground(Theme.getBgCard());
        pwdWrap.setBorder(new EmptyBorder(10, 0, 14, 0));
        pwdWrap.add(pwdField);
        outer.add(pwdWrap, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Theme.getBgCard());
        JButton btnCancel = new JButton("Cancel");
        JButton btnConfirm = new JButton("🗑 Delete");
        styleDialogBtn(btnCancel,  new Color(100, 110, 140));
        styleDialogBtn(btnConfirm, new Color(200, 60, 60));
        btnRow.add(btnCancel);
        btnRow.add(btnConfirm);
        outer.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(outer);

        // Allow Enter key to confirm
        pwdField.addActionListener(e -> btnConfirm.doClick());

        final boolean[] confirmed = {false};
        btnCancel .addActionListener(e -> dlg.dispose());
        btnConfirm.addActionListener(e -> {
            String pwd = new String(pwdField.getPassword()).trim();
            if (pwd.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Password cannot be empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verify password against DB
            UserDAO userDAO = new UserDAO();
            if (userDAO.authenticate(dashboard.getLoggedInUser(), pwd)) {
                confirmed[0] = true;
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Incorrect password. Delete cancelled.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                pwdField.setText("");
                pwdField.requestFocus();
            }
        });

        dlg.setVisible(true);  // blocks until disposed

        if (!confirmed[0]) return;

        // ── Do the actual delete in background ────────────────────────────────
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return empDAO.deleteEmployee(empId);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(EmployeeTablePanel.this,
                                "Employee \"" + name + "\" deleted successfully.",
                                "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        refresh();
                        dashboard.refreshHome();
                        dashboard.setStatus("Employee " + empId + " deleted.");
                    } else {
                        error("Failed to delete employee. Please try again.");
                    }
                } catch (Exception ex) {
                    error("Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void styleDialogBtn(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn(String msg)  {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);
    }
}
