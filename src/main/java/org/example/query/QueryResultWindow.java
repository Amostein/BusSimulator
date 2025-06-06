package org.example.query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QueryResultWindow extends JFrame {
    public QueryResultWindow(String title, DefaultTableModel model) {
        super(title);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
