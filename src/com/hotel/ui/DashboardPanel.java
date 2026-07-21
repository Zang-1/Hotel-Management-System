package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.hotel.util.LangManager;

public class DashboardPanel extends BasePanel {

    private JLabel lblTotalRooms, lblTotalRoomsSub;
    private JLabel lblActiveGuests, lblActiveGuestsSub;
    private JLabel lblTotalGuests;
    private JLabel lblRevenue;
    
    private DefaultTableModel recentModel;
    private JTable recentTable;
    private JPanel roomGridPanel;

    public DashboardPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
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
        
        JLabel titleLbl = new JLabel(LangManager.getString("dashboard.title"));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLbl.setForeground(Color.WHITE);
        
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM, yyyy", Locale.forLanguageTag("vi-VN")));
        // Capitalize first letter
        if (dateStr.length() > 0) {
            dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
        }
        
        JLabel subLbl = new JLabel(LangManager.getString("dashboard.sub") + " — " + dateStr);
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

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // --- ROW 1: STATS CARDS ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        statsPanel.setPreferredSize(new Dimension(0, 140));

        // TỔNG PHÒNG
        lblTotalRooms = new JLabel("0");
        lblTotalRoomsSub = new JLabel("0 " + LangManager.getString("status.empty") + " · 0 " + LangManager.getString("status.occupied"));
        statsPanel.add(buildStatCard(LangManager.getString("dashboard.total_rooms").toUpperCase(), lblTotalRooms, lblTotalRoomsSub, new Color(100, 150, 255)));

        // KHÁCH ĐANG LƯU TRÚ
        lblActiveGuests = new JLabel("0");
        lblActiveGuestsSub = new JLabel("0 " + LangManager.getString("status.pending"));
        statsPanel.add(buildStatCard(LangManager.getString("dashboard.occupied").toUpperCase(), lblActiveGuests, lblActiveGuestsSub, UIConstants.COLOR_SUCCESS));

        // TỔNG KHÁCH HÀNG
        lblTotalGuests = new JLabel("0");
        JLabel lblTotalGuestsSub = new JLabel(LangManager.getLanguage().equals("vi") ? "Trong hệ thống" : "In system");
        statsPanel.add(buildStatCard(LangManager.getString("dashboard.total_guests").toUpperCase(), lblTotalGuests, lblTotalGuestsSub, UIConstants.COLOR_GOLD));

