    package org.example.entities;
    import jakarta.persistence.*;
    import java.util.ArrayList;
    import java.util.List;
    @Entity
    public class Route {
        @Id
        private int id;
        private int number;

        @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
        private List<RouteStation> routeStations;

        @OneToMany(mappedBy = "route")
        private List<Bus> buses = new ArrayList<>();

        public Route() {}
        public Route(int id, int number) {
            this.id = id;
            this.number = number;
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
        public List<RouteStation> getStations() {
            return routeStations;
        }
        public ArrayList<Bus> getBuses() {
            return getBuses();
        }
        public void setBuses(List<Bus> buses) {
            this.buses = buses;
        }
    }
