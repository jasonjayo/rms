import java.util.ArrayList;

public class RestaurantManagementSystemSimulation {
    public static void main(String[] args) {

        RestaurantManagementSystem rms = new RestaurantManagementSystem();
        RestaurantManagementSystemMenu cliMenu = new RestaurantManagementSystemMenu();
        cliMenu.run(rms);

    }
}