        // DOANH THU
        lblRevenue = new JLabel("0");
        JLabel lblRevSub = new JLabel(LangManager.getLanguage().equals("vi") ? "Tổng tích lũy" : "Total accumulated");
        statsPanel.add(buildStatCard(LangManager.getString("dashboard.revenue").toUpperCase(), lblRevenue, lblRevSub, new Color(191, 90, 242)));

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(20));

        // --- ROW 2: TABLES & ROOM GRID ---
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setOpaque(false);
        
        // LEFT: Recent Reservations
        JPanel recentCard = createCardPanel();
        recentCard.setLayout(new BorderLayout(0, 15));
        JLabel recentTitle = new JLabel(LangManager.getString("dashboard.recent_res"));
        recentTitle.setFont(UIConstants.FONT_SMALL_BOLD);
        recentTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        recentCard.add(recentTitle, BorderLayout.NORTH);
        
        String[] cols = {"Mã phiếu", "Phòng", "Check-in", "Trạng thái"};
        recentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recentTable = new JTable(recentModel);
        UIHelper.styleTable(recentTable);
        recentTable.setRowHeight(45);
        
        // Custom renderer for Status badge
        recentTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
                p.setOpaque(false);
                p.setBorder(new EmptyBorder(5, 0, 0, 0));
                
                JLabel lbl = new JLabel((String)v);
                lbl.setFont(UIConstants.FONT_SMALL);
                lbl.setOpaque(true);
                lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
                
                if ("Chờ nhận".equals(v)) {
                    lbl.setBackground(new Color(60, 50, 20));
                    lbl.setForeground(UIConstants.COLOR_GOLD);
                } else if ("Đã nhận".equals(v)) {
                    lbl.setBackground(new Color(20, 60, 40));
                    lbl.setForeground(UIConstants.COLOR_SUCCESS);
                } else if ("Đã trả".equals(v)) {
                    lbl.setBackground(new Color(50, 50, 60));
                    lbl.setForeground(new Color(150, 160, 180));
                } else {
                    lbl.setBackground(new Color(60, 30, 30));
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
        
        recentCard.add(UIHelper.createScrollPane(recentTable), BorderLayout.CENTER);
        bottomRow.add(recentCard);
        
        // RIGHT: Room Status Grid
        JPanel roomCard = createCardPanel();
        roomCard.setLayout(new BorderLayout(0, 15));
        JLabel roomTitle = new JLabel("TRẠNG THÁI PHÒNG");
        roomTitle.setFont(UIConstants.FONT_SMALL_BOLD);
        roomTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        roomCard.add(roomTitle, BorderLayout.NORTH);
        
        roomGridPanel = new JPanel();
        roomGridPanel.setLayout(new BoxLayout(roomGridPanel, BoxLayout.Y_AXIS));
        roomGridPanel.setOpaque(false);
        
        JScrollPane roomScroll = new JScrollPane(roomGridPanel);
        roomScroll.setBorder(null);
        roomScroll.setOpaque(false);
        roomScroll.getViewport().setOpaque(false);
        roomCard.add(roomScroll, BorderLayout.CENTER);
        
        bottomRow.add(roomCard);

        content.add(bottomRow);

        add(content, BorderLayout.CENTER);
        refreshTable();
    }
    
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel buildStatCard(String title, JLabel mainVal, JLabel subVal, Color topColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, topColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIConstants.FONT_SMALL_BOLD);
        titleLbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        card.add(titleLbl, BorderLayout.NORTH);

        mainVal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        mainVal.setForeground(Color.WHITE);
        card.add(mainVal, BorderLayout.CENTER);

        subVal.setFont(UIConstants.FONT_SMALL);
        subVal.setForeground(UIConstants.COLOR_TEXT_MUTED);
        card.add(subVal, BorderLayout.SOUTH);

        return card;
    }

    @Override
    public void refreshTable() {
        // Rooms
        List<Room> rooms = roomManager.getAllRooms();
        long avail = roomManager.countAvailable();
        long occ = roomManager.countOccupied();
        lblTotalRooms.setText(String.valueOf(rooms.size()));
        lblTotalRoomsSub.setText(avail + " trống · " + occ + " có khách");

        // Guests & Revenue
        List<Reservation> resList = reservationManager.getAllReservations();
        long checkedIn = resList.stream().filter(r -> r.getStatus() == Reservation.Status.CHECKED_IN).count();
        long pending = resList.stream().filter(r -> r.getStatus() == Reservation.Status.PENDING).count();
        
        lblActiveGuests.setText(String.valueOf(checkedIn));
        lblActiveGuestsSub.setText(pending + " " + LangManager.getString("status.pending").toLowerCase());

        lblTotalGuests.setText(String.valueOf(guestManager.getAllGuests().size()));

        double revenue = resList.stream()
            .filter(r -> r.getStatus() != Reservation.Status.CANCELLED)
            .mapToDouble(Reservation::getTotalAmount)
            .sum();
        lblRevenue.setText(LangManager.formatCurrency(revenue));

        // Recent Reservations Table
        recentModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Theo Bài giảng Chương 4: Sử dụng lớp tiện ích Collections.sort() và Collections.reverse()
        List<Reservation> recent = new java.util.ArrayList<>(resList);
        java.util.Collections.sort(recent, new java.util.Comparator<Reservation>() {
            @Override
            public int compare(Reservation a, Reservation b) {
                return a.getCheckInDate().compareTo(b.getCheckInDate());
            }
        });
        java.util.Collections.reverse(recent);
        
        // Chỉ lấy 5 phiếu đặt gần nhất
        if (recent.size() > 5) recent = recent.subList(0, 5);
            
        for (Reservation r : recent) {
            String st = LangManager.getString("status.cancelled");
            if (r.getStatus() == Reservation.Status.PENDING) st = LangManager.getString("status.pending");
            else if (r.getStatus() == Reservation.Status.CHECKED_IN) st = LangManager.getString("status.checked_in");
            else if (r.getStatus() == Reservation.Status.CHECKED_OUT) st = LangManager.getString("status.checked_out");
            
            recentModel.addRow(new Object[]{
                r.getReservationId(),
                r.getRoom().getRoomId(),
                r.getCheckInDate().format(fmt),
                st
            });
        }
        
        // Room Grid by Floor
        roomGridPanel.removeAll();
        
        java.util.Map<Integer, List<Room>> floorMap = new java.util.TreeMap<>();
        for (Room r : rooms) {
            String numStr = r.getRoomId().replaceAll("[^0-9]", "");
            int floor = 1;
            if (!numStr.isEmpty()) floor = Integer.parseInt(numStr) / 100;
            floorMap.computeIfAbsent(floor, k -> new java.util.ArrayList<>()).add(r);
        }
        
        for (java.util.Map.Entry<Integer, List<Room>> entry : floorMap.entrySet()) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
            rowPanel.setOpaque(false);
            
            for (Room r : entry.getValue()) {
                JPanel rb = new JPanel(new BorderLayout());
                rb.setPreferredSize(new Dimension(100, 70));
                
                Color color = r.isAvailable() ? UIConstants.COLOR_SUCCESS : UIConstants.COLOR_DANGER;
                
                rb.setBackground(UIConstants.COLOR_BG_DARK);
                rb.setBorder(BorderFactory.createLineBorder(color.darker(), 1, true));
                
                JLabel rId = new JLabel(r.getRoomId().replaceAll("[^0-9]", ""), SwingConstants.CENTER);
                rId.setFont(UIConstants.FONT_BODY_BOLD);
                rId.setForeground(color);
                rId.setBorder(new EmptyBorder(10, 0, 0, 0));
                
                JLabel rType = new JLabel(r.getRoomType(), SwingConstants.CENTER);
                rType.setFont(UIConstants.FONT_SMALL);
                rType.setForeground(UIConstants.COLOR_TEXT_MUTED);
                rType.setBorder(new EmptyBorder(0, 0, 10, 0));
                
                rb.add(rId, BorderLayout.CENTER);
                rb.add(rType, BorderLayout.SOUTH);
                
                rowPanel.add(rb);
            }
            roomGridPanel.add(rowPanel);
        }
        
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    @Override
    public void clearForm() {}
}
