package com.hotel.ui;

import com.hotel.manager.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * BasePanel — Abstract base for all content panels (FlatLaf style).
 */
public abstract class BasePanel extends JPanel {

    protected final RoomManager roomManager;
    protected final GuestManager guestManager;
    protected final ReservationManager reservationManager;
    protected final StaffManager staffManager;

    protected BasePanel(RoomManager rm, GuestManager gm, ReservationManager resM, StaffManager sm) {
        this.roomManager        = rm;
        this.guestManager       = gm;
        this.reservationManager = resM;
        this.staffManager       = sm;
        setBackground(UIConstants.COLOR_BG_PANEL);
        setLayout(new BorderLayout(0, 0));
    }

    public abstract void refreshTable();
    public abstract void clearForm();

    /** Clean header for FlatLaf */
    protected JPanel buildHeader(String title, String icon) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftSide.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        leftSide.add(titleLbl);
        
        header.add(leftSide, BorderLayout.WEST);
        return header;
    }

    /** Search bar panel */
    protected JPanel buildSearchBar(JTextField searchField, JButton searchBtn, JButton clearBtn) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        searchField.setPreferredSize(new Dimension(280, UIConstants.INPUT_HEIGHT));
        bar.add(searchField);
        bar.add(searchBtn);
        bar.add(clearBtn);

        return bar;
    }

    /** Form row: label + field */
    protected void addFormRow(JPanel form, GridBagConstraints gbc, int row,
                               String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIConstants.FONT_SMALL_BOLD);
        lbl.setForeground(UIConstants.COLOR_TEXT_MUTED);
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        field.setPreferredSize(new Dimension(220, UIConstants.INPUT_HEIGHT));
        form.add(field, gbc);
    }

    /** Form panel builder (right side card) */
    protected JPanel buildFormCard(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLbl = UIHelper.createSectionHeader(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        return panel;
    }

    /** Button panel with vertical layout */
    protected JPanel buildButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (JButton btn : buttons) {
            if (btn == null) {
                panel.add(Box.createVerticalStrut(4));
                panel.add(UIHelper.createSeparator());
                panel.add(Box.createVerticalStrut(4));
            } else {
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
                btn.setAlignmentX(LEFT_ALIGNMENT);
                panel.add(btn);
                panel.add(Box.createVerticalStrut(8));
            }
        }
        return panel;
    }
}
