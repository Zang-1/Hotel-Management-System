package com.hotel.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * UIConstants — FlatMacDarkLaf Design System.
 */
public class UIConstants {

    // ── Core Color Palette (FlatMacDark colors) ───────────────────────────
    public static final Color COLOR_BG_DARK       = new Color(0x1E1E1E);
    public static final Color COLOR_BG_PANEL      = new Color(0x1E1E1E);
    public static final Color COLOR_SIDEBAR       = new Color(0x252526);
    public static final Color COLOR_CARD          = new Color(0x252526);
    
    // Semantic colors (Professional Mac colors)
    public static final Color COLOR_ACCENT        = new Color(0x0A84FF); // Mac Blue
    public static final Color COLOR_SUCCESS       = new Color(0x34C759); // Mac Green
    public static final Color COLOR_WARNING       = new Color(0xFF9F0A); // Mac Orange
    public static final Color COLOR_DANGER        = new Color(0xFF453A); // Mac Red
    public static final Color COLOR_INFO          = new Color(0x0A84FF); // Mac Blue

    // Text
    public static final Color COLOR_TEXT_PRIMARY  = new Color(0xFFFFFF);
    public static final Color COLOR_TEXT_MUTED    = new Color(0x9E9E9E);

    // Room type colors for charts
    public static final Color COLOR_STANDARD      = new Color(0x64D2FF); // Light Blue
    public static final Color COLOR_DELUXE        = new Color(0xBF5AF2); // Purple
    public static final Color COLOR_SUITE         = new Color(0xFFD60A); // Gold

    // Missing colors from previous version
    public static final Color COLOR_GOLD          = new Color(0xFFD60A);
    public static final Color COLOR_ACCENT_DARK   = new Color(0xB39D00);
    public static final Color COLOR_ACCENT_LIGHT  = new Color(0xFFE55C);
    public static final Color COLOR_BORDER        = new Color(0x38383A); // Mac dark border
    public static final Color COLOR_INPUT_BG      = new Color(0x1E1E1E); // Mac dark input
    
    public static final Border BORDER_INPUT       = BorderFactory.createEmptyBorder(4, 10, 4, 10);

    // ── Fonts ─────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD   = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_STAT_NUMBER  = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_INPUT        = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Sizes ─────────────────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH   = 230;
    public static final int HEADER_HEIGHT   = 60;
    public static final int ROW_HEIGHT      = 32;
    public static final int BUTTON_HEIGHT   = 36;
    public static final int INPUT_HEIGHT    = 36;

    public static void applyGlobalDefaults() {
        // FlatLaf handles all defaults automatically.
        // We only put specific properties here if strictly necessary.
    }
}
