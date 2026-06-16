package ui;

import database.DepartmentDAO;
import database.EmployeeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dashboard home panel — shows live statistics cards and quick-action buttons.
 * Feature 9: Dashboard Overview.
 */
public class HomePanel extends JPanel {

    private final DashboardFrame dashboard;
    private final EmployeeDAO    empDAO;
    private final DepartmentDAO  deptDAO;

    private JLabel lblEmpCount;
    private JLabel lblDeptCount;
    private JLabel lblLatestEmp;

    // Theme fields
    private JPanel       titleBar;
    private JLabel       pageTitle;
    private JPanel       centre;
    private JLabel       qaTitle;
    private JScrollPane  scroll;
    private final java.util.List<JPanel> statCardsList = new java.util.ArrayList<>();

    public HomePanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        this.empDAO    = dashboard.getEmployeeDAO();
        this.deptDAO   = dashboard.getDepartmentDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.getBgPanel());
        buildUI();
        refresh();
    }

    private void buildUI() {

        // ── Page title ───────────────────────────────────────────────────────
        titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.getBgPanel());
        titleBar.setBorder(new EmptyBorder(24, 28, 10, 28));

        pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(Theme.getTxtPrimary());
        titleBar.add(pageTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("⟳ Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(60, 130, 210));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refresh());
        titleBar.add(btnRefresh, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // ── Centre content ───────────────────────────────────────────────────
        centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(Theme.getBgPanel());
        centre.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEmpCount  = new JLabel("—");
        lblDeptCount = new JLabel("—");
        lblLatestEmp = new JLabel("—");

        JPanel card1 = statCard("Total Employees",   lblEmpCount,  new Color(60,  130, 210), "👥");
        JPanel card2 = statCard("Total Departments", lblDeptCount, new Color(50,  170, 130), "🏢");
        JPanel card3 = statCard("Latest Employee",   lblLatestEmp, new Color(180, 100, 220), "🆕");

        statCardsList.add(card1);
        statCardsList.add(card2);
        statCardsList.add(card3);

        statsRow.add(card1);
        statsRow.add(card2);
        statsRow.add(card3);
        centre.add(statsRow);

        centre.add(Box.createVerticalStrut(24));

        // Quick actions
        qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qaTitle.setForeground(Theme.getTxtPrimary());
        qaTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(qaTitle);
        centre.add(Box.createVerticalStrut(12));

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        quickActions.setOpaque(false);
        quickActions.setAlignmentX(Component.LEFT_ALIGNMENT);

        quickActions.add(quickBtn("➕ Add Employee", new Color(60, 130, 210), () -> {
            dashboard.showCard(DashboardFrame.CARD_EMP_FORM);
        }));
        quickActions.add(quickBtn("👥 View Employees", new Color(50, 170, 130), () -> {
            dashboard.getEmployeeDAO(); // trigger refresh inside panel
            dashboard.showCard(DashboardFrame.CARD_EMP_TABLE);
        }));
        quickActions.add(quickBtn("🔍 Search", new Color(220, 140, 40), () -> {
            dashboard.showCard(DashboardFrame.CARD_SEARCH);
        }));
        quickActions.add(quickBtn("📊 Reports", new Color(180, 80, 200), () -> {
            dashboard.showCard(DashboardFrame.CARD_REPORTS);
        }));
        quickActions.add(quickBtn("🏢 Departments", new Color(50, 140, 160), () -> {
            dashboard.showCard(DashboardFrame.CARD_DEPT);
        }));

        centre.add(quickActions);

        // Wrap in scroll pane in case window shrinks
        scroll = new JScrollPane(centre);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Theme.getBgPanel());
        scroll.getViewport().setBackground(Theme.getBgPanel());
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshTheme() {
        setBackground(Theme.getBgPanel());
        if (titleBar != null) titleBar.setBackground(Theme.getBgPanel());
        if (pageTitle != null) pageTitle.setForeground(Theme.getTxtPrimary());
        if (centre != null) centre.setBackground(Theme.getBgPanel());
        if (qaTitle != null) qaTitle.setForeground(Theme.getTxtPrimary());
        if (scroll != null) {
            scroll.setBackground(Theme.getBgPanel());
            scroll.getViewport().setBackground(Theme.getBgPanel());
        }

        // Recolor stat cards
        for (JPanel card : statCardsList) {
            card.setBackground(Theme.getBgCard());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorderColor(), 1),
                    new EmptyBorder(18, 20, 18, 20)));
            for (Component c : card.getComponents()) {
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    if (lbl.getFont().getSize() < 30) {
                        lbl.setForeground(Theme.getTxtSecondary());
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    // ── Stat card builder ─────────────────────────────────────────────────────

    private JPanel statCard(String title, JLabel valueLabel, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel iconLabel = new JLabel(icon + "  " + title);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        iconLabel.setForeground(new Color(100, 110, 130));
        card.add(iconLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);

        // Colour accent strip on left
        JPanel strip = new JPanel();
        strip.setPreferredSize(new Dimension(5, 0));
        strip.setBackground(accent);
        card.add(strip, BorderLayout.WEST);

        return card;
    }

    // ── Quick action button ────────────────────────────────────────────────────

    private JButton quickBtn(String text, Color bg, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ── Refresh live stats ────────────────────────────────────────────────────

    public void refresh() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            String latestName;
            @Override protected int[] doInBackground() {
                int empCount  = empDAO.getEmployeeCount();
                int deptCount = deptDAO.getAllDepartments().size();
                latestName    = empDAO.getLatestEmployeeName();
                return new int[]{empCount, deptCount};
            }
            @Override protected void done() {
                try {
                    int[] counts = get();
                    lblEmpCount .setText(String.valueOf(counts[0]));
                    lblDeptCount.setText(String.valueOf(counts[1]));
                    lblLatestEmp.setText(latestName != null ? latestName : "N/A");
                    lblLatestEmp.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    dashboard.setStatus("Dashboard refreshed.");
                } catch (Exception ex) {
                    dashboard.setStatus("Error loading stats: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
