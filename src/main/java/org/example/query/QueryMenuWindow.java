package org.example.query;

import jakarta.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryMenuWindow extends JFrame {

    public QueryMenuWindow(EntityManager em) {
        setTitle("Interogari SQL de prezentare");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JButton query1 = new JButton("Rute");
        JButton query2 = new JButton("Autobuze si rute");
        JButton query3 = new JButton("Timp total per ruta");

        add(query1);
        add(query2);
        add(query3);

        query1.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Introdu numarul rutei:");
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            try {
                int ruta = Integer.parseInt(input.trim());

                String sql =
                        "SELECT r.number AS ruta, s.name AS statie, rs.station_order, rs.travel_time " +
                                "FROM RouteStation rs " + "JOIN Route r ON r.id = rs.route_id " +
                                "JOIN Station s ON s.id = rs.station_id " + "WHERE r.number = " + ruta + " " +
                                "ORDER BY rs.station_order";

                String[] columns = {"Ruta", "Statie", "Ordine", "Timp"};
                DefaultTableModel model = QueryExecutor.executeQuery(em, sql, columns);
                new QueryResultWindow("Statii pe ruta " + ruta, model);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numar invalid!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        query2.addActionListener(e -> {
            String sql =
                    "SELECT b.number AS autobuz, bm.name AS model, r.number AS ruta " +
                            "FROM Bus b " + "JOIN BusModel bm ON bm.id = b.model_id " +
                            "JOIN Route r ON r.id = b.route_id " + "ORDER BY b.number";

            String[] columns = {"Autobuz", "Model", "Ruta"};
            DefaultTableModel model = QueryExecutor.executeQuery(em, sql, columns);
            new QueryResultWindow("Autobuze si rute", model);
        });

        query3.addActionListener(e -> {
            String sql =
                    "SELECT r.number AS ruta, SUM(rs.travel_time) AS timp_total " +
                            "FROM RouteStation rs " + "JOIN Route r ON r.id = rs.route_id " +
                            "GROUP BY r.number " + "ORDER BY timp_total ASC";

            String[] columns = {"Ruta", "Timp total"};
            DefaultTableModel model = QueryExecutor.executeQuery(em, sql, columns);
            new QueryResultWindow("Timp total per ruta", model);
        });

        JButton exitButton = new JButton("Iesire");
        add(exitButton);
        exitButton.addActionListener(e -> {
            dispose();
        });

        JButton deleteModelButton = new JButton("Sterge model autobuz");
        deleteModelButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "ID model de autobuz de sters:");
            if (input == null || input.trim().isEmpty()) return;

            try {
                int modelId = Integer.parseInt(input.trim());

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/public_transport2", "postgres", "1234");
                     PreparedStatement stmt = conn.prepareStatement("SELECT delete_bus_model(?)")) {

                    stmt.setInt(1, modelId);
                    stmt.execute();

                    JOptionPane.showMessageDialog(this,
                            "Modelul a fost sters cu succes.",
                            "Succes", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException ex) {
                String sqlState = ex.getSQLState();

                if ("P0001".equals(sqlState)) {
                    JOptionPane.showMessageDialog(this,
                            "Eroare: exista autobuze care folosesc acest model.",
                            "Eroare logica", JOptionPane.ERROR_MESSAGE);
                } else if ("P0002".equals(sqlState)) {
                    JOptionPane.showMessageDialog(this,
                            "Eroare: modelul specificat nu exista.",
                            "Eroare logica", JOptionPane.ERROR_MESSAGE);
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Eroare SQL:\n" + ex.getMessage(),
                            "Eroare necunoscuta", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "ID invalid!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(deleteModelButton);

        JButton showModelsButton = new JButton("Afiseaza modele autobuz");
        showModelsButton.addActionListener(e -> {
            try {
                String sql = "SELECT id, name, capacity FROM BusModel ORDER BY id";
                String[] columns = {"ID", "Nume model", "Capacitate"};
                DefaultTableModel model = QueryExecutor.executeQuery(em, sql, columns);

                new QueryResultWindow("Modele de autobuz", model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Eroare la interogare:\n" + ex.getMessage(),
                        "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(showModelsButton);

        JButton addStationButton = new JButton("Adauga statie noua");
        addStationButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Introduceti numele statiei:");
            if (name == null || name.trim().isEmpty()) return;

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/public_transport2", "postgres", "1234");
                 Statement stmt1 = conn.createStatement();
                 ResultSet rs = stmt1.executeQuery("SELECT MAX(id) FROM Station")) {

                int nextId = 1;
                if (rs.next()) {
                    nextId = rs.getInt(1) + 1;
                }

                try (PreparedStatement stmt2 = conn.prepareStatement(
                        "INSERT INTO Station(id, name, x, y) VALUES (?, ?, 0, 0)")) {

                    stmt2.setInt(1, nextId);
                    stmt2.setString(2, name.trim());
                    stmt2.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                        "Statia a fost adaugata cu succes (ID: " + nextId + ", coordonate generate automat).",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Eroare la inserare: " + ex.getMessage(),
                        "Eroare SQL", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(addStationButton);

        JButton showStationsButton = new JButton("Afiseaza statiile");
        showStationsButton.addActionListener(e -> {
            String[] columnNames = {"ID", "Nume", "X", "Y"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/public_transport2", "postgres", "1234");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name, x, y FROM Station ORDER BY id")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    model.addRow(new Object[]{id, name, x, y});
                }

                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                JFrame tableFrame = new JFrame("Lista statiilor");
                tableFrame.add(scrollPane);
                tableFrame.setSize(400, 300);
                tableFrame.setLocationRelativeTo(null);
                tableFrame.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Eroare la citirea statiilor:\n" + ex.getMessage(),
                        "Eroare SQL", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(showStationsButton);

        setVisible(true);
    }
}
