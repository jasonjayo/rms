import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Jason Gill
 */
public class Restaurant {

    private int id;
    private String name;
    private Menu menu;
    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private ArrayList<Chef> chefs = new ArrayList<>();
    private ArrayList<Waiter> waiters = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Customer> customers;

    LocalTime openTime = LocalTime.of(12, 0);
    LocalTime closeTime = LocalTime.of(21, 0);

    public Restaurant(String name, int id, ArrayList<Table> tables, Menu menu, ArrayList<Customer> customers, ArrayList<Reservation> reservations, ArrayList<Chef> chefs) {
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.customers = customers;
        this.reservations = reservations;
        this.chefs = chefs;
        this.tables = tables;

//        initTables();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    // table information stored in CSV files and accessed through restaurant ID
    public void initTables() throws NoTablesFileException {
        try {
            File tablesData = new File("tables.csv");
            Scanner scanner = new Scanner(tablesData);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // csv files in format restaurant id, table no., capacity
                int[] tableData = Arrays.stream(line.split(",")).mapToInt(Integer::valueOf).toArray();
                // if table is for this restaurant
                if (tableData[0] == id) {
                    tables.add(new Table(tableData[1], tableData[2]));
                }
            }

        } catch (FileNotFoundException e) {
            throw new NoTablesFileException("Missing tables file");
        }

    }

    public TreeMap<Integer, ArrayList<Table>> getAvailableTables(LocalDate date) {
        TreeMap<Integer, ArrayList<Table>> availableTables = new TreeMap<>();
        // assume open 12 pm - 9 pm

        for (int i = openTime.getHour(); i + 2 < closeTime.getHour(); i++) {
            LocalDateTime hourStart = date.atTime(i, 0);
            LocalDateTime hourEnd = date.atTime(i + 2, 0);


        }

        return availableTables;
    }

    public List<String> getAvailableStartTimes() {
        List<String> startTimes = new ArrayList<>();
        for (int i = openTime.getHour(); i + 2 <= closeTime.getHour(); i++) {
            startTimes.add(i + ":00");
        }
        return startTimes;
    }

    public List<Table> getAvailableTables(LocalDateTime dateTime, int numOfPeople) {
        ArrayList<Table> availableTablesThisHour = new ArrayList<>(tables);
        for (Table t : tables) {
            // TODO: check for overlap
            if (t.getCapacity() < numOfPeople) {
                availableTablesThisHour.remove(t);
                continue;
            }
            for (Reservation r : reservations) {
                if (r.getTable().equals(t)) {
                    if (r.getStartTime().isBefore(dateTime.plusHours(2)) && dateTime.isBefore(r.getEndTime())) {
                        availableTablesThisHour.remove(t);
                    }
                    // start1.before(end2) && start2.before(end1);
                }
            }
        }
        return availableTablesThisHour;
    }

    public String createReservation(Table t, int numOfPeople, int customerId, LocalDateTime startTime) {
        reservations.add(new Reservation(t, numOfPeople, customerId, startTime, startTime.plusHours(2)));


        String reservationForCSV = String.format("%s,%s,%s,%s,%s\n", id, numOfPeople, startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), t.getId(), customerId);

        try {
            Files.write(Paths.get("reservations.csv"), reservationForCSV.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d u");

        return String.format("From %s until %s on %s at %s for %d people. Table %d.", startTime.format(timeFormat), startTime.plusHours(2).format(timeFormat), startTime.format(dateFormat), name, numOfPeople, t.getId());
    }

    public Menu getMenu() {
        return menu;
    }

    public int createCustomer(String name, String phoneNum) {
        int id = (int) (Math.random() * Instant.now().getEpochSecond());
        try {
            String customerForCSV = String.format("%s,%s,%s\n", id, name, phoneNum);
            Files.write(Paths.get("customers.csv"), customerForCSV.getBytes(), StandardOpenOption.APPEND);
            return id;
        } catch (IOException e) {
            return 0;
        }
    }

    public Order createOrder(int tableId) {
        Order o = new Order(tableId);
        orders.add(o);
        return o;
    }

    public boolean cancelReservation(int customerId, LocalDateTime dateTime) {
        for (Reservation r : reservations) {
            if (r.customerId == customerId && r.getStartTime() == dateTime) {
                return reservations.remove(r);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}