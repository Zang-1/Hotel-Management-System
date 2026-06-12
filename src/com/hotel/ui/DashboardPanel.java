package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * DashboardPanel — Redesigned list-style dashboard inspired by PHPJabbers layout.
 */
public class DashboardPanel extends BasePanel {

    private JLabel statRoomsBooked, statPendingRooms, statAvailableRooms;
    private JLabel statStandard, statDeluxe, statSuite;
    private JLabel statGuestsTonight, statAdults, statChildren;
    
    private JTable recentTable;
    private DefaultTableModel recentModel;

    public DashboardPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Dashboard", "D"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(UIConstants.COLOR_BG_DARK);

        // --- LEFT COLUMN (Stats Lists) ---
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(UIConstants.COLOR_BG_PANEL);
        leftCol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        leftCol.setPreferredSize(new Dimension(380, 0));

        // Init labels
        statRoomsBooked = new JLabel("0");
        statPendingRooms = new JLabel("0");
        statAvailableRooms = new JLabel("0");
        
        statStandard = new JLabel("0");
        statDeluxe = new JLabel("0");
        statSuite = new JLabel("0");
        
        statGuestsTonight = new JLabel("0");
        statAdults = new JLabel("0");
        statChildren = new JLabel("0");

        // Section 1: Today
        leftCol.add(createListItem("Rooms Booked Today", statRoomsBooked, false));
        leftCol.add(createListItem("Pending Rooms Today", statPendingRooms, false));
        leftCol.add(createListItem("Available Rooms Today", statAvailableRooms, false));
        leftCol.add(Box.createVerticalStrut(30));

        // Section 2: Rooms By Type
        JLabel lblRoomsTitle = new JLabel("AVAILABLE ROOMS BY TYPE");
        lblRoomsTitle.setFont(UIConstants.FONT_SUBTITLE);
        lblRoomsTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        leftCol.add(lblRoomsTitle);
        leftCol.add(Box.createVerticalStrut(10));
        
        leftCol.add(createListItem("Standard Room", statStandard, false));
        leftCol.add(createListItem("Deluxe Room", statDeluxe, false));
        leftCol.add(createListItem("Suite", statSuite, false));
        leftCol.add(Box.createVerticalStrut(30));

        // Section 3: Guests
        JLabel lblGuestsTitle = new JLabel("GUESTS");
        lblGuestsTitle.setFont(UIConstants.FONT_SUBTITLE);
        lblGuestsTitle.setForeground(UIConstants.COLOR_TEXT_MUTED);
        leftCol.add(lblGuestsTitle);
        leftCol.add(Box.createVerticalStrut(10));

        leftCol.add(createListItem("Staying tonight", statGuestsTonight, false));
        leftCol.add(createListItem("Adults", statAdults, true));
        leftCol.add(createListItem("Children", statChildren, true));

        JScrollPane leftScroll = new JScrollPane(leftCol);
        leftScroll.setBorder(null);

        // --- RIGHT COLUMN (Recent/Arrivals Table) ---
        JPanel rightCol = new JPanel(new BorderLayout(0, 10));
        rightCol.setBackground(UIConstants.COLOR_BG_PANEL);
        rightCol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // We use tabs to mimic "Arrivals | Departures" but with our own data
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_BODY_BOLD);
        
        String[] cols = {"Res. ID", "Guest", "Room", "Check-In", "Check-Out", "Status"};
        recentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recentTable = new JTable(recentModel);
        UIHelper.styleTable(recentTable);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(UIHelper.createScrollPane(recentTable), BorderLayout.CENTER);
        
        tabbedPane.addTab("Recent Reservations", tableContainer);
        rightCol.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(leftScroll, BorderLayout.WEST);
        contentPanel.add(rightCol, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createListItem(String text, JLabel valueLabel, boolean isIndented) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel t = new JLabel(text);
        t.setFont(UIConstants.FONT_BODY);
        t.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        if (isIndented) {
            t.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        }
        
        valueLabel.setFont(UIConstants.FONT_BODY);
        valueLabel.setForeground(UIConstants.COLOR_SUCCESS); // Green numbers matching screenshot
        
        p.add(t, BorderLayout.WEST);
        p.add(valueLabel, BorderLayout.EAST);
        
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        return p;
    }

    @Override
    public void refreshTable() {
        List<Room> rooms = roomManager.getAllRooms();
        List<Reservation> reservations = reservationManager.getAllReservations();
        LocalDate today = LocalDate.now();

        int bookedToday = 0;
        int pendingToday = 0;
        int guestsTonight = 0;

        for (Reservation r : reservations) {
            boolean overlapsToday = !r.getCheckInDate().isAfter(today) && !r.getCheckOutDate().isBefore(today);
            
            if (overlapsToday && r.getStatus() == Reservation.Status.CHECKED_IN) {
                bookedToday++;
                guestsTonight++; // Roughly 1 guest per room for this stat
            }
            if (r.getCheckInDate().equals(today) && r.getStatus() == Reservation.Status.CONFIRMED) {
                pendingToday++;
            }
        }

        int std = 0, dlx = 0, ste = 0;
        int available = 0;
        for (Room r : rooms) {
            if (r.isAvailable()) {
                available++;
                if (r.getRoomType().equalsIgnoreCase("Standard")) std++;
                else if (r.getRoomType().equalsIgnoreCase("Deluxe")) dlx++;
                else if (r.getRoomType().equalsIgnoreCase("Suite")) ste++;
            }
        }

        statRoomsBooked.setText(String.valueOf(bookedToday));
        statPendingRooms.setText(String.valueOf(pendingToday));
        statAvailableRooms.setText(String.valueOf(available));

        statStandard.setText(String.valueOf(std));
        statDeluxe.setText(String.valueOf(dlx));
        statSuite.setText(String.valueOf(ste));

        statGuestsTonight.setText(String.valueOf(guestsTonight));
        statAdults.setText(String.valueOf(guestsTonight)); // Simplified mapping
        statChildren.setText("0"); // Not tracked in Guest model natively

        // Refresh table
        recentModel.setRowCount(0);
        int start = Math.max(0, reservations.size() - 15);
        for (int i = reservations.size() - 1; i >= start; i--) {
            Reservation r = reservations.get(i);
            recentModel.addRow(new Object[]{
                r.getReservationId(), r.getGuest().getName(), r.getRoom().getRoomId(),
                r.getCheckInDate(), r.getCheckOutDate(), r.getStatus().name()
            });
        }
    }

    @Override public void clearForm() {}
}
