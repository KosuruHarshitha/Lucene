package com.howtodoinjava.demo.lucene.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MultilineListCellRenderer extends JPanel implements ListCellRenderer<String> {
    private JLabel label = new JLabel();
    
    public MultilineListCellRenderer() {
        setLayout(new BorderLayout());
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(5, 5, 5, 5)); // Adjust the padding as needed
        add(label, BorderLayout.CENTER);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setText(value);
        label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        return this;
    }
}
