import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Jason Gill
 */
public class Restaurant {

    private int id;
    private String name;
    private Menu menu = new Menu();
    private ArrayList<Table> tables = new ArrayList<>();
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

        }
        catch (FileNotFoundException e) {
            throw new NoTablesFileException("Missing tables file");
        }

    }
}