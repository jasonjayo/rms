import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class RestaurantManagementSystemMenu {

    private final Scanner scanner;

    public RestaurantManagementSystemMenu() {
        scanner = new Scanner(System.in);
    }

    public void run(RestaurantManagementSystem rms) {
        boolean running = true;

        while (running) {

            System.out.println("Choose an option to continue:");
            System.out.println("M)ake a reservation Q)uit");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("M")) {
                System.out.println("Enter a date in the following format: dd/mm/yy");
                DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yy");
                boolean validInput = false;

                while (!validInput) {
                    LocalDate date = LocalDate.parse(scanner.nextLine(), format);
                    validInput = true;
                    System.out.println(date);
                    System.out.printf("Checking available tables for times during %s...\n", date);
                    rms.getRestaurants().get(0).getAvailableTables(date).forEach((key, value) -> {
                        System.out.println(LocalTime.of(key, 0));
                        if (value.size() > 0) {
                            System.out.println("There are tables available for this time.");
                        } else {
                            System.out.println("There are no tables available for this time.");
                        }
                    });
                }
            } else if (input.equals("Q")) {
                running = false;
            }
        }
    }

}
