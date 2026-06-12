package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

/**
 * ReservationPanel — Booking management with check-in/check-out workflow.
 * Extends BasePanel (Inheritance + Polymorphism).
 * Demonstrates: Polymorphism via dynamic pricing from room.calculatePricePerNight()
 */
public class ReservationPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Reservation> currentReservations;

    private JComboBox<Guest> cbGuest;
    private JComboBox<Room> cbRoom;
    private DatePicker txtCheckIn, txtCheckOut;
    private JLabel lblNights, lblTotalPrice, lblRoomType;
    private JTextArea txtNotes;
    private JTextField searchField;

    private JButton btnCreate, btnCheckIn, btnCheckOut, btnCancel, btnDelete, btnClear;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Reservation selectedReservation = null;

    public ReservationPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Reservation Management", "B"), BorderLayout.NORTH);

        // Container for Table + Form
        JPanel contentPanel = new JPanel(new BorderLayout());

        // LEFT: Table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UIConstants.COLOR_BG_PANEL);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 8));

        searchField = UIHelper.createTextField("");
        JButton searchBtn = UIHelper.createPrimaryButton("Search");
        JButton clearSearchBtn = UIHelper.createSecondaryButton("Clear");
        searchBtn.addActionListener(e -> performSearch());
        clearSearchBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        searchField.addActionListener(e -> performSearch());
        leftPanel.add(buildSearchBar(searchField, searchBtn, clearSearchBtn), BorderLayout.NORTH);

        String[] cols = {"Res. ID", "Guest", "Room", "Type", "Check-In", "Check-Out", "Nights", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });
        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        JScrollPane formScroll = new JScrollPane(buildFormPanel());
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        formScroll.setPreferredSize(new Dimension(400, 0));
        contentPanel.add(formScroll, BorderLayout.EAST);
        
        add(contentPanel, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = buildFormCard("New Booking");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Guest ComboBox
        cbGuest = new JComboBox<>();
        cbGuest.setBackground(UIConstants.COLOR_INPUT_BG);
        cbGuest.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        cbGuest.setFont(UIConstants.FONT_INPUT);
        addFormRow(form, gbc, 0, "Guest *", cbGuest);

        // Room ComboBox
        cbRoom = new JComboBox<>();
        cbRoom.setBackground(UIConstants.COLOR_INPUT_BG);
        cbRoom.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        cbRoom.setFont(UIConstants.FONT_INPUT);
        cbRoom.addActionListener(e -> recalculatePrice());
        addFormRow(form, gbc, 1, "Room *", cbRoom);

        // Room type indicator
        lblRoomType = UIHelper.createLabel("—");
        lblRoomType.setForeground(UIConstants.COLOR_ACCENT);
        lblRoomType.setFont(UIConstants.FONT_SMALL_BOLD);
        addFormRow(form, gbc, 2, "Room Type", lblRoomType);

        // Dates
        DatePickerSettings settingsIn = new DatePickerSettings();
        settingsIn.setFormatForDatesCommonEra("dd/MM/yyyy");
        UIHelper.applyCalendarDarkTheme(settingsIn);
        txtCheckIn = new DatePicker(settingsIn);
        txtCheckIn.getComponentDateTextField().setFont(UIConstants.FONT_INPUT);
        txtCheckIn.getComponentDateTextField().setBackground(UIConstants.COLOR_INPUT_BG);
        txtCheckIn.getComponentDateTextField().setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        txtCheckIn.getComponentDateTextField().setBorder(UIConstants.BORDER_INPUT);
        JButton btnIn = txtCheckIn.getComponentToggleCalendarButton();
        btnIn.setText(" ▼ ");
        btnIn.setFont(UIConstants.FONT_SMALL);
        btnIn.setBackground(UIConstants.COLOR_SIDEBAR);
        btnIn.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        DatePickerSettings settingsOut = new DatePickerSettings();
        settingsOut.setFormatForDatesCommonEra("dd/MM/yyyy");
        UIHelper.applyCalendarDarkTheme(settingsOut);
        txtCheckOut = new DatePicker(settingsOut);
        txtCheckOut.getComponentDateTextField().setFont(UIConstants.FONT_INPUT);
        txtCheckOut.getComponentDateTextField().setBackground(UIConstants.COLOR_INPUT_BG);
        txtCheckOut.getComponentDateTextField().setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        txtCheckOut.getComponentDateTextField().setBorder(UIConstants.BORDER_INPUT);
        JButton btnOut = txtCheckOut.getComponentToggleCalendarButton();
        btnOut.setText(" ▼ ");
        btnOut.setFont(UIConstants.FONT_SMALL);
        btnOut.setBackground(UIConstants.COLOR_SIDEBAR);
        btnOut.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        txtCheckIn.addDateChangeListener(e -> recalculatePrice());
        txtCheckOut.addDateChangeListener(e -> recalculatePrice());

        addFormRow(form, gbc, 3, "Check-In * (dd/mm/yyyy)", txtCheckIn);
        addFormRow(form, gbc, 4, "Check-Out * (dd/mm/yyyy)", txtCheckOut);

        // Today's button shortcut
        gbc.gridx = 1; gbc.gridy = 5;
        JButton btnToday = UIHelper.createSecondaryButton("Today");
        btnToday.setPreferredSize(new Dimension(80, 26));
        btnToday.addActionListener(e -> {
            txtCheckIn.setDate(LocalDate.now());
            txtCheckOut.setDate(LocalDate.now().plusDays(1));
            recalculatePrice();
        });
        form.add(btnToday, gbc);
        gbc.gridx = 0;

        // Nights & Price display
        lblNights     = UIHelper.createLabel("— nights");
        lblNights.setForeground(UIConstants.COLOR_ACCENT_LIGHT);
        lblTotalPrice = UIHelper.createLabel("$0");
        lblTotalPrice.setFont(UIConstants.FONT_SUBTITLE);
        lblTotalPrice.setForeground(UIConstants.COLOR_GOLD);

        addFormRow(form, gbc, 6, "Duration",   lblNights);
        addFormRow(form, gbc, 7, "Total Price", lblTotalPrice);

        // Notes
        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(UIConstants.FONT_INPUT);
        txtNotes.setBackground(UIConstants.COLOR_INPUT_BG);
        txtNotes.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        txtNotes.setBorder(UIConstants.BORDER_INPUT);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane notesSP = new JScrollPane(txtNotes);
        notesSP.setPreferredSize(new Dimension(220, 60));
        addFormRow(form, gbc, 8, "Notes", notesSP);

        panel.add(form, BorderLayout.CENTER);

        // Action buttons
        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        btnPanel.setOpaque(false);

        btnCreate  = UIHelper.createSuccessButton("+ Book Room");
        btnCheckIn = UIHelper.createPrimaryButton("Check-In");
        btnCheckOut= UIHelper.createWarningButton("Check-Out");
        btnCancel  = UIHelper.createDangerButton("\u00D7 Cancel Booking");
        btnDelete  = UIHelper.createDangerButton("\u00D7 Delete");
        btnClear   = UIHelper.createSecondaryButton("\u21BA Clear Form");

        btnCreate.addActionListener(e -> createReservation());
        btnCheckIn.addActionListener(e -> checkIn());
        btnCheckOut.addActionListener(e -> checkOut());
        btnCancel.addActionListener(e -> cancelReservation());
        btnDelete.addActionListener(e -> deleteReservation());
        btnClear.addActionListener(e -> clearForm());

        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(false);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

        btnPanel.add(btnCreate);
        btnPanel.add(btnCheckIn);
        btnPanel.add(btnCheckOut);
        btnPanel.add(btnCancel);
        btnPanel.add(UIHelper.createSeparator());
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void recalculatePrice() {
        Room room = (Room) cbRoom.getSelectedItem();
        if (room == null) {
            lblRoomType.setText("—");
        } else {
            lblRoomType.setText(room.getRoomType() + " — $" + String.format("%.0f", room.calculatePricePerNight()) + "/night");
        }

        LocalDate ci = txtCheckIn.getDate();
        LocalDate co = txtCheckOut.getDate();
        if (ci == null || co == null) {
            lblNights.setText("—");
            lblTotalPrice.setText("$0");
            return;
        }

        if (room == null) return;
        long nights = java.time.temporal.ChronoUnit.DAYS.between(ci, co);
        if (nights <= 0) {
            lblNights.setText("⚠ Check-out must be after check-in");
            lblTotalPrice.setText("$0");
        } else {
            lblNights.setText(nights + " night" + (nights > 1 ? "s" : ""));
            double total = nights * room.calculatePricePerNight();
            lblTotalPrice.setText(String.format("$%.0f", total));
        }
    }

    private void createReservation() {
        Guest guest = (Guest) cbGuest.getSelectedItem();
        Room room   = (Room)  cbRoom.getSelectedItem();
        if (guest == null) { UIHelper.showError(this, "Please select a guest!"); return; }
        if (room  == null) { UIHelper.showError(this, "Please select a room!"); return; }

        LocalDate ci = txtCheckIn.getDate();
        LocalDate co = txtCheckOut.getDate();
        if (ci == null || co == null) {
            UIHelper.showError(this, "Please select check-in and check-out dates!"); return;
        }
        if (!co.isAfter(ci)) { UIHelper.showError(this, "Check-out must be after check-in!"); return; }
        if (!reservationManager.isRoomAvailableForDates(room.getRoomId(), ci, co)) { 
            UIHelper.showError(this, "This room is already booked for the selected dates!"); 
            return; 
        }

        String id = reservationManager.generateNextId();
        Reservation res = new Reservation(id, guest, room, ci, co);
        res.setNotes(txtNotes.getText());
        reservationManager.createReservation(res);
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Reservation created!\nID: " + id + "\nTotal: $" + String.format("%.0f", res.getTotalAmount()));
    }

    private void checkIn() {
        if (selectedReservation == null) return;
        String guestName = selectedReservation.getGuest().getName();
        reservationManager.checkIn(selectedReservation.getReservationId());
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Check-in completed for " + guestName);
    }

    private void checkOut() {
        if (selectedReservation == null) return;
        String guestName = selectedReservation.getGuest().getName();
        if (!UIHelper.showConfirm(this, "Complete check-out for " + guestName + "?")) return;
        reservationManager.checkOut(selectedReservation.getReservationId());
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Check-out completed. Room is now available.");
    }

    private void cancelReservation() {
        if (selectedReservation == null) return;
        if (!UIHelper.showConfirm(this, "Cancel reservation " + selectedReservation.getReservationId() + "?")) return;
        reservationManager.cancelReservation(selectedReservation.getReservationId());
        refreshTable(); clearForm();
    }

    private void deleteReservation() {
        if (selectedReservation == null) return;
        if (!UIHelper.showConfirm(this, "Permanently delete reservation " + selectedReservation.getReservationId() + "?")) return;
        List<Reservation> all = reservationManager.getAllReservations();
        all.removeIf(r -> r.getReservationId().equals(selectedReservation.getReservationId()));
        // Re-save via manager by brute force (direct list manipulation)
        // In production, manager would have a deleteById method
        refreshTable(); clearForm();
    }

    private void performSearch() {
        currentReservations = reservationManager.searchReservations(searchField.getText().trim());
        populateTable(currentReservations);
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || currentReservations == null || row >= currentReservations.size()) return;
        selectedReservation = currentReservations.get(row);

        Reservation.Status status = selectedReservation.getStatus();
        btnCreate.setEnabled(false);
        btnCheckIn.setEnabled(status == Reservation.Status.CONFIRMED);
        btnCheckOut.setEnabled(status == Reservation.Status.CHECKED_IN);
        btnCancel.setEnabled(status == Reservation.Status.CONFIRMED || status == Reservation.Status.PENDING);
        btnDelete.setEnabled(true);
    }

    @Override
    public void refreshTable() {
        // Reload ComboBoxes
        cbGuest.removeAllItems();
        for (Guest g : guestManager.getAllGuests()) cbGuest.addItem(g);

        cbRoom.removeAllItems();
        for (Room r : roomManager.getAllRooms()) cbRoom.addItem(r);

        currentReservations = reservationManager.getAllReservations();
        populateTable(currentReservations);
    }

    private void populateTable(List<Reservation> list) {
        tableModel.setRowCount(0);
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                r.getReservationId(),
                r.getGuest().getName(),
                r.getRoom().getRoomId(),
                r.getRoom().getRoomType(),
                r.getCheckInDate().format(DATE_FMT),
                r.getCheckOutDate().format(DATE_FMT),
                r.getNumberOfNights() + "n",
                String.format("$%.0f", r.getTotalAmount()),
                r.getStatus().name()
            });
        }
    }

    @Override
    public void clearForm() {
        selectedReservation = null;
        txtCheckIn.setDate(null); txtCheckOut.setDate(null);
        txtNotes.setText("");
        lblNights.setText("—"); lblTotalPrice.setText("$0");
        btnCreate.setEnabled(true);
        btnCheckIn.setEnabled(false); btnCheckOut.setEnabled(false);
        btnCancel.setEnabled(false);  btnDelete.setEnabled(false);
        table.clearSelection();
        recalculatePrice();
    }
}
