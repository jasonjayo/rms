package com.rms.restaurantmanagementsystemgui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Jason Gill
 */
public class Restaurant {

    private int id;
    private String name;
    private Menu menu;
    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private ArrayList<Chef> chefs = new ArrayList<>();
    private ArrayList<Waiter> waiters = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private HashMap<Integer, Customer> customers;

    LocalTime openTime = LocalTime.of(12, 0);
    LocalTime closeTime = LocalTime.of(21, 0);

    public Restaurant(String name, int id, ArrayList<Table> tables, Menu menu, HashMap<Integer, Customer> customers, ArrayList<Reservation> reservations, ArrayList<Chef> chefs, ArrayList<Waiter> waiters, ArrayList<Order> incompleteOrders) {
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.customers = customers;
        this.reservations = reservations;
        this.chefs = chefs;
        this.waiters = waiters;
        this.tables = tables;
        this.orders = incompleteOrders;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public List<String> getAvailableStartTimes() {
        List<String> startTimes = new ArrayList<>();
        for (int i = openTime.getHour(); i + 2 <= closeTime.getHour(); i++) {
            startTimes.add(i + ":00");
        }
        return startTimes;
    }

    public List<Table> getAvailableTables(LocalDateTime dateTime, int numOfPeople) {
        ArrayList<Table> availableTablesThisHour = new ArrayList<>(tables);
        for (Table t : tables) {
            if (t.getCapacity() < numOfPeople) {
                availableTablesThisHour.remove(t);
                continue;
            }
            for (Reservation r : reservations) {
                if (r.getTable().equals(t)) {
                    if (r.getStartTime().isBefore(dateTime.plusHours(2)) && dateTime.isBefore(r.getEndTime())) {
                        availableTablesThisHour.remove(t);
                    }
                }
            }
        }
        return availableTablesThisHour;
    }

    public String createReservation(Table t, int numOfPeople, int customerId, LocalDateTime startTime) {
        for (Reservation r : reservations) {
            if (r.getStartTime().isBefore(startTime.plusHours(2)) && startTime.isBefore(r.getEndTime()) && r.getCustomerId() == customerId && customerId != 0) {
                return "We couldn't create your reservation because the following reservation already exists for you during the given time: \n" + r;
            }
        }

        Reservation reservation = new Reservation(t, numOfPeople, customerId, startTime, startTime.plusHours(2));
        reservations.add(reservation);


        String reservationForCSV = String.format("%s,%s,%s,%s,%s\n", id, numOfPeople, startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), t.getId(), customerId);

        try {
            Files.write(Paths.get("reservations.csv"), reservationForCSV.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("%s At %s", reservation, name);
    }

    public Menu getMenu() {
        return menu;
    }

    public int createCustomer(String name, String phoneNum) {
        int id = (int) (Math.random() * Instant.now().getEpochSecond());
        try {
            String customerForCSV = String.format("%s,%s,%s\n", id, name, phoneNum);
            Files.write(Paths.get("customers.csv"), customerForCSV.getBytes(), StandardOpenOption.APPEND);
            return id;
        } catch (IOException e) {
            return 0;
        }
    }

    public Order createOrder(int tableId) {
        Order o = new Order(tableId, id);
        orders.add(o);
        return o;
    }

    public void cancelOrder(Order o) {
        orders.remove(o);
    }

    public ArrayList<Order> getOutstandingOrder() {
        ArrayList<Order> outstandingOrders = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() != Order.Status.PAID) {
                outstandingOrders.add(o);
            }
        }
        return outstandingOrders;
    }

    public ArrayList<Chef> getChefs() {
        return chefs;
    }

    public boolean cancelReservation(int customerId, LocalDateTime dateTime) {
        for (Reservation r : reservations) {
            if (r.customerId == customerId && r.getStartTime().isEqual(dateTime)) {
                try {
                    File reservationsFile = new File("reservations.csv");
                    Scanner scanner = new Scanner(reservationsFile);
                    StringBuilder output = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] lineData = line.split(",");

                        int CSV_restaurantId = Integer.parseInt(lineData[0]);
                        LocalDateTime CSV_startTime = LocalDateTime.parse(lineData[2]);
                        int CSV_customerId = Integer.parseInt(lineData[4]);

                        if (!(CSV_startTime.equals(dateTime) && CSV_customerId == customerId && id == CSV_restaurantId)) {
                            output.append(line);
                            output.append("\n");
                        }
                    }
                    Files.write(Paths.get("reservations.csv"), output.toString().getBytes());
                } catch (IOException e) {
                    return false;
                }
                return reservations.remove(r);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public boolean login(String username, String password) {
        ArrayList<Staff> staff = new ArrayList<>();
        staff.addAll(chefs);
        staff.addAll(waiters);
        return staff.stream().anyMatch(s -> (s.getName().equals(username) && s.getPassword().equals(password)));
    }
}