import java.lang.reflect.Array;
import java.util.ArrayList;

public class RestaurantManagementSystem {

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    public RestaurantManagementSystem() {
        try {
            Restaurant myRestaurant = new Restaurant("Seaside Delight", 1, new Menu(), new ArrayList<>());
            restaurants.add(myRestaurant);
            System.out.println(myRestaurant.getTables());
        } catch (NoTablesFileException e) {
            System.out.println("Failed to create restaurants. The tables data file is missing.");
        }
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
