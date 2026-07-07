package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
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
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLbl = new JLabel("Thanh toán");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel("Thanh toán hóa đơn cho khách hàng");
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

        String[] cols = {"Mã Đặt", "Khách hàng", "Phòng", "Check-in", "Check-out", "Số đêm", "Tổng", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(50);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String resId = (String) table.getValueAt(row, 0);
                    for (int i = 0; i < cbReservation.getItemCount(); i++) {
                        if (cbReservation.getItemAt(i).getReservationId().equals(resId)) {
                            cbReservation.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        // RIGHT: Form
        JPanel formPanel = buildFormPanel();
        formPanel.setPreferredSize(new Dimension(350, 0));
        contentPanel.add(formPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel("LẬP HÓA ĐƠN");
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        
        JPanel topForm = new JPanel(new GridLayout(0, 1, 0, 10));
        topForm.setOpaque(false);
        
        cbReservation = new JComboBox<>();
        cbReservation.setBackground(UIConstants.COLOR_BG_DARK);
        cbReservation.addActionListener(e -> populateBillDetails());
        
        cbPayMethod = UIHelper.createComboBox(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        cbPayMethod.setBackground(UIConstants.COLOR_BG_DARK);
        cbPayMethod.addActionListener(e -> populateBillDetails());

        addVGroup(topForm, "Chọn mã đặt phòng", cbReservation);
        centerPanel.add(topForm, BorderLayout.NORTH);

        txaBillDetails = new JTextArea();
        txaBillDetails.setEditable(false);
        txaBillDetails.setFont(new Font("Consolas", Font.PLAIN, 13));
        txaBillDetails.setBackground(UIConstants.COLOR_BG_DARK);
        txaBillDetails.setForeground(Color.WHITE);
        txaBillDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(new JScrollPane(txaBillDetails), BorderLayout.CENTER);
        
        JPanel botForm = new JPanel(new GridLayout(0, 1, 0, 10));
        botForm.setOpaque(false);
        addVGroup(botForm, "Phương thức thanh toán", cbPayMethod);
        centerPanel.add(botForm, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnGenerateBill = new JButton("In Hóa đơn");
        btnGenerateBill.setBackground(UIConstants.COLOR_SUCCESS);
        btnGenerateBill.setForeground(Color.WHITE);
        btnGenerateBill.setFont(UIConstants.FONT_BODY_BOLD);
        btnGenerateBill.setFocusable(false);
        
        btnClear = new JButton("Làm mới");
        btnClear.setBackground(UIConstants.COLOR_BG_DARK);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(UIConstants.FONT_BODY);
        btnClear.setFocusable(false);

        btnGenerateBill.addActionListener(e -> {
            UIHelper.showInfo(this, "Đã in hóa đơn thành công!");
        });
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnGenerateBill);
        btnPanel.add(btnClear);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addVGroup(JPanel parent, String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        lbl.setFont(UIConstants.FONT_SMALL);
        p.add(lbl, BorderLayout.NORTH);
        comp.setPreferredSize(new Dimension(0, 40));
        p.add(comp, BorderLayout.CENTER);
        parent.add(p);
    }

    private void populateBillDetails() {
        Object selected = cbReservation.getSelectedItem();
        if (!(selected instanceof Reservation)) {
            txaBillDetails.setText("");
            return;
        }
        Reservation r = (Reservation) selected;
        String payMethod = (String) cbPayMethod.getSelectedItem();
        
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("      GRAND AZURE HOTEL\n");
        sb.append("==============================\n");
        sb.append("HÓA ĐƠN THANH TOÁN\n\n");
        sb.append("Mã Đặt: ").append(r.getReservationId()).append("\n");
        sb.append("Khách: ").append(r.getGuest().getName()).append("\n");
        sb.append("Phòng: ").append(r.getRoom().getRoomId()).append(" (").append(r.getRoom().getRoomType()).append(")\n");
        sb.append("Ngày In: ").append(r.getCheckInDate().format(DATE_FMT)).append("\n");
        sb.append("Ngày Out: ").append(r.getCheckOutDate().format(DATE_FMT)).append("\n");
        sb.append("Số đêm: ").append(r.getNumberOfNights()).append("\n");
        sb.append("Giá/đêm: $").append((int)r.getRoom().calculatePricePerNight()).append("\n");
        sb.append("------------------------------\n");
        sb.append("TỔNG CỘNG: $").append((int)r.getTotalAmount()).append("\n");
        sb.append("Phương thức: ").append(payMethod).append("\n");
        sb.append("==============================\n");
        sb.append("      Xin cảm ơn quý khách!\n");
        
        txaBillDetails.setText(sb.toString());
    }

    @Override
    public void refreshTable() {
        tableModel.setRowCount(0);
        cbReservation.removeAllItems();
        
        List<Reservation> list = reservationManager.getAllReservations();
        for (Reservation res : list) {
            tableModel.addRow(new Object[]{
                res.getReservationId(),
                res.getGuest().getName(),
                res.getRoom().getRoomId(),
                res.getCheckInDate().format(DATE_FMT),
                res.getCheckOutDate().format(DATE_FMT),
                res.getNumberOfNights(),
                "$" + (int)res.getTotalAmount(),
                res.getStatus().name()
            });
            cbReservation.addItem(res);
        }
    }

    @Override
    public void clearForm() {
        if(cbReservation.getItemCount() > 0) cbReservation.setSelectedIndex(0);
        cbPayMethod.setSelectedIndex(0);
        txaBillDetails.setText("");
    }
}
