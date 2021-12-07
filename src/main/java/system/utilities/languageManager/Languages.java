package system.utilities.languageManager;

import net.dv8tion.jda.api.entities.Emoji;
import org.jetbrains.annotations.*;

import java.util.Arrays;

public enum Languages {

    ARABIC(0, "ar","arabic", "العربية", Emoji.fromUnicode("\uD83C\uDDF8\uD83C\uDDE6")),
    ENGLISH(1, "en", "english", "English", Emoji.fromUnicode("\uD83C\uDDEC\uD83C\uDDE7"));

    private final int id;
    private final String code;
    private final String key;
    private final String displayName;
    private final Emoji emoji;

    Languages(int id, String code, String key, String displayName, Emoji emoji) {
        this.id = id;
        this.code = code;
        this.key = key;
        this.displayName = displayName;
        this.emoji = emoji;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public String getCode() {
        return code;
    }

    @Contract(pure = true)
    public String getKey() {
        return key;
    }

    @Contract(pure = true)
    public String getDisplayName() {
        return displayName;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    @Contract(pure = false)
    public static Languages getLanguage(int id) {
        return Languages.values()[id];
    }

    public static Languages getLanguageFromKey(String key) {
        return Arrays.stream(Languages.values()).filter(t -> t.getKey().equalsIgnoreCase(key)).findFirst().get();
    }

    public static Languages getLanguageFromId(int id) {
        return Arrays.stream(Languages.values()).filter(t -> t.getId()==id).findFirst().get();
    }

    public static Languages getLanguageFromCode(String code) {
        return Arrays.stream(Languages.values()).filter(t -> t.getCode().equalsIgnoreCase(code)).findFirst().get();
    }
}
