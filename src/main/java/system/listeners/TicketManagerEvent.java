package system.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import system.Core;
import system.commands.Managements.TicketManager.TicketManagerCommand;
import system.commands.Managements.TicketManager.closeTicketCommand;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.TicektManagerUtility.*;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.Languages;
import system.utilities.languageManager.MessageKeys;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketManagerEvent extends ListenerAdapter {

    public HashMap<User, Message> detector_channelchange = new HashMap<>();
    public HashMap<TextChannel, String> ticketschannels = new HashMap<>();
    public static HashMap<User, Message> ticketsCategory = new HashMap<>();

    public HashMap<User, Message> messageOrder = new HashMap<>();
    public HashMap<Message, EmbedBuilder> messageOrderEmebed = new HashMap<>();

    public static HashMap<User, Languages> lang = new HashMap<>();

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        try {

            System.out.println(event.getInteraction().getSelectionMenu().getId());

            User user = event.getUser();
            GuildManager guildManager = new GuildManager(event.getGuild());

            LanguageManager languageManager = new LanguageManager(event.getGuild());
            TicketCreateUtility ticketCreate = new TicketCreateUtility(event.getTextChannel(), event.getGuild());
            TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

            if (Objects.requireNonNull(event.getInteraction().getSelectionMenu().getId()).equalsIgnoreCase("ticket-open-select-language")) {
                Arrays.stream(Languages.values()).forEach(languages -> {

                    Objects.requireNonNull(event.getInteraction().getSelectedOptions()).forEach(v -> {
                        if (v.getValue().equalsIgnoreCase("option-open-language-" + languages.getKey())) {
                            lang.put(event.getUser(), languages);

                            event.getInteraction().deferReply().setEphemeral(true).queue(q -> q.editOriginal("language has been changed to " + languages.getDisplayName()).queue());
                        }
                    });

                });
            } else if (TicketManagerCommand.Message.containsKey(user)) {
                if (Objects.requireNonNull(event.getInteraction().getSelectionMenu()).getId().equalsIgnoreCase("ticket-controlpanel-" + user.getId())) {
                    Message message = TicketManagerCommand.Message.get(user);

                    List<SelectOption> menuOption = new ArrayList<>();
                    menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                    menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                    menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                    menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(menuOption);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);

                    for (SelectOption v : Objects.requireNonNull(event.getSelectedOptions())) {
                        if (v.getValue().equalsIgnoreCase("ticket-control-panel-main")) {

                            try {
                                Message msg = event.getInteraction().getMessage();

                                if (guildManager.getMaintenance()) {
                                    embed.setColor(new Color(255, 20, 20));
                                } else {
                                    embed.setColor(new Color(255, 176, 106));
                                }

                                embed.setDescription(null);
                                StringBuilder description = embed.getDescriptionBuilder();

                                if (guildManager.getTicketsCategory() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getTicketCategory() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getTicketChannel() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getMessageChannel() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                                    description.append("\n \n");

                                    if (guildManager.getTicketChannel() != null) {
                                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                                    }
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                                }
                                embed.setDescription(description.toString());

                                if (!event.getInteraction().getMessage().getButtons().isEmpty()) {
                                    List<Button> buttons = new ArrayList<>();
                                    event.getInteraction().getMessage().getButtons().forEach(b -> {
                                        buttons.add(b.asDisabled());
                                    });

                                    event.getInteraction().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();
                                } else {
                                    event.getInteraction().editMessageEmbeds(embed.build()).queue();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (v.getValue().equalsIgnoreCase("ticket-control-panel-history")) {
                            try {
                                embed.setDescription(null);

                                StringBuilder description = embed.getDescriptionBuilder();

                                description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETHISTORY_DESCRIPTION_TOTALTICKETS).replaceAll("<total_tickets>", String.valueOf(guildManager.getTotalTickets()))).append("\n");

                                if (ticketCreate.getFolder().exists()) {
                                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETHISTORY_DESCRIPTION_TICKETHOLD).replaceAll("<tickets-onhold>", String.valueOf(Arrays.stream(ticketCreate.getFolder().listFiles()).count()))).append("\n");
                                } else {
                                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETHISTORY_DESCRIPTION_TICKETHOLD).replaceAll("<tickets-onhold>", "0")).append("\n");
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (!event.getInteraction().getMessage().getButtons().isEmpty()) {
                                List<Button> buttons = new ArrayList<>();
                                event.getInteraction().getMessage().getButtons().forEach(b -> {
                                    buttons.add(b.asDisabled());
                                });

                                event.getInteraction().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();
                            } else {
                                event.getInteraction().editMessageEmbeds(embed.build()).queue();
                            }
                        }
                        else if (v.getValue().equalsIgnoreCase("ticket-control-panel-manager")) {
                            try {
                                Message msg = event.getInteraction().getMessage();

                                String prefix = Core.prefix;
                                embed.setDescription(null);
                                StringBuilder description = embed.getDescriptionBuilder();

                                if (guildManager.getMaintenance()) {
                                    embed.setColor(new Color(255, 20, 20));
                                } else {
                                    embed.setColor(new Color(255, 176, 106));
                                }

                                if (guildManager.getTicketsCategory() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getTicketCategory() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getTicketChannel() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                                    description.append("\n");
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                                    description.append("\n");
                                }

                                if (guildManager.getMessageChannel() == null) {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                                    description.append("\n \n");

                                    if (guildManager.getTicketChannel() != null) {
                                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                                    }
                                } else {
                                    description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                                }

                                description.append("```yml").append("\n \n");
                                description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                                description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                                description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                                description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                                description.append("\n```");

                                embed.setDescription(description.toString());

                                List<Button> buttons = new ArrayList<>();
                                buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));
                                buttons.add(Button.primary("ticket-controlpanel-button-ticketscategory-" + user.getId(), "Change ticket category"));

                                if (!guildManager.getMaintenance()) {
                                    buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                                } else {
                                    buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                                }

                                event.getInteraction().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).submit().whenComplete((then, eee)-> then.setEphemeral(false)).join();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (v.getValue().equalsIgnoreCase("ticket-control-panel-delete")) {
                            if (!event.getInteraction().isAcknowledged()) {
                                event.getInteraction().getMessage().delete().queue();
                            }

                            TicketManagerCommand.Embed.remove(message);
                            TicketManagerCommand.Message.remove(event.getUser());
                        }
                    }

                    if (event.getInteraction().getSelectedOptions().stream().noneMatch(all -> all.getValue().equalsIgnoreCase("ticket-control-panel-delete"))) {
                        if (!event.getInteraction().isAcknowledged()) {
                            event.getInteraction().deferEdit().queue();
                        }
                    }
                }
            } else if (messageOrder.containsKey(event.getUser()) || event.getMessage().equals(ticketCreate.getMessage())) {

                var ref = new Object() {
                    Message message = null;
                };

                if (messageOrder.containsKey(event.getUser())) {
                    ref.message = messageOrder.get(event.getUser());
                } else {
                    ref.message = ticketCreate.getMessage();
                }

                CartUtility cart = new CartUtility(event.getUser(), event.getTextChannel(), ref.message);

                Objects.requireNonNull(event.getInteraction().getSelectedOptions()).forEach(v -> {
                    if (Objects.equals(event.getInteraction().getSelectionMenu().getId(), "ticket-open-select-" + event.getUser().getId())) {
                        for (Bundles bundle : Bundles.values()) {
                            if (v.getValue().equalsIgnoreCase(bundle.getKey() + "-" + event.getUser().getId())) {
                                try {
                                    EmbedBuilder embed = ticketUtility.embedTicketOpenMessage(event.getUser(), event.getGuild());

                                    embed.clearFields();
                                    embed.setDescription(null);

                                    StringBuilder description = embed.getDescriptionBuilder();
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_SELECTED));
                                    description.append("\n").append(" ").append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_BUNDLE).replaceAll("<bundle>", languageManager.getMessage(lang.get(event.getUser()), bundle.getName())));
                                    description.append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_PRODUCTS).replaceAll("<products>", "-"));

                                    try {
                                        ticketCreate.setBundle(bundle);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    List<SelectOption> options = new ArrayList<>();
                                    Arrays.stream(Products.values()).filter(f -> f.getBuidle().equals(bundle)).forEach(products -> {
                                        try {
                                            options.add(SelectOption.of(languageManager.getMessage(lang.get(event.getUser()), products.getName()).replaceAll("<price>", String.valueOf(products.getPrice())), "ticket-open-select-" + products.getKey() + "-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode(languageManager.getMessage(products.getEmoji()))));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    options.add(SelectOption.of("back", "ticket-open-select-back-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("⬅")));

                                    Button button = Button.danger("ticket-open-select-close", "Close").withEmoji(Emoji.fromUnicode("✖"));

                                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-" + bundle.getKey() + "-" + event.getUser().getId());
                                    menu.addOptions(options);
                                    menu.setRequiredRange(1, 1);

                                    ref.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(button)).queue();

                                    if (!event.getInteraction().isAcknowledged()) {
                                        event.getInteraction().deferEdit().queue();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (event.getInteraction().getSelectionMenu().getId().equalsIgnoreCase("ticket-open-select-bundle-bots-" + event.getUser().getId())) {
                        for (Products products : Arrays.stream(Products.values()).filter(f -> f.getBuidle().equals(Bundles.BOTS)).toList()) {
                            if (v.getValue().equalsIgnoreCase("ticket-open-select-" + products.getKey() + "-" + user.getId())) {
                                try {
                                    EmbedBuilder embed = ticketUtility.embedTicketOpenMessage(event.getUser(), event.getGuild());

                                    embed.clearFields();
                                    embed.setDescription(null);

                                    StringBuilder description = embed.getDescriptionBuilder();
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_SELECTED));
                                    description.append("\n").append(" ").append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_BUNDLE).replaceAll("<bundle>", languageManager.getMessage(lang.get(event.getUser()), Bundles.BOTS.getName())));
                                    description.append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_PRODUCTS).replaceAll("<products>", Matcher.quoteReplacement(languageManager.getMessage(lang.get(event.getUser()), products.getName()).replaceAll("<price>", String.valueOf(products.getPrice())))));

                                    List<SelectOption> options = new ArrayList<>();
                                    options.add(SelectOption.of("back", "ticket-open-select-back-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("⬅")));

                                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-bundle-bot-" + event.getUser().getId());
                                    menu.addOptions(options);
                                    menu.setRequiredRange(1, 1);

                                    List<Button> button = new ArrayList<>();
                                    button.add(Button.success("ticket-open-check-out-" + user.getId(), "Check out"));
                                    button.add(Button.danger("ticket-open-select-close", "Close").withEmoji(Emoji.fromUnicode("✖")));

                                    cart.getCart().get(user).setBundles(Bundles.BOTS);
                                    cart.getCart().get(user).setProducts(products);


                                    try {
                                        ticketCreate.getProperties().setProperty("cart.bundle", cart.getCart().get(user).getBundles().getKey());
                                        ticketCreate.getProperties().setProperty("cart.product", cart.getCart().get(user).getProducts().getKey());
                                        ticketCreate.getProperties().setProperty("cart.price", String.valueOf(products.getPrice()));
                                        ticketCreate.save(ticketCreate.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ref.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(button)).queue();

                                    if (!event.getInteraction().isAcknowledged()) {
                                        event.getInteraction().deferEdit().queue();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (event.getInteraction().getSelectionMenu().getId().equalsIgnoreCase("ticket-open-select-bundle-boosters-" + event.getUser().getId())) {
                        for (Products products : Arrays.stream(Products.values()).filter(f -> f.getBuidle().equals(Bundles.BOOSTERS)).toList()) {
                            if (v.getValue().equalsIgnoreCase("ticket-open-select-" + products.getKey() + "-" + user.getId())) {
                                try {
                                    EmbedBuilder embed = ticketUtility.embedTicketOpenMessage(event.getUser(), event.getGuild());

                                    embed.clearFields();
                                    embed.setDescription(null);

                                    StringBuilder description = embed.getDescriptionBuilder();
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_SELECTED));
                                    description.append("\n").append(" ").append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_BUNDLE).replaceAll("<bundle>", Matcher.quoteReplacement(languageManager.getMessage(lang.get(event.getUser()), Bundles.BOOSTERS.getName()))));
                                    description.append("\n");
                                    description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_PRODUCTS).replaceAll("<products>", Matcher.quoteReplacement(languageManager.getMessage(lang.get(event.getUser()), products.getName()).replaceAll("<price>", String.valueOf(products.getPrice())))));

                                    List<SelectOption> options = new ArrayList<>();
                                    options.add(SelectOption.of("back", "ticket-open-select-back-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("⬅")));

                                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-bundle-bot-" + event.getUser().getId());
                                    menu.addOptions(options);
                                    menu.setRequiredRange(1, 1);

                                    List<Button> button = new ArrayList<>();
                                    button.add(Button.success("ticket-open-check-out-" + user.getId(), "Check out"));
                                    button.add(Button.danger("ticket-open-select-close", "Close").withEmoji(Emoji.fromUnicode("✖")));

                                    cart.getCart().get(user).setBundles(Bundles.BOOSTERS);
                                    cart.getCart().get(user).setProducts(products);

                                    try {
                                        ticketCreate.getProperties().setProperty("cart.bundle", cart.getCart().get(user).getBundles().getKey());
                                        ticketCreate.getProperties().setProperty("cart.product", cart.getCart().get(user).getProducts().getKey());
                                        ticketCreate.getProperties().setProperty("cart.price", String.valueOf(products.getPrice()));
                                        ticketCreate.save(ticketCreate.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ref.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(button)).queue();

                                    if (!event.getInteraction().isAcknowledged()) {
                                        event.getInteraction().deferEdit().queue();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    try {
                        if (v.getValue().equalsIgnoreCase("ticket-open-select-back-" + event.getUser().getId())) {
                            for (Bundles bundle : Bundles.values()) {
                                EmbedBuilder embed = ticketUtility.embedTicketOpenMessage(event.getUser(), event.getGuild());

                                embed.clearFields();
                                embed.setDescription(null);

                                StringBuilder description = embed.getDescriptionBuilder();
                                description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_SELECTED));
                                description.append("\n").append(" ").append("\n");
                                description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_BUNDLE).replaceAll("<bundle>", "-"));
                                description.append("\n");
                                description.append(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_PRODUCTS).replaceAll("<products>", "-"));

                                List<SelectOption> options = new ArrayList<>();
                                for (Bundles b : Bundles.values()) {
                                    options.add(SelectOption.of(languageManager.getMessage(lang.get(event.getUser()), b.getName()), b.getKey() + "-" + user.getId()).withEmoji(Emoji.fromUnicode(languageManager.getMessage(b.getEmoji()))).withDescription(languageManager.getMessage(lang.get(event.getUser()), b.getDescription())));
                                }

                                List<Button> button = new ArrayList<>();
                                button.add(Button.danger("ticket-open-select-close", "Close").withEmoji(Emoji.fromUnicode("✖")));

                                SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-" + event.getUser().getId());
                                menu.addOptions(options);
                                menu.setRequiredRange(1, 1);

                                cart.getCart().get(user).setBundles(null);
                                cart.getCart().get(user).setProducts(null);

                                try {
                                    ticketCreate.getProperties().setProperty("cart.bundle", "-");
                                    ticketCreate.getProperties().setProperty("cart.product", "-");
                                    ticketCreate.getProperties().setProperty("cart.price", String.valueOf(0));
                                    ticketCreate.save(ticketCreate.getFile());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ref.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(button)).queue();

                                if (!event.getInteraction().isAcknowledged()) {
                                    event.getInteraction().deferEdit().queue();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (event.getInteraction().getSelectionMenu().getId().equalsIgnoreCase("selection-menu-checkout-" + event.getUser().getId())) {
                        EmbedBuilder embed = new EmbedBuilder();

                        Arrays.stream(PaymentMethods.values()).forEach(payment -> {
                            if (v.getValue().equalsIgnoreCase("ticket-checkout-" + payment.getKey() + "-" + event.getUser().getId())) {
                                cart.getCart().get(user).setPayment(payment);

                                try {
                                    ticketCreate.getProperties().setProperty("cart.payment", payment.getKey());
                                    ticketCreate.save(ticketCreate.getFile());

                                    embed.setTitle(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_TITLE));
                                    embed.setDescription(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_DESCRIPTION_SELECTED).replaceAll("<payment_method>", languageManager.getMessage(lang.get(event.getUser()), payment.getDisplayName())).replaceAll("<payment_info>", guildManager.getPaymentLink(payment)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        });

                        event.getInteraction().replyEmbeds(embed.build()).setEphemeral(true).queue();

                        if (!event.getInteraction().isAcknowledged()) {
                            event.getInteraction().deferEdit().queue();
                        }
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage() + ", " + e.getCause());
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {

        try {
            User user = event.getUser();
            GuildManager guildManager = new GuildManager(Objects.requireNonNull(event.getGuild()));
            LanguageManager languageManager = new LanguageManager(event.getGuild());
            TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

            if (guildManager.getMaintenance() && !Objects.requireNonNull(event.getGuild().getMember(event.getUser())).hasPermission(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)) {
                event.getInteraction().reply(languageManager.getMessage(MessageKeys.UNDERMAINTENANCEMODE)).setEphemeral(true).queue();
                return;
            }

            if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).equalsIgnoreCase("open-ticket")) {

                if (!lang.containsKey(event.getUser())) {
                    lang.put(event.getUser(), guildManager.getLanguage());
                }

                Category category = guildManager.getTicketsCategory();
                TextChannel textChannel = null;

                if (category!= null) {
                    textChannel = Objects.requireNonNull(guildManager.getTicketsCategory()).createTextChannel("ticket").complete();
                } else {
                    textChannel = Objects.requireNonNull(Objects.requireNonNull(guildManager.getTicketChannel()).getParentCategory()).createTextChannel("ticket").complete();
                }

                RestAction<PermissionOverride> everyone = textChannel.createPermissionOverride(event.getGuild().getPublicRole());
                RestAction<PermissionOverride> userperm = textChannel.createPermissionOverride(Objects.requireNonNull(event.getMember()));

                everyone.queue(permissionOverride -> {
                    permissionOverride.getManager().setDeny(Permission.VIEW_CHANNEL).queue();
                });

                userperm.queue(permissionOverride -> {
                    permissionOverride.getManager().setAllow(Permission.VIEW_CHANNEL).queue();
                });

                EmbedBuilder embed = ticketUtility.embedTicketOpenMessage(event.getUser(), event.getGuild());

                embed.setColor(new Color(187, 255, 148));
                embed.setTitle(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_TITLE, user, event.getGuild()));
                embed.setDescription(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_DESCRIPTION_UNSELECTED) + "\n");
                embed.setFooter(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_FLOOR));

                List<SelectOption> options = new ArrayList<>();
                Arrays.stream(Bundles.values()).forEach(bundle -> {
                    try {
                        options.add(SelectOption.of(bundle.toLanguageName(lang.get(event.getUser()), event.getGuild()), bundle.getKey() + "-" + user.getId()).withEmoji(Emoji.fromUnicode(bundle.toLanguageEmoji(event.getGuild()))).withDescription(bundle.toLanguageDescription(lang.get(event.getUser()), event.getGuild())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                SelectionMenu.Builder bundleSelectMenu = SelectionMenu.create("ticket-open-select-" + user.getId());
                bundleSelectMenu.setRequiredRange(1, 1);
                bundleSelectMenu.addOptions(options);

                Button button = Button.danger("ticket-open-select-close", "Close").withEmoji(Emoji.fromUnicode("✖"));

                Message message = textChannel.sendMessageEmbeds(embed.build()).setActionRows(ActionRow.of(bundleSelectMenu.build()), ActionRow.of(button)).complete();

                TicketCreateUtility ticketCreate = new TicketCreateUtility(event.getGuild(), event.getUser(), textChannel, message);

                try {
                    ticketCreate.getProperties().setProperty("language", String.valueOf(lang.get(event.getUser()).getCode()));
                    ticketCreate.save(ticketCreate.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                textChannel.getManager().setName("ticket-" + ticketCreate.getTicketId()).queue();

                ticketschannels.put(textChannel, ticketCreate.getTicketId());

                messageOrder.put(ticketCreate.getCreator(), message);
                messageOrderEmebed.put(messageOrder.get(ticketCreate.getCreator()), embed);

                Button jump = Button.link(message.getJumpUrl(), "Jump").withEmoji(Emoji.fromUnicode("\uD83D\uDCE9"));

                EmbedBuilder openMessageTicket = new EmbedBuilder();
                openMessageTicket.setDescription(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_OPEN_JUMPMESSAGE).replaceAll("<textchannel_mention>", textChannel.getAsMention()));

                event.getInteraction().replyEmbeds(openMessageTicket.build()).addActionRow(jump).setEphemeral(true).queue();
                guildManager.addTotalTicket(1);
            }

            if (Objects.requireNonNull(event.getButton().getId()).equalsIgnoreCase("ticket-open-select-close")) {
                TicketCreateUtility ticketsCreate = new TicketCreateUtility(event.getTextChannel(), event.getGuild());

                if (ticketsCreate.getCreatorByProperties().equals(event.getUser())) {
                    ticketsCreate.getTextChannel().delete().queue();
                }
            }

            if (event.getButton().getId().equalsIgnoreCase("ticket-open-check-out-" + event.getUser().getId())) {

                TicketCreateUtility ticketCreate = new TicketCreateUtility(event.getTextChannel(), event.getGuild());

                if (ticketCreate.getCreatorByProperties().equals(event.getUser())) {
                    List<SelectOption> options = new ArrayList<>();
                    options.add(SelectOption.of(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_SELECTION_PAYPAL), "ticket-checkout-paypal-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("\uD83D\uDCB0")));
                    options.add(SelectOption.of(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_SELECTION_BITCOINS), "ticket-checkout-bitcoin-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("\uD83D\uDCB0")));
                    options.add(SelectOption.of(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_SELECTION_CASHAPP), "ticket-checkout-cashapp-" + event.getUser().getId()).withEmoji(Emoji.fromUnicode("\uD83D\uDCB0")));

                    SelectionMenu.Builder menu = SelectionMenu.create("selection-menu-checkout-" + event.getUser().getId());
                    menu.addOptions(options);
                    menu.setRequiredRange(1, 1);

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_TITLE));
                    embed.setDescription(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_DESCRIPTION));
                    embed.setFooter(languageManager.getMessage(lang.get(event.getUser()), MessageKeys.TICKET_CHECKOUT_FLOOR).replaceAll("<date>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date())));

                    event.getInteraction().replyEmbeds(embed.build()).addActionRow(menu.build()).setEphemeral(true).queue();
                }

            }

            if (TicketManagerCommand.Message.containsKey(user)) {
                if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).equalsIgnoreCase("ticket-controlpanel-button-channelchange-" + user.getId())) {
                    Message message = TicketManagerCommand.Message.get(user);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                    embed.setDescription(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_CHANNELCHANGING_DESCRIPTION).replaceAll("<channel_mention>", (guildManager.getTicketChannel() == null ? "`NONE`" : guildManager.getTicketChannel().getAsMention())));

                    if (guildManager.getMaintenance()) {
                        embed.setColor(new Color(255, 20, 20));
                    } else {
                        embed.setColor(new Color(255, 176, 106));
                    }

                    List<Button> buttons = new ArrayList<>();
                    buttons.add(event.getButton().asDisabled());
                    buttons.add(Button.danger("ticket-controlpanel-button-channelchange-close-" + user.getId(), "Close"));

                    event.getInteraction().getMessage().editMessageEmbeds(embed.build()).setActionRow(buttons).queue();

                    detector_channelchange.put(user, message);
                }
                else if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).equalsIgnoreCase("ticket-controlpanel-button-undermaintenance-" + user.getId())) {

                    Message message = TicketManagerCommand.Message.get(user);

                    List<SelectOption> menuOption = new ArrayList<>();
                    menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                    menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withDefault(true).withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                    menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                    menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                    if (guildManager.getMaintenance()) {
                        guildManager.setMaintenance(false);
                    } else {
                        guildManager.setMaintenance(true);
                    }

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(menuOption);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                    embed.setDescription(null);

                    if (guildManager.getMaintenance()) {
                        embed.setColor(new Color(255, 20, 20));
                    } else {
                        embed.setColor(new Color(255, 176, 106));
                    }

                    StringBuilder description = embed.getDescriptionBuilder();

                    if (guildManager.getTicketsCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getMessageChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                        description.append("\n \n");

                        if (guildManager.getTicketChannel() != null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                        }
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                    }

                    description.append("```yml").append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                    description.append("\n```");

                    embed.setDescription(description.toString());

                    List<Button> buttons = new ArrayList<>();
                    buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));

                    if (!guildManager.getMaintenance()) {
                        buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    } else {
                        buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    }

                    event.getInteraction().getMessage().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();

                    if (!event.getInteraction().isAcknowledged()) {
                        event.getInteraction().deferEdit().queue();
                    }
                }
                else if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).equalsIgnoreCase("ticket-controlpanel-button-channelchange-close-" + user.getId())) {
                    Message message = TicketManagerCommand.Message.get(user);

                    List<SelectOption> menuOption = new ArrayList<>();
                    menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                    menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withDefault(true).withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                    menuOption.add(SelectOption.of("Ticket Design", "ticket-control-panel-design").withDescription("to change the ticket how it looks like").withEmoji(Emoji.fromUnicode("\uD83D\uDCDD")));
                    menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                    menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(menuOption);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                    embed.setDescription(null);

                    if (guildManager.getMaintenance()) {
                        embed.setColor(new Color(255, 20, 20));
                    } else {
                        embed.setColor(new Color(255, 176, 106));
                    }

                    StringBuilder description = embed.getDescriptionBuilder();

                    if (guildManager.getTicketsCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getMessageChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                        description.append("\n \n");

                        if (guildManager.getTicketChannel() != null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                        }
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                    }

                    description.append("```yml").append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                    description.append("\n```");

                    embed.setDescription(description.toString());

                    List<Button> buttons = new ArrayList<>();
                    buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));
                    buttons.add(Button.primary("ticket-controlpanel-button-ticketscategory-" + user.getId(), "Change ticket category"));

                    if (!guildManager.getMaintenance()) {
                        buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    } else {
                        buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    }

                    event.getInteraction().getMessage().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();

                    if (!event.getInteraction().isAcknowledged()) {
                        event.getInteraction().deferEdit().queue();
                    }

                    detector_channelchange.remove(event.getUser());
                }
                else if (event.getButton().getId().equalsIgnoreCase("ticket-controlpanel-button-ticketscategory-" + user.getId())) {
                    Message message = TicketManagerCommand.Message.get(user);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                    embed.setDescription(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_CHANGETICKETSCATEGORY_DESCRIPTION).replaceAll("<category_mention>", (guildManager.getTicketChannel() == null ? "`NONE`" : guildManager.getTicketChannel().getAsMention())));

                    if (guildManager.getMaintenance()) {
                        embed.setColor(new Color(255, 20, 20));
                    } else {
                        embed.setColor(new Color(255, 176, 106));
                    }

                    List<Button> buttons = new ArrayList<>();
                    buttons.add(event.getButton().asDisabled());
                    buttons.add(Button.danger("ticket-controlpanel-button-ticketscategory-close-" + user.getId(), "Close"));

                    event.getInteraction().getMessage().editMessageEmbeds(embed.build()).setActionRow(buttons).queue();

                    ticketsCategory.put(user, message);
                }
                else if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).equalsIgnoreCase("ticket-controlpanel-button-ticketscategory-close-" + user.getId())) {
                    Message message = TicketManagerCommand.Message.get(user);

                    List<SelectOption> menuOption = new ArrayList<>();
                    menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                    menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withDefault(true).withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                    menuOption.add(SelectOption.of("Ticket Design", "ticket-control-panel-design").withDescription("to change the ticket how it looks like").withEmoji(Emoji.fromUnicode("\uD83D\uDCDD")));
                    menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                    menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                    menu.setRequiredRange(1, 1);
                    menu.addOptions(menuOption);

                    EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                    embed.setDescription(null);

                    if (guildManager.getMaintenance()) {
                        embed.setColor(new Color(255, 20, 20));
                    } else {
                        embed.setColor(new Color(255, 176, 106));
                    }

                    StringBuilder description = embed.getDescriptionBuilder();

                    if (guildManager.getTicketsCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketCategory() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getTicketChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                        description.append("\n");
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                        description.append("\n");
                    }

                    if (guildManager.getMessageChannel() == null) {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                        description.append("\n \n");

                        if (guildManager.getTicketChannel() != null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                        }
                    } else {
                        description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                    }

                    description.append("```yml").append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                    description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                    description.append("\n```");

                    embed.setDescription(description.toString());

                    List<Button> buttons = new ArrayList<>();
                    buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));
                    buttons.add(Button.primary("ticket-controlpanel-button-ticketscategory-" + user.getId(), "Change ticket category"));

                    if (!guildManager.getMaintenance()) {
                        buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    } else {
                        buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                    }

                    event.getInteraction().getMessage().editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();

                    if (!event.getInteraction().isAcknowledged()) {
                        event.getInteraction().deferEdit().queue();
                    }

                    ticketsCategory.remove(event.getUser());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChannelUpdateParent(@NotNull ChannelUpdateParentEvent event) {
        try {
            LanguageManager languageManager = new LanguageManager(event.getGuild());
            GuildManager guildManager = new GuildManager(event.getGuild());

            if (event.getChannel().getId().equalsIgnoreCase(guildManager.getTicketChannel().getId()) && event.getOldValue().getId().equalsIgnoreCase(guildManager.getTicketCategory().getId())) {
                guildManager.setCategoryTicket(event.getNewValue().getId());
                guildManager.setChannelTicket(event.getChannel().getId());
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        if (event.getChannelType().isGuild() && event.getChannelType().isMessage()) {
            try {
                LanguageManager languageManager = new LanguageManager(event.getGuild());
                GuildManager guildManager = new GuildManager(event.getGuild());

                event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_DELETE).queue(list -> {
                    try {
                        AuditLogEntry entry = list.get(0);

                        if (event.getChannel().getId().equalsIgnoreCase(guildManager.getTicketChannelId())) {
                            User user = event.getGuild().getOwner().getUser();
                            RestAction<PrivateChannel> channel = user.openPrivateChannel();

                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255, 89, 89));
                            embed.setTitle(languageManager.getMessage(MessageKeys.PRIVATE_WARN_CHANNEL_OWNERSHIP_TITLE).replaceAll("<guild>", event.getGuild().getName()));
                            embed.setDescription(languageManager.getMessage(MessageKeys.PRIVATE_WARN_CHANNEL_OWNERSHIP_DESCRIPTION).replaceAll("<user>", entry.getUser().getAsMention()));
                            embed.setFooter(languageManager.getMessage(MessageKeys.PRIVATE_WARN_CHANNEL_OWNERSHIP_FLOOR).replaceAll("<date>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date()))).setTimestamp(Instant.now());

                            RestAction<TextChannel> tca = Objects.requireNonNull(event.getGuild().getCategoryById(guildManager.getTicketCategory().getId())).createTextChannel("Ticket");

                            List<SelectOption> options = new ArrayList<>();
                            Arrays.stream(Languages.values()).forEach(languages -> {
                                options.add(SelectOption.of(languages.getDisplayName(), "option-open-language-" + languages.getKey()).withEmoji(languages.getEmoji()));
                            });

                            SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-language");
                            menu.addOptions(options);
                            menu.setRequiredRange(1, 1);

                            tca.submit().whenComplete((value, error) -> {
                                value.createPermissionOverride(event.getGuild().getPublicRole()).setAllow(Permission.VIEW_CHANNEL).setDeny(Permission.ALL_TEXT_PERMISSIONS).queue();

                                try {
                                    TicketUtility ticketUtility = new TicketUtility(value, event.getGuild());
                                    CompletableFuture<Message> message = value.sendMessageEmbeds(ticketUtility.embedTicketMessage().build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(Button.primary("open-ticket", "Open ticket"))).submit();

                                    guildManager.setChannelTicket(value.getId());
                                    message.whenComplete((q, e) -> {
                                        guildManager.setMessageTicket(q.getId());
                                    }).join();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).join();

                            channel.queue(msg -> msg.sendMessageEmbeds(embed.build()).queue());
                        }

                        TextChannel tc = (TextChannel) event.getChannel();

                        TicketCreateUtility ticketsCreate = new TicketCreateUtility(tc, event.getGuild());

                        if (ticketsCreate.getFile() != null && ticketsCreate.getFile().exists()) {
                            User creator = ticketsCreate.getCreatorByProperties();

                            if (ticketsCreate.getFile().delete()) {

                                if (closeTicketCommand.ticketchannel.containsKey(creator)) {
                                    RestAction<PrivateChannel> customChannel = creator.openPrivateChannel();

                                    EmbedBuilder cembed = new EmbedBuilder();
                                    cembed.setColor(new Color(255, 89, 89));
                                    cembed.setTitle(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_CLOSE_EMBED_TITLE));
                                    cembed.setDescription(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_CLOSE_EMBED_DESCRIPTION).replaceAll("<deletor>", (entry.getUser() != null ? entry.getUser().getAsMention() : "-")).replaceAll("<customer>", creator.getAsMention()).replaceAll("<reason>", (!closeTicketCommand.reason.get(creator).isEmpty() ? closeTicketCommand.reason.get(creator) : "-")).replaceAll("<ticket-id>", ticketsCreate.getTicketId()).replaceAll("<time-create>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(ticketsCreate.getTimeCreated())).replaceAll("<time-delete>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date())));
                                    cembed.setFooter(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_CLOSE_EMBED_FLOOR).replaceAll("<date>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date()))).setTimestamp(Instant.now());

                                    customChannel.queue(msg -> msg.sendMessageEmbeds(cembed.build()).queue());
                                } else {
                                    User user = null;
                                    if (ticketsCreate.getResponsible() != null) {
                                        user = event.getGuild().getMember(ticketsCreate.getResponsible()).getUser();
                                    } else {
                                        user = event.getGuild().getOwner().getUser();
                                    }

                                    RestAction<PrivateChannel> channel = user.openPrivateChannel();
                                    RestAction<PrivateChannel> channel2 = creator.openPrivateChannel();

                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setColor(new Color(255, 89, 89));
                                    embed.setTitle(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_DELETED_EMBED_TITLE));
                                    embed.setDescription(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_DELETED_EMBED_DESCRIPTION).replaceAll("<deletor>", (entry.getUser().getAsMention() != null ? entry.getUser().getAsMention() : "Not a user")).replaceAll("<customer>", creator.getAsMention()).replaceAll("<ticket-id>", ticketsCreate.getTicketId()).replaceAll("<time-create>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(ticketsCreate.getTimeCreated())).replaceAll("<time-delete>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date())));
                                    embed.setFooter(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_DELETED_EMBED_FLOOR).replaceAll("<time>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date()))).setTimestamp(Instant.now());

                                    EmbedBuilder embed2 = new EmbedBuilder();
                                    embed2.setColor(new Color(255, 89, 89));
                                    embed2.setTitle(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_DELETED_EMBED_TITLE));
                                    embed2.setDescription(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_DELETED_EMBED_DESCRIPTION).replaceAll("<deletor>", (entry.getUser().getAsMention() != null ? entry.getUser().getAsMention() : "Not a user")).replaceAll("<customer>", creator.getAsMention()).replaceAll("<ticket-id>", ticketsCreate.getTicketId()).replaceAll("<time-create>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(ticketsCreate.getTimeCreated())).replaceAll("<time-delete>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date())));
                                    embed2.setFooter(languageManager.getMessage(MessageKeys.PRIVATE_TICKET_CREATOR_DELETED_EMBED_FLOOR).replaceAll("<date>", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date()))).setTimestamp(Instant.now());

                                    channel.queue(msg -> msg.sendMessageEmbeds(embed.build()).queue());
                                    channel2.queue(msg -> msg.sendMessageEmbeds(embed2.build()).queue());
                                }
                            }
                        } else return;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType().isGuild()) {
            try {
                LanguageManager languageManager = new LanguageManager(event.getGuild());
                GuildManager guildManager = new GuildManager(event.getGuild()); // recall the database folder

                User user = event.getAuthor();

                if (detector_channelchange.containsKey(user) && guildManager.getSetup()) {
                    try {
                        guildManager.getMessageChannel().delete().queue();

                        String channel = event.getMessage().getContentRaw();
                        Message message = detector_channelchange.get(user);

                        final Pattern regexNotChars = Pattern.compile("\\p{Punct}");
                        final Matcher matcherNotChars = regexNotChars.matcher(channel);

                        final Pattern regexIsChar = Pattern.compile("-?[A-Za-z]+");
                        final Matcher matcherIsChars = regexIsChar.matcher(channel);

                        if (matcherNotChars.find()) {
                            channel = channel.replace("<", "").replace("#", "").replace(">", "");
                        } else if (matcherIsChars.find()) {
                            channel = event.getGuild().getTextChannelsByName(channel, false).get(0).getId();
                        }

                        TextChannel textChannel = event.getGuild().getTextChannelById(channel);
                        assert textChannel != null;

                        if (textChannel.getParentCategory() != null) {
                            Category category = textChannel.getParentCategory();
                            assert category != null;
                            guildManager.setCategoryTicket(category.getId()); // ti set the channel category
                        } else {
                            guildManager.setCategoryTicket(null);
                        }

                        guildManager.setChannelTicket(textChannel.getId()); // to set the channel ticket in folders

                        TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

                        List<SelectOption> options = new ArrayList<>();
                        Arrays.stream(Languages.values()).forEach(languages -> {
                            options.add(SelectOption.of(languages.getDisplayName(), "option-open-language-" + languages.getKey()).withEmoji(languages.getEmoji()));
                        });

                        SelectionMenu.Builder langmenu = SelectionMenu.create("ticket-open-select-language");
                        langmenu.addOptions(options);
                        langmenu.setRequiredRange(1, 1);

                        Message msg = textChannel.sendMessageEmbeds(ticketUtility.embedTicketMessage().build()).setActionRows(ActionRow.of(langmenu.build()), ActionRow.of(Button.primary("open-ticket", "Open ticket"))).complete();
                        guildManager.setMessageTicket(msg.getId());

                        List<SelectOption> menuOption = new ArrayList<>();
                        menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                        menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withDefault(true).withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                        menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                        menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                        SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                        menu.setRequiredRange(1, 1);
                        menu.addOptions(menuOption);

                        EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                        embed.setDescription(null);

                        if (guildManager.getMaintenance()) {
                            embed.setColor(new Color(255, 20, 20));
                        } else {
                            embed.setColor(new Color(255, 176, 106));
                        }

                        StringBuilder description = embed.getDescriptionBuilder();

                        if (guildManager.getTicketsCategory() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getTicketCategory() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getTicketChannel() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getMessageChannel() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                            description.append("\n \n");

                            if (guildManager.getTicketChannel() != null) {
                                description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                            }
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                        }

                        description.append("```yml").append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                        description.append("\n```");

                        embed.setDescription(description.toString());

                        List<Button> buttons = new ArrayList<>();
                        buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));
                        buttons.add(Button.primary("ticket-controlpanel-button-ticketscategory-" + user.getId(), "Change ticket category"));

                        if (!guildManager.getMaintenance()) {
                            buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                        } else {
                            buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                        }

                        message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();
                        event.getMessage().delete().queue();

                        detector_channelchange.remove(user);
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        event.getMessage().delete().queue();
                        event.getMessage().reply(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_CHANNELCHANGING_WRONGMESSAGE)).queue(q -> q.delete().queueAfter(6, TimeUnit.SECONDS));
                    }
                } else if (ticketsCategory.containsKey(user) && guildManager.getSetup()) {
                    try {
                        String cateogryString = event.getMessage().getContentRaw();
                        Message message = ticketsCategory.get(user);

                        final Pattern regexIsChar = Pattern.compile("-?[A-Za-z]+");
                        final Matcher matcherIsChars = regexIsChar.matcher(cateogryString);

                        Category category = null;
                        if (matcherIsChars.find()) {
                            category = event.getGuild().getCategoriesByName(cateogryString, false).stream().findFirst().get();
                        } else {
                            category = event.getGuild().getCategoryById(cateogryString);
                        }

                        if (category != null) {
                            guildManager.setCategoryTickets(category.getId()); // ti set the channel category
                        } else {
                            guildManager.setCategoryTickets("none");
                        }

                        TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

                        List<SelectOption> menuOption = new ArrayList<>();
                        menuOption.add(SelectOption.of("Ticket Main", "ticket-control-panel-main").withDescription("The main menu of the ticket control panel").withEmoji(Emoji.fromUnicode("\uD83D\uDCE7")));
                        menuOption.add(SelectOption.of("Ticket Manager", "ticket-control-panel-manager").withDescription("to change the settings of ticket message").withDefault(true).withEmoji(Emoji.fromUnicode("\uD83D\uDDC4")));
                        menuOption.add(SelectOption.of("Ticket History", "ticket-control-panel-history").withDescription("to get the current history of tickets").withEmoji(Emoji.fromUnicode("\uD83D\uDCD6")));
                        menuOption.add(SelectOption.of("Delete", "ticket-control-panel-delete").withDescription("to delete the message").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1")));

                        SelectionMenu.Builder menu = SelectionMenu.create("ticket-controlpanel-" + user.getId());
                        menu.setRequiredRange(1, 1);
                        menu.addOptions(menuOption);

                        EmbedBuilder embed = TicketManagerCommand.Embed.get(message);
                        embed.setDescription(null);

                        if (guildManager.getMaintenance()) {
                            embed.setColor(new Color(255, 20, 20));
                        } else {
                            embed.setColor(new Color(255, 176, 106));
                        }

                        StringBuilder description = embed.getDescriptionBuilder();

                        if (guildManager.getTicketsCategory() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_TCATEGORY)).replaceAll("<category>", guildManager.getTicketsCategory().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getTicketCategory() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CATEGORY)).replaceAll("<category>", guildManager.getTicketCategory().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getTicketChannel() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", "-"));
                            description.append("\n");
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_CHANNEL)).replaceAll("<channel>", guildManager.getTicketChannel().getAsMention()));
                            description.append("\n");
                        }

                        if (guildManager.getMessageChannel() == null) {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "-"));
                            description.append("\n \n");

                            if (guildManager.getTicketChannel() != null) {
                                description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MISS_MESSAGE)).replaceAll("<prefix>", Core.prefix)).append("\n");
                            }
                        } else {
                            description.append(new String(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_DESCRIPTION_MESSAGE)).replaceAll("<message-jumplink>", "[jump to message](" + guildManager.getMessageChannel().getJumpUrl() + ")"));
                        }

                        description.append("```yml").append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_NOTE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CATEGORY_CHANGE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_CHANNEL_CHANGE)).append("\n \n");
                        description.append(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_DESCRIPTION_UNDERMAINTENANCEMODE)).append("\n \n");
                        description.append("\n```");

                        embed.setDescription(description.toString());

                        List<Button> buttons = new ArrayList<>();
                        buttons.add(Button.primary("ticket-controlpanel-button-channelchange-" + user.getId(), "Change channel"));
                        buttons.add(Button.primary("ticket-controlpanel-button-ticketscategory-" + user.getId(), "Change ticket category"));

                        if (!guildManager.getMaintenance()) {
                            buttons.add(Button.success("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                        } else {
                            buttons.add(Button.danger("ticket-controlpanel-button-undermaintenance-" + user.getId(), "Under maintenance"));
                        }

                        message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(buttons)).queue();
                        event.getMessage().delete().queue();

                        ticketsCategory.remove(user);
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        event.getMessage().delete().queue();
                        event.getMessage().reply(languageManager.getMessage(MessageKeys.TICKET_CONTROLPANEL_TICKETMANAGER_CHANGETICKETSCATEGORY_WRONGMESSAGE)).queue(q -> q.delete().queueAfter(6, TimeUnit.SECONDS));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        try {
            GuildManager guildManager = new GuildManager(event.getGuild());
            LanguageManager languageManager = new LanguageManager(event.getGuild());
            TicketUtility ticketUtility = new TicketUtility(event.getTextChannel(), event.getGuild());

            if (guildManager.getProperties().getProperty("ticket-message-id") != null || !guildManager.getProperties().getProperty("ticket-message-id").equalsIgnoreCase("none")) {
                if (event.getMessageId().equalsIgnoreCase(guildManager.getProperties().getProperty("ticket-message-id"))) {

                    event.getGuild().retrieveAuditLogs().type(ActionType.MESSAGE_DELETE).limit(1).queue(list -> {
                        try {
                            AuditLogEntry entry = list.get(0);

                            User ownership = Objects.requireNonNull(event.getGuild().getOwner()).getUser();
                            User deletor = entry.getUser();
                            RestAction<PrivateChannel> action = ownership.openPrivateChannel();

                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255, 3, 3));
                            embed.setTitle(languageManager.getMessage(guildManager.getLanguage(), MessageKeys.PRIVATE_WARN_MESSAGE_OWNERSHIP_TITLE).replaceAll("<guild>", event.getGuild().getName()));
                            embed.setDescription(languageManager.getMessage(guildManager.getLanguage(), MessageKeys.PRIVATE_WARN_MESSAGE_OWNERSHIP_DESCRIPTION).replaceAll("<channel_mention>", event.getChannel().getAsMention()).replaceAll("<user>", deletor.getAsMention()));

                            action.queue(msg -> msg.sendMessageEmbeds(embed.build()).queue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    List<SelectOption> options = new ArrayList<>();
                    Arrays.stream(Languages.values()).forEach(languages -> {
                        options.add(SelectOption.of(languages.getDisplayName(), "option-open-language-" + languages.getKey()).withEmoji(languages.getEmoji()));
                    });

                    SelectionMenu.Builder menu = SelectionMenu.create("ticket-open-select-language");
                    menu.addOptions(options);
                    menu.setRequiredRange(1, 1);

                    Message message = event.getChannel().sendMessageEmbeds(ticketUtility.embedTicketMessage().build()).setActionRows(ActionRow.of(menu.build()), ActionRow.of(Button.primary("open-ticket", "Open ticket"))).complete();
                    guildManager.setMessageTicket(message.getId());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
