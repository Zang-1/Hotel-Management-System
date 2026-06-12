package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BillingPanel — View and manage billing for reservations.
 * Extends BasePanel (Inheritance + Polymorphism).
 */
public class BillingPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblBillId, lblGuest, lblRoom, lblDates, lblNights, lblPriceNight, lblTotal;
    private JComboBox<String> cbPayMethod;
    private JTextArea txaBillDetails;
    private JButton btnGenerateBill, btnClear;
    private JComboBox<Reservation> cbReservation;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BillingPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Billing & Payments", "P"), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(700);
        split.setDividerSize(3);
        split.setBorder(null);

        // LEFT: Billing history table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UIConstants.COLOR_BG_PANEL);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 8));

        JLabel tableTitle = UIHelper.createSectionHeader("All Reservations & Billing");
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        leftPanel.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Res. ID", "Guest", "Room", "Type", "Check-In", "Check-Out", "Nights", "Price/Night", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTableSelect();
        });
        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);

        // Summary bar at bottom
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryBar.setBackground(UIConstants.COLOR_CARD);
        summaryBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel totalRevLbl = new JLabel("Total Revenue: ");
        totalRevLbl.setFont(UIConstants.FONT_BODY_BOLD);
        totalRevLbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        summaryBar.add(totalRevLbl);
        JLabel totalRevVal = new JLabel("$0");
        totalRevVal.setFont(UIConstants.FONT_SUBTITLE);
        totalRevVal.setForeground(UIConstants.COLOR_GOLD);
        summaryBar.add(totalRevVal);
        leftPanel.add(summaryBar, BorderLayout.SOUTH);
        split.setLeftComponent(leftPanel);

        // RIGHT: Bill generator
        JPanel rightPanel = buildFormCard("Generate Bill");

        // Reservation selector
        JPanel selectorPanel = new JPanel(new BorderLayout(8, 0));
        selectorPanel.setOpaque(false);
        selectorPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel resLabel = UIHelper.createLabel("Select Reservation:");
        cbReservation = new JComboBox<>();
        cbReservation.setBackground(UIConstants.COLOR_INPUT_BG);
        cbReservation.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        cbReservation.setFont(UIConstants.FONT_INPUT);
        cbReservation.addActionListener(e -> populateBillDetails());
        selectorPanel.add(resLabel, BorderLayout.WEST);
        selectorPanel.add(cbReservation, BorderLayout.CENTER);
        rightPanel.add(selectorPanel, BorderLayout.NORTH);

        // Bill details display
        txaBillDetails = new JTextArea();
        txaBillDetails.setEditable(false);
        txaBillDetails.setFont(new Font("Consolas", Font.PLAIN, 12));
        txaBillDetails.setBackground(UIConstants.COLOR_INPUT_BG);
        txaBillDetails.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        txaBillDetails.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txaBillDetails.setLineWrap(true);
        JScrollPane billScroll = new JScrollPane(txaBillDetails);
        billScroll.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));
        rightPanel.add(billScroll, BorderLayout.CENTER);

        // Payment method + buttons
        JPanel bottomPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel payRow = new JPanel(new BorderLayout(8, 0));
        payRow.setOpaque(false);
        payRow.add(UIHelper.createLabel("Payment Method:"), BorderLayout.WEST);
        cbPayMethod = UIHelper.createComboBox(new String[]{"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Payment"});
        payRow.add(cbPayMethod, BorderLayout.CENTER);
        bottomPanel.add(payRow);

        btnGenerateBill = UIHelper.createSuccessButton("Print Bill");
        btnClear        = UIHelper.createSecondaryButton("\u21BA Clear");
        btnGenerateBill.addActionListener(e -> printBill());
        btnClear.addActionListener(e -> clearForm());

        bottomPanel.add(btnGenerateBill);
        bottomPanel.add(btnClear);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
        refreshTable();
    }

    private void onTableSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Reservation> all = reservationManager.getAllReservations();
        if (row >= all.size()) return;
        Reservation r = all.get(all.size() - 1 - row); // reversed display
        cbReservation.setSelectedItem(r);
        populateBillDetails();
    }

    private void populateBillDetails() {
        Object selected = cbReservation.getSelectedItem();
        if (!(selected instanceof Reservation)) return;
        Reservation r = (Reservation) selected;

        String payMethod = (String) cbPayMethod.getSelectedItem();
        String bill = buildBillText(r, payMethod);
        txaBillDetails.setText(bill);
    }

    private String buildBillText(Reservation r, String payMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        sb.append("       GRAND AZURE HOTEL           \n");
        sb.append("       ★★★★★ Receipt              \n");
        sb.append("═══════════════════════════════════\n\n");
        sb.append(String.format("Bill ID     : BILL-%s\n", r.getReservationId()));
        sb.append(String.format("Date        : %s\n\n", LocalDate.now().format(DATE_FMT)));
        sb.append("─── GUEST INFORMATION ─────────────\n");
        sb.append(String.format("Guest       : %s\n", r.getGuest().getName()));
        sb.append(String.format("Phone       : %s\n", r.getGuest().getPhoneNumber()));
        sb.append(String.format("ID Card     : %s\n\n", r.getGuest().getIdentityCard()));
        sb.append("─── ROOM INFORMATION ───────────────\n");
        sb.append(String.format("Room        : %s (%s)\n", r.getRoom().getRoomId(), r.getRoom().getRoomType()));
        sb.append(String.format("Floor       : %d\n", r.getRoom().getFloor()));
        sb.append(String.format("Check-in    : %s\n", r.getCheckInDate().format(DATE_FMT)));
        sb.append(String.format("Check-out   : %s\n", r.getCheckOutDate().format(DATE_FMT)));
        sb.append(String.format("Duration    : %d night(s)\n\n", r.getNumberOfNights()));
        sb.append("─── PRICING ────────────────────────\n");
        // Polymorphism: display dynamic price per night
        sb.append(String.format("Base price  : $%.0f/night\n", r.getRoom().getBasePricePerNight()));
        sb.append(String.format("Actual price: $%.0f/night\n", r.getRoom().calculatePricePerNight()));
        sb.append(String.format("Subtotal    : $%.0f x %d nights\n", r.getRoom().calculatePricePerNight(), r.getNumberOfNights()));
        sb.append(String.format("TAX (10%%)   : $%.0f\n", r.getTotalAmount() * 0.1));
        sb.append(String.format("─────────────────────────────────\n"));
        sb.append(String.format("TOTAL DUE   : $%.0f\n\n", r.getTotalAmount() * 1.1));
        sb.append(String.format("Payment     : %s\n", payMethod));
        sb.append(String.format("Status      : %s\n\n", r.getStatus().name()));
        sb.append("═══════════════════════════════════\n");
        sb.append("  Thank you for staying with us!   \n");
        sb.append("  We hope to see you again soon.   \n");
        sb.append("═══════════════════════════════════\n");
        return sb.toString();
    }

    private void printBill() {
        if (txaBillDetails.getText().isEmpty()) {
            UIHelper.showError(this, "Please select a reservation first!"); return;
        }
        UIHelper.showInfo(this, "Bill generated!\n(In a production system, this would print or export as PDF.)");
    }

    @Override
    public void refreshTable() {
        cbReservation.removeAllItems();
        List<Reservation> all = reservationManager.getAllReservations();
        for (Reservation r : all) cbReservation.addItem(r);

        tableModel.setRowCount(0);
        for (int i = all.size() - 1; i >= 0; i--) {
            Reservation r = all.get(i);
            tableModel.addRow(new Object[]{
                r.getReservationId(),
                r.getGuest().getName(),
                r.getRoom().getRoomId(),
                r.getRoom().getRoomType(),
                r.getCheckInDate().format(DATE_FMT),
                r.getCheckOutDate().format(DATE_FMT),
                r.getNumberOfNights(),
                String.format("$%.0f", r.getRoom().calculatePricePerNight()),
                String.format("$%.0f", r.getTotalAmount()),
                r.getStatus().name()
            });
        }
    }

    @Override
    public void clearForm() {
        txaBillDetails.setText("");
        cbReservation.setSelectedIndex(0);
        populateBillDetails();
    }
}
