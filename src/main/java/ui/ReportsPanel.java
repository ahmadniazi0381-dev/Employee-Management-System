package ui;

import util.ReportGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PrinterException;

/**
 * Feature 8 — Report Generation.
 * Tab 1: Department Summary (employee count per department).
 * Tab 2: Salary Range Report (filterable min–max salary).
 */
public class ReportsPanel extends JPanel {

    private final DashboardFrame  dashboard;
    private final ReportGenerator generator;

    // Dept summary
    private JTable deptSummaryTable;

    // Salary range
    private JTextField txtMinSalary;
    private JTextField txtMaxSalary;
    private JTable     salaryTable;

    // Theme fields
    private JPanel      header;
    private JLabel      title;
    private JTabbedPane tabs;
    private JPanel      deptSummaryTab;
    private JPanel      salaryRangeTab;
    private JLabel      lblMinSalary;
    private JLabel      lblMaxSalary;

    public ReportsPanel(DashboardFrame dashboard) {
        this.dashboard  = dashboard;
        this.generator  = new ReportGenerator();
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.getBgPanel());
        buildUI();
    }

    private void buildUI() {

        // ── Header ───────────────────────────────────────────────────────────
        header = new JPanel(new BorderLayout());
        header.setBackground(Theme.getBgPanel());
        header.setBorder(new EmptyBorder(20, 24, 10, 24));
        title = new JLabel("Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.getTxtPrimary());
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Tabbed reports ────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(new EmptyBorder(0, 24, 16, 24));

        deptSummaryTab = buildDeptSummaryTab();
        salaryRangeTab = buildSalaryRangeTab();

        tabs.addTab("📋 Department Summary",  deptSummaryTab);
        tabs.addTab("💰 Salary Range Report", salaryRangeTab);

        add(tabs, BorderLayout.CENTER);
    }

    // ── Department Summary Tab ────────────────────────────────────────────────

    private JPanel buildDeptSummaryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Theme.getBgCard());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        JButton btnGenerate = makeBtn("🔄 Generate", new Color(60, 130, 210));
        JButton btnPrint    = makeBtn("🖨 Print",     new Color(80, 90, 110));
        JButton btnExport   = makeBtn("📁 Export TXT",new Color(50, 155, 100));
        toolbar.add(btnGenerate);
        toolbar.add(btnPrint);
        toolbar.add(btnExport);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        deptSummaryTable = buildStyledTable();
        panel.add(new JScrollPane(deptSummaryTable), BorderLayout.CENTER);

        // Listeners
        btnGenerate.addActionListener(e -> loadDeptSummary());
        btnPrint   .addActionListener(e -> printTable(deptSummaryTable));
        btnExport  .addActionListener(e -> exportToText(deptSummaryTable));

        // Auto-load
        loadDeptSummary();
        return panel;
    }

    private void loadDeptSummary() {
        SwingWorker<DefaultTableModel, Void> w = new SwingWorker<>() {
            @Override protected DefaultTableModel doInBackground() {
                return generator.getDepartmentSummary();
            }
            @Override protected void done() {
                try {
                    deptSummaryTable.setModel(get());
                    styleTableHeader(deptSummaryTable);
                    dashboard.setStatus("Department summary generated.");
                } catch (Exception ex) {
                    dashboard.setStatus("Error: " + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    // ── Salary Range Tab ──────────────────────────────────────────────────────

    private JPanel buildSalaryRangeTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Theme.getBgCard());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Filter controls
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterRow.setOpaque(false);

        txtMinSalary = new JTextField("0",      10);
        txtMaxSalary = new JTextField("500000", 10);
        styleInput(txtMinSalary);
        styleInput(txtMaxSalary);

        JButton btnFilter = makeBtn("🔍 Apply Filter", new Color(60, 130, 210));
        JButton btnPrint  = makeBtn("🖨 Print",         new Color(80, 90, 110));
        JButton btnExport = makeBtn("📁 Export TXT",    new Color(50, 155, 100));

        lblMinSalary = new JLabel("Min Salary: ");
        lblMinSalary.setForeground(Theme.getTxtPrimary());
        lblMaxSalary = new JLabel("  Max Salary: ");
        lblMaxSalary.setForeground(Theme.getTxtPrimary());

        filterRow.add(lblMinSalary);
        filterRow.add(txtMinSalary);
        filterRow.add(lblMaxSalary);
        filterRow.add(txtMaxSalary);
        filterRow.add(btnFilter);
        filterRow.add(btnPrint);
        filterRow.add(btnExport);
        panel.add(filterRow, BorderLayout.NORTH);

        // Table
        salaryTable = buildStyledTable();
        panel.add(new JScrollPane(salaryTable), BorderLayout.CENTER);

        // Listeners
        btnFilter.addActionListener(e -> loadSalaryRange());
        btnPrint .addActionListener(e -> printTable(salaryTable));
        btnExport.addActionListener(e -> exportToText(salaryTable));

        // Auto-load with defaults
        loadSalaryRange();
        return panel;
    }

    private void loadSalaryRange() {
        double min, max;
        try {
            min = Double.parseDouble(txtMinSalary.getText().trim());
            max = Double.parseDouble(txtMaxSalary.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for salary range.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (min > max) {
            JOptionPane.showMessageDialog(this,
                    "Minimum salary cannot be greater than maximum salary.",
                    "Invalid Range", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double fMin = min, fMax = max;
        SwingWorker<DefaultTableModel, Void> w = new SwingWorker<>() {
            @Override protected DefaultTableModel doInBackground() {
                return generator.getSalaryRangeReport(fMin, fMax);
            }
            @Override protected void done() {
                try {
                    salaryTable.setModel(get());
                    styleTableHeader(salaryTable);
                    dashboard.setStatus("Salary report generated (PKR " + (int) fMin + " – " + (int) fMax + ").");
                } catch (Exception ex) {
                    dashboard.setStatus("Error: " + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private JTable buildStyledTable() {
        JTable t = new JTable();
        t.setRowHeight(28);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setGridColor(Theme.getTableGrid());
        t.setSelectionBackground(Theme.getTableSelectionBg());
        t.setSelectionForeground(Theme.getTableSelectionFg());
        t.setBackground(Theme.getBgCard());
        t.setForeground(Theme.getTxtPrimary());
        t.setFillsViewportHeight(true);
        styleTableHeader(t);

        // Alternating row colors
        t.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
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

        return t;
    }

    private void styleTableHeader(JTable t) {
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(Theme.getTableHeaderBg());
        h.setForeground(Theme.getTableHeaderFg());
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (header != null) header.setBackground(Theme.getBgPanel());
        if (title != null) title.setForeground(Theme.getTxtPrimary());
        if (tabs != null) {
            tabs.setBackground(Theme.getBgPanel());
            tabs.setForeground(Theme.getTxtPrimary());
        }
        if (deptSummaryTab != null) deptSummaryTab.setBackground(Theme.getBgCard());
        if (salaryRangeTab != null) salaryRangeTab.setBackground(Theme.getBgCard());
        if (lblMinSalary != null) lblMinSalary.setForeground(Theme.getTxtPrimary());
        if (lblMaxSalary != null) lblMaxSalary.setForeground(Theme.getTxtPrimary());
        
        if (txtMinSalary != null) {
            txtMinSalary.setBackground(Theme.getFieldBg());
            txtMinSalary.setForeground(Theme.getTxtPrimary());
            txtMinSalary.setCaretColor(Theme.getTxtPrimary());
            txtMinSalary.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor()),
                    new EmptyBorder(3, 6, 3, 6)));
        }
        if (txtMaxSalary != null) {
            txtMaxSalary.setBackground(Theme.getFieldBg());
            txtMaxSalary.setForeground(Theme.getTxtPrimary());
            txtMaxSalary.setCaretColor(Theme.getTxtPrimary());
            txtMaxSalary.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor()),
                    new EmptyBorder(3, 6, 3, 6)));
        }

        // Recolor tables
        for (JTable t : new JTable[]{deptSummaryTable, salaryTable}) {
            if (t != null) {
                t.setBackground(Theme.getBgCard());
                t.setForeground(Theme.getTxtPrimary());
                t.setGridColor(Theme.getTableGrid());
                t.setSelectionBackground(Theme.getTableSelectionBg());
                t.setSelectionForeground(Theme.getTableSelectionFg());
                styleTableHeader(t);
                
                // Recolor scrollpane viewport if applicable
                Container parent = t.getParent();
                if (parent instanceof JViewport) {
                    parent.setBackground(Theme.getBgCard());
                    Container grandParent = parent.getParent();
                    if (grandParent instanceof JScrollPane) {
                        ((JScrollPane) grandParent).setBorder(BorderFactory.createLineBorder(Theme.getBorderColor()));
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    private void printTable(JTable t) {
        try {
            boolean ok = t.print();
            if (ok) JOptionPane.showMessageDialog(this, "Print job sent successfully.");
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToText(JTable t) {
        if (t.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("report.txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
            DefaultTableModel m = (DefaultTableModel) t.getModel();
            // Header
            for (int c = 0; c < m.getColumnCount(); c++) {
                pw.printf("%-30s", m.getColumnName(c));
            }
            pw.println();
            pw.println("-".repeat(30 * m.getColumnCount()));
            // Rows
            for (int r = 0; r < m.getRowCount(); r++) {
                for (int c = 0; c < m.getColumnCount(); c++) {
                    Object val = m.getValueAt(r, c);
                    pw.printf("%-30s", val != null ? val.toString() : "");
                }
                pw.println();
            }
            JOptionPane.showMessageDialog(this, "Report exported to:\n" + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(145, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleInput(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 200, 220)),
                new EmptyBorder(3, 6, 3, 6)));
    }
}
