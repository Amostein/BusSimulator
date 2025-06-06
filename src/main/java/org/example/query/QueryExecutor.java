package org.example.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class QueryExecutor {
    public static DefaultTableModel executeQuery(EntityManager em, String sql, String[] columnNames) {
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Object[] row : results) {
            model.addRow(row);
        }
        return model;
    }
}
