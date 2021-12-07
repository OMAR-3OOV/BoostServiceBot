package system.utilities.StringUtilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class GenerateStringAPI {

    HashMap<Guild, String> map;
    String key;
    int number;

    public GenerateStringAPI(Guild guild ,int number) {
        this.map = new HashMap<>();
        this.key = generateKey(number);
        this.number = number;

        map.put(guild, this.key);
    }

    public String getKey() {
        return key;
    }

    public HashMap<Guild, String> getMap() {
        return map;
    }

    public String regenerate() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(this.number);

        for (int i = 0; i < this.number; i++) {
            int index = (int) (characters.length()*Math.random());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    private String generateKey(int number) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(number);

        for (int i = 0; i < number; i++) {
            int index = (int) (characters.length()*Math.random());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
