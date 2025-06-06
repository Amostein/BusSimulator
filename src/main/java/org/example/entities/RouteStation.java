package org.example.entities;

import jakarta.persistence.*;

@Entity
public class RouteStation {

    @EmbeddedId
    private RouteStationId id;

    @ManyToOne
    @MapsId("routeId")
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne
    @MapsId("stationId")
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "station_order")
    private int stationOrder;

    @Column(name = "travel_time") // în minute, de exemplu
    private int travelTime;


    public RouteStation() {
    }

    public RouteStation(Route route, Station station, int order) {
        this.route = route;
        this.station = station;
        this.stationOrder = order;
        this.id = new RouteStationId(route.getId(), station.getId());
    }

    public RouteStationId getId() {
        return id;
    }

    public void setId(RouteStationId id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getStationOrder() {
        return stationOrder;
    }

    public void setStationOrder(int stationOrder) {
        this.stationOrder = stationOrder;
    }

    public int getTravelTime() {
        return travelTime;
    }
    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    @Override
    public String toString() {
        return "RouteStation{" +
                "route=" + route.getId() +
                ", station=" + station.getName() +
                ", order=" + stationOrder +
                '}';
    }
}
