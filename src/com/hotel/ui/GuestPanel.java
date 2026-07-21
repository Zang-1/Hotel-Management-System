package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    
    // Form fields
    private JTextField txtGuestId, txtName, txtPhone, txtIdCard, txtEmail, txtAddress;
    private JTextField searchField;
    private JButton btnAdd, btnClear;

    public GuestPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLbl = new JLabel(com.hotel.util.LangManager.getString("menu.guests"));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel(com.hotel.util.LangManager.getString("sub.guest"));
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

        // ── CENTER: Table + Form ────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // LEFT: Search bar + Table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
        leftPanel.setBackground(UIConstants.COLOR_CARD);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Search bar
        searchField = UIHelper.createTextField("Tìm theo tên, mã KH, SĐT...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBackground(UIConstants.COLOR_BG_DARK);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.addActionListener(e -> refreshTable());
        
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setOpaque(false);
        searchBox.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchBox, BorderLayout.NORTH);

        // Table
        String[] cols = {
            com.hotel.util.LangManager.getString("lbl.guest_id").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.name").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.phone").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.id_card"),
            com.hotel.util.LangManager.getString("lbl.email")
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(60);

        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        // RIGHT: Form panel in scroll pane
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

        JLabel titleLbl = new JLabel((com.hotel.util.LangManager.getString("btn.add") + " " + com.hotel.util.LangManager.getString("lbl.guest")).toUpperCase());
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);

        txtGuestId = UIHelper.createTextField("");
        txtName = UIHelper.createTextField("");
        txtPhone = UIHelper.createTextField("");
        txtIdCard = UIHelper.createTextField("");
        txtEmail = UIHelper.createTextField("");
        txtAddress = UIHelper.createTextField("");

        addVGroup(form, com.hotel.util.LangManager.getString("lbl.guest_id"), txtGuestId);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.name"), txtName);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.phone"), txtPhone);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.id_card"), txtIdCard);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.email"), txtEmail);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.address"), txtAddress);

        panel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnAdd = new JButton(com.hotel.util.LangManager.getString("btn.add"));
        btnAdd.setBackground(UIConstants.COLOR_GOLD);
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(UIConstants.FONT_BODY_BOLD);
        btnAdd.setFocusable(false);
        
        btnClear = new JButton(com.hotel.util.LangManager.getString("btn.clear"));
        btnClear.setBackground(UIConstants.COLOR_BG_DARK);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(UIConstants.FONT_BODY);
        btnClear.setFocusable(false);

        btnAdd.addActionListener(e -> addGuest());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
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
        
        if (comp instanceof JTextField) {
            comp.setBackground(UIConstants.COLOR_BG_DARK);
        }
        comp.setPreferredSize(new Dimension(0, 40));
        p.add(comp, BorderLayout.CENTER);
        parent.add(p);
    }

    private void addGuest() {
        String id = txtGuestId.getText().trim();
        String name = txtName.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            UIHelper.showError(this, com.hotel.util.LangManager.getString("err.empty_fields"));
            return;
        }
        if (guestManager.findById(id) != null) {
            UIHelper.showError(this, com.hotel.util.LangManager.getString("err.id_exists"));
            return;
        }
        
        Guest g = new Guest(id, name, txtPhone.getText(), txtIdCard.getText(), txtEmail.getText(), txtAddress.getText());
        guestManager.addGuest(g);
        UIHelper.showInfo(this, com.hotel.util.LangManager.getString("msg.add_success"));
        refreshTable();
        clearForm();
    }

    @Override
    public void refreshTable() {
        tableModel.setRowCount(0);
        String q = searchField.getText().trim().toLowerCase();
        for (Guest g : guestManager.getAllGuests()) {
            if (q.isEmpty() || g.getGuestId().toLowerCase().contains(q) || 
                g.getName().toLowerCase().contains(q) || g.getPhoneNumber().contains(q)) {
                tableModel.addRow(new Object[]{
                    g.getGuestId(), g.getName(), g.getPhoneNumber(), g.getIdentityCard(), g.getEmail()
                });
            }
        }
    }

    @Override
    public void clearForm() {
        txtGuestId.setText("");
        btnAdd.setText(com.hotel.util.LangManager.getString("btn.add"));
        txtName.setText("");
        txtPhone.setText("");
        txtIdCard.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
    }
}
