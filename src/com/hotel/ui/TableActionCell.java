package com.hotel.ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;

public class TableActionCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    
    public interface TableActionCallback {
        JPanel getActionPanel(int row);
    }
    
    private final TableActionCallback callback;
    private JPanel currentEditorPanel;
    
    public TableActionCell(TableActionCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = callback.getActionPanel(row);
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentEditorPanel = callback.getActionPanel(row);
        currentEditorPanel.setBackground(table.getSelectionBackground());
        return currentEditorPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
    
    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }
}
