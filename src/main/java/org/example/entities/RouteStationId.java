package org.example.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RouteStationId implements Serializable {
    private int routeId;
    private int stationId;

    public RouteStationId() {
    }

    public RouteStationId(int routeId, int stationId) {
        this.routeId = routeId;
        this.stationId = stationId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteStationId that = (RouteStationId) o;
        return routeId == that.routeId && stationId == that.stationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, stationId);
    }
}
