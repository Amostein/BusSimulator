package org.example.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entities.RouteStation;
import org.example.entities.Route;

import java.util.List;

public class RouteStationRepository {
    private final EntityManager em;

    public RouteStationRepository(EntityManager em) {
        this.em = em;
    }

    public List<RouteStation> findByRouteOrdered(Route route) {
        TypedQuery<RouteStation> query = em.createQuery(
                "SELECT rs FROM RouteStation rs WHERE rs.route = :route ORDER BY rs.stationOrder",
                RouteStation.class
        );
        query.setParameter("route", route);
        return query.getResultList();
    }
    public List<RouteStation> findAll() {
        return em.createQuery("SELECT rs FROM RouteStation rs", RouteStation.class).getResultList();
    }

}
