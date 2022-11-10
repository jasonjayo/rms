import java.util.ArrayList;

public class RestaurantManagementSystem {

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    public RestaurantManagementSystem() {
        try {
            // create restaurants in chain
            Restaurant myRestaurant = new Restaurant("Seaside Delight", 1, new Menu(), new ArrayList<>());
            restaurants.add(myRestaurant);
            Restaurant anotherRestaurant = new Restaurant("Crab Hut", 2, new Menu(), new ArrayList<>());
            restaurants.add(anotherRestaurant);
        } catch (NoTablesFileException e) {
            System.out.println("Failed to create restaurants. The tables data file is missing.");
        }
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
