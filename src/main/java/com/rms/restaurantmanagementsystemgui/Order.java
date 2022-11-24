package com.rms.restaurantmanagementsystemgui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Order {

    enum Status {
        TAKEN,
        IN_KITCHEN,
        READY,
        SERVED,
        PAID
    }

    private int tableID;
    private final int orderID;
    private final int restaurantID;
    private final ArrayList<FoodItem> newOrder;
    private Bill bill;

    private Status orderStatus;

    /**
     * Constructs Order Object
     */
    public Order(int tableID, int restaurantID) {
        this(tableID, restaurantID, (int) (Math.random() * Instant.now().getEpochSecond()));
    }

    public Order(int tableID, int restaurantID, int orderID) {
        this.tableID = tableID;
        this.restaurantID = restaurantID;
        this.orderID = orderID;

        newOrder = new ArrayList<FoodItem>();
    }


    public void addFood(FoodItem food) {
        newOrder.add(food);

    }

    public Bill getBill() {
        if (bill == null) {
            bill = new Bill(newOrder);
        }
        return bill;
    }

    public String getPrintableBill() {
        return String.format("%s\nOrder #%s\n%s", "=".repeat(30), orderID, getBill());
    }

    public void removeFood(FoodItem food) {
        newOrder.remove(food);
    }

    public int getOrderSize() {
        return newOrder.size();
    }

    public String orderSummery() { //printing the bill method?
        //print Bill total and tip as amount due to pay
        String nameAndPrice = "";
        for (FoodItem food : newOrder) {
            nameAndPrice += food + "\n";
        }
        return String.format("%s\n%s%s", "=".repeat(30), nameAndPrice, "=".repeat(30));
    }


    public String toString() {
        String items = newOrder.stream().map(FoodItem::getName).collect(Collectors.joining(", "));
        return String.format("Order #%s      EUR %.2f      (%s items: %s)", orderID, getBill().getBillTotal(), newOrder.size(), items);
    }


    public void setOrderStatus(Status s, boolean save) { // 0.ordered 1.in da kitchen 2. en route 3.served, enjoy!
        this.orderStatus = s;
        updateStoredOrder();
        if (save) {
            saveOrder();
        }
        /*
        maybe the orderStatus should use an array of strings of statuses,
         which I then change by incrementing i, after a wee timer of like
         30 seconds 0.0
         RESEARCH THIS! QUEUING !!!
         have an array of strings of differet statuses, and setOrderStatus should take in an integer from 1-4, which corresponds to the index of the statuses in the array.
         */
    }

    public Status getStatus() {
        return orderStatus;
    }

    public void saveOrder() {
        // format
        // time, order id, restaurant id, bill total, table id, serialised items, paid

        String serialisedItems = newOrder.stream().map(food -> String.valueOf(food.getId())).collect(Collectors.joining(";"));

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);


        String orderForCSV = String.format("%s,%s,%s,%s,%s,%s,%s\n", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), orderID, restaurantID, getBill().getBillTotal(), tableID, serialisedItems, orderStatus);

        try {
            Files.write(Paths.get("orders.csv"), orderForCSV.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public double payBillByCash(double amt) {
        if (amt < getBill().getBillTotal()) {
            return -1;
        }
        setOrderStatus(Status.PAID, false);
        updateStoredOrder();
        return amt - getBill().getBillTotal();
    }

    public boolean payBillByCard(String cardNum, String expiry, String csv) {
        Pattern expiryPattern = Pattern.compile("[0-9]{2}/[0-9]{4}");
        if (cardNum.length() != 16) {
            // check card number
            return false;
        }
        if (csv.length() != 3) {
            // check CSV
            return false;
        }
        if (!expiryPattern.matcher(expiry).find()) {
            // check expiry
            return false;
        }
        setOrderStatus(Status.PAID, false);
        updateStoredOrder();
        return true;
    }

    private void updateStoredOrder() {
        try {
            File ordersFile = new File("orders.csv");
            Scanner scanner = new Scanner(ordersFile);
            StringBuilder output = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineData = line.split(",");
                int orderId = Integer.parseInt(lineData[1]);

                if (orderId != this.orderID) {
                    output.append(line);
                    output.append("\n");
                } else {
                    String serialisedItems = newOrder.stream().map(food -> String.valueOf(food.getId())).collect(Collectors.joining(";"));
                    String orderForCSV = String.format("%s,%s,%s,%s,%s,%s,%s\n", lineData[0], orderID, restaurantID, getBill().getBillTotal(), tableID, serialisedItems, orderStatus);
                    output.append(orderForCSV);
                }

            }
            Files.write(Paths.get("orders.csv"), output.toString().getBytes());
        } catch (IOException e) {
        }
    }

}
