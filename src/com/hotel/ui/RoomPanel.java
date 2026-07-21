package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import com.hotel.exception.DuplicateDataException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoomPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    
    // Form fields
    private JTextField txtRoomId;
    private JComboBox<String> cbRoomType;
    private JComboBox<String> cbAvailable;
    private JTextField searchField;

    // Action buttons
    private JButton btnAdd, btnClear;

    public RoomPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLbl = new JLabel(com.hotel.util.LangManager.getString("menu.rooms"));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel(com.hotel.util.LangManager.getString("sub.room"));
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
        searchField = UIHelper.createTextField("Tìm kiếm mã phòng, loại phòng...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBackground(UIConstants.COLOR_BG_DARK);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.addActionListener(e -> refreshTable()); // Simple enter search
        
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setOpaque(false);
        searchBox.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchBox, BorderLayout.NORTH);

        // Table
        String[] cols = {
            com.hotel.util.LangManager.getString("lbl.room_id").replace(" *", "").toUpperCase(),
            com.hotel.util.LangManager.getString("lbl.room_type").toUpperCase(),
            "GIÁ/ĐÊM",
            com.hotel.util.LangManager.getString("dashboard.room_status").toUpperCase(),
            com.hotel.util.LangManager.getString("lbl.action").toUpperCase()
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; } // Only Thao tác is editable for button clicks
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(80);
        
        // Column 0: Mã Phòng (Gold)
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, r, c);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setForeground(UIConstants.COLOR_GOLD);
                lbl.setFont(UIConstants.FONT_BODY_BOLD);
                return lbl;
            }
        });
        
        // Column 2: Giá (Green)
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, r, c);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setForeground(UIConstants.COLOR_SUCCESS);
                return lbl;
            }
        });
        
        // Status renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JPanel p = new JPanel(new GridBagLayout());
                p.setOpaque(false);
                
                JLabel lbl = new JLabel((String)v, SwingConstants.CENTER);
                lbl.setFont(UIConstants.FONT_SMALL);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                
                if (com.hotel.util.LangManager.getString("status.empty").equals(v)) {
                    lbl.setBackground(new Color(20, 60, 40));
                    lbl.setForeground(UIConstants.COLOR_SUCCESS);
                } else {
                    lbl.setBackground(new Color(60, 30, 30));
                    lbl.setForeground(UIConstants.COLOR_DANGER);
                }
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.WEST;
                gbc.weightx = 1.0;
                gbc.insets = new java.awt.Insets(0, 10, 0, 0);
                p.add(lbl, gbc);
                
                if (isSelected) {
                    p.setBackground(t.getSelectionBackground());
                    p.setOpaque(true);
                }
                return p;
            }
        });
        
        // Action renderer
        TableActionCell actionCell = new TableActionCell(row -> {
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setOpaque(false);
            
            JButton bEdit = UIHelper.createActionButton(com.hotel.util.LangManager.getString("btn.edit"), UIConstants.COLOR_ACCENT);
            JButton bDel = UIHelper.createActionButton("Xóa", UIConstants.COLOR_DANGER);
            
            bEdit.addActionListener(e -> {
                int rIdx = table.convertRowIndexToModel(row);
                editRoomMode(tableModel.getValueAt(rIdx, 0).toString());
            });
            bEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
            bDel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            inner.add(bEdit);
            inner.add(Box.createVerticalStrut(8));
            inner.add(bDel);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setOpaque(false);
            panel.add(inner);
            
            bDel.addActionListener(e -> {
                String id = (String) table.getValueAt(row, 0);
                if (UIHelper.showConfirm(this, "Xóa phòng " + id + "?")) {
                    roomManager.deleteRoom(id);
                    refreshTable();
                }
            });
            
            
            return panel;
        });
        table.getColumnModel().getColumn(4).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(4).setCellEditor(actionCell);

        leftPanel.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        // RIGHT: Form
        JPanel formPanel = buildFormPanel();
        formPanel.setPreferredSize(new Dimension(320, 0));
        contentPanel.add(formPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel("THÊM PHÒNG MỚI");
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);

        txtRoomId = UIHelper.createTextField("");
        txtRoomId.setBackground(UIConstants.COLOR_BG_DARK);
        
        cbRoomType = new JComboBox<>(new String[]{
            "Standard — " + com.hotel.util.LangManager.formatCurrency(500000.0) + "/đêm",
            "Deluxe — " + com.hotel.util.LangManager.formatCurrency(800000.0) + "/đêm",
            "Suite — " + com.hotel.util.LangManager.formatCurrency(1500000.0) + "/đêm"
        });cbRoomType.setBackground(UIConstants.COLOR_BG_DARK);
        
        cbAvailable = new JComboBox<>(new String[]{
            com.hotel.util.LangManager.getString("status.empty"),
            com.hotel.util.LangManager.getString("status.occupied")
        });cbAvailable.setBackground(UIConstants.COLOR_BG_DARK);

        addVGroup(form, com.hotel.util.LangManager.getString("lbl.room_id"), txtRoomId);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.room_type"), cbRoomType);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.status"), cbAvailable);

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

        btnAdd.addActionListener(e -> addRoom());
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
        comp.setPreferredSize(new Dimension(0, 40));
        p.add(comp, BorderLayout.CENTER);
        parent.add(p);
    }

    private void addRoom() {
        String id = txtRoomId.getText().trim();
        if (id.isEmpty()) {
            UIHelper.showError(this, "Vui lòng nhập mã phòng!");
            return;
        }
        // Xóa lệnh kiểm tra thủ công để nhường cho DuplicateDataException
        
        int typeIdx = cbRoomType.getSelectedIndex();
        Room r = null;
        boolean isAvail = cbAvailable.getSelectedIndex() == 0;
        
        if (typeIdx == 0) r = new StandardRoom(id, 50, isAvail, 1);
        else if (typeIdx == 1) r = new DeluxeRoom(id, 90, isAvail, 1);
        else r = new SuiteRoom(id, 150, isAvail, 1);
        
        // Theo Bài giảng Chương 3: Sử dụng khối try-catch để bắt ngoại lệ (Checked Exception)
        try {
            roomManager.addRoom(r);
            UIHelper.showInfo(this, "Đã thêm phòng!");
            refreshTable();
            clearForm();
        } catch (DuplicateDataException e) {
            UIHelper.showError(this, e.getMessage());
        }
    }
    
    private void editRoomMode(String id) {
        Room r = roomManager.findById(id);
        if (r != null) {
            txtRoomId.setText(r.getRoomId());
            txtRoomId.setEnabled(false);
            if (r instanceof StandardRoom) cbRoomType.setSelectedIndex(0);
            else if (r instanceof DeluxeRoom) cbRoomType.setSelectedIndex(1);
            else cbRoomType.setSelectedIndex(2);
            
            cbAvailable.setSelectedIndex(r.isAvailable() ? 0 : 1);
            
            // Switch button to Edit
            btnAdd.setText(com.hotel.util.LangManager.getString("btn.save"));
            for(java.awt.event.ActionListener al : btnAdd.getActionListeners()) {
                btnAdd.removeActionListener(al);
            }
            btnAdd.addActionListener(e -> {
                boolean isAvail = cbAvailable.getSelectedIndex() == 0;
                r.setAvailable(isAvail);
                // Can't easily change type without re-creating, so let's just update status
                roomManager.updateRoom(r);
                UIHelper.showInfo(this, "Cập nhật thành công!");
                refreshTable();
                clearForm();
            });
        }
    }

    @Override
    public void refreshTable() {
        tableModel.setRowCount(0);
        String q = searchField.getText().trim().toLowerCase();
        for (Room r : roomManager.getAllRooms()) {
            if (q.isEmpty() || r.getRoomId().toLowerCase().contains(q) || r.getRoomType().toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomType(),
                    com.hotel.util.LangManager.formatCurrency(r.calculatePricePerNight()),
                    r.isAvailable() ? com.hotel.util.LangManager.getString("status.empty") : com.hotel.util.LangManager.getString("status.occupied"),
                    "" // Button column
                });
            }
        }
    }

    @Override
    public void clearForm() {
        txtRoomId.setText("");
        txtRoomId.setEnabled(true);
        cbRoomType.setSelectedIndex(0);
        cbAvailable.setSelectedIndex(0);
        
        btnAdd.setText(com.hotel.util.LangManager.getString("btn.add"));
        for(java.awt.event.ActionListener al : btnAdd.getActionListeners()) {
            btnAdd.removeActionListener(al);
        }
        btnAdd.addActionListener(e -> addRoom());
    }
}
