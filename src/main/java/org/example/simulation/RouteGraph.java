package org.example.simulation;

import org.example.entities.*;

import jakarta.persistence.EntityManager;
import java.util.*;

public class RouteGraph {
    public static class Edge {
        public final Station to;
        public final int time; // în minute

        public Edge(Station to, int time) {
            this.to = to;
            this.time = time;
        }
    }

    private final Map<Station, List<Edge>> graph = new HashMap<>();

    public RouteGraph(EntityManager em, int routeId) {
        List<RouteStation> routeStations = em.createQuery(
                        "SELECT rs FROM RouteStation rs WHERE rs.route.id = :id ORDER BY rs.stationOrder", RouteStation.class)
                .setParameter("id", routeId)
                .getResultList();

        for (int i = 0; i < routeStations.size() - 1; i++) {
            Station from = routeStations.get(i).getStation();
            Station to = routeStations.get(i + 1).getStation();
            int time = routeStations.get(i + 1).getTravelTime();

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, time));
        }
    }

    public Map<Station, List<Edge>> getGraph() {
        return graph;
    }
}
