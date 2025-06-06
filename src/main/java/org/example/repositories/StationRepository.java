package org.example.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entities.Station;

import java.util.List;

public class StationRepository {

    private static List<Station> stations;

    public static void loadStations(EntityManager em) {
        TypedQuery<Station> query = em.createQuery("SELECT s FROM Station s", Station.class);
        stations = query.getResultList();
    }

    public static List<Station> getStations() {
        return stations;
    }
}
