package org.example.algorithm;
import org.example.entities.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
public class PathFinder {
    private final Map<Station, List<Station>> graph;

    public PathFinder(List<RouteStation> routeStations) {
        graph = new HashMap<>();
        Map<Integer, List<RouteStation>> grouped = new HashMap<>();

        for (RouteStation rs : routeStations) {
            grouped.computeIfAbsent(rs.getRoute().getId(), k -> new ArrayList<>()).add(rs);
        }

        for (List<RouteStation> rsList : grouped.values()) {
            rsList.sort(Comparator.comparingInt(RouteStation::getStationOrder));
            for (int i = 0; i < rsList.size() - 1; i++) {
                Station a = rsList.get(i).getStation();
                Station b = rsList.get(i + 1).getStation();
                graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            }
        }
    }

    public List<Station> findPath(Station start, Station goal) {
        Queue<List<Station>> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();
        queue.add(List.of(start));

        while (!queue.isEmpty()) {
            List<Station> path = queue.poll();
            Station last = path.get(path.size() - 1);
            if (last.equals(goal)) return path;

            if (!visited.add(last)) continue;

            for (Station neighbor : graph.getOrDefault(last, List.of())) {
                List<Station> newPath = new ArrayList<>(path);
                newPath.add(neighbor);
                queue.add(newPath);
            }
        }
        return List.of();
    }
}
