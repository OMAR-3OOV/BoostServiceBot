package system.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import system.utilities.DataBase.GuildUtility.GuildManager;

import java.io.IOException;

public class BotEvents extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        try {
            GuildManager guildManager = new GuildManager(event.getGuild());

            guildManager.createNewGuildData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        try {
            GuildManager guildManager = new GuildManager(event.getGuild());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
