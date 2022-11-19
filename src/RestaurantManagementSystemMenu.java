import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RestaurantManagementSystemMenu {

    private final Scanner scanner;

    public RestaurantManagementSystemMenu() {
        scanner = new Scanner(System.in);
    }

    public void run(RestaurantManagementSystem rms) {
        boolean running = false;

        System.out.println("Choose a restaurant:");
        Restaurant currentRestaurant;
        int numRestaurants = rms.getRestaurants().size();
        int i = 0;
        while (i < numRestaurants) {
            Restaurant restaurant = rms.getRestaurants().get(i++);
            System.out.printf("%d) %s\n", i, restaurant.getName());
        }
        while (true) {
            int index = scanner.nextInt() - 1;
            if (index >= 0 && index < numRestaurants) {
                currentRestaurant = rms.getRestaurants().get(index);
                running = true;
                break;
            }
        }

        scanner.nextLine(); // consumes \n after int input above

        while (running) {

            System.out.println("Choose an option to continue:");
            System.out.println("M)ake a reservation C)hoose restaurant S)ee menu Q)uit");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("M")) {
                System.out.println("For what date are you creating a reservation? (dd/mm/yy)");
                DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yy");
                boolean validInput = false;

                while (!validInput) {
                    LocalDate date = LocalDate.parse(scanner.nextLine(), format);
                    validInput = true;

                    System.out.println("Please choose a time:");
                    LocalTime startTime = LocalTime.parse(getChoice(new ArrayList<>(currentRestaurant.getAvailableStartTimes())));

                    System.out.println("How many people will be dining?");
                    int numOfPeople = 0;
                    while (numOfPeople == 0) {
                        if (scanner.hasNextInt()) {
                            numOfPeople = scanner.nextInt();
                        } else {
                            scanner.next(); // flush input
                        }
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

                    // TODO: refactor this
                    System.out.println("To complete your reservation, we need some personal details.");
                    System.out.println("If you've made a reservation with us before, enter your customer ID. Otherwise, enter your name and we'll make an account for you.");

                    String customerName = "";
                    int customerId = 0;
                    while (customerName.equals("") && customerId == 0) {
                        if (scanner.hasNextLine() || scanner.hasNextInt()) {
                            if (scanner.hasNextInt()) {
                                // already has account
                                customerId = scanner.nextInt(); // consumes \n after int input above
                                scanner.nextLine();
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
                            }
                        } else {
                            scanner.next(); // flush input
                        }
                    }

                    System.out.println("Here's a summary of your reservation:");
                    System.out.println(currentRestaurant.createReservation(table, numOfPeople, customerId, dateTime));

                }
            } else if (input.equals("S")) {
                for (MenuCategory mc : currentRestaurant.getMenu().getCategories()) {
                    System.out.printf("\n%s\n%15s\n%s\n", "=".repeat(30), mc.getName().toUpperCase(), "=".repeat(30));
                    for (FoodItem food : mc.getFood()) {
                        System.out.println(food);
                    }
                }
            } else if (input.equals("Q")) {
                running = false;
            }
        }
    }

    public <T> T getChoice(List<T> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, options.get(i));
        }
        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consumes \n after int input above

                if ((0 < choice) && (choice <= options.size())) {
                    return options.get(choice - 1);
                }

            } else {
                scanner.next(); // flush input
            }
            System.out.println("Please enter valid input.");
        }
    }

}
