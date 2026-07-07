package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    
    // Form fields
    private JComboBox<String> cbGuest;
    private JComboBox<String> cbRoom;
    private JTextField txtCheckIn, txtCheckOut;
    private JTextField searchField;
    private JButton btnAdd, btnClear;
    private JLabel lblTotal;
    
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReservationPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLbl = new JLabel("Đặt phòng");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel("Quản lý khách hàng đặt phòng và nhận/trả phòng");
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

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // LEFT: Table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
        leftPanel.setBackground(UIConstants.COLOR_CARD);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        searchField = UIHelper.createTextField("Tìm theo mã đặt, tên khách...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBackground(UIConstants.COLOR_BG_DARK);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.addActionListener(e -> refreshTable());
        
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setOpaque(false);
        searchBox.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchBox, BorderLayout.NORTH);

        String[] cols = {"Mã Đặt", "Khách hàng", "Phòng", "Check-in", "Check-out", "Trạng thái", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(60);
        
        // Status renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String status = (String)v;
                if ("PENDING".equals(status)) lbl.setForeground(new Color(255, 179, 71)); // Orange
                else if ("CHECKED_IN".equals(status)) lbl.setForeground(UIConstants.COLOR_SUCCESS);
                else if ("CHECKED_OUT".equals(status)) lbl.setForeground(new Color(150, 160, 180));
                else lbl.setForeground(UIConstants.COLOR_DANGER);
                return lbl;
            }
        });
        
        TableActionCell actionCell = new TableActionCell(row -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(false);
            
            String status = (String) table.getValueAt(row, 5);
            String id = (String) table.getValueAt(row, 0);
            
            if ("PENDING".equals(status)) {
                JButton bIn = UIHelper.createActionButton("Check-in", UIConstants.COLOR_SUCCESS);
                JButton bCancel = UIHelper.createActionButton("Hủy", UIConstants.COLOR_DANGER);
                bIn.addActionListener(e -> {
                    reservationManager.checkIn(id);
                    refreshTable();
                });
                bCancel.addActionListener(e -> {
                    if(UIHelper.showConfirm(this, "Hủy đặt phòng này?")) {
                        reservationManager.cancelReservation(id);
                        refreshTable();
                    }
                });
                panel.add(bIn);
                panel.add(bCancel);
            } else if ("CHECKED_IN".equals(status)) {
                JButton bOut = UIHelper.createActionButton("Check-out", UIConstants.COLOR_ACCENT);
                bOut.addActionListener(e -> {
                    reservationManager.checkOut(id);
                    refreshTable();
                });
                panel.add(bOut);
            }
            
            return panel;
        });
        table.getColumnModel().getColumn(6).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(6).setCellEditor(actionCell);

        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        // RIGHT: Form
        JPanel formPanel = buildFormPanel();
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        formScroll.setPreferredSize(new Dimension(320, 0));
        
        contentPanel.add(formScroll, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel("TẠO ĐẶT PHÒNG");
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);

        cbGuest = new JComboBox<>();
        cbGuest.setBackground(UIConstants.COLOR_BG_DARK);
        
        cbRoom = new JComboBox<>();
        cbRoom.setBackground(UIConstants.COLOR_BG_DARK);
        
        txtCheckIn = UIHelper.createTextField(LocalDate.now().format(DATE_FMT));
        txtCheckOut = UIHelper.createTextField(LocalDate.now().plusDays(1).format(DATE_FMT));
        
        lblTotal = new JLabel("$0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(UIConstants.COLOR_GOLD);

        addVGroup(form, "Chọn khách hàng *", cbGuest);
        addVGroup(form, "Chọn phòng *", cbRoom);
        addVGroup(form, "Ngày Check-in (dd/MM/yyyy) *", txtCheckIn);
        addVGroup(form, "Ngày Check-out (dd/MM/yyyy) *", txtCheckOut);
        addVGroup(form, "Thành tiền ($)", lblTotal);

        panel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnAdd = new JButton("Tạo đặt phòng");
        btnAdd.setBackground(UIConstants.COLOR_GOLD);
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(UIConstants.FONT_BODY_BOLD);
        btnAdd.setFocusable(false);
        
        btnClear = new JButton("Làm mới form");
        btnClear.setBackground(UIConstants.COLOR_BG_DARK);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(UIConstants.FONT_BODY);
        btnClear.setFocusable(false);

        btnAdd.addActionListener(e -> addReservation());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        // Auto update comboboxes
        cbRoom.addActionListener(e -> calcPrice());
        txtCheckIn.addActionListener(e -> calcPrice());
        txtCheckOut.addActionListener(e -> calcPrice());

        return panel;
    }

    private void addVGroup(JPanel parent, String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        lbl.setFont(UIConstants.FONT_SMALL);
        p.add(lbl, BorderLayout.NORTH);
        
        if (comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setBackground(UIConstants.COLOR_BG_DARK);
        }
        comp.setPreferredSize(new Dimension(0, 40));
        p.add(comp, BorderLayout.CENTER);
        parent.add(p);
    }
    
    private void calcPrice() {
        if(cbRoom.getSelectedItem() == null) return;
        String roomStr = (String)cbRoom.getSelectedItem();
        String roomId = roomStr.split(" -")[0];
        Room r = roomManager.findById(roomId);
        if(r == null) return;
        
        try {
            LocalDate in = LocalDate.parse(txtCheckIn.getText(), DATE_FMT);
            LocalDate out = LocalDate.parse(txtCheckOut.getText(), DATE_FMT);
            long days = java.time.temporal.ChronoUnit.DAYS.between(in, out);
            if(days <= 0) { lblTotal.setText("$0"); return; }
            double total = r.calculatePricePerNight() * days;
            lblTotal.setText("$" + (int)total);
        } catch(Exception e) {
            lblTotal.setText("$0");
        }
    }

    private void addReservation() {
        if (cbGuest.getSelectedItem() == null || cbRoom.getSelectedItem() == null) {
            UIHelper.showError(this, "Vui lòng chọn Khách hàng và Phòng!");
            return;
        }
        
        String gStr = (String)cbGuest.getSelectedItem();
        String rStr = (String)cbRoom.getSelectedItem();
        
        Guest g = guestManager.findById(gStr.split(" -")[0]);
        Room r = roomManager.findById(rStr.split(" -")[0]);
        
        try {
            LocalDate in = LocalDate.parse(txtCheckIn.getText(), DATE_FMT);
            LocalDate out = LocalDate.parse(txtCheckOut.getText(), DATE_FMT);
            
            if (!in.isBefore(out)) {
                UIHelper.showError(this, "Ngày check-out phải sau ngày check-in!");
                return;
            }
            if (in.isBefore(LocalDate.now())) {
                UIHelper.showError(this, "Không thể đặt phòng trong quá khứ!");
                return;
            }
            if (!reservationManager.isRoomAvailableForDates(r.getRoomId(), in, out)) {
                UIHelper.showError(this, "Phòng không trống trong khoảng thời gian này!");
                return;
            }
            
            String id = reservationManager.generateNextId();
            Reservation res = new Reservation(id, g, r, in, out);
            reservationManager.createReservation(res);
            UIHelper.showInfo(this, "Đã tạo đặt phòng!");
            refreshTable();
            clearForm();
            
        } catch(DateTimeParseException ex) {
            UIHelper.showError(this, "Ngày không hợp lệ! Định dạng: dd/MM/yyyy");
        }
    }

    @Override
    public void refreshTable() {
        tableModel.setRowCount(0);
        String q = searchField.getText().trim().toLowerCase();
        for (Reservation res : reservationManager.getAllReservations()) {
            if (q.isEmpty() || res.getReservationId().toLowerCase().contains(q) || 
                res.getGuest().getName().toLowerCase().contains(q)) {
                
                tableModel.addRow(new Object[]{
                    res.getReservationId(),
                    res.getGuest().getName(),
                    res.getRoom().getRoomId(),
                    res.getCheckInDate().format(DATE_FMT),
                    res.getCheckOutDate().format(DATE_FMT),
                    res.getStatus().name(),
                    ""
                });
            }
        }
        
        // Update combos
        cbGuest.removeAllItems();
        for(Guest g : guestManager.getAllGuests()) {
            cbGuest.addItem(g.getGuestId() + " - " + g.getName());
        }
        cbRoom.removeAllItems();
        for(Room r : roomManager.getAllRooms()) {
            cbRoom.addItem(r.getRoomId() + " - " + r.getRoomType());
        }
    }

    @Override
    public void clearForm() {
        if(cbGuest.getItemCount() > 0) cbGuest.setSelectedIndex(0);
        if(cbRoom.getItemCount() > 0) cbRoom.setSelectedIndex(0);
        txtCheckIn.setText(LocalDate.now().format(DATE_FMT));
        txtCheckOut.setText(LocalDate.now().plusDays(1).format(DATE_FMT));
        lblTotal.setText("$0");
        calcPrice();
    }
}
