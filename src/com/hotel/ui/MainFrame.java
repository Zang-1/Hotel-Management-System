package com.hotel.ui;

import com.hotel.manager.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * MainFrame — Neon Gold Premium main window.
 */
public class MainFrame extends JFrame {

    private final RoomManager roomManager;
    private final GuestManager guestManager;
    private final ReservationManager reservationManager;
    private final StaffManager staffManager;

    private JPanel contentArea;
    private CardLayout cardLayout;
    private JLabel clockLabel, dateLabel;

    private DashboardPanel dashboardPanel;
    private RoomPanel roomPanel;
    private GuestPanel guestPanel;
    private ReservationPanel reservationPanel;
    private StaffPanel staffPanel;
    private BillingPanel billingPanel;
    private ReportPanel reportPanel;

    private JButton activeNavBtn = null;

    public MainFrame(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        this.roomManager        = rm;
        this.guestManager       = gm;
        this.reservationManager = resM;
        this.staffManager       = sm;

        setTitle("Grand Azure Hotel — Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 800);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);

        buildUI();
        startClock();
        showPanel("Dashboard");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIManager.getColor("Panel.background"));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        // Content area
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UIConstants.COLOR_BG_PANEL);

        dashboardPanel   = new DashboardPanel(roomManager, guestManager, reservationManager, staffManager);
        roomPanel        = new RoomPanel(roomManager, guestManager, reservationManager, staffManager);
        guestPanel       = new GuestPanel(roomManager, guestManager, reservationManager, staffManager);
        reservationPanel = new ReservationPanel(roomManager, guestManager, reservationManager, staffManager);
        staffPanel       = new StaffPanel(roomManager, guestManager, reservationManager, staffManager);
        billingPanel     = new BillingPanel(roomManager, guestManager, reservationManager, staffManager);
        reportPanel      = new ReportPanel(roomManager, guestManager, reservationManager, staffManager);

        contentArea.add(dashboardPanel,   "Dashboard");
        contentArea.add(roomPanel,        "Rooms");
        contentArea.add(guestPanel,       "Guests");
        contentArea.add(reservationPanel, "Reservations");
        contentArea.add(staffPanel,       "Staff");
        contentArea.add(billingPanel,     "Billing");
        contentArea.add(reportPanel,      "Reports");

        root.add(contentArea, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIManager.getColor("RootPane.background"));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(0, 20, 0, 16)
        ));
        header.setPreferredSize(new Dimension(0, UIConstants.HEADER_HEIGHT));

        // Left: Logo
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSide.setOpaque(false);

        JPanel starBadge = UIHelper.createBadge("GA", UIConstants.COLOR_ACCENT);
        starBadge.setPreferredSize(new Dimension(36, 36));
        leftSide.add(starBadge);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("GRAND AZURE");
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setForeground(Color.WHITE);
        titlePanel.add(name);
        JLabel tag = new JLabel("Hotel Management System");
        tag.setFont(UIConstants.FONT_SMALL);
        tag.setForeground(UIConstants.COLOR_ACCENT_DARK); // Dark gold
        titlePanel.add(tag);
        leftSide.add(titlePanel);
        header.add(leftSide, BorderLayout.WEST);

        // Right: Clock + User + Logout
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightSide.setOpaque(false);

        // Date + time
        JPanel clockPanel = new JPanel();
        clockPanel.setOpaque(false);
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
        dateLabel  = new JLabel();
        dateLabel.setFont(UIConstants.FONT_SMALL);
        dateLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);
        dateLabel.setAlignmentX(RIGHT_ALIGNMENT);
        clockLabel = new JLabel();
        clockLabel.setFont(UIConstants.FONT_BODY_BOLD);
        clockLabel.setForeground(UIConstants.COLOR_ACCENT); // Neon Gold
        clockLabel.setAlignmentX(RIGHT_ALIGNMENT);
        clockPanel.add(dateLabel);
        clockPanel.add(clockLabel);
        rightSide.add(clockPanel);

        JPanel userBadge = UIHelper.createBadge("ADMIN", UIConstants.COLOR_ACCENT);
        userBadge.setPreferredSize(new Dimension(80, 30));
        rightSide.add(userBadge);

        JButton logoutBtn = UIHelper.createDangerButton("Logout");
        logoutBtn.putClientProperty("JButton.buttonType", "borderless");
        logoutBtn.addActionListener(e -> {
            if (UIHelper.showConfirm(this, "Are you sure you want to logout?")) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        rightSide.add(logoutBtn);
        header.add(rightSide, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIManager.getColor("RootPane.background"));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("Component.borderColor")));
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(12));

        // Section label
        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(UIConstants.COLOR_ACCENT_DARK); // Dark gold
        navLabel.setAlignmentX(LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        sidebar.add(navLabel);
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items: icon, label, card
        Object[][] navItems = {
            {"", "Dashboard",    "Dashboard"},
            {"", "Rooms",        "Rooms"},
            {"", "Guests",       "Guests"},
            {"", "Reservations", "Reservations"},
            {"", "Staff",        "Staff"},
            {"", "Billing",      "Billing"},
            {"", "Reports",      "Reports"},
        };

        for (Object[] item : navItems) {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrapper.setOpaque(false);
            wrapper.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 46));
            JButton btn = createNavButton((String)item[1], (String)item[2]);
            wrapper.add(btn);
            sidebar.add(wrapper);
        }

        sidebar.add(Box.createVerticalGlue());

        // Version
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        versionPanel.setOpaque(false);
        JLabel versionLbl = new JLabel("v1.0  Grand Azure HMS");
        versionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        versionPanel.add(versionLbl);
        sidebar.add(versionPanel);
        sidebar.add(Box.createVerticalStrut(8));

        return sidebar;
    }

    private JButton createNavButton(String label, String card) {
        JButton btn = new JButton("  " + label);
        btn.putClientProperty("JButton.buttonType", "borderless");
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(UIConstants.COLOR_TEXT_MUTED);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, 42));
        btn.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        btn.addActionListener(e -> showPanel(card));
        return btn;
    }

    private void showPanel(String cardName) {
        cardLayout.show(contentArea, cardName);

        // Update active states on all sidebar buttons
        for (Component c : ((JPanel)getContentPane().getComponent(1)).getComponents()) {
            if (c instanceof JPanel && ((JPanel)c).getComponentCount() > 0) {
                Component inner = ((JPanel)c).getComponent(0);
                if (inner instanceof JButton) {
                    JButton btn = (JButton) inner;
                    boolean isActive = btn.getText().contains(cardName.length() > 4
                        ? cardName.substring(0, 4) : cardName);

                    if (isActive) {
                        btn.setForeground(Color.WHITE);
                        btn.setFont(UIConstants.FONT_BODY_BOLD);
                        btn.setOpaque(true);
                        btn.setBackground(UIManager.getColor("Component.accentColor"));
                        activeNavBtn = btn;
                    } else {
                        btn.setForeground(UIConstants.COLOR_TEXT_MUTED);
                        btn.setFont(UIConstants.FONT_BODY);
                        btn.setOpaque(false);
                        btn.setBackground(null);
                    }
                }
            }
        }

        // Refresh visible panel
        for (Component comp : contentArea.getComponents()) {
            if (comp.isVisible() && comp instanceof BasePanel) {
                ((BasePanel) comp).refreshTable();
                break;
            }
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy")));
        });
        timer.start();
        // Immediate first tick
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy")));
    }
}
