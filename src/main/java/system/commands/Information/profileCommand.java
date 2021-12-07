package system.commands.Information;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import system.Core;
import system.utilities.languageManager.LanguageManager;
import system.utilities.manager.Categories;
import system.utilities.manager.Command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profileCommand implements Command {

    public String clientid = "AdLjpVUleWVDVcwzuiPL5vz7PE4IFKdQIqUxwqdm43tYZfdgV174m5Any9wIddiF40HJZSuM5tFPkwK2";
    public String clientsecret = "EPdOxU20Ms0oHCV8Gt2QvyCC8E_a9_kj63g3EoQ2C45MruUE6WEh8E0behKfm50RETGCTwo8-upOngYQ";

    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "sandbox");
        return configMap;
    }

    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientid, clientsecret, paypalSdkConfig());
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) throws IOException {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Paypal payment");
            embed.setDescription("Status: ONLINE");
            embed.addField("Payment method", "-", false);

            event.getMessage().replyEmbeds(embed.build()).queue();

            System.out.println(embed);
    }

    @Override
    public void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException {
        event.reply("Your profile id is " + event.getMember().getId()).queue();
    }

    private Payment executePayment(APIContext context, String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(context, paymentExecution);
    }

    @Override
    public String getHelp() {
        return Core.prefix + "profile";
    }

    @Override
    public String getInVoke() {
        return "profile";
    }

    @Override
    public Categories getCategory() {
        return Categories.INFORMATION;
    }

    @Override
    public String getDescription() {
        return "to see your profile stats!";
    }

    @Override
    public Permission getPermission() {
        return Permission.MESSAGE_WRITE;
    }
}
