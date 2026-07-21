package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    
    private JTextField txtStaffId, txtName, txtPhone, txtSalary;
    private JComboBox<String> cbRole, cbShift;
    private JTextField searchField;
    private JButton btnAdd, btnClear;

    public StaffPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLbl = new JLabel(com.hotel.util.LangManager.getString("menu.staff"));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        
        JLabel subLbl = new JLabel(com.hotel.util.LangManager.getString("sub.staff"));
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

        searchField = UIHelper.createTextField("Tìm theo tên hoặc mã NV...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBackground(UIConstants.COLOR_BG_DARK);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.addActionListener(e -> refreshTable());
        
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setOpaque(false);
        searchBox.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchBox, BorderLayout.NORTH);

        String[] cols = {
            com.hotel.util.LangManager.getString("lbl.staff_id").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.name").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.role"),
            com.hotel.util.LangManager.getString("lbl.phone").replace(" *", ""),
            com.hotel.util.LangManager.getString("lbl.salary"),
            com.hotel.util.LangManager.getString("lbl.shift"),
            com.hotel.util.LangManager.getString("lbl.action")
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setRowHeight(60);
        
        // Role renderer
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("ADMIN".equals(v)) {
                    lbl.setForeground(new Color(191, 90, 242)); // Purple
                } else {
                    lbl.setForeground(new Color(100, 210, 255)); // Light blue
                }
                return lbl;
            }
        });
        
        TableActionCell actionCell = new TableActionCell(row -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(false);
            JButton bEdit = UIHelper.createActionButton(com.hotel.util.LangManager.getString("btn.edit"), UIConstants.COLOR_ACCENT);
            JButton bDel = UIHelper.createActionButton(com.hotel.util.LangManager.getString("btn.delete"), UIConstants.COLOR_DANGER);
            
            bEdit.addActionListener(e -> {
                String id = (String) table.getValueAt(row, 0);
                editMode(id);
            });
            bDel.addActionListener(e -> {
                String id = (String) table.getValueAt(row, 0);
                if (UIHelper.showConfirm(this, "Delete staff " + id + "?")) {
                    staffManager.deleteStaff(id);
                    refreshTable();
                }
            });
            
            panel.add(bEdit);
            panel.add(bDel);
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

        JLabel titleLbl = new JLabel((com.hotel.util.LangManager.getString("btn.add") + " " + com.hotel.util.LangManager.getString("menu.staff")).toUpperCase());
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setOpaque(false);

        txtStaffId = UIHelper.createTextField("");
        txtName = UIHelper.createTextField("");
        cbRole = UIHelper.createComboBox(new String[]{"ADMIN - Quản trị", "RECEPTIONIST - Lễ tân"});
        txtPhone = UIHelper.createTextField("");
        txtSalary = UIHelper.createTextField("");
        cbShift = UIHelper.createComboBox(new String[]{"Ca sáng (6:00 - 14:00)", "Ca chiều (14:00 - 22:00)", "Ca tối (22:00 - 6:00)"});

        addVGroup(form, com.hotel.util.LangManager.getString("lbl.staff_id"), txtStaffId);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.name"), txtName);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.role"), cbRole);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.phone").replace(" *", ""), txtPhone);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.salary") + " (VND)", txtSalary);
        addVGroup(form, com.hotel.util.LangManager.getString("lbl.shift"), cbShift);

        panel.add(form, BorderLayout.CENTER);

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

        btnAdd.addActionListener(e -> addStaff());
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
        
        if (comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setBackground(UIConstants.COLOR_BG_DARK);
        }
        comp.setPreferredSize(new Dimension(0, 40));
        p.add(comp, BorderLayout.CENTER);
        parent.add(p);
    }

    private void addStaff() {
        String id = txtStaffId.getText().trim();
        String name = txtName.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            UIHelper.showError(this, com.hotel.util.LangManager.getString("err.empty_fields"));
            return;
        }
        if (staffManager.findById(id) != null) {
            UIHelper.showError(this, com.hotel.util.LangManager.getString("err.id_exists"));
            return;
        }
        
        double sal = 0;
        try { sal = Double.parseDouble(txtSalary.getText().trim()); } catch(Exception ignored) {}
        
        Staff.Role r = cbRole.getSelectedIndex() == 0 ? Staff.Role.MANAGER : Staff.Role.RECEPTIONIST;
        String shift = cbShift.getSelectedItem().toString().split(" ")[0] + " " + cbShift.getSelectedItem().toString().split(" ")[1];
        
        Staff s = new Staff(id, name, r, txtPhone.getText(), sal, shift);
        staffManager.addStaff(s);
        UIHelper.showInfo(this, com.hotel.util.LangManager.getString("msg.add_success"));
        refreshTable();
        clearForm();
    }
    
    private void editMode(String id) {
        Staff s = staffManager.findById(id);
        if (s != null) {
            txtStaffId.setText(s.getStaffId());
            txtStaffId.setEnabled(false);
            txtName.setText(s.getName());
            cbRole.setSelectedIndex(s.getRole() == Staff.Role.MANAGER ? 0 : 1);
            txtPhone.setText(s.getPhoneNumber());
            txtSalary.setText(String.valueOf(s.getSalary()));
            
            btnAdd.setText(com.hotel.util.LangManager.getString("btn.save"));
            for(java.awt.event.ActionListener al : btnAdd.getActionListeners()) {
                btnAdd.removeActionListener(al);
            }
            btnAdd.addActionListener(e -> {
                s.setName(txtName.getText());
                s.setRole(cbRole.getSelectedIndex() == 0 ? Staff.Role.MANAGER : Staff.Role.RECEPTIONIST);
                s.setPhoneNumber(txtPhone.getText());
                try { s.setSalary(Double.parseDouble(txtSalary.getText())); } catch(Exception ignored){}
                staffManager.updateStaff(s);
                UIHelper.showInfo(this, com.hotel.util.LangManager.getString("msg.update_success"));
                refreshTable();
                clearForm();
            });
        }
    }

    @Override
    public void refreshTable() {
        tableModel.setRowCount(0);
        String q = searchField.getText().trim().toLowerCase();
        for (Staff s : staffManager.getAllStaff()) {
            if (q.isEmpty() || s.getStaffId().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{
                    s.getStaffId(), s.getName(), s.getRole().toString(), 
                    s.getPhoneNumber(), "$" + (int)s.getSalary(), s.getShift(), ""
                });
            }
        }
    }

    @Override
    public void clearForm() {
        txtStaffId.setText("");
        txtStaffId.setEnabled(true);
        txtName.setText("");
        txtPhone.setText("");
        txtSalary.setText("");
        cbRole.setSelectedIndex(0);
        
        btnAdd.setText(com.hotel.util.LangManager.getString("btn.add"));
        for(java.awt.event.ActionListener al : btnAdd.getActionListeners()) {
            btnAdd.removeActionListener(al);
        }
        btnAdd.addActionListener(e -> addStaff());
    }
}
