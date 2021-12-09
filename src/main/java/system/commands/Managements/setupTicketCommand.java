package system.commands.Managements;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.TicektManagerUtility.TicketUtility;
import system.utilities.languageManager.Languages;
import system.utilities.manager.Categories;
import system.utilities.manager.Command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class setupTicketCommand implements Command {

    @Override
    public void handle(List<String> args, MessageReceivedEvent event) throws IOException {
        try {
            GuildManager guildManager = new GuildManager(event.getGuild());

            if (!guildManager.getSetup()) {
                Category category = null;
                TextChannel textChannel = null;

                if (event.getGuild().getCategoryCache().stream().anyMatch(cate -> cate.getName().equalsIgnoreCase("support"))) {
                    category = event.getGuild().getCategoriesByName("support", false).stream().findFirst().get();
                    guildManager.setCategoryTicket(category.getId());
                    guildManager.setCategoryTickets(category.getId());
                } else {
                    category = event.getGuild().createCategory("support").complete();
                    guildManager.setCategoryTicket(category.getId());
                    guildManager.setCategoryTickets(category.getId());
                }

                if (event.getGuild().getTextChannels().stream().anyMatch(tc -> tc.getName().equalsIgnoreCase("ticket") && tc.getParentCategory().getName().equalsIgnoreCase("support"))) {
                    textChannel = event.getGuild().getTextChannelsByName("ticket", false).stream().findFirst().get();
                    guildManager.setChannelTicket(textChannel.getId());
                } else {
                    textChannel = category.createTextChannel("ticket").complete();
                    textChannel.createPermissionOverride(event.getGuild().getRoleCache().stream().filter(f -> f.isPublicRole()).findFirst().get()).setDeny(Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.CREATE_PUBLIC_THREADS, Permission.CREATE_PRIVATE_THREADS, Permission.MANAGE_THREADS).queue();
                    guildManager.setChannelTicket(textChannel.getId());
                }

                if (guildManager.getMessageChannel() == null) {
                    TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

                    List<SelectOption> options = new ArrayList<>();
                    Arrays.stream(Languages.values()).forEach(languages -> {
                        options.add(SelectOption.of(languages.getDisplayName(),"option-open-language-"+languages.getKey()).withEmoji(languages.getEmoji()));
                    });

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-language");
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(options);

                    Message message = textChannel.sendMessageEmbeds(ticketUtility.embedTicketMessage().build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(Button.primary("open-ticket", "Open ticket"))).complete();
                    guildManager.setMessageTicket(message.getId());
                    guildManager.setSetup(true);
                } else {
                    event.getMessage().reply("Ticket message is already exist in " + guildManager.getTicketChannel().getAsMention()).queue();
                }
            } else {
                Category category = null;
                TextChannel textChannel = null;

                if (event.getGuild().getCategoryCache().stream().noneMatch(cate -> cate.getName().equalsIgnoreCase("support"))) {
                    category = event.getGuild().createCategory("support").complete();
                    guildManager.setCategoryTicket(category.getId());
                } else {
                    category = guildManager.getTicketCategory();
                }

                if (event.getGuild().getTextChannels().stream().noneMatch(tc -> tc.getName().equalsIgnoreCase("ticket"))) {
                    textChannel = category.createTextChannel("ticket").complete();
                    textChannel.createPermissionOverride(event.getGuild().getRoleCache().stream().filter(f -> f.isPublicRole()).findFirst().get()).setDeny(Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.CREATE_PUBLIC_THREADS, Permission.CREATE_PUBLIC_THREADS, Permission.MANAGE_THREADS).queue();
                    guildManager.setChannelTicket(textChannel.getId());
                } else {
                    assert category != null;
                    textChannel = event.getGuild().getTextChannels().stream().filter(f -> f.getName().equalsIgnoreCase("ticket")).findFirst().get();
                }

                if (guildManager.getMessageChannel() == null) {
                    TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

                    List<SelectOption> options = new ArrayList<>();
                    Arrays.stream(Languages.values()).forEach(languages -> {
                        options.add(SelectOption.of(languages.getDisplayName(),"option-open-language-"+languages.getKey()).withEmoji(languages.getEmoji()));
                    });

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-language");
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(options);

                    Message message = textChannel.sendMessageEmbeds(ticketUtility.embedTicketMessage().build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(Button.primary("open-ticket", "Open ticket"))).complete();
                    guildManager.setMessageTicket(message.getId());
                    guildManager.setSetup(true);
                } else {
                    event.getMessage().reply("Ticket message is already exist in " + guildManager.getTicketChannel().getAsMention()).queue();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException {

    }

    @Override
    public String getHelp() {
        return Core.prefix + "setup";
    }

    @Override
    public String getInVoke() {
        return "setup";
    }

    @Override
    public Categories getCategory() {
        return Categories.MANAGEMENT;
    }

    @Override
    public String getDescription() {
        return "to setup the ticket channel and category";
    }

    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }
}
