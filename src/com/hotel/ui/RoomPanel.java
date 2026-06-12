package com.hotel.ui;

import com.hotel.manager.*;
import com.hotel.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * RoomPanel — Full CRUD management for Rooms.
 * Extends BasePanel (Inheritance + Polymorphism).
 */
public class RoomPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Room> currentRooms;

    // Form fields
    private JTextField txtRoomId, txtBasePrice, txtFloor;
    private JComboBox<String> cbRoomType;
    private JCheckBox chkAvailable;
    private JTextField searchField;

    // Action buttons
    private JButton btnAdd, btnEdit, btnDelete, btnClear, btnSearch, btnClearSearch;

    private Room selectedRoom = null;

    public RoomPanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        super(rm, gm, resM, sm);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader("Room Management", "R"), BorderLayout.NORTH);

        // ── CENTER: Table + Form ────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout());

        // LEFT: Search bar + Table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UIConstants.COLOR_BG_PANEL);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 8));

        // Search bar
        searchField = UIHelper.createTextField("");
        JButton searchBtn = UIHelper.createPrimaryButton("Search");
        JButton clearSearchBtn = UIHelper.createSecondaryButton("Clear");
        searchBtn.addActionListener(e -> performSearch());
        clearSearchBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        searchField.addActionListener(e -> performSearch());

        leftPanel.add(buildSearchBar(searchField, searchBtn, clearSearchBtn), BorderLayout.NORTH);

        // Table
        String[] cols = {"Room ID", "Type", "Floor", "Price/Night", "Actual Price", "Available"};
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

        // RIGHT: Form panel in scroll pane
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
        JPanel panel = buildFormCard("Room Details");

        // Form grid
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtRoomId   = UIHelper.createTextField("");
        cbRoomType  = UIHelper.createComboBox(new String[]{"Standard", "Deluxe", "Suite"});
        txtBasePrice= UIHelper.createTextField("");
        txtFloor    = UIHelper.createTextField("");
        chkAvailable= new JCheckBox("Available");
        chkAvailable.setOpaque(false);
        chkAvailable.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        chkAvailable.setFont(UIConstants.FONT_BODY);
        chkAvailable.setSelected(true);

        addFormRow(form, gbc, 0, "Room ID *",       txtRoomId);
        addFormRow(form, gbc, 1, "Room Type *",     cbRoomType);
        addFormRow(form, gbc, 2, "Base Price ($) *", txtBasePrice);
        addFormRow(form, gbc, 3, "Floor",           txtFloor);
        addFormRow(form, gbc, 4, "Status",          chkAvailable);

        // Pricing note label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("<html><i>Actual price: Standard=base, Deluxe=base×1.5, Suite=base×2.2</i></html>");
        noteLabel.setFont(UIConstants.FONT_SMALL);
        noteLabel.setForeground(UIConstants.COLOR_TEXT_MUTED);
        form.add(noteLabel, gbc);
        gbc.gridwidth = 1;

        panel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        btnPanel.setOpaque(false);

        btnAdd    = UIHelper.createSuccessButton("+ Add Room");
        btnEdit   = UIHelper.createWarningButton("\u270E Update"); // Pencil
        btnDelete = UIHelper.createDangerButton("\u00D7 Delete"); // Multiply
        btnClear  = UIHelper.createSecondaryButton("\u21BA Clear Form"); // Circular arrow

        btnAdd.addActionListener(e -> addRoom());
        btnEdit.addActionListener(e -> editRoom());
        btnDelete.addActionListener(e -> deleteRoom());
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

    private void addRoom() {
        // Input validation
        String id    = txtRoomId.getText().trim();
        String type  = (String) cbRoomType.getSelectedItem();
        String price = txtBasePrice.getText().trim();
        String floor = txtFloor.getText().trim();

        if (id.isEmpty()) { UIHelper.showError(this, "Room ID is required!"); return; }
        if (price.isEmpty()) { UIHelper.showError(this, "Base price is required!"); return; }

        double basePrice;
        int floorNum = 1;
        try { basePrice = Double.parseDouble(price); if (basePrice <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { UIHelper.showError(this, "Price must be a positive number!"); return; }
        try { if (!floor.isEmpty()) floorNum = Integer.parseInt(floor); }
        catch (NumberFormatException ex) { UIHelper.showError(this, "Floor must be a whole number!"); return; }

        Room room = createRoomByType(id, type, basePrice, chkAvailable.isSelected(), floorNum);
        if (!roomManager.addRoom(room)) {
            UIHelper.showError(this, "Room ID '" + id + "' already exists!");
            return;
        }
        refreshTable();
        clearForm();
        UIHelper.showInfo(this, "Room added successfully!");
    }

    private void editRoom() {
        if (selectedRoom == null) return;
        String type  = (String) cbRoomType.getSelectedItem();
        String price = txtBasePrice.getText().trim();
        String floor = txtFloor.getText().trim();

        if (price.isEmpty()) { UIHelper.showError(this, "Base price is required!"); return; }
        double basePrice;
        int floorNum = selectedRoom.getFloor();
        try { basePrice = Double.parseDouble(price); if (basePrice <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { UIHelper.showError(this, "Price must be a positive number!"); return; }
        try { if (!floor.isEmpty()) floorNum = Integer.parseInt(floor); }
        catch (NumberFormatException ex) { UIHelper.showError(this, "Floor must be a whole number!"); return; }

        Room updated = createRoomByType(selectedRoom.getRoomId(), type, basePrice,
                                        chkAvailable.isSelected(), floorNum);
        roomManager.updateRoom(updated);
        refreshTable();
        clearForm();
        UIHelper.showInfo(this, "Room updated successfully!");
    }

    private Room createRoomByType(String id, String type, double base, boolean avail, int floor) {
        switch (type) {
            case "Deluxe": return new DeluxeRoom(id, base, avail, floor);
            case "Suite":  return new SuiteRoom(id, base, avail, floor);
            default:       return new StandardRoom(id, base, avail, floor);
        }
    }

    private void deleteRoom() {
        if (selectedRoom == null) return;
        if (!UIHelper.showConfirm(this, "Delete room " + selectedRoom.getRoomId() + "?")) return;
        roomManager.deleteRoom(selectedRoom.getRoomId());
        refreshTable();
        clearForm();
    }

    private void performSearch() {
        String kw = searchField.getText().trim();
        currentRooms = roomManager.searchRooms(kw);
        populateTable(currentRooms);
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || currentRooms == null || row >= currentRooms.size()) return;
        selectedRoom = currentRooms.get(row);
        txtRoomId.setText(selectedRoom.getRoomId());
        txtRoomId.setEditable(false);
        cbRoomType.setSelectedItem(selectedRoom.getRoomType());
        txtBasePrice.setText(String.valueOf(selectedRoom.getBasePricePerNight()));
        txtFloor.setText(String.valueOf(selectedRoom.getFloor()));
        chkAvailable.setSelected(selectedRoom.isAvailable());
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAdd.setEnabled(false);
    }

    /** Polymorphism: overrides BasePanel.refreshTable() */
    @Override
    public void refreshTable() {
        currentRooms = roomManager.getAllRooms();
        populateTable(currentRooms);
    }

    private void populateTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                r.getRoomId(),
                r.getRoomType(),
                r.getFloor(),
                String.format("$%.0f", r.getBasePricePerNight()),
                String.format("$%.0f", r.calculatePricePerNight()),
                r.isAvailable() ? "[ Y ] Available" : "[ N ] Occupied"
            });
        }
    }

    /** Polymorphism: overrides BasePanel.clearForm() */
    @Override
    public void clearForm() {
        selectedRoom = null;
        txtRoomId.setText(""); txtRoomId.setEditable(true);
        txtBasePrice.setText(""); txtFloor.setText("");
        cbRoomType.setSelectedIndex(0);
        chkAvailable.setSelected(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(true);
        table.clearSelection();
    }
}
