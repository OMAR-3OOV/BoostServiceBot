package system.utilities.TicektManagerUtility;

import net.dv8tion.jda.api.entities.Guild;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.MessageKeys;

import java.io.IOException;
import java.util.Arrays;

public enum Products {

    BOT_PROTECTION(Bundles.BOTS, "bot-protection", 5, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_PROTECTION_NAME, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_PROTECTION_DESCRIPTION, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_PROTECTION_EMOJI),
    BOT_MUSIC(Bundles.BOTS, "bot-music", 2, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_MUSIC_NAME, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_MUSIC_DESCRIPTION, MessageKeys.TICKET_BUNDLE_BOTS_PRODUCT_MUSIC_EMOJI),

    BOOSTER_1MONTH_2_NAME(Bundles.BOOSTERS, "booster-1month-2-name", 4, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_1MONTH_2_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    BOOSTER_1MONTH_7_NAME(Bundles.BOOSTERS, "booster-1month-4-name", 15, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_1MONTH_7_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    BOOSTER_1MONTH_14_NAME(Bundles.BOOSTERS, "booster-1month-7-name", 25, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_1MONTH_14_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),

    BOOSTER_3MONTH_2_NAME(Bundles.BOOSTERS, "booster-3month-2-name", 10, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_3MONTH_2_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    BOOSTER_3MONTH_7_NAME(Bundles.BOOSTERS, "booster-3month-4-name", 35, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_3MONTH_7_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    BOOSTER_3MONTH_14_NAME(Bundles.BOOSTERS, "booster-3month-7-name", 65, MessageKeys.TICKET_BUNDLE_BOOSTER_PRODUCT_3MONTH_14_NAME, MessageKeys.EMPTY, MessageKeys.TICKET_BUNDLE_BOOSTER_EMOJI),
    ;

    private LanguageManager languageManager;
    private final Bundles buidle;
    private final String key;
    private final int price;
    private final MessageKeys name;
    private final MessageKeys description;
    private final MessageKeys emoji;

    Products(Bundles bundles, String key, int price, MessageKeys name, MessageKeys description, MessageKeys emoji) {
        this.buidle = bundles;
        this.key = key;
        this.price = price;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public Bundles getBuidle() {
        return buidle;
    }

    public String getKey() {
        return key;
    }

    public int getPrice() {
        return price;
    }

    public MessageKeys getName() {
        return name;
    }

    public MessageKeys getDescription() {
        return description;
    }

    public MessageKeys getEmoji() {
        return emoji;
    }

    public String toLanguageName(Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(this.name);
    }

    public String toLanguageDescription(Guild guild) throws IOException {
        languageManager = new LanguageManager(guild);
        return languageManager.getMessage(this.description);
    }

    public static Products getProductByKey(String key) {
        return Arrays.stream(Products.values()).filter(f -> f.getKey().equals(key)).findFirst().get();
    }
}
