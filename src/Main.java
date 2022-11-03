import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            Restaurant myRestaurant = new Restaurant("Seaside Delight", 1, new Menu(), new ArrayList<>());
            System.out.println(myRestaurant.getTables());
        }
        catch(NoTablesFileException e) {
            System.out.println("Failed to create restaurants. The tables data file is missing.");
        }
    }
}