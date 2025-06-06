package org.example.entities;
import java.util.List;
import java.util.ArrayList;
import org.example.simulation.*;
public class Passenger {
    private final String name;
    private final Station source;
    private final Station destination;
    private Station currentStation;
    private List<Station> itinerary = new ArrayList<>();
    private int itineraryIndex = 0;
    private boolean justAdvanced = false;
    private boolean inBus = false;
    private BusSimulator currentBus = null;

    public Passenger(String name, Station source, Station destination) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.currentStation = source;
    }

    public void setItinerary(List<Station> itinerary) {
        this.itinerary = itinerary;
        this.itineraryIndex = 0;
    }

    public Station getNextStation() {
        if (itineraryIndex + 1 < itinerary.size()) {
            return itinerary.get(itineraryIndex + 1);
        }
        return null;
    }

    public void advance() {
        if (itineraryIndex + 1 < itinerary.size()) {
            itineraryIndex++;
            currentStation = itinerary.get(itineraryIndex);
        }
    }

    public boolean hasArrived() {
        return currentStation.equals(destination);
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public String getName() {
        return name;
    }

    public Station getDestination() {
        return destination;
    }
    public boolean isJustAdvanced() {
        return justAdvanced;
    }

    public void setJustAdvanced(boolean val) {
        justAdvanced = val;
    }
    public boolean isInBus() {
        return inBus;
    }

    public void setInBus(boolean inBus) {
        this.inBus = inBus;
    }

    public BusSimulator getCurrentBus() {
        return currentBus;
    }
    public void setCurrentBus(BusSimulator bus) {
        this.currentBus = bus;
    }
}
