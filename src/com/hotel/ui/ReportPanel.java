package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ReportPanel extends BasePanel {

    private JLabel lblTotalRev, lblOccRate, lblGuests, lblTotalBookings;
    
    // Progress bar panels
    private CustomProgressBar pbStandard, pbDeluxe, pbSuite;
    private JLabel lblStdRev, lblDeluxeRev, lblSuiteRev;
    private JLabel lblStdCount, lblDeluxeCount, lblSuiteCount;
    
    // Status counts
    private JLabel lblPendingCount, lblCheckedInCount, lblCheckedOutCount, lblCancelledCount;
    
    private DefaultTableModel tableModel;
    private JTable table;

    public ReportPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLbl = new JLabel(com.hotel.util.LangManager.getString("menu.reports"));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel(com.hotel.util.LangManager.getString("sub.report"));
        subLbl.setFont(UIConstants.FONT_BODY);
        subLbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        
        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLbl);
        titleGroup.add(Box.createVerticalStrut(5));
        titleGroup.add(subLbl);
        
        header.add(titleGroup, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // CENTER SCROLL (In case window is small)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // --- ROW 1: Revenue by Room Type & Status Distribution ---
        JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        row1.setPreferredSize(new Dimension(0, 220));

        // LEFT CARD: DOANH THU THEO LOẠI PHÒNG
        JPanel revCard = createCardPanel();
        revCard.setLayout(new BorderLayout(0, 15));
        JLabel revTitle = new JLabel(com.hotel.util.LangManager.getString("lbl.rev_by_room"));
        revTitle.setFont(UIConstants.FONT_SMALL_BOLD);
        revTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        revCard.add(revTitle, BorderLayout.NORTH);
        
        JPanel revContent = new JPanel(new GridLayout(3, 1, 0, 15));
        revContent.setOpaque(false);
        
        // Standard
        lblStdRev = new JLabel("0"); lblStdRev.setForeground(new Color(100, 210, 255)); lblStdRev.setFont(UIConstants.FONT_BODY_BOLD);
        lblStdCount = new JLabel("0 " + com.hotel.util.LangManager.getString("lbl.res_short")); lblStdCount.setForeground(UIConstants.COLOR_TEXT_MUTED); lblStdCount.setFont(UIConstants.FONT_SMALL);
        pbStandard = new CustomProgressBar(new Color(100, 210, 255));
        revContent.add(createRoomRevRow("Standard", lblStdRev, lblStdCount, pbStandard));
        
        // Deluxe
        lblDeluxeRev = new JLabel("0"); lblDeluxeRev.setForeground(UIConstants.COLOR_GOLD); lblDeluxeRev.setFont(UIConstants.FONT_BODY_BOLD);
        lblDeluxeCount = new JLabel("0 " + com.hotel.util.LangManager.getString("lbl.res_short")); lblDeluxeCount.setForeground(UIConstants.COLOR_TEXT_MUTED); lblDeluxeCount.setFont(UIConstants.FONT_SMALL);
        pbDeluxe = new CustomProgressBar(UIConstants.COLOR_GOLD);
        revContent.add(createRoomRevRow("Deluxe", lblDeluxeRev, lblDeluxeCount, pbDeluxe));
        
        // Suite
        lblSuiteRev = new JLabel("0"); lblSuiteRev.setForeground(new Color(191, 90, 242)); lblSuiteRev.setFont(UIConstants.FONT_BODY_BOLD);
        lblSuiteCount = new JLabel("0 " + com.hotel.util.LangManager.getString("lbl.res_short")); lblSuiteCount.setForeground(UIConstants.COLOR_TEXT_MUTED); lblSuiteCount.setFont(UIConstants.FONT_SMALL);
        pbSuite = new CustomProgressBar(new Color(191, 90, 242));
        revContent.add(createRoomRevRow("Suite", lblSuiteRev, lblSuiteCount, pbSuite));
        
        revCard.add(revContent, BorderLayout.CENTER);
        row1.add(revCard);

        // RIGHT CARD: PHÂN BỔ TRẠNG THÁI
        JPanel statusCard = createCardPanel();
        statusCard.setLayout(new BorderLayout(0, 15));
        JLabel statusTitle = new JLabel(com.hotel.util.LangManager.getString("lbl.status_dist"));
        statusTitle.setFont(UIConstants.FONT_SMALL_BOLD);
        statusTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        statusCard.add(statusTitle, BorderLayout.NORTH);
        
        JPanel statusContent = new JPanel(new GridLayout(4, 1, 0, 10));
        statusContent.setOpaque(false);
        statusContent.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        lblPendingCount = new JLabel("0"); lblPendingCount.setForeground(Color.WHITE); lblPendingCount.setFont(UIConstants.FONT_BODY_BOLD);
        lblCheckedInCount = new JLabel("0"); lblCheckedInCount.setForeground(Color.WHITE); lblCheckedInCount.setFont(UIConstants.FONT_BODY_BOLD);
        lblCheckedOutCount = new JLabel("0"); lblCheckedOutCount.setForeground(Color.WHITE); lblCheckedOutCount.setFont(UIConstants.FONT_BODY_BOLD);
        lblCancelledCount = new JLabel("0"); lblCancelledCount.setForeground(Color.WHITE); lblCancelledCount.setFont(UIConstants.FONT_BODY_BOLD);
        
        statusContent.add(createStatusRow(com.hotel.util.LangManager.getString("status.pending"), new Color(255, 179, 71), lblPendingCount));
        statusContent.add(createStatusRow(com.hotel.util.LangManager.getString("status.checked_in"), UIConstants.COLOR_SUCCESS, lblCheckedInCount));
        statusContent.add(createStatusRow(com.hotel.util.LangManager.getString("status.checked_out"), new Color(100, 210, 255), lblCheckedOutCount));
        statusContent.add(createStatusRow(com.hotel.util.LangManager.getString("status.cancelled"), UIConstants.COLOR_DANGER, lblCancelledCount));
        
        statusCard.add(statusContent, BorderLayout.CENTER);
        row1.add(statusCard);

        content.add(row1);
        content.add(Box.createVerticalStrut(20));

        // --- ROW 2: Stats Cards ---
        JPanel row2 = new JPanel(new GridLayout(1, 4, 20, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        row2.setPreferredSize(new Dimension(0, 120));
        
        lblTotalRev = new JLabel("0");
        lblOccRate = new JLabel("0%");
        lblGuests = new JLabel("0");
        lblTotalBookings = new JLabel("0");
        
        row2.add(buildStatCard(com.hotel.util.LangManager.getString("lbl.total_rev"), lblTotalRev, UIConstants.COLOR_SUCCESS));
        row2.add(buildStatCard(com.hotel.util.LangManager.getString("lbl.occ_rate"), lblOccRate, UIConstants.COLOR_GOLD));
        row2.add(buildStatCard(com.hotel.util.LangManager.getString("lbl.total_guests_upper"), lblGuests, new Color(100, 210, 255)));
        row2.add(buildStatCard(com.hotel.util.LangManager.getString("lbl.total_res"), lblTotalBookings, new Color(191, 90, 242)));
        
        content.add(row2);
        content.add(Box.createVerticalStrut(20));

        // --- ROW 3: Table ---
        JPanel row3 = createCardPanel();
        row3.setLayout(new BorderLayout(0, 10));
        JLabel tableTitle = new JLabel(com.hotel.util.LangManager.getString("lbl.room_details"));
        tableTitle.setFont(UIConstants.FONT_SMALL_BOLD);
        tableTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        row3.add(tableTitle, BorderLayout.NORTH);
        
        String[] cols = {
            com.hotel.util.LangManager.getString("lbl.room"),
            com.hotel.util.LangManager.getString("lbl.room_type"),
            "Giá/đêm",
            com.hotel.util.LangManager.getString("lbl.total_res"),
            com.hotel.util.LangManager.getString("lbl.nights_total"),
            com.hotel.util.LangManager.getString("dashboard.revenue"),
            com.hotel.util.LangManager.getString("lbl.status")
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(45);
        
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                p.setOpaque(false);
                p.setBorder(new EmptyBorder(8, 0, 0, 0));
                
                JLabel lbl = new JLabel((String)v);
                lbl.setFont(UIConstants.FONT_SMALL);
                lbl.setOpaque(false);
                lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
                
                if (com.hotel.util.LangManager.getString("status.empty").equals(v)) {
            lbl.setForeground(UIConstants.COLOR_SUCCESS);
        } else {
            lbl.setForeground(UIConstants.COLOR_DANGER);
        }
                
                p.add(lbl);
                
                if (isSelected) {
                    p.setBackground(t.getSelectionBackground());
                    p.setOpaque(true);
                }
                return p;
            }
        });
        
        row3.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        
        content.add(row3);
        
        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
        
        refreshTable();
    }
    
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    private JPanel buildStatCard(String title, JLabel valueLabel, Color valColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UIConstants.FONT_SMALL_BOLD);
        titleLbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        card.add(titleLbl, BorderLayout.SOUTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(valColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
    
    private JPanel createRoomRevRow(String type, JLabel lblRev, JLabel lblCount, CustomProgressBar pb) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(type);
        name.setForeground(Color.WHITE);
        name.setFont(UIConstants.FONT_BODY);
        top.add(name, BorderLayout.WEST);
        top.add(lblRev, BorderLayout.EAST);
        
        p.add(top, BorderLayout.NORTH);
        p.add(pb, BorderLayout.CENTER);
        p.add(lblCount, BorderLayout.SOUTH);
        return p;
    }
    
    private JPanel createStatusRow(String label, Color dotColor, JLabel lblCount) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        
        // Dot
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 0, 8, 8);
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        
        JLabel name = new JLabel(label);
        name.setForeground(UIConstants.COLOR_TEXT_MUTED);
        name.setFont(UIConstants.FONT_BODY);
        
        left.add(dot);
        left.add(name);
        
        p.add(left, BorderLayout.WEST);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(lblCount);
        
        // Mini line bar
        CustomProgressBar miniPb = new CustomProgressBar(dotColor);
        miniPb.setPreferredSize(new Dimension(40, 4));
        // just hardcode progress to some static value for aesthetics like the screenshot
        miniPb.setProgress(0.6f); 
        
        // center align the mini pb
        JPanel pbWrapper = new JPanel(new GridBagLayout());
        pbWrapper.setOpaque(false);
        pbWrapper.add(miniPb);
        right.add(pbWrapper);
        
        p.add(right, BorderLayout.EAST);
        
        return p;
    }

    @Override
    public void refreshTable() {
        List<Reservation> list = reservationManager.getAllReservations();
        List<Room> rooms = roomManager.getAllRooms();
        
        double totalRev = 0;
        
        double standardRev = 0; double deluxeRev = 0; double suiteRev = 0;
        int standardCnt = 0; int deluxeCnt = 0; int suiteCnt = 0;
        
        int pending = 0, checkedIn = 0, checkedOut = 0, cancelled = 0;
        
        Map<String, RoomStats> roomStats = new HashMap<>();
        for(Room r : rooms) {
            roomStats.put(r.getRoomId(), new RoomStats(r));
        }
        
        for(Reservation r : list) {
            switch(r.getStatus()) {
                case PENDING: pending++; break;
                case CHECKED_IN: checkedIn++; break;
                case CHECKED_OUT: checkedOut++; break;
                case CANCELLED: cancelled++; break;
                default: break;
            }
            
            if (r.getStatus() != Reservation.Status.CANCELLED) {
                double amt = r.getTotalAmount();
                totalRev += amt;
                
                if (r.getRoom() instanceof StandardRoom) { standardRev += amt; standardCnt++; }
                else if (r.getRoom() instanceof DeluxeRoom) { deluxeRev += amt; deluxeCnt++; }
                else if (r.getRoom() instanceof SuiteRoom) { suiteRev += amt; suiteCnt++; }
                
                RoomStats st = roomStats.get(r.getRoom().getRoomId());
                if (st != null) {
                    st.bookings++;
                    st.nights += r.getNumberOfNights();
                    st.revenue += amt;
                }
            }
        }
        
        lblTotalRev.setText(com.hotel.util.LangManager.formatCurrency(totalRev));
        
        long occ = roomManager.countOccupied();
        int rate = rooms.isEmpty() ? 0 : (int)(occ * 100.0 / rooms.size());
        lblOccRate.setText(rate + "%");
        
        lblGuests.setText(String.valueOf(guestManager.getAllGuests().size()));
        lblTotalBookings.setText(String.valueOf(list.size()));
        
        // Status counts
        lblPendingCount.setText(String.valueOf(pending));
        lblCheckedInCount.setText(String.valueOf(checkedIn));
        lblCheckedOutCount.setText(String.valueOf(checkedOut));
        lblCancelledCount.setText(String.valueOf(cancelled));
        
        // Rev by room type
        lblStdRev.setText(com.hotel.util.LangManager.formatCurrency(standardRev));
        lblStdCount.setText(standardCnt + " " + com.hotel.util.LangManager.getString("lbl.res_short"));
        lblDeluxeRev.setText(com.hotel.util.LangManager.formatCurrency(deluxeRev));
        lblDeluxeCount.setText(deluxeCnt + " " + com.hotel.util.LangManager.getString("lbl.res_short"));
        lblSuiteRev.setText(com.hotel.util.LangManager.formatCurrency(suiteRev));
        lblSuiteCount.setText(suiteCnt + " " + com.hotel.util.LangManager.getString("lbl.res_short"));
        
        double maxRev = Math.max(standardRev, Math.max(deluxeRev, suiteRev));
        if (maxRev == 0) maxRev = 1; // avoid / 0
        pbStandard.setProgress((float)(standardRev / maxRev));
        pbDeluxe.setProgress((float)(deluxeRev / maxRev));
        pbSuite.setProgress((float)(suiteRev / maxRev));
        
        // Populate table
        tableModel.setRowCount(0);
        for(Room r : rooms) {
            RoomStats st = roomStats.get(r.getRoomId());
            String status = r.isAvailable() ? com.hotel.util.LangManager.getString("status.empty") : com.hotel.util.LangManager.getString("status.occupied");
            tableModel.addRow(new Object[]{
                r.getRoomId(),
                r.getRoomType(),
                com.hotel.util.LangManager.formatCurrency(r.calculatePricePerNight()),
                st.bookings,
                st.nights,
                com.hotel.util.LangManager.formatCurrency(st.revenue),
                status
            });
        }
    }

    @Override
    public void clearForm() {}
    
    // Inner class for progress bars
    class CustomProgressBar extends JPanel {
        private Color barColor;
        private float progress = 0f; // 0.0 to 1.0
        
        public CustomProgressBar(Color c) {
            this.barColor = c;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 6));
        }
        
        public void setProgress(float p) {
            this.progress = Math.max(0, Math.min(1, p));
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // bg
            g2.setColor(UIConstants.COLOR_BG_DARK);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            
            // fill
            int w = (int)(getWidth() * progress);
            if (w > 0) {
                g2.setColor(barColor);
                g2.fillRoundRect(0, 0, w, getHeight(), getHeight(), getHeight());
            }
        }
    }
    
    // Helper class for table row stats
    class RoomStats {
        int bookings = 0;
        long nights = 0;
        double revenue = 0;
        RoomStats(Room r) {}
    }
}
