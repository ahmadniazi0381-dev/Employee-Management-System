package ui;

import database.DepartmentDAO;
import database.EmployeeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window.
 * Uses CardLayout to switch between content panels.
 * Provides JMenuBar for navigation and a sidebar with quick links.
 */
public class DashboardFrame extends JFrame {

    // ── Shared DAO instances (passed down to panels) ─────────────────────────
    private final EmployeeDAO   employeeDAO   = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    // ── Card names ───────────────────────────────────────────────────────────
    public static final String CARD_HOME       = "HOME";
    public static final String CARD_EMP_TABLE  = "EMP_TABLE";
    public static final String CARD_EMP_FORM   = "EMP_FORM";
    public static final String CARD_SEARCH     = "SEARCH";
    public static final String CARD_DEPT       = "DEPT";
    public static final String CARD_DEPT_FORM  = "DEPT_FORM";
    public static final String CARD_REPORTS    = "REPORTS";

    // ── Layout & panels ──────────────────────────────────────────────────────
    private final CardLayout   cardLayout  = new CardLayout();
    private final JPanel       contentArea = new JPanel(cardLayout);

    private HomePanel         homePanel;
    private EmployeeTablePanel empTablePanel;
    private EmployeeFormPanel  empFormPanel;
    private SearchPanel        searchPanel;
    private DepartmentPanel    deptPanel;
    private DepartmentFormPanel deptFormPanel;
    private ReportsPanel       reportsPanel;

    private final String loggedInUser;
    private JLabel statusLabel;

    // Theme integration fields
    private JPanel  topBar;
    private JLabel  logoLabel;
    private JLabel  userLabel;
    private JButton btnTheme;
    private JPanel  sidebar;
    private JPanel  statusBar;
    private final java.util.List<JButton> sidebarButtons = new java.util.ArrayList<>();
    private final java.util.List<JLabel> sidebarSections = new java.util.ArrayList<>();

    public DashboardFrame(String loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("Employee Management System — Dashboard");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        buildUI();
    }

    // ── UI Construction ──────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.getBgPanel());

        topBar = buildMenuBar_asPanel();
        sidebar = buildSidebar();
        statusBar = buildStatusBar();

        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Register all panels
        homePanel     = new HomePanel(this);
        empTablePanel = new EmployeeTablePanel(this);
        empFormPanel  = new EmployeeFormPanel(this);
        searchPanel   = new SearchPanel(this);
        deptPanel     = new DepartmentPanel(this);
        deptFormPanel = new DepartmentFormPanel(this);
        reportsPanel  = new ReportsPanel(this);

        contentArea.add(homePanel,     CARD_HOME);
        contentArea.add(empTablePanel, CARD_EMP_TABLE);
        contentArea.add(empFormPanel,  CARD_EMP_FORM);
        contentArea.add(searchPanel,   CARD_SEARCH);
        contentArea.add(deptPanel,     CARD_DEPT);
        contentArea.add(deptFormPanel, CARD_DEPT_FORM);
        contentArea.add(reportsPanel,  CARD_REPORTS);

