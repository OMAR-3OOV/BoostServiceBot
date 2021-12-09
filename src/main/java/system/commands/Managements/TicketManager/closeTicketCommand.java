package system.commands.Managements.TicketManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.TicektManagerUtility.TicketCreateUtility;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.MessageKeys;
import system.utilities.manager.Categories;
import system.utilities.manager.Command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//abstract
public class closeTicketCommand implements Command {

    public static HashMap<User, TextChannel> ticketchannel = new HashMap<>();
    public static HashMap<User, String> reason = new HashMap<>();

    @Override
    public void handle(List<String> args, MessageReceivedEvent event) throws IOException {
        TicketCreateUtility ticketCreate = new TicketCreateUtility(event.getTextChannel(), event.getGuild());

        if (ticketCreate.getTextChannel() != null) {
            try {
                LanguageManager languageManager = new LanguageManager(event.getGuild());
                GuildManager guildManager = new GuildManager(event.getGuild());

                if (ticketCreate.getTextChannel().getId().equalsIgnoreCase(event.getChannel().getId())) {
                    List<String> handlers = new ArrayList<>(args);

                    StringBuilder reason = new StringBuilder();

                    for (String r : handlers) {
                        reason.append(r).append(" ");
                    }

                    User customer = ticketCreate.getCreatorByProperties();

                    closeTicketCommand.reason.put(customer, reason.toString());
                    ticketCreate.getTextChannel().delete().queue();
                    ticketchannel.put(customer, ticketCreate.getTextChannel());
                } else {
                    event.getMessage().reply(languageManager.getMessage(MessageKeys.TICKET_CLOSE_ERRORMESSAGE)).queue();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException {

    }

    @Override
    public String getHelp() {
        return Core.prefix + "close";
    }

    @Override
    public String getInVoke() {
        return "close";
    }

    @Override
    public Categories getCategory() {
        return Categories.MANAGEMENT;
    }

    @Override
    public String getDescription() {
        return "to close a ticket";
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_CHANNEL;
    }
}
