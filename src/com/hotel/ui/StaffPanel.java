package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * StaffPanel — Full CRUD management for Hotel Staff.
 * Extends BasePanel (Inheritance + Polymorphism).
 */
public class StaffPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Staff> currentStaff;

    private JTextField txtId, txtName, txtPhone, txtSalary;
    private JComboBox<Staff.Role> cbRole;
    private JComboBox<String> cbShift;
    private JTextField searchField;
    private JButton btnAdd, btnEdit, btnDelete, btnClear;

    private Staff selectedStaff = null;

    public StaffPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Staff Management", "S"), BorderLayout.NORTH);

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

        String[] cols = {"Staff ID", "Name", "Role", "Phone", "Salary ($)", "Shift"};
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
        JPanel panel = buildFormCard("Staff Details");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtId     = UIHelper.createTextField("");
        txtName   = UIHelper.createTextField("");
        txtPhone  = UIHelper.createTextField("");
        txtSalary = UIHelper.createTextField("");
        cbRole    = UIHelper.createComboBox(Staff.Role.values());
        cbShift   = UIHelper.createComboBox(new String[]{"Morning", "Afternoon", "Night"});

        addFormRow(form, gbc, 0, "Staff ID *",  txtId);
        addFormRow(form, gbc, 1, "Full Name *", txtName);
        addFormRow(form, gbc, 2, "Role *",      cbRole);
        addFormRow(form, gbc, 3, "Phone *",     txtPhone);
        addFormRow(form, gbc, 4, "Salary ($)",  txtSalary);
        addFormRow(form, gbc, 5, "Shift",       cbShift);

        gbc.gridx = 1; gbc.gridy = 6;
        JButton btnGenId = UIHelper.createSecondaryButton("Auto ID");
        btnGenId.setPreferredSize(new Dimension(100, 28));
        btnGenId.addActionListener(e -> txtId.setText(staffManager.generateNextId()));
        form.add(btnGenId, gbc);

        panel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        btnPanel.setOpaque(false);

        btnAdd    = UIHelper.createSuccessButton("+ Add Staff");
        btnEdit   = UIHelper.createWarningButton("\u270E Update");
        btnDelete = UIHelper.createDangerButton("\u00D7 Delete");
        btnClear  = UIHelper.createSecondaryButton("\u21BA Clear Form");

        btnAdd.addActionListener(e -> addStaff());
        btnEdit.addActionListener(e -> editStaff());
        btnDelete.addActionListener(e -> deleteStaff());
        btnClear.addActionListener(e -> clearForm());
        btnEdit.setEnabled(false); btnDelete.setEnabled(false);

        btnPanel.add(btnAdd); btnPanel.add(btnEdit);
        btnPanel.add(btnDelete); btnPanel.add(UIHelper.createSeparator()); btnPanel.add(btnClear);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private boolean validateForm() {
        if (txtId.getText().trim().isEmpty())   { UIHelper.showError(this, "Staff ID required!"); return false; }
        if (txtName.getText().trim().isEmpty())  { UIHelper.showError(this, "Name required!"); return false; }
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{10,11}")) {
            UIHelper.showError(this, "Phone must be 10–11 digits!"); return false;
        }
        String sal = txtSalary.getText().trim();
        if (!sal.isEmpty()) {
            try { double s = Double.parseDouble(sal); if (s < 0) throw new NumberFormatException(); }
            catch (NumberFormatException e) { UIHelper.showError(this, "Salary must be a positive number!"); return false; }
        }
        return true;
    }

    private Staff buildStaff() {
        double salary = txtSalary.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSalary.getText().trim());
        return new Staff(
            txtId.getText().trim(), txtName.getText().trim(),
            (Staff.Role) cbRole.getSelectedItem(),
            txtPhone.getText().trim(), salary,
            (String) cbShift.getSelectedItem()
        );
    }

    private void addStaff() {
        if (!validateForm()) return;
        if (!staffManager.addStaff(buildStaff())) { UIHelper.showError(this, "Staff ID already exists!"); return; }
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Staff member added successfully!");
    }

    private void editStaff() {
        if (selectedStaff == null || !validateForm()) return;
        staffManager.updateStaff(buildStaff());
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Staff updated successfully!");
    }

    private void deleteStaff() {
        if (selectedStaff == null) return;
        if (!UIHelper.showConfirm(this, "Delete staff: " + selectedStaff.getName() + "?")) return;
        staffManager.deleteStaff(selectedStaff.getStaffId());
        refreshTable(); clearForm();
    }

    private void performSearch() {
        currentStaff = staffManager.searchStaff(searchField.getText().trim());
        populateTable(currentStaff);
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || currentStaff == null || row >= currentStaff.size()) return;
        selectedStaff = currentStaff.get(row);
        txtId.setText(selectedStaff.getStaffId()); txtId.setEditable(false);
        txtName.setText(selectedStaff.getName());
        cbRole.setSelectedItem(selectedStaff.getRole());
        txtPhone.setText(selectedStaff.getPhoneNumber());
        txtSalary.setText(String.valueOf(selectedStaff.getSalary()));
        cbShift.setSelectedItem(selectedStaff.getShift());
        btnEdit.setEnabled(true); btnDelete.setEnabled(true); btnAdd.setEnabled(false);
    }

    @Override
    public void refreshTable() {
        currentStaff = staffManager.getAllStaff();
        populateTable(currentStaff);
    }

    private void populateTable(List<Staff> list) {
        tableModel.setRowCount(0);
        for (Staff s : list) {
            tableModel.addRow(new Object[]{
                s.getStaffId(), s.getName(), s.getRole().name(),
                s.getPhoneNumber(), String.format("$%.0f", s.getSalary()), s.getShift()
            });
        }
    }

    @Override
    public void clearForm() {
        selectedStaff = null;
        txtId.setText(""); txtId.setEditable(true);
        txtName.setText(""); txtPhone.setText(""); txtSalary.setText("");
        cbRole.setSelectedIndex(0); cbShift.setSelectedIndex(0);
        btnEdit.setEnabled(false); btnDelete.setEnabled(false); btnAdd.setEnabled(true);
        table.clearSelection();
    }
}
