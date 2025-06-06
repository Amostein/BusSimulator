package org.example.Window;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entities.*;
import org.example.repositories.*;
import org.example.simulation.BusSimulator;
import org.example.algorithm.*;

import java.awt.BasicStroke;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class MainWindow extends JPanel {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bus_simulator");
    private final EntityManager em;
    private final List<Station> stations;
    private final List<RouteStation> routeStations;
    private final List<BusSimulator> simulators = new ArrayList<>();
    private Timer timer;
    private boolean paused = false;
    private long startTime;
    private Integer highlightedRouteId = null;
    List<Passenger> passengers = new ArrayList<>();

    public MainWindow() {
        em = emf.createEntityManager();

        StationRepository.loadStations(em);
        stations = StationRepository.getStations();

        RouteStationRepository rsRepo = new RouteStationRepository(em);
        routeStations = rsRepo.findAll();

        BusRepository busRepo = new BusRepository(em);
        List<Bus> buses = busRepo.findAll();
        for (Bus bus : buses) {
            List<RouteStation> rsForRoute = rsRepo.findByRouteOrdered(bus.getRoute());
            simulators.add(new BusSimulator(bus.getRoute(), rsForRoute,bus.getNumber()));
        }

        Random random = new Random();

        PathFinder pathFinder = new PathFinder(routeStations);

        for (int i = 0; i < 2; i++) {
            Station source = stations.get(random.nextInt(stations.size()));
            Station dest;
            do {
                dest = stations.get(random.nextInt(stations.size()));
            } while (dest == source);

            Passenger p = new Passenger("Pasager " + (i + 1), source, dest);
            List<Station> path = pathFinder.findPath(source, dest);
            p.setItinerary(path);
            passengers.add(p);
        }

        startTime = System.currentTimeMillis();

        timer = new Timer(50, e -> {
            simulators.forEach(BusSimulator::updatePosition);
            repaint();

            for (Passenger p : passengers) {
                if (p.hasArrived()) continue;

                if (!p.isInBus()) {
                    for (BusSimulator sim : simulators) {
                        if (sim.isStoppedAtStation(p.getCurrentStation())) {
                            p.setInBus(true);
                            p.setCurrentBus(sim);
                            break;
                        }
                    }
                } else {
                    BusSimulator bus = p.getCurrentBus();
                    if (bus != null && bus.isStoppedAtStation(p.getNextStation())) {
                        p.advance();
                        if (p.hasArrived()) {
                            p.setInBus(false);
                            p.setCurrentBus(null);
                        }
                    }
                }
            }
        });

        timer.start();

        setLayout(null);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setBounds(1020, 220, 100, 30);
        add(pauseButton);

        pauseButton.addActionListener(e -> {
            paused = !paused;
            if (paused) {
                timer.stop();
                pauseButton.setText("Resume");
            } else {
                timer.start();
                pauseButton.setText("Pause");
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(1020, 260, 100, 30);
        add(resetButton);

        resetButton.addActionListener(e -> {
            simulators.clear();
            passengers.clear();

            List<Bus> resetBuses = busRepo.findAll();
            for (Bus bus : resetBuses) {
                List<RouteStation> rsForRoute = rsRepo.findByRouteOrdered(bus.getRoute());
                simulators.add(new BusSimulator(bus.getRoute(), rsForRoute, bus.getNumber()));
            }

            for (int i = 0; i < 2; i++) {
                Station source = stations.get(random.nextInt(stations.size()));
                Station dest;
                do {
                    dest = stations.get(random.nextInt(stations.size()));
                } while (dest == source);

                Passenger p = new Passenger("Pasager " + (i + 1), source, dest);
                List<Station> path = pathFinder.findPath(source, dest);
                p.setItinerary(path);
                passengers.add(p);
            }

            startTime = System.currentTimeMillis();
            repaint();
        });


        RouteRepository routeRepo = new RouteRepository(em);
        List<Route> allRoutes = routeRepo.findAll();

        int buttonX = 20;
        for (Route route : allRoutes) {
            JButton routeButton = new JButton("Ruta " + route.getNumber());
            routeButton.setBounds(buttonX, 60, 100, 30);
            buttonX += 110;
            add(routeButton);

            routeButton.addActionListener(e -> {
                if (highlightedRouteId != null && highlightedRouteId.equals(route.getId())) {
                    highlightedRouteId = null;
                } else {
                    highlightedRouteId = route.getId();
                }
                repaint();
            });
        }

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(1020, 300, 100, 30);
        add(exitButton);
        exitButton.addActionListener(e -> {
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
            System.exit(0);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
        }));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.drawLine(1000, 0, 1000, 800);
        g.drawLine(0, 100, 1200, 100);

        g.setColor(Color.RED);
        for (Station s : stations) {
            g.fillRect(s.getX() - 5, s.getY() - 5, 10, 10);
            g.drawString(s.getName(), s.getX() + 10, s.getY());
        }

        Map<Integer, List<RouteStation>> routesGrouped = new HashMap<>();
        for (RouteStation rs : routeStations) {
            routesGrouped.computeIfAbsent(rs.getRoute().getId(), k -> new ArrayList<>()).add(rs);
        }

        Map<Integer, Color> routeColors = Map.of(
                1, Color.BLUE,
                2, Color.GREEN,
                3, Color.ORANGE
        );

        for (Map.Entry<Integer, List<RouteStation>> entry : routesGrouped.entrySet()) {
            int routeId = entry.getKey();
            List<RouteStation> stationsForRoute = entry.getValue();
            stationsForRoute.sort(Comparator.comparingInt(RouteStation::getStationOrder));

            g2.setColor(routeColors.getOrDefault(routeId, Color.GRAY));
            if (highlightedRouteId != null && highlightedRouteId.equals(routeId)) {
                g2.setStroke(new BasicStroke(4));
            } else {
                g2.setStroke(new BasicStroke(1));
            }

            int offset = 1 * routeId;
            for (int i = 0; i < stationsForRoute.size() - 1; i++) {
                Station s1 = stationsForRoute.get(i).getStation();
                Station s2 = stationsForRoute.get(i + 1).getStation();
                g2.drawLine(s1.getX(), s1.getY() + offset, s2.getX(), s2.getY() + offset);
            }
        }
        g2.setStroke(new BasicStroke(1));


        for (BusSimulator sim : simulators) {
            int x = sim.getX();
            int y = sim.getY();
            g.setColor(Color.MAGENTA);
            g.fillOval(x - 6, y - 6, 12, 12);

            g.setColor(Color.BLACK);
            g.drawString("Bus " + sim.getBusNumber(), x + 10, y);
        }


        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        g.setColor(Color.BLACK);
        g.drawString(String.format("Timp: %02d:%02d", minutes, seconds), 1020, 40);

        for (Passenger p : passengers) {
            int x, y;
            if (p.isInBus() && p.getCurrentBus() != null) {
                x = p.getCurrentBus().getX();
                y = p.getCurrentBus().getY();
            } else {
                Station s = p.getCurrentStation();
                x = s.getX();
                y = s.getY();
            }

            g.setColor(Color.CYAN);
            g.fillOval(x - 3, y - 3, 6, 6);
            g.setColor(Color.BLACK);
            g.drawString(p.getName(), x + 6, y - 6);
        }
    }

    public static void launchUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fereastra Principala");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            MainWindow panel = new MainWindow();
            frame.add(panel);

            frame.setVisible(true);
        });
    }
}