        showCard(CARD_HOME);
        refreshTheme(); // Ensure colors match default theme
    }

    // ── Top bar (branding + user info) ───────────────────────────────────────

    private JPanel buildMenuBar_asPanel() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(22, 36, 58));
        bar.setBorder(new EmptyBorder(10, 18, 10, 18));

        logoLabel = new JLabel("EMS  ·  Employee Management System");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoLabel.setForeground(new Color(100, 180, 255));
        bar.add(logoLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        userLabel = new JLabel("Logged in as: " + loggedInUser);
        userLabel.setForeground(new Color(180, 200, 230));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        right.add(userLabel);

        // Theme Toggle Button
        btnTheme = new ThemeToggleButton();
        btnTheme.addActionListener(e -> toggleTheme());
        right.add(btnTheme);



        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar navigation ───────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBackground(new Color(30, 45, 68));
        bar.setPreferredSize(new Dimension(185, 0));
        bar.setBorder(new EmptyBorder(12, 0, 12, 0));

        bar.add(sidebarSection("NAVIGATION"));
        bar.add(sidebarBtn("🏠  Dashboard",   () -> showCard(CARD_HOME)));
        bar.add(sidebarBtn("👥  Employees",   () -> { empTablePanel.refresh(); showCard(CARD_EMP_TABLE); }));
        bar.add(sidebarBtn("🔍  Search",       () -> showCard(CARD_SEARCH)));
        bar.add(sidebarSection("MANAGEMENT"));
        bar.add(sidebarBtn("🏢  Departments",  () -> { deptPanel.refresh(); showCard(CARD_DEPT); }));
        bar.add(sidebarBtn("📊  Reports",      () -> showCard(CARD_REPORTS)));
        bar.add(Box.createVerticalGlue());
        bar.add(sidebarSection("SYSTEM"));
        bar.add(sidebarBtn("🚪  Logout",       this::logout));

        return bar;
    }

    private JLabel sidebarSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 120, 155));
        lbl.setBorder(new EmptyBorder(14, 14, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarSections.add(lbl);
        return lbl;
    }

    private JButton sidebarBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(200, 215, 235));
        btn.setBackground(new Color(30, 45, 68));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { 
                btn.setBackground(Theme.isDarkMode ? new Color(35, 50, 75) : new Color(50, 75, 110)); 
            }
            @Override public void mouseExited (MouseEvent e) { 
                btn.setBackground(Theme.isDarkMode ? new Color(20, 30, 48) : new Color(30, 45, 68));  
            }
        });
        btn.addActionListener(e -> action.run());
        sidebarButtons.add(btn);
        return btn;
    }

    // ── Status bar ───────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(235, 238, 245));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 215)),
                new EmptyBorder(4, 14, 4, 14)));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(80, 90, 110));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void toggleTheme() {
        Theme.isDarkMode = !Theme.isDarkMode;
        refreshTheme();
    }

    public void refreshTheme() {
        // Toggle button update
        if (btnTheme != null) {
            btnTheme.repaint();
        }

        // Apply background of Dashboard frame
        setBackground(Theme.getBgPanel());

        // Top bar updates
        if (topBar != null) {
            topBar.setBackground(Theme.isDarkMode ? new Color(15, 23, 38) : new Color(22, 36, 58));
        }
        if (logoLabel != null) {
            logoLabel.setForeground(Theme.isDarkMode ? new Color(100, 180, 255) : new Color(120, 190, 255));
        }
        if (userLabel != null) {
            userLabel.setForeground(Theme.isDarkMode ? new Color(160, 180, 210) : new Color(180, 200, 230));
        }

        // Sidebar updates
        if (sidebar != null) {
            sidebar.setBackground(Theme.isDarkMode ? new Color(20, 30, 48) : new Color(30, 45, 68));
        }
        for (JButton btn : sidebarButtons) {
            btn.setBackground(Theme.isDarkMode ? new Color(20, 30, 48) : new Color(30, 45, 68));
            btn.setForeground(Theme.isDarkMode ? new Color(210, 225, 245) : new Color(200, 215, 235));
        }
        for (JLabel sec : sidebarSections) {
            sec.setForeground(Theme.isDarkMode ? new Color(80, 100, 130) : new Color(100, 120, 155));
        }

        // Status bar updates
        if (statusBar != null) {
            statusBar.setBackground(Theme.isDarkMode ? new Color(15, 23, 38) : new Color(235, 238, 245));
            statusBar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorderColor()),
                    new EmptyBorder(4, 14, 4, 14)));
        }
        if (statusLabel != null) {
            statusLabel.setForeground(Theme.getTxtSecondary());
        }

        // Content area background
        if (contentArea != null) {
            contentArea.setBackground(Theme.getBgPanel());
        }

        // Refresh all sub-panels
        if (homePanel != null) homePanel.refreshTheme();
        if (empTablePanel != null) empTablePanel.refreshTheme();
        if (empFormPanel != null) empFormPanel.refreshTheme();
        if (searchPanel != null) searchPanel.refreshTheme();
        if (deptPanel != null) deptPanel.refreshTheme();
        if (deptFormPanel != null) deptFormPanel.refreshTheme();
        if (reportsPanel != null) reportsPanel.refreshTheme();

        revalidate();
        repaint();
    }

    // ── Public navigation API (used by panels) ────────────────────────────────

    public void showCard(String card) {
        cardLayout.show(contentArea, card);
    }

    public void showEmployeeForm(model.Employee employeeToEdit) {
        empFormPanel.prepareForEdit(employeeToEdit);
        showCard(CARD_EMP_FORM);
    }

    /** Called by EmployeeFormPanel after a successful save/update. */
    public void navigateToEmployeeTable() {
        empTablePanel.refresh();
        showCard(CARD_EMP_TABLE);
    }

    public void showDepartmentFormForAdd() {
        deptFormPanel.prepareForAdd();
        showCard(CARD_DEPT_FORM);
    }

    public void showDepartmentFormForEdit(model.Department d) {
        deptFormPanel.prepareForEdit(d);
        showCard(CARD_DEPT_FORM);
    }

    public void navigateToDepartmentTable() {
        deptPanel.refresh();
        showCard(CARD_DEPT);
    }

    public void refreshHome() {
        homePanel.refresh();
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    // ── Accessors for shared DAOs ─────────────────────────────────────────────

    public EmployeeDAO        getEmployeeDAO()       { return employeeDAO;   }
    public DepartmentDAO      getDepartmentDAO()     { return departmentDAO; }
    public String             getLoggedInUser()      { return loggedInUser;  }
    public EmployeeFormPanel  getEmployeeFormPanel() { return empFormPanel;  }

    // ── Logout / Exit ─────────────────────────────────────────────────────────

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Exit the application?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) System.exit(0);
    }

    // ── Small helper for top-bar buttons ─────────────────────────────────────

    private void styleTopBtn(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(80, 28));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}

// ── Custom Circular Toggle Button with Status Light ──────────────────────────
class ThemeToggleButton extends JButton {
    public ThemeToggleButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(28, 28)); // Perfect circle
        setToolTipText("Toggle Theme (Dark / Light)");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int size = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        
        // Background circle
        g2.setColor(Theme.isDarkMode ? new Color(30, 45, 70) : new Color(220, 225, 235));
        g2.fillOval(x, y, size, size);
        
        // Border circle
        g2.setColor(Theme.isDarkMode ? new Color(80, 110, 160) : new Color(170, 180, 195));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, size, size);
        
        // Inner "light" (smaller circle)
        int lightSize = size / 2;
        int lx = (getWidth() - lightSize) / 2;
        int ly = (getHeight() - lightSize) / 2;
        
        // Glow effect / color
        Color lightColor;
        if (Theme.isDarkMode) {
            lightColor = new Color(100, 200, 255); // cool glowing blue/cyan for dark mode
        } else {
            lightColor = new Color(255, 200, 50);  // bright glowing amber/yellow for light mode
        }
        
        // Draw the light
        g2.setColor(lightColor);
        g2.fillOval(lx, ly, lightSize, lightSize);
        
        g2.dispose();
    }
}
