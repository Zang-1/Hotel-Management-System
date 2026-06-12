package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportPanel — Statistics and reporting overview.
 * Extends BasePanel (Inheritance + Polymorphism).
 */
public class ReportPanel extends BasePanel {

    private JPanel statsContent;

    public ReportPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Reports & Analytics", "A"), BorderLayout.NORTH);

        statsContent = new JPanel();
        statsContent.setBackground(UIConstants.COLOR_BG_PANEL);
        statsContent.setLayout(new BoxLayout(statsContent, BoxLayout.Y_AXIS));
        statsContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(statsContent);
        scrollPane.setBackground(UIConstants.COLOR_BG_PANEL);
        scrollPane.getViewport().setBackground(UIConstants.COLOR_BG_PANEL);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();
    }

    @Override
    public void refreshTable() {
        statsContent.removeAll();

        List<Room>        rooms        = roomManager.getAllRooms();
        List<Guest>       guests       = guestManager.getAllGuests();
        List<Reservation> reservations = reservationManager.getAllReservations();

        // ── Room Type Distribution ──────────────────────────────────────
        statsContent.add(UIHelper.createSectionHeader("Room Type Distribution"));
        statsContent.add(Box.createVerticalStrut(10));

        Map<String, Long> byType = rooms.stream()
            .collect(Collectors.groupingBy(Room::getRoomType, Collectors.counting()));

        JPanel roomBars = new JPanel(new GridLayout(0, 1, 0, 10));
        roomBars.setOpaque(false);
        roomBars.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        roomBars.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Map.Entry<String, Long> entry : byType.entrySet()) {
            int pct = (int) (entry.getValue() * 100.0 / Math.max(rooms.size(), 1));
            Color barColor = "Deluxe".equals(entry.getKey()) ? UIConstants.COLOR_DELUXE
                            : "Suite".equals(entry.getKey())  ? UIConstants.COLOR_SUITE
                            : UIConstants.COLOR_STANDARD;
            roomBars.add(buildBar(entry.getKey(), entry.getValue().intValue(), rooms.size(), pct, barColor));
        }
        statsContent.add(roomBars);
        statsContent.add(Box.createVerticalStrut(20));

        // ── Occupancy Rate ─────────────────────────────────────────────
        statsContent.add(UIHelper.createSectionHeader("Occupancy Rate"));
        statsContent.add(Box.createVerticalStrut(10));

        long occupied  = roomManager.countOccupied();
        long available = roomManager.countAvailable();
        int occPct = (int) (occupied * 100.0 / Math.max(rooms.size(), 1));

        JPanel occPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        occPanel.setOpaque(false);
        occPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        occPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        occPanel.add(buildBar("Occupied",  (int) occupied,  rooms.size(), occPct,          UIConstants.COLOR_DANGER));
        occPanel.add(buildBar("Available", (int) available, rooms.size(), 100 - occPct,    UIConstants.COLOR_SUCCESS));
        statsContent.add(occPanel);
        statsContent.add(Box.createVerticalStrut(20));

        // ── Reservation Status Breakdown ───────────────────────────────
        statsContent.add(UIHelper.createSectionHeader("Reservation Status"));
        statsContent.add(Box.createVerticalStrut(10));

        Map<Reservation.Status, Long> byStatus = reservations.stream()
            .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));

        JPanel statusPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        statusPanel.setOpaque(false);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Reservation.Status status : Reservation.Status.values()) {
            long count = byStatus.getOrDefault(status, 0L);
            int pct = (int) (count * 100.0 / Math.max(reservations.size(), 1));
            Color c = status == Reservation.Status.CONFIRMED  ? UIConstants.COLOR_ACCENT :
                      status == Reservation.Status.CHECKED_IN ? UIConstants.COLOR_SUCCESS :
                      status == Reservation.Status.CHECKED_OUT? UIConstants.COLOR_TEXT_MUTED :
                      status == Reservation.Status.CANCELLED  ? UIConstants.COLOR_DANGER :
                                                                UIConstants.COLOR_WARNING;
            statusPanel.add(buildBar(status.name(), (int) count, reservations.size(), pct, c));
        }
        statsContent.add(statusPanel);
        statsContent.add(Box.createVerticalStrut(20));

        // ── Revenue Summary ────────────────────────────────────────────
        statsContent.add(UIHelper.createSectionHeader("Revenue Summary"));
        statsContent.add(Box.createVerticalStrut(10));

        double totalRevenue = reservationManager.getTotalRevenue();
        double avgPerRes = reservations.isEmpty() ? 0 : totalRevenue / reservations.size();

        JPanel revPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        revPanel.setOpaque(false);
        revPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        revPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        revPanel.add(buildMiniCard("Total Revenue",    String.format("$%.0f", totalRevenue),   UIConstants.COLOR_GOLD));
        revPanel.add(buildMiniCard("Total Bookings",   String.valueOf(reservations.size()),     UIConstants.COLOR_ACCENT));
        revPanel.add(buildMiniCard("Avg. per Booking", String.format("$%.0f", avgPerRes),       UIConstants.COLOR_SUCCESS));
        statsContent.add(revPanel);
        statsContent.add(Box.createVerticalStrut(20));

        // ── Top Room Types by Revenue ─────────────────────────────────
        statsContent.add(UIHelper.createSectionHeader("Revenue by Room Type"));
        statsContent.add(Box.createVerticalStrut(10));

        Map<String, Double> revenueByType = new LinkedHashMap<>();
        for (Reservation r : reservations) {
            if (r.getStatus() != Reservation.Status.CANCELLED) {
                revenueByType.merge(r.getRoom().getRoomType(), r.getTotalAmount(), Double::sum);
            }
        }
        double maxRevenue = revenueByType.values().stream().mapToDouble(v -> v).max().orElse(1);

        JPanel revTypePanel = new JPanel(new GridLayout(0, 1, 0, 8));
        revTypePanel.setOpaque(false);
        revTypePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        revTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Map.Entry<String, Double> entry : revenueByType.entrySet()) {
            int pct = (int) (entry.getValue() * 100.0 / maxRevenue);
            Color c = "Deluxe".equals(entry.getKey()) ? UIConstants.COLOR_DELUXE
                    : "Suite".equals(entry.getKey())  ? UIConstants.COLOR_SUITE
                    : UIConstants.COLOR_STANDARD;
            revTypePanel.add(buildBar(entry.getKey() + " Revenue",
                (int) (double) entry.getValue(), (int) maxRevenue, pct, c));
        }
        statsContent.add(revTypePanel);

        statsContent.revalidate();
        statsContent.repaint();
    }

    private JPanel buildBar(String label, int value, int max, int pct, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(UIConstants.FONT_BODY);
        nameLbl.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        nameLbl.setPreferredSize(new Dimension(160, 24));
        row.add(nameLbl, BorderLayout.WEST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setForeground(color);
        bar.setBackground(UIConstants.COLOR_CARD);
        bar.setBorderPainted(false);
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(0, 22));
        row.add(bar, BorderLayout.CENTER);

        JLabel valLbl = new JLabel(value + " (" + pct + "%)");
        valLbl.setFont(UIConstants.FONT_SMALL_BOLD);
        valLbl.setForeground(color);
        valLbl.setPreferredSize(new Dimension(90, 24));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(valLbl, BorderLayout.EAST);

        return row;
    }

    private JPanel buildMiniCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIManager.getColor("Component.background"));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            )
        ));
        card.putClientProperty("FlatLaf.style", "arc: 8");
        
        JLabel t = new JLabel(title);
        t.setFont(UIConstants.FONT_SMALL_BOLD);
        t.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        card.add(t, BorderLayout.NORTH);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        v.setForeground(color);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void clearForm() { /* Reports have no form */ }
}
