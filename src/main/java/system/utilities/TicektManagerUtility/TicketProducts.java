package system.utilities.TicektManagerUtility;

import java.io.File;
import java.util.*;

public class TicketProducts {

    private final File file;
    private final Properties properties;

    private final List<String> bundles;
    private final HashMap<String, String> products = new HashMap<>();

    public TicketProducts() {
        this.file = new File("Database/Guilds/Products");
        this.properties = new Properties();
        this.bundles = new ArrayList<>();
    }

    public File getFile() {
        return file;
    }

    public Properties getProperties() {
        return properties;
    }

    public HashMap<String, String> getProducts() {
        return products;
    }

    public void addProduct(String bundleName, String ProductName) {
        this.products.put(bundleName, ProductName);
    }

}
