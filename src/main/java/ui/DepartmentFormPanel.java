package ui;

import database.DepartmentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Department Form panel for adding and editing departments.
 * Form layout and transitions match EmployeeFormPanel exactly.
 */
public class DepartmentFormPanel extends JPanel {

    private final DashboardFrame dashboard;
    private final DepartmentDAO  deptDAO;

    private JTextField fId, fName;
    private JLabel     lblMode;
    private JButton    btnSave, btnCancel;

    private boolean editMode  = false;
    private int     editingId = -1;

    // Theme fields
    private JPanel      header;
    private JPanel      card;
    private JScrollPane scroll;

    public DepartmentFormPanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
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

        lblMode = new JLabel("Add New Department");
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

        fId = field("");
        fId.setEditable(false);
        fId.setFocusable(false);
        fId.setForeground(Theme.getTxtSecondary());
        
        fName = field("");

        // Row 0
        lc.gridx = 0; lc.gridy = 0; card.add(label("Department ID"), lc);
        fc.gridx = 1; fc.gridy = 0; card.add(fId, fc);

        // Row 1
        lc.gridx = 0; lc.gridy = 1; card.add(label("Department Name *"), lc);
        fc.gridx = 1; fc.gridy = 1; card.add(fName, fc);

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);

        btnSave   = makeBtn("Save",   new Color(60, 140, 220));
        btnCancel = makeBtn("Cancel", new Color(120, 130, 150));
        btnRow.add(btnSave);
        btnRow.add(btnCancel);

        // Add to card
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = 2; bc.gridwidth = 2;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(24, 0, 0, 0);
        card.add(btnRow, bc);

        // Center card inside a scrollpane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.getBgPanel());
        wrapper.setBorder(new EmptyBorder(12, 28, 28, 28));
        wrapper.add(card, BorderLayout.NORTH);

        scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setBackground(Theme.getBgPanel());
        add(scroll, BorderLayout.CENTER);

        // ── Listeners ────────────────────────────────────────────────────────
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> cancel());
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField(placeholder, 20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(Theme.getFieldBg());
        f.setForeground(Theme.getTxtPrimary());
        f.setCaretColor(Theme.getTxtPrimary());
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                new EmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Theme.getTxtPrimary());
        return l;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void prepareForAdd() {
        editMode  = false;
        editingId = -1;
        lblMode.setText("Add New Department");
        fId.setText("(Auto Allocated)");
        fName.setText("");
        fName.requestFocusInWindow();
    }

    public void prepareForEdit(Department d) {
        editMode  = true;
        editingId = d.getId();
        lblMode.setText("Edit Department");
        fId.setText(String.valueOf(d.getId()));
        fName.setText(d.getName());
        fName.requestFocusInWindow();
    }

    private void save() {
        String name = fName.getText().trim();
        if (name.isEmpty()) {
            warn("Department Name is required!");
            return;
        }

        if (editMode) {
            Department d = new Department(editingId, name);
            if (deptDAO.update(d)) {
                info("Department updated successfully!");
                dashboard.navigateToDepartmentTable();
            } else {
                error("Update failed. Department name may already exist.");
            }
        } else {
            Department d = new Department();
            d.setName(name);
            if (deptDAO.save(d)) {
                info("Department saved successfully!");
                dashboard.navigateToDepartmentTable();
            } else {
                error("Save failed. Department name may already exist.");
            }
        }
    }

    private void cancel() {
        dashboard.navigateToDepartmentTable();
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (header != null) header.setBackground(Theme.getBgPanel());
        if (lblMode != null) lblMode.setForeground(Theme.getTxtPrimary());
        if (scroll != null) scroll.setBackground(Theme.getBgPanel());
        
        if (card != null) {
            card.setBackground(Theme.getBgCard());
            card.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(0, 28, 0, 28),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.getBorderColor()),
                            new EmptyBorder(24, 28, 24, 28))));
        }

        if (fId != null) {
            fId.setBackground(Theme.getFieldBg());
            fId.setForeground(Theme.getTxtSecondary());
            fId.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                    new EmptyBorder(4, 8, 4, 8)));
        }

        if (fName != null) {
            fName.setBackground(Theme.getFieldBg());
            fName.setForeground(Theme.getTxtPrimary());
            fName.setCaretColor(Theme.getFieldBg());
            fName.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                    new EmptyBorder(4, 8, 4, 8)));
        }
    }

    private void info (String msg) { JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE); }
    private void warn (String msg) { JOptionPane.showMessageDialog(this, msg, "Warning",     JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",       JOptionPane.ERROR_MESSAGE); }
}
