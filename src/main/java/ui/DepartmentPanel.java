package ui;

import database.DepartmentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Feature 7 — Department Management (Add / Edit / Delete).
 * Displays a list of departments in a JTable and provides a button bar
 * to navigate to DepartmentFormPanel for creation/updating.
 */
public class DepartmentPanel extends JPanel {

    private final DashboardFrame dashboard;
    private final DepartmentDAO  deptDAO;

    private JTable            table;
    private DefaultTableModel model;
    private JLabel            lblCount;

    // Theme fields
    private JPanel      header;
    private JLabel      title;
    private JScrollPane scroll;
    private JPanel      btnBar;

    public DepartmentPanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
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
        title = new JLabel("Department Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.getTxtPrimary());
        header.add(title, BorderLayout.WEST);
        lblCount = new JLabel();
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(Theme.getTxtSecondary());
        header.add(lblCount, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"ID", "Department Name"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Theme.getTableHeaderBg());
        table.getTableHeader().setForeground(Theme.getTableHeaderFg());
        table.setSelectionBackground(Theme.getTableSelectionBg());
        table.setSelectionForeground(Theme.getTableSelectionFg());
        table.setGridColor(Theme.getTableGrid());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(new TableRowSorter<>(model));

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);

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
        scroll.setBorder(BorderFactory.createLineBorder(Theme.getBorderColor()));
        add(scroll, BorderLayout.CENTER);

        // ── Button bar ────────────────────────────────────────────────────────
        btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(Theme.getBgPanel());
        btnBar.setBorder(new EmptyBorder(0, 24, 6, 24));

        JButton btnAdd     = makeBtn("➕ Add Department",    new Color(60, 130, 210));
        JButton btnEdit    = makeBtn("✏ Edit",               new Color(50, 160, 110));
        JButton btnDelete  = makeBtn("🗑 Delete",             new Color(200, 60, 60));
        JButton btnRefresh = makeBtn("⟳ Refresh",            new Color(100, 110, 140));

        btnBar.add(btnAdd);
        btnBar.add(btnEdit);
        btnBar.add(btnDelete);
        btnBar.add(btnRefresh);
        add(btnBar, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────────────────
        btnRefresh.addActionListener(e -> refresh());

        btnAdd.addActionListener(e -> {
            dashboard.showDepartmentFormForAdd();
        });

        btnEdit.addActionListener(e -> {
            int vRow = table.getSelectedRow();
            if (vRow == -1) { warn("Select a department to edit."); return; }
            int    id  = (int)    model.getValueAt(table.convertRowIndexToModel(vRow), 0);
            String name = (String) model.getValueAt(table.convertRowIndexToModel(vRow), 1);
            dashboard.showDepartmentFormForEdit(new Department(id, name));
        });

        btnDelete.addActionListener(e -> {
            int vRow = table.getSelectedRow();
            if (vRow == -1) { warn("Select a department to delete."); return; }
            int    id  = (int)    model.getValueAt(table.convertRowIndexToModel(vRow), 0);
            String name = (String) model.getValueAt(table.convertRowIndexToModel(vRow), 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete department \"" + name + "\"?\n" +
                    "Note: departments with assigned employees cannot be deleted.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (deptDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Department deleted.");
                    refresh();
                    dashboard.refreshHome();
                } else {
                    error("Cannot delete — employees may be assigned to this department.\n" +
                          "Reassign or remove employees first.");
                }
            }
        });

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
            scroll.setBorder(BorderFactory.createLineBorder(Theme.getBorderColor()));
        }
        revalidate();
        repaint();
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    public void refresh() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override protected List<Department> doInBackground() { return deptDAO.getAllDepartments(); }
            @Override protected void done() {
                try {
                    List<Department> depts = get();
                    model.setRowCount(0);
                    for (Department d : depts) {
                        model.addRow(new Object[]{d.getId(), d.getName()});
                    }
                    lblCount.setText(depts.size() + " department(s)");
                    dashboard.setStatus("Departments loaded.");
                } catch (Exception ex) {
                    dashboard.setStatus("Error: " + ex.getMessage());
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
        btn.setPreferredSize(new Dimension(160, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn (String msg) { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
}
