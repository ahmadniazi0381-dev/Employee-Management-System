package ui;

import database.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JButton        btnExit;
    private JLabel         lblStatus;

    public LoginFrame() {
        setTitle("Employee Management System — Login");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initializeUI();
    }

    private void initializeUI() {

        // ── Main container ──────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(30, 40, 60));

        // ── Header banner ───────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(new Color(22, 32, 52));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel title = new JLabel("Employee Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(new Color(100, 180, 255));
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // ── Form card ───────────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(40, 55, 80));
        card.setBorder(new EmptyBorder(20, 35, 20, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 5, 6, 5);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtUsername = new JTextField(18);
        styleField(txtUsername);
        card.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel passWrapper = new JPanel(new BorderLayout(5, 0));
        passWrapper.setOpaque(false);

        txtPassword = new JPasswordField(14);
        styleField(txtPassword);

        JButton btnShowHide = new JButton("👁");
        btnShowHide.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnShowHide.setPreferredSize(new Dimension(32, 28));
        btnShowHide.setBackground(new Color(55, 72, 105));
        btnShowHide.setForeground(Color.WHITE);
        btnShowHide.setBorder(BorderFactory.createLineBorder(new Color(80, 110, 160), 1));
        btnShowHide.setFocusPainted(false);
        btnShowHide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final char defaultEcho = txtPassword.getEchoChar();
        btnShowHide.addActionListener(e -> {
            if (txtPassword.getEchoChar() == (char) 0) {
                txtPassword.setEchoChar(defaultEcho);
                btnShowHide.setText("👁");
                btnShowHide.setToolTipText("Show Password");
            } else {
                txtPassword.setEchoChar((char) 0);
                btnShowHide.setText("🕶");
                btnShowHide.setToolTipText("Hide Password");
            }
        });

        passWrapper.add(txtPassword, BorderLayout.CENTER);
        passWrapper.add(btnShowHide, BorderLayout.EAST);
        card.add(passWrapper, gbc);

        // Status label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(255, 100, 100));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblStatus, gbc);

        // Buttons
        gbc.gridy = 3;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);

        btnLogin = makeButton("Login", new Color(60, 140, 220));
        btnExit  = makeButton("Exit",  new Color(90, 90, 110));
        btnRow.add(btnLogin);
        btnRow.add(btnExit);
        card.add(btnRow, gbc);

        root.add(card, BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────────────────
        JLabel footer = new JLabel("© 2024 EMS  •  Default: admin / admin123");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(120, 130, 150));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(new EmptyBorder(6, 0, 8, 0));
        footer.setOpaque(true);
        footer.setBackground(new Color(22, 32, 52));
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);

        // ── Listeners ───────────────────────────────────────────────────────
        btnLogin.addActionListener(e -> login());
        btnExit .addActionListener(e -> System.exit(0));

        // Allow Enter key to trigger login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        getRootPane().setDefaultButton(btnLogin);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(new Color(55, 72, 105));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 110, 160), 1),
                new EmptyBorder(4, 8, 4, 8)));
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            Color original = bg;
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }

    // ── Login logic ─────────────────────────────────────────────────────────

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("⚠  Please enter both username and password.");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Authenticating…");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return new UserDAO().authenticate(username, password);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        DashboardFrame dashboard = new DashboardFrame(username);
                        dashboard.setVisible(true);
                        dispose();
                    } else {
                        lblStatus.setText("✗  Invalid username or password.");
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblStatus.setText("✗  Database error. Check connection.");
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}