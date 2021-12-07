package system.utilities.TicektManagerUtility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import system.listeners.TicketManagerEvent;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.Languages;
import system.utilities.languageManager.MessageKeys;

import java.awt.*;
import java.io.IOException;

public class TicketUtility {

    private final GuildManager guildManager;

    private final Guild guild;

    /* Related to ticket manager */

    private final Category category;
    private TextChannel textChannel;
    private final Message message;

    public TicketUtility(TextChannel textChannel, Guild guild) throws IOException {
        this.guild = guild;
        guildManager = new GuildManager(guild);

        this.category = guildManager.getTicketCategory();
        this.textChannel = guildManager.getTicketChannel();
        this.message = guildManager.getMessageChannel();
        this.textChannel = textChannel;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public Category getCategory() {
        return category;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Message getMessage() {
        return message;
    }

    public EmbedBuilder embedTicketMessage() throws IOException {
        LanguageManager languageManager = new LanguageManager(this.guild);
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(languageManager.getMessage(languageManager.getGuildManager().getLanguage(), MessageKeys.TICKET_MESSAGE_TITLE));
        embed.setColor(new Color(254, 204, 157));
        embed.setDescription(languageManager.getMessage(languageManager.getGuildManager().getLanguage(), MessageKeys.TICKET_MESSAGE_DESCRIPTION));
        embed.setFooter(languageManager.getMessage(languageManager.getGuildManager().getLanguage(), MessageKeys.TICKET_MESSAGE_FLOOR_ICON) + " " + languageManager.getMessage(MessageKeys.TICKET_MESSAGE_FLOOR));
        return embed;
    }

    public EmbedBuilder embedTicketOpenMessage(User user, Guild guild) throws IOException {
        LanguageManager languageManager = new LanguageManager(this.guild);
        EmbedBuilder embed = new EmbedBuilder();

        if (TicketManagerEvent.lang.containsKey(user)) {
            embed.setColor(new Color(187, 255, 148));
            embed.setTitle(languageManager.getMessage(TicketManagerEvent.lang.get(user), MessageKeys.TICKET_OPEN_TITLE, user, guild));
            embed.setDescription(languageManager.getMessage(TicketManagerEvent.lang.get(user), MessageKeys.TICKET_OPEN_DESCRIPTION_UNSELECTED) + "\n");
            embed.setFooter(languageManager.getMessage(TicketManagerEvent.lang.get(user), MessageKeys.TICKET_OPEN_FLOOR));
        } else {
            TicketCreateUtility ticketCreate =  new TicketCreateUtility(this.textChannel, this.guild);
            System.out.println(ticketCreate.getProperties().getProperty("language"));

            Languages languages = Languages.getLanguageFromCode(ticketCreate.getProperties().getProperty("language"));

            embed.setColor(new Color(187, 255, 148));
            embed.setTitle(languageManager.getMessage(languages, MessageKeys.TICKET_OPEN_TITLE, user, guild));
            embed.setDescription(languageManager.getMessage(languages, MessageKeys.TICKET_OPEN_DESCRIPTION_UNSELECTED) + "\n");
            embed.setFooter(languageManager.getMessage(languages, MessageKeys.TICKET_OPEN_FLOOR));

            TicketManagerEvent.lang.put(user, languages);
        }
        return embed;
    }
}
