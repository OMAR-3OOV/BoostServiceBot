package system.utilities.TicektManagerUtility;

import net.dv8tion.jda.api.entities.Guild;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.Languages;
import system.utilities.languageManager.MessageKeys;

import java.io.IOException;
import java.util.Arrays;

public enum Bundles {

    BOTS("bundle-bots", MessageKeys.TICKET_BUNDLE_BOTS_NAME, MessageKeys.TICKET_BUNDLE_BOTS_DESCRIPTION, MessageKeys.TICKET_BUNDLE_BOTS_EMOJI),
    BOOSTERS("bundle-boosters", MessageKeys.TICKET_BUNDLE_BOOSTER_NAME, MessageKeys.TICKET_BUNDLE_BOOSTER_DESCRIPTION, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    ;

    private LanguageManager languageManager;
    private final String key;
    private final MessageKeys name;
    private final MessageKeys description;
    private final MessageKeys emoji;

    Bundles(String key, MessageKeys name, MessageKeys description, MessageKeys emoji) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public String getKey() {
        return key;
    }

    public MessageKeys getName() {
        return name;
    }

    public String getName(Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(name);
    }

    public MessageKeys getDescription() {
        return description;
    }

    public MessageKeys getEmoji() {
        return emoji;
    }

    public String toLanguageName(Languages language, Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(language, this.name);
    }

    public String toLanguageDescription(Languages language, Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(language, this.description);
    }

    public String toLanguageEmoji(Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(this.emoji);
    }

    public static Bundles getBundleByKey(String key) {
        return Arrays.stream(Bundles.values()).filter(f -> f.key.equalsIgnoreCase(key)).findFirst().get();
    }
}
