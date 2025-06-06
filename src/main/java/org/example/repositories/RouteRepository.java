package org.example.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entities.Route;

import java.util.List;

public class RouteRepository {
    private final EntityManager em;

    public RouteRepository(EntityManager em) {
        this.em = em;
    }

    public List<Route> findAll() {
        TypedQuery<Route> query = em.createQuery("SELECT r FROM Route r", Route.class);
        return query.getResultList();
    }

    public Route findById(int id) {
        return em.find(Route.class, id);
    }
}
