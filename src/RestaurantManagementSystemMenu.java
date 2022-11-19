import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class RestaurantManagementSystemMenu {

    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yy");
    private static final String INCORRECT_DATE_MESSAGE = "Please enter a valid date in the format dd/mm/yy. e.g., 12/6/22";
    private static final String INCORRECT_TIME_MESSAGE = "Please enter a valid time in the format hh:ss using the 24-hour clock. e.g., 15:00";


    private Restaurant currentRestaurant;

    public RestaurantManagementSystemMenu() {
        scanner = new Scanner(System.in);
    }

    public void run(RestaurantManagementSystem rms) {

        System.out.println("Choose a restaurant:");
        currentRestaurant = getChoice(rms.getRestaurants());
        boolean running = true;


        while (running) {

            System.out.println("Choose an option to continue:");
            //noinspection SpellCheckingInspection
            System.out.println("M)ake a reservation C)ancel a reservation S)ee menu O)rder P)ick another restaurant Q)uit");
            String input = scanner.nextLine().toUpperCase();

            switch (input) {
                case "M":
                    System.out.println("For what date are you creating a reservation? (dd/mm/yy)");
                    LocalDate date = null;
                    while (date == null) {
                        try {
                            date = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
                        } catch (DateTimeParseException e) {
                            System.out.println(INCORRECT_DATE_MESSAGE);
                        }
                    }

                    System.out.println("Please choose a time:");
                    LocalTime startTime = LocalTime.parse(getChoice(new ArrayList<>(currentRestaurant.getAvailableStartTimes())));

                    System.out.println("How many people will be dining?");
                    int numOfPeople = 0;
                    while (numOfPeople < 2) {
                        if (scanner.hasNextInt()) {
                            numOfPeople = scanner.nextInt();
                        } else {
                            scanner.next(); // flush input
                        }
                        System.out.println("Please enter valid input. Note that a reservation must contain at least 2 people.");
                    }

                    LocalDateTime dateTime = LocalDateTime.of(date, startTime);
                    List<Table> availableTables = currentRestaurant.getAvailableTables(dateTime, numOfPeople);
                    if (availableTables.size() == 0) {
                        System.out.println("Sorry! We don't have any tables with suitable capacity for that time.");
                        scanner.nextLine(); // consumes \n after int input above
                        break;
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

                    break;
                case "S":
                    for (MenuCategory mc : currentRestaurant.getMenu().getCategories()) {
                        System.out.printf("\n%s\n%10s%s\n%s\n", "=".repeat(30), "", mc.getName().toUpperCase(), "=".repeat(30));
                        for (FoodItem food : mc.getFood()) {
                            System.out.println(food);
                        }
                    }
                    break;
                case "P":
                    currentRestaurant = getChoice(rms.getRestaurants());
                    break;
                case "C":
                    cancelReservation();
                    break;
                case "O":
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
                    System.out.println("Here's a summary of the order:");
                    System.out.println(order);
                    break;
                case "Q":
                    running = false;
                    break;
            }
        }
    }

    public <T> T getChoice(List<T> options) {
        return getChoice(options, false);
    }

    public <T> T getChoice(List<T> options, boolean allowReturnNull) {
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

    public void cancelReservation() {
        System.out.println("What is the date of the reservation you wish to cancel?");
        LocalDate date = null;
        while (date == null) {
            try {
                date = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println(INCORRECT_DATE_MESSAGE);
            }
        }
        System.out.println("What is the time of the reservation you wish to cancel?");
        LocalTime time = null;
        while (time == null) {
            try {
                time = LocalTime.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println(INCORRECT_TIME_MESSAGE);
            }
        }
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
            System.out.println("We couldn't cancel your reservation. Please call us for assistance.");
        }
    }

}
