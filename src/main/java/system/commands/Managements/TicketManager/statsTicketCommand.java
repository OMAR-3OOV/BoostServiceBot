package system.commands.Managements.TicketManager;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.TicektManagerUtility.CartUtility;
import system.utilities.TicektManagerUtility.PaymentMethods;
import system.utilities.TicektManagerUtility.Products;
import system.utilities.TicektManagerUtility.TicketCreateUtility;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.MessageKeys;
import system.utilities.manager.Categories;
import system.utilities.manager.Command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

public class statsTicketCommand implements Command {

    @Override
    public void handle(List<String> args, MessageReceivedEvent event) throws IOException {
        try {
            GuildManager guildManager = new GuildManager(event.getGuild());
            LanguageManager languageManager = new LanguageManager(event.getGuild());
            TicketCreateUtility ticketCreate = new TicketCreateUtility(event.getTextChannel(), event.getGuild());
            CartUtility cart = new CartUtility(ticketCreate.getCreatorByProperties(), ticketCreate.getTextChannel());

            if (ticketCreate.getTextChannel().equals(event.getChannel())) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(languageManager.getMessage(MessageKeys.TICKET_CART_STATS_TITLE).replaceAll("<ticket_id>", ticketCreate.getTicketId()));


                // String products = (Products.getProductByKey(ticketCreate.getProperties().getProperty("cart.product"))!=null?"-":languageManager.getMessage(Products.getProductByKey(ticketCreate.getProperties().getProperty("cart.product")).getName()));
                // String payment = (PaymentMethods.getPaymentByKey(ticketCreate.getProperties().getProperty("cart.payment"))!=null?languageManager.getMessage(PaymentMethods.getPaymentByKey(ticketCreate.getProperties().getProperty("cart.payment")).getDisplayName()):"-");
                // int price = (ticketCreate.getProperties().getProperty("cart.price")!=null?Integer.parseInt(ticketCreate.getProperties().getProperty("cart.price")):0);

                String product = "-";
                String payment = "-";

                int price = 0;

                if (ticketCreate.getProperties().getProperty("cart.product") != null) {
                    product = languageManager.getMessage(Products.getProductByKey(ticketCreate.getProperties().getProperty("cart.product")).getName());
                }

                if (ticketCreate.getProperties().getProperty("cart.product") != null) {
                    payment = languageManager.getMessage(PaymentMethods.getPaymentByKey(ticketCreate.getProperties().getProperty("cart.payment")).getDisplayName());
                }

                if (ticketCreate.getProperties().getProperty("cart.price") != null) {
                    price = Integer.parseInt(ticketCreate.getProperties().getProperty("cart.price"));
                }

                embed.setDescription(languageManager.getMessage(MessageKeys.TICKET_CART_STATS_DESCRIPTION)
                        .replaceAll("<customer>", Matcher.quoteReplacement(ticketCreate.getCreatorByProperties().getAsMention()))
                        .replaceAll("<product>", Matcher.quoteReplacement(product))
                        .replaceAll("<payment>", Matcher.quoteReplacement(payment))
                        .replaceAll("<price>", Matcher.quoteReplacement(price + "$"))
                        .replaceAll("<time_onhold>", Matcher.quoteReplacement(TimeAgo.using(ticketCreate.getTimeCreated().getTime())))
                );

                event.getMessage().replyEmbeds(embed.build()).queue();
            } else {
                event.getMessage().reply(Matcher.quoteReplacement(languageManager.getMessage(MessageKeys.TICKET_CART_STATS_ERROR))).queue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException {

    }

    @Override
    public String getHelp() {
        return Core.prefix + "stats";
    }

    @Override
    public String getInVoke() {
        return "stats";
    }

    @Override
    public Categories getCategory() {
        return Categories.MANAGEMENT;
    }

    @Override
    public String getDescription() {
        return "to check the ticket information and cart for customer";
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_CHANNEL;
    }
}
