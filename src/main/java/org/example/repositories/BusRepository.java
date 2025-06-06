package org.example.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entities.Bus;

import java.util.List;

public class BusRepository {

    private final EntityManager em;

    public BusRepository(EntityManager em) {
        this.em = em;
    }

    public List<Bus> findAll() {
        TypedQuery<Bus> query = em.createQuery("SELECT b FROM Bus b", Bus.class);
        return query.getResultList();
    }

    public Bus findById(int id) {
        return em.find(Bus.class, id);
    }
}
