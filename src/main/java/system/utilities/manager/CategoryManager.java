package system.utilities.manager;

import net.dv8tion.jda.api.entities.Emoji;

public final class CategoryManager {

    private final String name;
    private final String description;
    private final Emoji emoji;

    public CategoryManager(Categories categories) {
        this.name = categories.getName();
        this.description = categories.getDescription();
        this.emoji = categories.getEmoji();
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
