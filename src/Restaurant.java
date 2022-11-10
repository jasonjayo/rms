import com.sun.source.tree.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
// you smell

/**
 * @author Jason Gill
 */
public class Restaurant {

    private int id;
    private String name;
    private Menu menu = new Menu();
    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private ArrayList<Chef> chefs = new ArrayList<>();
    private ArrayList<Waiter> waiters = new ArrayList<>();

    public Restaurant(String name, int id, Menu menu, ArrayList<Chef> chefs) throws NoTablesFileException {
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.chefs = chefs;

        initTables();
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

    public void makeReservation(Table table, int numOfPeople, int customerId, LocalDateTime from, LocalDateTime to) {
        reservations.add(new Reservation(table, numOfPeople, customerId, from, to));
    }

    public TreeMap<Integer, ArrayList<Table>> getAvailableTables(LocalDate date) {
        TreeMap<Integer, ArrayList<Table>> availableTables = new TreeMap<>();
        // assume open 12 pm - 9 pm
        LocalTime openTime = LocalTime.of(12, 0);
        LocalTime closeTime = LocalTime.of(21, 0);
        for (int i = openTime.getHour(); i < closeTime.getHour(); i++) {
            LocalDateTime hourStart = date.atTime(i, 0);
            LocalDateTime hourEnd = date.atTime(i + 1, 0);
            ArrayList<Table> availableTablesThisHour = new ArrayList<>(tables);
            for (Reservation reservation : reservations) {
                if (reservation.getEndTime().isAfter(hourStart) && reservation.getEndTime().isBefore((hourEnd))) {
                    availableTablesThisHour.remove(reservation.getTable());
                } else if (reservation.getStartTime().isAfter(hourStart) && reservation.getEndTime().isBefore(hourEnd)) {
                    availableTablesThisHour.remove(reservation.getTable());
                } else if (reservation.getStartTime().isBefore(hourStart) && reservation.getEndTime().isAfter(hourEnd)) {
                    availableTablesThisHour.remove(reservation.getTable());
                }
            }
            availableTables.put(hourStart.getHour(), availableTablesThisHour);
        }

        return availableTables;
    }
}