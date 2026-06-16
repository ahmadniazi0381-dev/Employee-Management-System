package ui;

import database.DepartmentDAO;
import database.EmployeeDAO;
import model.Department;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Feature 6 — Search Employees.
 * Supports: exact match by ID, partial match by name / department / designation.
 */
public class SearchPanel extends JPanel {

    private final DashboardFrame dashboard;
    private final EmployeeDAO    empDAO;
    private final DepartmentDAO  deptDAO;

    private JComboBox<String> txtSearch;
    private boolean           isUpdating = false;
    private JComboBox<String> cbSearchType;
    private JTable            table;
    private DefaultTableModel model;
    private JLabel            lblResult;

    // Theme fields
    private JPanel      header;
    private JLabel      title;
    private JPanel      searchBar;
    private JLabel      lblType;
    private JScrollPane scroll;
    private JPanel      bottomBar;

    private static final String[] COLUMNS = {
            "ID", "Name", "Department", "Designation", "Salary", "Hire Date", "Email", "Phone"
    };

    public SearchPanel(DashboardFrame dashboard) {
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
        title = new JLabel("Search Employees");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.getTxtPrimary());
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Search bar ────────────────────────────────────────────────────────
        searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBar.setBackground(Theme.getBgCard());
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 24, 0, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.getBorderColor()),
                        new EmptyBorder(8, 12, 8, 12))));

        lblType = new JLabel("Search by:");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblType.setForeground(Theme.getTxtPrimary());

        cbSearchType = new JComboBox<>(new String[]{
                "Name / Designation", "Employee ID", "Department"
        });
        cbSearchType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbSearchType.setPreferredSize(new Dimension(185, 30));
        cbSearchType.setBackground(Theme.getFieldBg());
        cbSearchType.setForeground(Theme.getTxtPrimary());

        txtSearch = new JComboBox<>();
        txtSearch.setEditable(true);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(280, 30));

        JTextField editor = (JTextField) txtSearch.getEditor().getEditorComponent();
        editor.setBackground(Theme.getFieldBg());
        editor.setForeground(Theme.getTxtPrimary());
        editor.setCaretColor(Theme.getTxtPrimary());
        editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorderColor()),
                new EmptyBorder(4, 8, 4, 8)));

        // Typing dynamic suggestion listeners
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    return;
                }
                updateSuggestions();
            }
        });

        JButton btnSearch  = makeBtn("🔍 Search",   new Color(60, 130, 210));
        JButton btnClear   = makeBtn("✕ Clear",     new Color(130, 140, 160));
        JButton btnShowAll = makeBtn("Show All",     new Color(50, 155, 100));

        searchBar.add(lblType);
        searchBar.add(cbSearchType);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnClear);
        searchBar.add(btnShowAll);

        lblResult = new JLabel(" ");
        lblResult.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblResult.setForeground(Theme.getTxtSecondary());
        searchBar.add(lblResult);

        add(searchBar, BorderLayout.NORTH);  // layered below header

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(new TableRowSorter<>(model));

        // Col widths
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
                new EmptyBorder(10, 24, 0, 24),
                BorderFactory.createLineBorder(Theme.getBorderColor())));
        add(scroll, BorderLayout.CENTER);

        // ── Bottom bar ────────────────────────────────────────────────────────
        bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomBar.setBackground(Theme.getBgPanel());
        bottomBar.setBorder(new EmptyBorder(0, 24, 6, 24));

        JButton btnEdit   = makeBtn("✏ Edit Selected",   new Color(50, 155, 100));
        JButton btnDelete = makeBtn("🗑 Delete Selected", new Color(200, 60, 60));
        bottomBar.add(btnEdit);
        bottomBar.add(btnDelete);
        add(bottomBar, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────────────────
        btnSearch.addActionListener(e -> doSearch());
        
        JTextField editorComp = (JTextField) txtSearch.getEditor().getEditorComponent();
        editorComp.addActionListener(e -> doSearch());   // Enter key

        // Update suggestions when search category changes
        cbSearchType.addActionListener(e -> {
            editorComp.setText("");
            txtSearch.removeAllItems();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setSelectedItem("");
            editorComp.setText("");
            model.setRowCount(0);
            lblResult.setText(" ");
        });

        btnShowAll.addActionListener(e -> {
            txtSearch.setSelectedItem("");
            editorComp.setText("");
            loadAll();
        });

        btnEdit.addActionListener(e -> {
            int vRow = table.getSelectedRow();
            if (vRow == -1) { warn("Select an employee first."); return; }
            int id = (int) model.getValueAt(table.convertRowIndexToModel(vRow), 0);
            Employee emp = empDAO.getById(id);
            if (emp != null) dashboard.showEmployeeForm(emp);
        });

        btnDelete.addActionListener(e -> deleteSelected());
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (header != null) header.setBackground(Theme.getBgPanel());
        if (title != null) title.setForeground(Theme.getTxtPrimary());
        if (searchBar != null) {
            searchBar.setBackground(Theme.getBgCard());
            searchBar.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(0, 24, 0, 24),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.getBorderColor()),
                            new EmptyBorder(8, 12, 8, 12))));
        }
        if (lblType != null) lblType.setForeground(Theme.getTxtPrimary());
        if (cbSearchType != null) {
            cbSearchType.setBackground(Theme.getFieldBg());
            cbSearchType.setForeground(Theme.getTxtPrimary());
        }
        if (txtSearch != null) {
            txtSearch.setBackground(Theme.getFieldBg());
            txtSearch.setForeground(Theme.getTxtPrimary());
            JTextField editorCompTheme = (JTextField) txtSearch.getEditor().getEditorComponent();
            editorCompTheme.setBackground(Theme.getFieldBg());
            editorCompTheme.setForeground(Theme.getTxtPrimary());
            editorCompTheme.setCaretColor(Theme.getTxtPrimary());
            editorCompTheme.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor()),
                    new EmptyBorder(4, 8, 4, 8)));
        }
        if (lblResult != null) lblResult.setForeground(Theme.getTxtSecondary());
        if (bottomBar != null) bottomBar.setBackground(Theme.getBgPanel());
        
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
                    new EmptyBorder(10, 24, 0, 24),
                    BorderFactory.createLineBorder(Theme.getBorderColor())));
        }
        revalidate();
        repaint();
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void doSearch() {
        String keyword = ((JTextField) txtSearch.getEditor().getEditorComponent()).getText().trim();
        if (keyword.isEmpty()) { warn("Please enter a search term."); return; }

        String type = (String) cbSearchType.getSelectedItem();

        SwingWorker<List<Employee>, Void> w = new SwingWorker<>() {
            @Override protected List<Employee> doInBackground() {
                if ("Employee ID".equals(type)) {
                    try {
                        int id = Integer.parseInt(keyword);
                        Employee e = empDAO.searchEmployee(id);
                        return e != null ? List.of(e) : List.of();
                    } catch (NumberFormatException ex) {
                        return List.of();
                    }
                } else if ("Department".equals(type)) {
                    // Search via department name -> find dept IDs -> search employees
                    List<Department> depts = deptDAO.getAllDepartments();
                    String kw = keyword.toLowerCase();
                    List<Employee> result = new java.util.ArrayList<>();
                    for (Department d : depts) {
                        if (d.getName().toLowerCase().contains(kw)) {
                            result.addAll(empDAO.searchByDepartment(d.getId()));
                        }
                    }
                    return result;
                } else {
                    return empDAO.searchEmployee(keyword);
                }
            }
            @Override protected void done() {
                try {
                    List<Employee> results = get();
                    populate(results);
                    lblResult.setText(results.size() + " result(s) found.");
                    dashboard.setStatus("Search complete: " + results.size() + " result(s).");
                } catch (Exception ex) {
                    dashboard.setStatus("Search error: " + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    private void loadAll() {
        SwingWorker<List<Employee>, Void> w = new SwingWorker<>() {
            @Override protected List<Employee> doInBackground() { return empDAO.getAllEmployees(); }
            @Override protected void done() {
                try {
                    List<Employee> all = get();
                    populate(all);
                    lblResult.setText("Showing all " + all.size() + " employee(s).");
                } catch (Exception ex) { dashboard.setStatus("Error: " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void populate(List<Employee> employees) {
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
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    private void deleteSelected() {
        int vRow = table.getSelectedRow();
        if (vRow == -1) { warn("Select an employee first."); return; }
        int id   = (int) model.getValueAt(table.convertRowIndexToModel(vRow), 0);
        String n = (String) model.getValueAt(table.convertRowIndexToModel(vRow), 1);
        int ok   = JOptionPane.showConfirmDialog(this,
                "Delete employee \"" + n + "\" (ID: " + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            if (empDAO.deleteEmployee(id)) {
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                model.removeRow(table.convertRowIndexToModel(vRow));
                dashboard.refreshHome();
                dashboard.setStatus("Employee " + id + " deleted.");
            } else {
                error("Delete failed.");
            }
        }
    }

    // ── Suggestions autocomplete ──────────────────────────────────────────────

    private void updateSuggestions() {
        if (isUpdating) return;

        JTextField editor = (JTextField) txtSearch.getEditor().getEditorComponent();
        String typed = editor.getText().trim();
        String searchType = (String) cbSearchType.getSelectedItem();

        SwingWorker<java.util.List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<String> doInBackground() {
                java.util.List<String> candidates = new java.util.ArrayList<>();
                String typedLower = typed.toLowerCase();

                if ("Department".equals(searchType)) {
                    for (Department d : deptDAO.getAllDepartments()) {
                        if (d.getName() != null &&
                            (typed.isEmpty() || d.getName().toLowerCase().startsWith(typedLower))) {
                            candidates.add(d.getName());
                        }
                    }
                } else if ("Employee ID".equals(searchType)) {
                    for (Employee e : empDAO.getAllEmployees()) {
                        String id = String.valueOf(e.getId());
                        if (typed.isEmpty() || id.startsWith(typed)) {
                            candidates.add(id);
                        }
                    }
                } else {
                    java.util.Set<String> added = new java.util.LinkedHashSet<>();
                    for (Employee e : empDAO.getAllEmployees()) {
                        if (e.getName() != null &&
                            (typed.isEmpty() || e.getName().toLowerCase().startsWith(typedLower))) {
                            added.add(e.getName());
                        }
                        if (e.getDesignation() != null &&
                            (typed.isEmpty() || e.getDesignation().toLowerCase().startsWith(typedLower))) {
                            added.add(e.getDesignation());
                        }
                    }
                    candidates.addAll(added);
                }
                return candidates;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<String> results = get();
                    isUpdating = true;
                    txtSearch.removeAllItems();
                    for (String item : results) {
                        txtSearch.addItem(item);
                    }
                    editor.setText(typed);
                    isUpdating = false;

                    if (txtSearch.getItemCount() > 0) {
                        txtSearch.showPopup();
                    } else {
                        txtSearch.hidePopup();
                    }
                } catch (Exception ex) {
                    isUpdating = false;
                }
            }
        };
        worker.execute();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn (String msg) { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);   }
}
