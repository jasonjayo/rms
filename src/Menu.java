import java.util.ArrayList;
import java.util.TreeMap;

public class Menu {

    TreeMap<String, ArrayList<MenuCategory>> menu = new TreeMap<>();


    public Menu(TreeMap<String, ArrayList<MenuCategory>> menu) {
        this.menu = menu;
    }

    public TreeMap<String, ArrayList<MenuCategory>> getMenu() {
        return menu;
    }
}