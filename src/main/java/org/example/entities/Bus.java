package org.example.entities;

import jakarta.persistence.*;

@Entity
public class Bus {
    @Id
    private int id;

    private int number;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private BusModel model;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private int x;
    private int y;

    public Bus() {}

    public Bus(int id, int number, BusModel model, Route route) {
        this.id = id;
        this.number = number;
        this.model = model;
        this.route = route;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
}
