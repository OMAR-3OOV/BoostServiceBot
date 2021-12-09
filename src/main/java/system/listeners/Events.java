package system.listeners;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.TicektManagerUtility.TicketCreateUtility;
import system.utilities.manager.CommandManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Events extends ListenerAdapter {

    private final CommandManager manager;

    public Events(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    GuildManager guildManager = new GuildManager(Objects.requireNonNull(event.getJDA().getGuildById("741944707747151904")));
                    TicketCreateUtility ticketCreate = new TicketCreateUtility();

                    List<String> activities = new ArrayList<>();
                    activities.add(event.getJDA().getSelfUser().getName() + " is Online");
                    activities.add(" || ");
                    activities.add("Tickets: " + (ticketCreate.getFolder().listFiles()!=null?Arrays.stream(ticketCreate.getFolder().listFiles()).count():0));

                    event.getJDA().getPresence().setPresence(Activity.playing(String.join("\n", activities)), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 6 * 1000, 6 * 1000);

    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String prefix = Core.prefix;

        if (!(event.getAuthor().isBot() || event.getMessage().isWebhookMessage() || event.getMessage().getContentRaw().startsWith(prefix)))
            return;

        try {
            manager.handleCommand(event, prefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        try {
            manager.handleSlash(event);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
