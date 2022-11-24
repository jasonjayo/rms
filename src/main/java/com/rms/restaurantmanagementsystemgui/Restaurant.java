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
 * stores information about a single restaurant in the chain
 *
 * @author Jason Gill
 */
public class Restaurant {

    private final int id;
    private final String name;
    private final Menu menu;
    private final ArrayList<Table> tables;
    private final ArrayList<Reservation> reservations;
    private final ArrayList<Chef> chefs;
    private final ArrayList<Waiter> waiters;
    private final ArrayList<Order> orders;

    LocalTime openTime = LocalTime.of(12, 0);
    LocalTime closeTime = LocalTime.of(21, 0);

    /**
     * create a new restaurant object
     *
     * @param name             String restaurant name
     * @param id               int unique restaurant id
     * @param tables           array list of tables
     * @param menu             menu for restaurant
     * @param customers        customers array list
     * @param reservations     reservations array list
     * @param chefs            chefs array list
     * @param waiters          waiters array list
     * @param incompleteOrders array list of incomplete order (i.e., unpaid orders)
     */
    public Restaurant(String name, int id, ArrayList<Table> tables, Menu menu, HashMap<Integer, Customer> customers, ArrayList<Reservation> reservations, ArrayList<Chef> chefs, ArrayList<Waiter> waiters, ArrayList<Order> incompleteOrders) {
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.reservations = reservations;
        this.chefs = chefs;
        this.waiters = waiters;
        this.tables = tables;
        this.orders = incompleteOrders;
    }

    /**
     * returns restaurant name
     *
     * @return String restaurant name
     */
    public String getName() {
        return name;
    }

    /**
     * returns restaurant id
     *
     * @return int restaurant id
     */
    public int getId() {
        return id;
    }

    /**
     * returns tables in this restaurant
     *
     * @return array list of tables
     */
    public ArrayList<Table> getTables() {
        return tables;
    }

    /**
     * returns available start times based on restaurant opening and closing times
     *
     * @return list of strings containing available start times (e.g., 14:00)
     */
    public List<String> getAvailableStartTimes() {
        List<String> startTimes = new ArrayList<>();
        for (int i = openTime.getHour(); i + 2 <= closeTime.getHour(); i++) {
            startTimes.add(i + ":00");
        }
        return startTimes;
    }

    /**
     * returns list of tables available for given date and time and with suitable capacity
     *
     * @param dateTime    LocalDateTime date time of reservation
     * @param numOfPeople int number of people in reservation
     * @return list of suitable tables
     */
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

    /**
     * creates reservation for this restaurant
     *
     * @param t           table
     * @param numOfPeople int number of people
     * @param customerId  int customer id of customer making reservation
     * @param startTime   LocalDateTime start time
     * @return String summary of reservation
     */
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

    /**
     * returns restaurant menu
     *
     * @return Menu
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * creates a customer
     *
     * @param name     String customer name
     * @param phoneNum String customer phone number
     * @return customer id for newly created customer
     */
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

    /**
     * creates order for a table
     *
     * @param tableId int table id indicating for which table this order is for
     * @return the created order
     */
    public Order createOrder(int tableId) {
        Order o = new Order(tableId, id);
        orders.add(o);
        return o;
    }

    /**
     * cancel an existing order
     *
     * @param o the order to cancel
     */
    public void cancelOrder(Order o) {
        orders.remove(o);
    }

    /**
     * returns list of outstanding order (i.e., unpaid orders)
     *
     * @return array list of orders
     */
    public ArrayList<Order> getOutstandingOrders() {
        ArrayList<Order> outstandingOrders = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() != Order.Status.PAID) {
                outstandingOrders.add(o);
            }
        }
        return outstandingOrders;
    }

    /**
     * returns list of this restaurant's chefs
     *
     * @return array list of chefs
     */
    public ArrayList<Chef> getChefs() {
        return chefs;
    }

    /**
     * cancel a reservation
     *
     * @param customerId int customer id
     * @param dateTime   int start date time
     * @return boolean indicating whether reservation was successfully cancelled
     */
    public boolean cancelReservation(int customerId, LocalDateTime dateTime) {
        for (Reservation r : reservations) {
            if (r.getCustomerId() == customerId && r.getStartTime().isEqual(dateTime)) {
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

    /**
     * returns string representation of restaurant
     *
     * @return String restaurant name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * returns list of reservations
     *
     * @return array list of reservations
     */
    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    /**
     * attempt to log in a user into this restaurant with the given credentials
     *
     * @param username String username
     * @param password String password
     * @return boolean indicating whether user has successfully been logged in
     */
    public boolean login(String username, String password) {
        ArrayList<Staff> staff = new ArrayList<>();
        staff.addAll(chefs);
        staff.addAll(waiters);
        return staff.stream().anyMatch(s -> (s.getName().equals(username) && s.getPassword().equals(password)));
    }
}