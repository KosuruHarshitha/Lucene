package com.howtodoinjava.demo.lucene.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//// This class is for modifying the JList cell element properties
public class CustomListCellRenderer extends DefaultListCellRenderer  {
    private static final int HORIZONTAL_PADDING = 5;
    private static final int VERTICAL_PADDING = 5;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(new EmptyBorder(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
        return label;
    }
}