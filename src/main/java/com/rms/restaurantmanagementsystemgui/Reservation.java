package com.rms.restaurantmanagementsystemgui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * holds information about a customer's reservation
 *
 * @author Jason Gill
 */
public class Reservation {


    private final Table table;
    private final int numOfPeople;
    private final int customerId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * creates a reservation given the details
     *
     * @param t           Table
     * @param numOfPeople int number of people
     * @param customerId  int customer id
     * @param startTime   LocalDateTime start time
     * @param endTime     LocalDateTime end time
     */
    public Reservation(Table t, int numOfPeople, int customerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.table = t;
        this.numOfPeople = numOfPeople;
        this.customerId = customerId;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    /**
     * returns start date and time of reservation
     *
     * @return LocalDateTime
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * returns end date and time of reservation
     *
     * @return LocalDateTime
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * returns number of people in reservation
     *
     * @return int number of people
     */
    public int getNumOfPeople() {
        return numOfPeople;
    }

    /**
     * returns table associated with this reservation
     *
     * @return Table
     */
    public Table getTable() {
        return table;
    }

    /**
     * returns customer id of customer who created reservation
     *
     * @return int customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * indicates whether reservation is a walk-in booking
     *
     * @return boolean
     */
    public boolean isWalkIn() {
        return customerId == 0;
    }

    /**
     * returns summary string of reservation
     *
     * @return String
     */
    public String toString() {

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d u");

        return String.format("From %s until %s on %s for %d people. Table %d.", startTime.format(timeFormat), startTime.plusHours(2).format(timeFormat), startTime.format(dateFormat), numOfPeople, table.getId());
    }
}
