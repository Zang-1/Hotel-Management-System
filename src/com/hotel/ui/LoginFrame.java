package com.hotel.ui;

import com.hotel.manager.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * LoginFrame — Neon Gold Cyberpunk Login Screen.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel statusLabel;

    private final RoomManager roomManager;
    private final GuestManager guestManager;
    private final ReservationManager reservationManager;
    private final StaffManager staffManager;

    public LoginFrame() {
        this.roomManager        = new RoomManager();
        this.guestManager       = new GuestManager();
        this.reservationManager = new ReservationManager(roomManager);
        this.staffManager       = new StaffManager();

        setTitle("Grand Azure Hotel — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setMinimumSize(new Dimension(860, 540));
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        buildUI();
    }

    private void buildUI() {
        // Root: deep navy background
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x02050A),
                    getWidth(), getHeight(), new Color(0x050D1A)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);
        setContentPane(root);

        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.fill = GridBagConstraints.BOTH;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;

        // ── Split: Left Branding | Right Form ─────────────────────────────
        JPanel mainSplit = new JPanel(new GridLayout(1, 2, 0, 0));
        mainSplit.setOpaque(false);

        // LEFT BRANDING PANEL
        mainSplit.add(buildBrandPanel());

        // RIGHT FORM PANEL
        mainSplit.add(buildFormPanel());

        root.add(mainSplit, rootGbc);
    }

    private JPanel buildBrandPanel() {
        JPanel left = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Deep background
                g2.setColor(new Color(0x040812));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative cyberpunk circles (gold glow)
                g2.setColor(new Color(UIConstants.COLOR_ACCENT.getRed(), UIConstants.COLOR_ACCENT.getGreen(), UIConstants.COLOR_ACCENT.getBlue(), 10));
                g2.fillOval(-80, -80, 320, 320);
                g2.fillOval(getWidth() - 120, getHeight() - 120, 240, 240);

                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(UIConstants.COLOR_ACCENT.getRed(), UIConstants.COLOR_ACCENT.getGreen(), UIConstants.COLOR_ACCENT.getBlue(), 30));
                g2.drawOval(-80, -80, 320, 320);
                g2.drawOval(getWidth() - 120, getHeight() - 120, 240, 240);

                g2.dispose();
            }
        };
        left.setOpaque(false);
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.COLOR_BORDER));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Stars
        JLabel stars = new JLabel("* * * * *", SwingConstants.CENTER);
        stars.setFont(new Font("Segoe UI", Font.BOLD, 18));
        stars.setForeground(UIConstants.COLOR_ACCENT);
        stars.setAlignmentX(CENTER_ALIGNMENT);
        content.add(stars);
        content.add(Box.createVerticalStrut(16));

        // Hotel name with accent underline
        JLabel hotelName = new JLabel("GRAND AZURE", SwingConstants.CENTER);
        hotelName.setFont(new Font("Segoe UI Light", Font.BOLD, 32));
        hotelName.setForeground(Color.WHITE);
        hotelName.setAlignmentX(CENTER_ALIGNMENT);
        content.add(hotelName);

        JLabel hotelSub = new JLabel("HOTEL & RESORT", SwingConstants.CENTER);
        hotelSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hotelSub.setForeground(UIConstants.COLOR_ACCENT);
        hotelSub.setAlignmentX(CENTER_ALIGNMENT);
        content.add(hotelSub);
        content.add(Box.createVerticalStrut(28));

        // Neon Gold divider
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] fractions = {0f, 0.5f, 1f};
                Color[] colors    = {new Color(0, 0, 0, 0), UIConstants.COLOR_ACCENT, new Color(0, 0, 0, 0)};
                java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(
                    0, 0, getWidth(), 0, fractions, colors
                );
                g2.setPaint(lgp);
                g2.fillRoundRect(0, 0, getWidth(), 2, 2, 2);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(180, 2));
        divider.setAlignmentX(CENTER_ALIGNMENT);
        content.add(divider);
        content.add(Box.createVerticalStrut(28));

        // Feature list
        String[][] features = {
            {"-", "Room Management"},
            {"-", "Guest Management"},
            {"-", "Reservations & Bookings"},
            {"-", "Staff Management"},
            {"-", "Billing & Reports"},
        };
        for (String[] f : features) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(240, 32));
            row.setAlignmentX(CENTER_ALIGNMENT);

            JLabel icon = new JLabel(f[0]);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            row.add(icon);

            JLabel lbl = new JLabel(f[1]);
            lbl.setFont(UIConstants.FONT_BODY);
            lbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
            row.add(lbl);

            content.add(row);
            content.add(Box.createVerticalStrut(4));
        }

        content.add(Box.createVerticalStrut(30));

        JLabel tagLine = new JLabel("\"Where Luxury Meets Comfort\"", SwingConstants.CENTER);
        tagLine.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagLine.setForeground(UIConstants.COLOR_TEXT_MUTED);
        tagLine.setAlignmentX(CENTER_ALIGNMENT);
        content.add(tagLine);

        left.add(content);
        return left;
    }

    private JPanel buildFormPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);

        // Glass card with Neon Gold Border
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass background
                g2.setColor(UIConstants.COLOR_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                
                // Border glow
                g2.setColor(UIConstants.COLOR_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 8, 8));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        // Title
        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(LEFT_ALIGNMENT);
        card.add(welcome);

        JLabel subtitle = new JLabel("Sign in to the management portal");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));

        // Username field
        card.add(makeFieldLabel("USERNAME"));
        card.add(Box.createVerticalStrut(6));
        txtUsername = UIHelper.createTextField("");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername.setText("admin");
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));

        // Password field
        card.add(makeFieldLabel("PASSWORD"));
        card.add(Box.createVerticalStrut(6));
        txtPassword = UIHelper.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(6));

        JLabel hintLabel = new JLabel("Default credentials: admin / admin123");
        hintLabel.setFont(UIConstants.FONT_SMALL);
        hintLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);
        hintLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hintLabel);
        card.add(Box.createVerticalStrut(24));

        // Status label (shows error messages inline)
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIConstants.FONT_SMALL_BOLD);
        statusLabel.setForeground(UIConstants.COLOR_DANGER);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(4));

        // Login button - Neon Style
        btnLogin = UIHelper.createPrimaryButton("SIGN IN  ->");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(btnLogin);
        card.add(btnLogin);

        card.add(Box.createVerticalStrut(24));
        JSeparator sep = UIHelper.createSeparator();
        sep.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(16));

        JLabel footer = new JLabel("(c) 2026 Grand Azure Hotel Management System");
        footer.setFont(UIConstants.FONT_SMALL);
        footer.setForeground(UIConstants.COLOR_TEXT_MUTED);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        card.add(footer);

        // Add card to right with padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 50, 40, 50);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        right.add(card, gbc);

        return right;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(UIConstants.COLOR_ACCENT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void attemptLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("!  Username and password are required");
            return;
        }
        if (user.equals("admin") && pass.equals("admin123")) {
            statusLabel.setText("OK  Authenticating...");
            statusLabel.setForeground(UIConstants.COLOR_SUCCESS);
            Timer t = new Timer(400, e -> {
                MainFrame mf = new MainFrame(roomManager, guestManager, reservationManager, staffManager);
                mf.setVisible(true);
                dispose();
            });
            t.setRepeats(false);
            t.start();
        } else {
            statusLabel.setText("X  Invalid credentials. Try admin / admin123");
            statusLabel.setForeground(UIConstants.COLOR_DANGER);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
