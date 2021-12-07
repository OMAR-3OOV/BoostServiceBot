package system.utilities.manager;

import net.dv8tion.jda.api.entities.Emoji;

public enum Categories {

    MANAGEMENT(0, "management", "Only managers & adamins can use this category commands", Emoji.fromUnicode("\uD83D\uDEE1")),
    INFORMATION(1, "information", "Information commands", Emoji.fromUnicode("\uD83D\uDCD6"));

    private int id;
    private String name;
    private String description;
    private Emoji emoji;

    Categories(int id, String name, String description, Emoji emoji) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Emoji getEmoji() {
        return emoji;
    }
}
