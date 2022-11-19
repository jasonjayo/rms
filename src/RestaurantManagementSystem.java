import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class RestaurantManagementSystem {
    Scanner scanner;

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    TreeMap<Integer, ArrayList<Table>> tablesData = new TreeMap<>();
    TreeMap<Integer, Menu> menusData = new TreeMap<>();
    TreeMap<Integer, ArrayList<Reservation>> reservationsData = new TreeMap<>();

    ArrayList<Customer> customers = new ArrayList<>();


    public RestaurantManagementSystem() {

        try {
            initRestaurantData();
        } catch (FileNotFoundException e) {
            System.out.println("Warning: The tables data file is missing.");
        }

        try {

            File restaurantsData = new File("restaurants.csv");
            Scanner scanner = new Scanner(restaurantsData);

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");

                // csv files in format restaurant id, name
                int id = Integer.parseInt(line[0]);
                String name = line[1];

                Restaurant restaurant = new Restaurant(name, id, tablesData.get(id), menusData.get(id), customers, new ArrayList<>());
                restaurants.add(restaurant);


            }

        } catch (FileNotFoundException e) {
            System.out.println("Warning: The restaurants data file is missing.");
            System.exit(1);
        }


    }

    public void initRestaurantData() throws FileNotFoundException {

        File tablesFile = new File("tables.csv");
        Scanner tablesFileScanner = new Scanner(tablesFile);

        File menusFile = new File("menus.csv");
        Scanner menusFileScanner = new Scanner(menusFile);

        File customersFile = new File("customers.csv");
        Scanner customersFileScanner = new Scanner(customersFile);

        File reservationsFile = new File("reservations.csv");
        Scanner reservationsFileScanner = new Scanner(reservationsFile);

        while (tablesFileScanner.hasNextLine()) {

            String line = tablesFileScanner.nextLine();
            // csv files in format restaurant id, table no., capacity
            int[] tableData = Arrays.stream(line.split(",")).mapToInt(Integer::valueOf).toArray();

            int id = tableData[0];
            Table table = new Table(tableData[1], tableData[2]);

            if (!tablesData.containsKey(id)) {
                tablesData.put(id, new ArrayList<>());
            }
            tablesData.get(id).add(table);

        }

        outer:
        while (menusFileScanner.hasNextLine()) {

            String[] line = menusFileScanner.nextLine().split(",");
            // csv files in format restaurant id, category, item, price
            int id = Integer.parseInt(line[0]);
            String category = line[1];
            FoodItem food = new FoodItem(line[2], Double.parseDouble(line[3]));

            if (!menusData.containsKey(id)) {
                menusData.put(id, new Menu());
            }

            for (MenuCategory mc : menusData.get(id).getCategories()) {
                if (mc.getName().equals(category)) {
                    mc.addFoodItem(food);
                    continue outer;
                }
            }

            // given menu category not found, so create it
            MenuCategory mc = new MenuCategory(category);
            menusData.get(id).getCategories().add(mc);
            mc.addFoodItem(food);


        }

        while (customersFileScanner.hasNextLine()) {

            String line = customersFileScanner.nextLine();
            // csv files in format id,name,phoneNum
            String[] customerData = line.split(",");

            int id = Integer.parseInt(customerData[0]);
            String name = customerData[1];
            String phoneNum = customerData[2];

            customers.add(new Customer(id, name, phoneNum));

        }

        while (reservationsFileScanner.hasNextLine()) {

            String line = reservationsFileScanner.nextLine();
            // csv files in format id,name,phoneNum
            String[] reservationData = line.split(",");

            int restaurantId = Integer.parseInt(reservationData[0]);
            int numOfPeople = Integer.parseInt(reservationData[1]);
            LocalDateTime startTime = LocalDateTime.parse(reservationData[2]);
            int tableId = Integer.parseInt(reservationData[3]);
            String phoneNum = reservationData[4];
            int customerId = Integer.parseInt(reservationData[4]);

        }

    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
