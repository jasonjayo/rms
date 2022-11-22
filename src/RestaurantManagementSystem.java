import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RestaurantManagementSystem {
    Scanner scanner;

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    TreeMap<Integer, ArrayList<Table>> tablesData = new TreeMap<>();
    TreeMap<Integer, Menu> menusData = new TreeMap<>();
    TreeMap<Integer, ArrayList<Reservation>> reservationsData = new TreeMap<>();

    HashMap<Integer, Customer> customers = new HashMap<>();

    ScheduledExecutorService scheduledExecutor;

    public RestaurantManagementSystem() {

        try {
            initRestaurantData();
        } catch (FileNotFoundException e) {
            System.out.println("Warning: At least one CSV file is missing: " + e.getMessage());
        }

        try {

            File restaurantsData = new File("restaurants.csv");
            Scanner scanner = new Scanner(restaurantsData);

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");

                // csv files in format restaurant id, name
                int id = Integer.parseInt(line[0]);
                String name = line[1];

                // ensure null values are never passed to constructor
                tablesData.computeIfAbsent(id, k -> new ArrayList<>());
                menusData.computeIfAbsent(id, k -> new Menu());
                reservationsData.computeIfAbsent(id, k -> new ArrayList<>());

                Restaurant restaurant = new Restaurant(name, id, tablesData.get(id), menusData.get(id), customers, reservationsData.get(id), new ArrayList<>());
                restaurants.add(restaurant);


            }

        } catch (FileNotFoundException e) {
            System.out.println("Warning: The restaurants.csv file is missing.");
            System.exit(1);
        }

        scheduledExecutor = Executors.newScheduledThreadPool(1);
//        scheduledExecutor.scheduleAtFixedRate(this::sendReminders, 0, 1, TimeUnit.MINUTES);
    }

    public void stopRemindersService() {
        scheduledExecutor.shutdown();
    }

    public void sendReminders() {
        for (ArrayList<Reservation> reservationsList : reservationsData.values()) {
            for (Reservation r : reservationsList) {
                System.out.printf("AUTO REMINDER SYSTEM: System sent a reminder to customer %s at %s about the following reservation: \n %s\n", customers.get(r.getCustomerId()).getName(), customers.get(r.getCustomerId()).getPhoneNum(), r);
            }
        }
    }

    public HashMap<LocalDate, Double> getIncomeSummary(Restaurant r, LocalDate from, LocalDate to) {

        HashMap<LocalDate, Double> output = new HashMap<>();

        try {

            File restaurantsData = new File("orders.csv");
            Scanner scanner = new Scanner(restaurantsData);

            while (scanner.hasNextLine()) {

                String[] data = scanner.nextLine().split(",");
                LocalDate date = LocalDateTime.parse(data[0]).toLocalDate();
                int restaurantId = Integer.parseInt(data[1]);
                double income = Double.parseDouble(data[2]);

                if (date.isAfter(from.minusDays(1)) && date.isBefore(to.plusDays(1)) && restaurantId == r.getId()) {
                    double subTotal = 0;
                    if (output.containsKey(date)) {
                        subTotal = output.get(date);
                    }
                    output.put(date, subTotal + income);
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Warning: The order.csv file is missing.");
            System.exit(1);
        }

        return output;
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

            customers.put(id, new Customer(id, name, phoneNum));

        }

        while (reservationsFileScanner.hasNextLine()) {

            String line = reservationsFileScanner.nextLine();
            // csv files in format id,name,phoneNum
            String[] reservationData = line.split(",");

            int restaurantId = Integer.parseInt(reservationData[0]);
            int numOfPeople = Integer.parseInt(reservationData[1]);
            LocalDateTime startTime = LocalDateTime.parse(reservationData[2]);
            int tableId = Integer.parseInt(reservationData[3]);
            int customerId = Integer.parseInt(reservationData[4]);

            Table table = null;
            ArrayList<Table> tables = tablesData.get(restaurantId);
            for (Table t : tables) {
                if (t.getId() == tableId) {
                    table = t;
                }
            }

            Reservation reservation = new Reservation(table, numOfPeople, customerId, startTime, startTime.plusHours(2));

            if (!reservationsData.containsKey(restaurantId)) {
                reservationsData.put(restaurantId, new ArrayList<>());
            }
            reservationsData.get(restaurantId).add(reservation);

        }

    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
