package system;

import me.duncte123.botcommons.web.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.cache.*;
import org.slf4j.*;
import system.listeners.BotEvents;
import system.listeners.Events;
import system.listeners.TicketManagerEvent;
import system.utilities.manager.CommandManager;

import javax.security.auth.login.*;
import java.io.*;
import java.util.*;

public class Core {

    /*
        Created on 12/11/2021
        Created by Indra#4646

        for any help or fix contact with me!
     */

    public static File file = new File("DataSystem/bot_data.properties");
    public static String prefix;
    public static Logger LOGGER;

    public static void main(String[] args) throws LoginException {
        LOGGER = LoggerFactory.getLogger(Core.class);

        WebUtils.setUserAgent("BoostService/5.0 BoostService JDA Discord bot/Indra");

        LOGGER.info("Boost Service bot is ready");
        JDABuilder jdaBuilder = JDABuilder.create(Secret.TOKEN, GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES);
        jdaBuilder.disableCache(EnumSet.of(CacheFlag.EMOTE));

        jdaBuilder.disableCache(CacheFlag.VOICE_STATE);

        CommandManager commandManager = new CommandManager();
        jdaBuilder.addEventListeners(new Events(commandManager), new BotEvents(), new TicketManagerEvent());
        JDA jda = jdaBuilder.build();
        commandManager.loadSlashCommands(jda);

        Properties properties = new Properties();

        try {
            if (getFile().exists()) {
                properties.load(new FileInputStream(getFile()));
            }

            Icon icon = Icon.from(new File("DataSystem/"+properties.getProperty("avatar")), Icon.IconType.PNG);
            prefix = properties.getProperty("prefix");

            AccountManager bot = jda.getSelfUser().getManager();

            bot.setAvatar(icon).queue(ignore -> {}, error -> {
                LOGGER.error("You can't change the bot avatar/username more than twice in hour!");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFile() {
        return file;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
