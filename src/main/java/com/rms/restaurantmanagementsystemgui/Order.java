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
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * holds information about a table's order, including the food ordered and status of order.
 *
 * @author Caragh Morahan
 */
public class Order {

    enum Status {
        TAKEN,
        IN_KITCHEN,
        READY,
        SERVED,
        PAID
    }

    private final int tableID;
    private final int orderID;
    private final int restaurantID;
    private final ArrayList<FoodItem> newOrder;
    private Bill bill;
    private Status orderStatus;

    /**
     * creates an order. order id genereated automatically
     *
     * @param tableID      int table id
     * @param restaurantID int restaurant id
     */
    public Order(int tableID, int restaurantID) {
        this(tableID, restaurantID, (int) (Math.random() * Instant.now().getEpochSecond()));
    }

    /**
     * creates an order
     *
     * @param tableID      int table id
     * @param restaurantID int restaurant id
     * @param orderID      int order id
     */
    public Order(int tableID, int restaurantID, int orderID) {
        this.tableID = tableID;
        this.restaurantID = restaurantID;
        this.orderID = orderID;

        newOrder = new ArrayList<>();
    }

    /**
     * adds a food item to the order
     *
     * @param food food item to add to order
     */
    public void addFood(FoodItem food) {
        newOrder.add(food);

    }

    /**
     * gets the bill associated with this order
     *
     * @return bill
     */
    public Bill getBill() {
        if (bill == null) {
            bill = new Bill(newOrder);
        }
        return bill;
    }

    /**
     * returns string suitable for printing containing order id and bill info
     *
     * @return string
     */
    public String getPrintableBill() {
        return String.format("%s\nOrder #%s\n%s", "=".repeat(30), orderID, getBill());
    }

    /**
     * remove an item from order
     *
     * @param food food item to remove
     */
    public void removeFood(FoodItem food) {
        newOrder.remove(food);
    }

    /**
     * gets number of food items in order
     *
     * @return int number of food items in order
     */
    public int getOrderSize() {
        return newOrder.size();
    }

    /**
     * returns string for printing containing order info, table info and bill.
     *
     * @return string order summary
     */
    public String orderSummery() { //printing the bill method?
        //print Bill total and tip as amount due to pay
        String nameAndPrice = "Order #" + orderID + "\nTable #" + tableID + "\n" + "-".repeat(30) + "\n";
        for (FoodItem food : newOrder) {
            nameAndPrice += food + "\n";
        }
        return String.format("%s\n%s%s", "=".repeat(30), nameAndPrice, "=".repeat(30));
    }

    /**
     * returns single-line presentation of order
     *
     * @return string
     */
    public String toString() {
        String items = newOrder.stream().map(FoodItem::getName).collect(Collectors.joining(", "));
        return String.format("Order #%s      EUR %.2f      (%s items: %s)", orderID, getBill().getBillTotal(), newOrder.size(), items);
    }

    /**
     * set status of order
     *
     * @param s    Order.Status new status
     * @param save boolean whether to save order to file now
     */
    public void setOrderStatus(Status s, boolean save) { // 0.ordered 1.in da kitchen 2. en route 3.served, enjoy!
        this.orderStatus = s;
        updateStoredOrder();
        if (save) {
            saveOrder();
        }
    }

    /**
     * get status of order
     *
     * @return Order.Status status of order
     */
    public Status getStatus() {
        return orderStatus;
    }

    /**
     * saves order to orders.csv file
     */
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

    /**
     * pay for order by cash
     *
     * @param amt double amount of cash provided by customers
     * @return double change given back to customer or -1 if payment unsuccessful
     */
    public double payBillByCash(double amt) {
        if (amt < getBill().getBillTotal()) {
            return -1;
        }
        setOrderStatus(Status.PAID, false);
        updateStoredOrder();
        return amt - getBill().getBillTotal();
    }

    /**
     * pay for order by card
     *
     * @param cardNum string 16-digit card number
     * @param expiry  string expiry in format mm/yyyy
     * @param csv     string 3-digit CSV code
     * @return boolean indicating if payment was successful
     */
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

    /**
     * updates stored order in CSV file
     */
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
            System.out.println("Warning: error while rewriting order to orders.csv");
        }
    }

}
