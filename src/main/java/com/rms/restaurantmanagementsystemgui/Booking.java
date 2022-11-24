package com.rms.restaurantmanagementsystemgui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Booking {
    private String name;
    private LocalDateTime dateTime;
    private int noOfPeople;

    public void newBooking(String name, int noOfPeople) {
        this.name = name;
        this.dateTime = LocalDateTime.now();
        this.noOfPeople = noOfPeople;

    }

    public String getName() {
        return name;
    }

    public int getNoOfPeople() {
        return noOfPeople;
    }

}
