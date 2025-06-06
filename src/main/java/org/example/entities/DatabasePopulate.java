package org.example.entities;
import jakarta.persistence.*;
import java.util.List;
import java.util.Arrays;
/*
CREATE TABLE BusModel (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE Route (
    id INT NOT NULL PRIMARY KEY,
    number INT NOT NULL
);

CREATE TABLE Station (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL
);

CREATE TABLE RouteStation (
    route_id INT NOT NULL,
    station_id INT NOT NULL,
    station_order INT NOT NULL,
    travel_time INT NOT NULL,
    PRIMARY KEY (route_id, station_order),
    FOREIGN KEY (route_id) REFERENCES Route(id),
    FOREIGN KEY (station_id) REFERENCES Station(id)
);

CREATE TABLE Bus (
    id INT NOT NULL PRIMARY KEY,
    number INT NOT NULL,
    model_id INT NOT NULL,
    route_id INT NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    FOREIGN KEY (model_id) REFERENCES BusModel(id),
    FOREIGN KEY (route_id) REFERENCES Route(id)
);

 */

/*
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS RouteStation;
DROP TABLE IF EXISTS Bus;
DROP TABLE IF EXISTS Station;
DROP TABLE IF EXISTS Route;
DROP TABLE IF EXISTS BusModel;

SET FOREIGN_KEY_CHECKS = 1;

 */
public class DatabasePopulate {
    public static void populateDB() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bus_simulator");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Station A = new Station(1, "A", 200, 200);
        Station B = new Station(2, "B", 200, 300);
        Station C = new Station(3, "C", 300, 300);
        Station D = new Station(4, "D", 400, 300);
        Station E = new Station(5, "E", 400, 400);
        Station F = new Station(6, "F", 400, 500);
        Station G = new Station(7, "G", 500, 300);
        Station H = new Station(8, "H", 600, 300);
        Station I = new Station(9, "I", 500, 200);
        Station J = new Station(10, "J", 600, 200);

        List<Station> stations = Arrays.asList(A, B, C, D, E, F, G, H, I, J);
        stations.forEach(em::persist);

        Route route1 = new Route(1, 101); // Linie 101
        Route route2 = new Route(2, 102); // Linie 102
        Route route3 = new Route(3, 103); // Linie 103

        em.persist(route1);
        em.persist(route2);
        em.persist(route3);

        List<RouteStation> routeStations = Arrays.asList(
                new RouteStation(route1, A, 1),
                new RouteStation(route1, B, 2),
                new RouteStation(route1, C, 3),
                new RouteStation(route1, D, 4),

                new RouteStation(route2, I, 1),
                new RouteStation(route2, G, 2),
                new RouteStation(route2, D, 3),
                new RouteStation(route2, E, 4),
                new RouteStation(route2, F, 5),

                new RouteStation(route3, J, 1),
                new RouteStation(route3, H, 2),
                new RouteStation(route3, G, 3),
                new RouteStation(route3, D, 4),
                new RouteStation(route3, C, 5)
        );
        routeStations.forEach(em::persist);

        BusModel model = new BusModel(1, "Solaris", 40);
        em.persist(model);

        Bus bus1 = new Bus(1, 101, model, route1);
        Bus bus2 = new Bus(2, 102, model, route2);
        Bus bus3 = new Bus(3, 103, model, route3);
        em.persist(bus1);
        em.persist(bus2);
        em.persist(bus3);

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Datele au fost salvate cu succes!");
    }
}
