package com.rms.restaurantmanagementsystemgui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class RestaurantManagementSystemMenu {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yy");
    private static final String INCORRECT_DATE_MESSAGE = "Please enter a valid date in the format dd/mm/yy. e.g., 12/6/22";
    private static final String INCORRECT_TIME_MESSAGE = "Please enter a valid time in the format hh:ss using the 24-hour clock. e.g., 15:00";

    private final Scanner scanner;
    private Restaurant currentRestaurant;
    RestaurantManagementSystem rms;

    public RestaurantManagementSystemMenu() {
        scanner = new Scanner(System.in);
    }

    public void run(RestaurantManagementSystem rms) {

        this.rms = rms;

        System.out.println("Choose a restaurant:");
        currentRestaurant = getChoice(rms.getRestaurants());
        boolean running = true;

        while (running) {
            System.out.println("Choose an option to continue:");
            //noinspection SpellCheckingInspection
            System.out.println("M)ake a reservation C)ancel a reservation V)iew menu B)ack to choose restaurant P)ay Bill S)taff menu Q)uit");
            String input = scanner.nextLine().toUpperCase();

            switch (input) {
                case "M":
                    makeReservation();
                    break;
                case "V":
                    for (MenuCategory mc : currentRestaurant.getMenu().getCategories()) {
                        System.out.printf("\n%s\n%10s%s\n%s\n", "=".repeat(30), "", mc.getName().toUpperCase(), "=".repeat(30));
                        for (FoodItem food : mc.getFood()) {
                            System.out.println(food);
                        }
                    }
                    break;
                case "B":
                    currentRestaurant = getChoice(rms.getRestaurants());
                    break;
                case "S":
                    staffMode();
                    break;
                case "P":
                    payForOrder();
                    break;
                case "C":
                    cancelReservation();
                    break;
                case "Q":
                    rms.stopRemindersService();
                    running = false;
                    break;
            }
        }
    }

    /*
     * customer methods
     */
    private void makeReservation() {
        System.out.println("For what date are you creating a reservation? (dd/mm/yy)");
        LocalDate date = getDate();

        System.out.println("Please choose a time:");
        LocalTime startTime = LocalTime.parse(getChoice(new ArrayList<>(currentRestaurant.getAvailableStartTimes())));

        int numOfPeople = getNumberOfPeople();

        LocalDateTime dateTime = LocalDateTime.of(date, startTime);
        List<Table> availableTables = currentRestaurant.getAvailableTables(dateTime, numOfPeople);
        if (availableTables.size() == 0) {
            System.out.println("Sorry! We don't have any tables with suitable capacity for that time.");
            return;
        }
        System.out.println("Here's a list of the available tables for that time. Please choose your preferred table.");
        Table table = getChoice(availableTables);

        System.out.println("To complete your reservation, we need some personal details.");
        System.out.println("If you've made a reservation with us before, enter your customer ID. Otherwise, enter your name and we'll make an account for you.");

        String customerName = "";
        int customerId = 0;
        while (customerName.equals("") && customerId == 0) {
            if (scanner.hasNextLine() || scanner.hasNextInt()) {
                if (scanner.hasNextInt()) {
                    // already has account
                    customerId = scanner.nextInt();
                    scanner.nextLine(); // consumes \n after int input above

                } else {
                    // create new account
                    customerName = scanner.nextLine();

                    System.out.println("Almost there! What's your phone number?");
                    String phoneNum = "";
                    while (phoneNum.equals("")) {
                        if (scanner.hasNextLine()) {
                            phoneNum = scanner.nextLine();
                        } else {
                            scanner.next(); // flush input
                        }
                    }
                    customerId = currentRestaurant.createCustomer(customerName, phoneNum);
                    System.out.printf("We've created a new account for you. Keep note of your customer ID: %s\n", customerId);
                }
            } else {
                scanner.next(); // flush input
            }
        }

        System.out.println("Here's a summary of your reservation:");
        System.out.println(currentRestaurant.createReservation(table, numOfPeople, customerId, dateTime));
    }

    private void payForOrder() {
        ArrayList<Order> outstandingOrders = currentRestaurant.getOutstandingOrder();
        if (outstandingOrders.size() == 0) {
            System.out.println("There are no unpaid orders.");
            return;
        }
        System.out.println("Choose an order:");
        Order o = getChoice(outstandingOrders);
        System.out.println("If you'd like to tip, please enter the amount in decimal format. If not, enter 0");
        double tip = -1;
        while (tip < 0) {
            if (scanner.hasNextDouble()) {
                tip = scanner.nextDouble();
            } else {
                System.out.println("Please enter valid input. e.g., 8.0 or 4.35");
                scanner.next();
            }
        }
        o.getBill().setTip(tip);
        scanner.nextLine(); // consumes \n after double input above
        System.out.println(o.getPrintableBill());
        System.out.println("Choose payment method:");
        String paymentMethod = getChoice(new ArrayList<>(Arrays.asList("Card", "Cash")));
        if (paymentMethod.equalsIgnoreCase("card")) {
            String cardNumber;
            String csv;
            String expiry;
            boolean paymentComplete = false;

            while (!paymentComplete) {
                cardNumber = "";
                csv = "";
                expiry = "";

                System.out.println("Please enter your 16-digit card number:");
                while (cardNumber.equals("")) {
                    if (scanner.hasNextLine()) {
                        cardNumber = scanner.nextLine();
                    }
                }
                System.out.println("Please enter your card's CSV code:");
                while (csv.equals("")) {
                    if (scanner.hasNextLine()) {
                        csv = scanner.nextLine();
                    }
                }
                System.out.println("Please enter your card's expiry date (mm/yyyy):");
                while (expiry.equals("")) {
                    if (scanner.hasNextLine()) {
                        expiry = scanner.nextLine();
                    }
                }
                paymentComplete = o.payBillByCard(cardNumber, expiry, csv);
                if (!paymentComplete) {
                    System.out.println("Payment failed. Please try again.");
                } else {
                    System.out.println("Payment successful. Thank you!");
                }
            }

        } else if (paymentMethod.equalsIgnoreCase(("cash"))) {
            boolean paymentComplete = false;
            System.out.println("What is the total value of the cash you are paying with? e.g., 4.00");
            while (!paymentComplete) {
                double givenAmount = 0;
                while (givenAmount == 0) {
                    if (scanner.hasNextDouble()) {
                        givenAmount = scanner.nextDouble();
                    } else {
                        scanner.next(); // flush input
                        System.out.println("Please enter valid input.");
                    }
                }
                scanner.nextLine(); // consumes \n after double input above
                double change = o.payBillByCash(givenAmount);
                paymentComplete = change != -1;
                if (!paymentComplete) {
                    System.out.println("Payment failed. Please try again. Make sure you've given enough cash.");
                } else {
                    System.out.printf("Payment successful. Thank you! Change given: EUR %.2f\n", change);

                }
            }
        }
    }

    private void cancelReservation() {
        System.out.println("What is the date of the reservation you wish to cancel? (dd/mm/yyyy)");
        LocalDate date = getDate();
        System.out.println("What is the time of the reservation you wish to cancel?");
        LocalTime time = getTime();
        System.out.println("What's your customer ID?");
        int customerId = 0;
        while (customerId == 0) {
            if (scanner.hasNextInt()) {
                customerId = scanner.nextInt();
                scanner.nextLine(); // consumes \n after int input above
            } else {
                System.out.println("Please enter valid input.");
                scanner.next(); // flush input
            }
        }
        if (currentRestaurant.cancelReservation(customerId, LocalDateTime.of(date, time))) {
            System.out.println("Your reservation has been cancelled successfully");
        } else {
            System.out.println("We couldn't cancel your reservation. Please recheck the details you entered. Call us for further assistance.");
        }
    }

    /*
     * end customer methods
     */

    /*
     * staff methods
     */
    private void staffMode() {
        // TODO: change back to false before final
        boolean logInSuccessful = true;

        while (!logInSuccessful) {
            System.out.println("Enter username or 0 leave staff mode:");
            String username = "";
            while (username.equals("")) {
                username = scanner.nextLine();
            }
            if (username.equals("0")) {
                return;
            }
            System.out.println("Enter password:");
            String password = "";
            while (password.equals("")) {
                password = scanner.nextLine();
            }
            logInSuccessful = currentRestaurant.login(username, password);
            if (!logInSuccessful) {
                System.out.println("Incorrect details.");
            } else {
                System.out.printf("Login successful. Welcome %s!\n", username);
            }
        }


        boolean staffMode = true;
        while (staffMode) {

            System.out.println("Choose an option to continue:");
            //noinspection SpellCheckingInspection
            System.out.println("A)lter order status I)ncome summary W)alk-in booking O)rder L)og out");
            String input = scanner.nextLine().toUpperCase();

            switch (input) {
                case "A" -> alterOrderStatus();
                case "L" -> staffMode = false;
                case "I" -> incomeSummery();
                case "W" -> walkInBooking();
                case "O" -> order();
            }

        }
    }


    private void incomeSummery() {
        System.out.println("Please enter the from date in the format dd/mm/yy:");
        LocalDate fromDateOfIncome = getDate();

        System.out.println("Please enter the to date in the format dd/mm/yy");
        LocalDate toDateOfIncome = getDate();

        HashMap<LocalDate, Double> result = rms.getIncomeSummary(currentRestaurant, fromDateOfIncome, toDateOfIncome);
        System.out.printf("Showing income generated by %s each day from %s to %s inclusive:\n", currentRestaurant.getName(), fromDateOfIncome, toDateOfIncome);
        result.forEach((day, total) -> System.out.printf("%-20s EUR %.2f\n", day, total));
    }

    private void walkInBooking() {
        int numOfPeople = getNumberOfPeople();
        System.out.println("Please choose a table from the list of currently available tables:");
        Table tableForBooking = getChoice(currentRestaurant.getAvailableTables(LocalDateTime.now(), numOfPeople));
        System.out.println(currentRestaurant.createReservation(tableForBooking, numOfPeople, 0, LocalDateTime.now()));
    }

    private void order() {
        System.out.println("What table is this order for?");
        Table tableForOder = getChoice(currentRestaurant.getTables());
        boolean orderTakingComplete = false;
        Order order = currentRestaurant.createOrder(tableForOder.getId());

        while (!orderTakingComplete) {
            System.out.println("Please choose an item to add to the order or 0 to finish adding items:");
            FoodItem food = getChoice(currentRestaurant.getMenu().getAllItems(), true);

            if (food == null) {
                orderTakingComplete = true;
            } else {
                order.addFood(food);
            }
        }
        if (order.getOrderSize() == 0) {
            currentRestaurant.cancelOrder(order);
            System.out.println("Empty order discarded.");
            return;
        }
        order.setOrderStatus(Order.Status.TAKEN, true);
        System.out.println("Here's a summary of the order:");
        System.out.println(order.orderSummery());
    }

    private void alterOrderStatus() {
        ArrayList<Order> ordersInProgress = currentRestaurant.getOutstandingOrder();
        if (ordersInProgress.size() == 0) {
            System.out.println("There are no orders in progress.");
            return;
        }
        System.out.println("Please choose an order for which to update the status:");
        Order orderToUpdate = getChoice(ordersInProgress);
        System.out.println("Please set the new status:");
        Order.Status statusToSet = getChoice(Arrays.asList(Order.Status.values()));
        orderToUpdate.setOrderStatus(statusToSet, false);
        System.out.printf("Status updated to %s successfully.\n", statusToSet);
    }

    /*
     * end staff methods
     */

    /*
     * utility methods
     */

    private <T> T getChoice(List<T> options) {
        return getChoice(options, false);
    }

    private <T> T getChoice(List<T> options, boolean allowReturnNull) {
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%2d) %s\n", i + 1, options.get(i));
        }
        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consumes \n after int input above

                if ((0 < choice) && (choice <= options.size())) {
                    return options.get(choice - 1);
                }
                // exit
                if (allowReturnNull && choice == 0) {
                    return null;
                }

            } else {
                scanner.next(); // flush input
            }
            System.out.println("Please enter valid input.");
        }
    }

    private int getNumberOfPeople() {
        System.out.println("How many people will be dining?");
        int numOfPeople = 0;
        while (numOfPeople < 2 || numOfPeople > 10) {
            if (scanner.hasNextInt()) {
                numOfPeople = scanner.nextInt();
                scanner.nextLine(); // consumes \n after int input above
            } else {
                scanner.next(); // flush input
                System.out.println("Please enter valid input.");
            }
            if (numOfPeople < 2 || numOfPeople > 10) {
                System.out.println("Note that a reservation must contain between 2 and 10 people.");
            }
        }
        return numOfPeople;
    }

    private LocalDate getDate() {
        LocalDate date = null;
        while (date == null) {
            try {
                date = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println(INCORRECT_DATE_MESSAGE);
            }
        }
        return date;
    }

    private LocalTime getTime() {
        LocalTime time = null;
        while (time == null) {
            try {
                time = LocalTime.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println(INCORRECT_TIME_MESSAGE);
            }
        }
        return time;
    }

}
