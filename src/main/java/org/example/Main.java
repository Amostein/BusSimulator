package org.example;

import org.example.Window.*;
import org.example.entities.DatabasePopulate;
import org.example.query.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        //DatabasePopulate.populateDB();
        MainWindow.launchUI();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bus_simulator");
        EntityManager em = emf.createEntityManager();

        SwingUtilities.invokeLater(() -> {
            new QueryMenuWindow(em);
        });
    }
}
