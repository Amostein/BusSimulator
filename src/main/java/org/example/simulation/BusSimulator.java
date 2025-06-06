package org.example.simulation;

import org.example.entities.*;

import java.util.List;

public class BusSimulator {
    private final Route route;
    private final List<RouteStation> stations;
    private int currentIndex = 0;
    private double t = 0.0;
    private final double speed = 0.02;
    private long pauseTimeLeft = 0;
    private long lastUpdateTime = System.currentTimeMillis();
    private volatile boolean paused = false;
    private boolean forward = true;
    private final int busNumber;

    public BusSimulator(Route route, List<RouteStation> stations, int busNumber) {
        this.route = route;
        this.stations = stations;
        this.busNumber = busNumber;
    }

    public void updatePosition() {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;

        if (pauseTimeLeft > 0) {
            pauseTimeLeft -= delta;
            return;
        }

        t += speed;
        if (t >= 1.0) {
            t = 0.0;

            boolean atEnd = (forward && currentIndex == stations.size() - 2)
                    || (!forward && currentIndex == 1);

            if (forward) {
                if (currentIndex < stations.size() - 2) {
                    currentIndex++;
                    pauseTimeLeft = 1000;
                } else if (atEnd) {
                    currentIndex++;
                    pauseTimeLeft = 3000;
                } else {
                    forward = false;
                    pauseTimeLeft = 0;
                }
            } else {
                if (currentIndex > 1) {
                    currentIndex--;
                    pauseTimeLeft = 1000;
                } else if (atEnd) {
                    currentIndex--;
                    pauseTimeLeft = 3000;
                } else {
                    forward = true;
                    pauseTimeLeft = 0;
                }
            }
        }
    }

    public int getX() {
        int nextIndex;
        if (forward) {
            nextIndex = currentIndex + 1;
        } else {
            nextIndex = currentIndex - 1;
        }
        if (nextIndex < 0 || nextIndex >= stations.size()) {
            return stations.get(currentIndex).getStation().getX();
        }
        Station s1 = stations.get(currentIndex).getStation();
        Station s2 = stations.get(nextIndex).getStation();
        return (int) (s1.getX() * (1 - t) + s2.getX() * t);
    }

    public int getY() {
        int nextIndex;
        if (forward) {
            nextIndex = currentIndex + 1;
        } else {
            nextIndex = currentIndex - 1;
        }
        if (nextIndex < 0 || nextIndex >= stations.size()) {
            return stations.get(currentIndex).getStation().getY();
        }
        Station s1 = stations.get(currentIndex).getStation();
        Station s2 = stations.get(nextIndex).getStation();
        return (int) (s1.getY() * (1 - t) + s2.getY() * t);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getBusNumber() {
        return busNumber;
    }

    public boolean isStoppedAtStation(Station station) {
        return pauseTimeLeft > 0 && getCurrentStation().equals(station);
    }

    public Station getCurrentStation() {
        return stations.get(currentIndex).getStation();
    }
}
