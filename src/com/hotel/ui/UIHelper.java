package com.hotel.ui;

import javax.swing.*;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;

/**
 * UIHelper — Factory methods for FlatLaf standard components.
 */
public class UIHelper {

    // ── Buttons ───────────────────────────────────────────────────────────

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(UIConstants.COLOR_INFO);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(UIConstants.COLOR_DANGER);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(UIConstants.COLOR_SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    public static JButton createWarningButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(UIConstants.COLOR_WARNING);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    public static JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "borderless");
        btn.setPreferredSize(new Dimension(120, UIConstants.BUTTON_HEIGHT));
        return btn;
    }

    // ── Table Action Buttons ───────────────────────────────────────────────
    
    public static JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "outlined");
        btn.setForeground(color);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(2, 8, 2, 8));
        return btn;
    }

    // ── Form Inputs ────────────────────────────────────────────────────────

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        return field;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        return combo;
    }

    // ── Labels ─────────────────────────────────────────────────────────────

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY);
        return lbl;
    }

    public static JLabel createSectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_SUBTITLE);
        lbl.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        return lbl;
    }

    public static void applyCalendarDarkTheme(DatePickerSettings settings) {
        settings.setColor(DateArea.BackgroundOverallCalendarPanel, UIConstants.COLOR_CARD);
        
        settings.setColor(DateArea.BackgroundMonthAndYearMenuLabels, UIConstants.COLOR_CARD);
        settings.setColor(DateArea.TextMonthAndYearMenuLabels, Color.WHITE);
        
        settings.setColor(DateArea.CalendarBackgroundNormalDates, UIConstants.COLOR_BG_DARK);
        settings.setColor(DateArea.CalendarTextNormalDates, UIConstants.COLOR_TEXT_PRIMARY);
        
        // settings.setColor(DateArea.CalendarBackgroundWeekdays, UIConstants.COLOR_CARD);
        // settings.setColor(DateArea.CalendarTextWeekdays, UIConstants.COLOR_TEXT_MUTED);
        
        settings.setColor(DateArea.BackgroundMonthAndYearNavigationButtons, UIConstants.COLOR_CARD);
        settings.setColor(DateArea.TextMonthAndYearNavigationButtons, Color.WHITE);
        
        settings.setColor(DateArea.BackgroundTopLeftLabelAboveWeekNumbers, UIConstants.COLOR_CARD);
        
        settings.setColor(DateArea.BackgroundTodayLabel, UIConstants.COLOR_CARD);
        settings.setColor(DateArea.TextTodayLabel, UIConstants.COLOR_ACCENT);
        
        settings.setColor(DateArea.BackgroundClearLabel, UIConstants.COLOR_CARD);
        settings.setColor(DateArea.TextClearLabel, UIConstants.COLOR_DANGER);
        
        settings.setColor(DateArea.CalendarBackgroundSelectedDate, UIConstants.COLOR_ACCENT);
        // settings.setColor(DateArea.CalendarTextSelectedDate, Color.WHITE);
    }

    public static JPanel createBadge(String text, Color bg) {
        JPanel badgePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badgePanel.setOpaque(false);
        
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(UIConstants.FONT_SMALL_BOLD);
        badgePanel.add(lbl, BorderLayout.CENTER);
        
        return badgePanel;
    }

    // ── Panels ─────────────────────────────────────────────────────────────

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.COLOR_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.putClientProperty("FlatLaf.style", "arc: 12");
        return card;
    }

    public static JSeparator createSeparator() {
        return new JSeparator();
    }

    // ── Table ──────────────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setRowHeight(UIConstants.ROW_HEIGHT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
    }

    public static JScrollPane createScrollPane(JTable table) {
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    // ── Dialogs ────────────────────────────────────────────────────────────

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
            parent, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
