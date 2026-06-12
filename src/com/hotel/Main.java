package com.hotel;

import com.hotel.ui.LoginFrame;
import com.hotel.ui.UIConstants;
import javax.swing.*;

/**
 * Main entry point for the Hotel Reservation Management System.
 * Demonstrates: SwingUtilities.invokeLater for thread safety.
 */
public class Main {
    public static void main(String[] args) {
        // Apply system look and feel then override with custom theme
        try {
            com.formdev.flatlaf.themes.FlatMacDarkLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            // Minimal UI defaults application since FlatLaf handles most of it beautifully
            UIConstants.applyGlobalDefaults();
        } catch (Exception e) {
            System.err.println("Could not set FlatLaf: " + e.getMessage());
        }

        // Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
