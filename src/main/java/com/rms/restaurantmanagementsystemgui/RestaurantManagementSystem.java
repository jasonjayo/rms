package com.rms.restaurantmanagementsystemgui;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RestaurantManagementSystem {
    Scanner scanner;

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    TreeMap<Integer, ArrayList<Table>> tablesData = new TreeMap<>();
    TreeMap<Integer, Menu> menusData = new TreeMap<>();

    TreeMap<Integer, ArrayList<Waiter>> waitersData = new TreeMap<>();
    TreeMap<Integer, ArrayList<Chef>> chefsData = new TreeMap<>();

    TreeMap<Integer, ArrayList<Reservation>> reservationsData = new TreeMap<>();

    HashMap<Integer, Customer> customers = new HashMap<>();
    HashMap<Integer, ArrayList<Order>> ordersData = new HashMap<>();

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
                chefsData.computeIfAbsent(id, k -> new ArrayList<>());
                waitersData.computeIfAbsent(id, k -> new ArrayList<>());
                ordersData.computeIfAbsent(id, k -> new ArrayList<>());


                Restaurant restaurant = new Restaurant(name, id, tablesData.get(id), menusData.get(id), customers, reservationsData.get(id), chefsData.get(id), waitersData.get(id), ordersData.get(id));
                restaurants.add(restaurant);

            }

        } catch (FileNotFoundException e) {
            System.out.println("Warning: The restaurants.csv file is missing.");
            System.exit(1);
        }

        scheduledExecutor = Executors.newScheduledThreadPool(1);
        scheduledExecutor.scheduleAtFixedRate(this::sendReminders, 5, 1, TimeUnit.MINUTES);
        sendReminders();
    }

    public void stopRemindersService() {
        scheduledExecutor.shutdown();
    }

    public void sendReminders() {
        for (Restaurant restaurant : restaurants) {
            for (Reservation r : restaurant.getReservations()) {
                if (r.getCustomerId() != 0 && (r.getStartTime().minusDays(1).truncatedTo(ChronoUnit.MINUTES).isEqual(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))) {
                    System.out.printf("AUTO REMINDER SYSTEM: System sent a reminder to customer %s at %s about the following reservation: \n %s\n", customers.get(r.getCustomerId()).getName(), customers.get(r.getCustomerId()).getPhoneNum(), r);
                }
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
                int restaurantId = Integer.parseInt(data[2]);
                double income = Double.parseDouble(data[3]);
                String status = data[6];

                if (date.isAfter(from.minusDays(1)) && date.isBefore(to.plusDays(1)) && restaurantId == r.getId() && status.equalsIgnoreCase("paid")) {
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

    private void initRestaurantData() throws FileNotFoundException {

        File tablesFile = new File("tables.csv");
        Scanner tablesFileScanner = new Scanner(tablesFile);

        File menusFile = new File("menus.csv");
        Scanner menusFileScanner = new Scanner(menusFile);

        File customersFile = new File("customers.csv");
        Scanner customersFileScanner = new Scanner(customersFile);

        File reservationsFile = new File("reservations.csv");
        Scanner reservationsFileScanner = new Scanner(reservationsFile);

        File staffFile = new File("staff.csv");
        Scanner staffFileScanner = new Scanner(staffFile);

        Scanner ordersFileScanner = new Scanner(new File("orders.csv"));

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
            FoodItem food = new FoodItem(line[2], Double.parseDouble(line[3]), Integer.parseInt(line[4]));

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

        while (staffFileScanner.hasNextLine()) {

            String line = staffFileScanner.nextLine();
            // csv files in format staff id, username, password, role, restaurant id
            String[] staffData = line.split(",");

            int id = Integer.parseInt(staffData[0]);
            String name = staffData[1];
            String password = staffData[2];
            String role = staffData[3];
            int restaurantId = Integer.parseInt(staffData[4]);

            if (role.equals("waiter")) {
                Waiter waiter = new Waiter(id, name, password, restaurantId);
                if (!waitersData.containsKey(restaurantId)) {
                    waitersData.put(restaurantId, new ArrayList<>());
                }
                waitersData.get(restaurantId).add(waiter);
            } else if (role.equals("chef")) {
                Chef chef = new Chef(id, name, password, restaurantId);
                if (!chefsData.containsKey(restaurantId)) {
                    chefsData.put(restaurantId, new ArrayList<>());
                }
                chefsData.get(restaurantId).add(chef);
            }


        }

        while (ordersFileScanner.hasNextLine()) {

            String line = ordersFileScanner.nextLine();
            // csv files in format time, restaurant id, total cost, items, paid
            String[] orderData = line.split(",");

            String orderStatus = orderData[6];
            if (!orderStatus.equals("paid")) {
                //DateTimeFormatter.ISO_LOCAL_DATE_TIME), orderID, restaurantID, bill.getBillTotal(), tableID, serialisedItems, "false"
                int orderId = Integer.parseInt(orderData[1]);
                int restaurantId = Integer.parseInt(orderData[2]);
                int tableId = Integer.parseInt(orderData[4]);
                int[] itemsIds = Arrays.stream(orderData[5].split(";")).mapToInt(Integer::parseInt).toArray();

                Order o = new Order(tableId, restaurantId, orderId);
                for (int itemId : itemsIds) {
                    menusData.get(restaurantId).getAllItems().forEach(menuItem -> {
                        if (itemId == menuItem.getId()) {
                            o.addFood(menuItem);
                        }
                    });
                }
                o.setOrderStatus(Order.Status.valueOf(orderStatus.toUpperCase()), false);
                if (!ordersData.containsKey(restaurantId)) {
                    ordersData.put(restaurantId, new ArrayList<>());
                }
                ordersData.get(restaurantId).add(o);
            }


        }

    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
