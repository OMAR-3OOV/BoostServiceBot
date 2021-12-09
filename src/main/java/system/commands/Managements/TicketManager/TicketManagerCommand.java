package system.commands.Managements.TicketManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.MessageKeys;
import system.utilities.manager.Categories;
import system.utilities.manager.Command;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketManagerCommand implements Command {

    public static HashMap<User, Message> Message = new HashMap<>();
    public static HashMap<Message, EmbedBuilder> Embed = new HashMap<>();

    @Override
    public void handle(List<String> args, MessageReceivedEvent event) throws IOException {

        User user = event.getAuthor();

        if (Message.containsKey(user)) {
            Message.get(user).delete().queue();
            Embed.remove(Message.remove(user));
        }

        GuildManager guildManager = new GuildManager(event.getGuild());
        LanguageManager languageManager = new LanguageManager(event.getGuild());

        EmbedBuilder embed = new EmbedBuilder();

        if (guildManager.getMaintenance()) {
            embed.setColor(new Color(255, 20, 20));
        } else {
            embed.setColor(new Color(255, 176, 106));
        }

        embed.setTitle("Ticket Control panel " + Emoji.fromUnicode("\uD83D\uDCE7").getName());

        StringBuilder description = embed.getDescriptionBuilder();

        if (guildManager.getTicketsCategory() == null) {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
            description.append("\n");
        } else {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
            description.append("\n");
        }

        if (guildManager.getTicketCategory() != null) {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
            description.append("\n");
        } else {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
            description.append("\n");
        }

        System.out.println(guildManager.getTicketChannel());
        if (guildManager.getTicketChannel() != null) {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
            description.append("\n");
        } else {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
            description.append("\n");
        }

        System.out.println(guildManager.getMessageChannel());
        if (guildManager.getMessageChannel() == null) {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
            description.append("\n \n");

            if ((guildManager.getTicketChannel() == null || guildManager.getMessageChannel() == null)) {
                description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix));
            }
        } else {
            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
        }

        embed.setDescription(description.toString());
        embed.setFooter(languageManager.getMessage(MessageKeys.TICKET_CONTORLPANEL_FLOOR));

        List<SelectOption> menuOption = new ArrayList<>();
        menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
        menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
        menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
        menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

        SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-"+user.getId());
        menu.setRequiredRange(1, 1);
        menu.addOptions(menuOption);

        Message message = null;
        if (guildManager.getMessageChannel() != null) {
            message = event.getChannel().sendMessageEmbeds(embed.build()).setActionRow(menu.build()).complete();
        } else {
            message = event.getChannel().sendMessageEmbeds(embed.build()).setActionRows( ActionRow.of(menu.build())).complete();
        }

        Message.put(user, message);
        Embed.put(message, embed);

        event.getMessage().delete().queue();
    }

    @Override
    public void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException {

    }

    @Override
    public String getHelp() {
        return Core.prefix + "ticketmanager";
    }

    @Override
    public String getInVoke() {
        return "controlpanel";
    }

    @Override
    public Categories getCategory() {
        return Categories.MANAGEMENT;
    }

    @Override
    public String getDescription() {
        return "control panel for ticket manager system";
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_CHANNEL;
    }
}
