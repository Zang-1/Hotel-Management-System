package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.Guest;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * GuestPanel — Full CRUD management for Guests/Customers.
 * Extends BasePanel (Inheritance + Polymorphism).
 */
public class GuestPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Guest> currentGuests;

    private JTextField txtId, txtName, txtPhone, txtIdCard, txtEmail, txtAddress;
    private JTextField searchField;
    private JButton btnAdd, btnEdit, btnDelete, btnClear;

    private Guest selectedGuest = null;

    public GuestPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Guest Management", "G"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());

        // Left: Table
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

        String[] cols = {"Guest ID", "Full Name", "Phone", "ID Card", "Email", "Address"};
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

        // Right: Form
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
        JPanel panel = buildFormCard("Guest Details");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtId      = UIHelper.createTextField("");
        txtName    = UIHelper.createTextField("");
        txtPhone   = UIHelper.createTextField("");
        txtIdCard  = UIHelper.createTextField("");
        txtEmail   = UIHelper.createTextField("");
        txtAddress = UIHelper.createTextField("");

        addFormRow(form, gbc, 0, "Guest ID *",    txtId);
        addFormRow(form, gbc, 1, "Full Name *",   txtName);
        addFormRow(form, gbc, 2, "Phone *",       txtPhone);
        addFormRow(form, gbc, 3, "ID Card *",     txtIdCard);
        addFormRow(form, gbc, 4, "Email",         txtEmail);
        addFormRow(form, gbc, 5, "Address",       txtAddress);

        // Auto-generate ID button
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 1;
        JButton btnGenId = UIHelper.createSecondaryButton("Auto ID");
        btnGenId.addActionListener(e -> txtId.setText(guestManager.generateNextId()));
        btnGenId.setPreferredSize(new Dimension(100, 28));
        form.add(btnGenId, gbc);

        panel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        btnPanel.setOpaque(false);

        btnAdd    = UIHelper.createSuccessButton("+ Add Guest");
        btnEdit   = UIHelper.createWarningButton("\u270E Update");
        btnDelete = UIHelper.createDangerButton("\u00D7 Delete");
        btnClear  = UIHelper.createSecondaryButton("\u21BA Clear Form");

        btnAdd.addActionListener(e -> addGuest());
        btnEdit.addActionListener(e -> editGuest());
        btnDelete.addActionListener(e -> deleteGuest());
        btnClear.addActionListener(e -> clearForm());

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(UIHelper.createSeparator());
        btnPanel.add(btnClear);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private boolean validateForm() {
        if (txtId.getText().trim().isEmpty())    { UIHelper.showError(this, "Guest ID is required!"); return false; }
        if (txtName.getText().trim().isEmpty())  { UIHelper.showError(this, "Full name is required!"); return false; }

        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) { UIHelper.showError(this, "Phone number is required!"); return false; }
        if (!phone.matches("\\d{10,11}")) { UIHelper.showError(this, "Phone must be 10–11 digits!"); return false; }

        String idCard = txtIdCard.getText().trim();
        if (idCard.isEmpty()) { UIHelper.showError(this, "ID Card is required!"); return false; }
        if (!idCard.matches("\\d{9,12}")) { UIHelper.showError(this, "ID Card must be 9–12 digits!"); return false; }

        return true;
    }

    private Guest buildGuest() {
        return new Guest(
            txtId.getText().trim(),
            txtName.getText().trim(),
            txtPhone.getText().trim(),
            txtIdCard.getText().trim(),
            txtEmail.getText().trim(),
            txtAddress.getText().trim()
        );
    }

    private void addGuest() {
        if (!validateForm()) return;
        if (!guestManager.addGuest(buildGuest())) {
            UIHelper.showError(this, "Guest ID already exists!"); return;
        }
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Guest added successfully!");
    }

    private void editGuest() {
        if (selectedGuest == null || !validateForm()) return;
        guestManager.updateGuest(buildGuest());
        refreshTable(); clearForm();
        UIHelper.showInfo(this, "Guest updated successfully!");
    }

    private void deleteGuest() {
        if (selectedGuest == null) return;
        if (!UIHelper.showConfirm(this, "Delete guest: " + selectedGuest.getName() + "?")) return;
        guestManager.deleteGuest(selectedGuest.getGuestId());
        refreshTable(); clearForm();
    }

    private void performSearch() {
        currentGuests = guestManager.searchGuests(searchField.getText().trim());
        populateTable(currentGuests);
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || currentGuests == null || row >= currentGuests.size()) return;
        selectedGuest = currentGuests.get(row);
        txtId.setText(selectedGuest.getGuestId()); txtId.setEditable(false);
        txtName.setText(selectedGuest.getName());
        txtPhone.setText(selectedGuest.getPhoneNumber());
        txtIdCard.setText(selectedGuest.getIdentityCard());
        txtEmail.setText(selectedGuest.getEmail());
        txtAddress.setText(selectedGuest.getAddress());
        btnEdit.setEnabled(true); btnDelete.setEnabled(true); btnAdd.setEnabled(false);
    }

    @Override
    public void refreshTable() {
        currentGuests = guestManager.getAllGuests();
        populateTable(currentGuests);
    }

    private void populateTable(List<Guest> guests) {
        tableModel.setRowCount(0);
        for (Guest g : guests) {
            tableModel.addRow(new Object[]{
                g.getGuestId(), g.getName(), g.getPhoneNumber(),
                g.getIdentityCard(), g.getEmail(), g.getAddress()
            });
        }
    }

    @Override
    public void clearForm() {
        selectedGuest = null;
        txtId.setText(""); txtId.setEditable(true);
        txtName.setText(""); txtPhone.setText("");
        txtIdCard.setText(""); txtEmail.setText(""); txtAddress.setText("");
        btnEdit.setEnabled(false); btnDelete.setEnabled(false); btnAdd.setEnabled(true);
        table.clearSelection();
    }
}
