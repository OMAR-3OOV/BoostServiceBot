package system.utilities.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import system.commands.Information.profileCommand;
import system.commands.Managements.TicketManager.TicketManagerCommand;
import system.commands.Managements.TicketManager.closeTicketCommand;
import system.commands.Managements.TicketManager.statsTicketCommand;
import system.commands.Managements.setupTicketCommand;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.MessageKeys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CommandManager {

    public static final Map<String, Command> commands = new HashMap<>();
    private final Map<Integer, Categories> categories = new HashMap<>();

    public CommandManager() {

        /*
            Commands that applies to bot !
         */

        addCommand(new profileCommand());
        addCommand(new setupTicketCommand());
        addCommand(new TicketManagerCommand());
        addCommand(new closeTicketCommand());
        addCommand(new statsTicketCommand());

        /*
            Categories that applies to bot !
         */
        addCategory(Categories.MANAGEMENT);
        addCategory(Categories.INFORMATION);
    }

    private void addCommand(Command command) {
        if (!commands.containsKey(command.getInVoke())) {
            commands.put(command.getInVoke(), command);
        }
    }

    public void loadSlashCommands(JDA jda) {
        getCommands().forEach(command -> {
            jda.upsertCommand(command.getInVoke(), command.getDescription()).queue();
        });
    }

    private void addCategory(Categories categories) {
        if (!this.categories.containsKey(categories)) {
            this.categories.put(categories.getId(), categories);
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Command getCommand(String command) {
        return commands.get(command);
    }

    public Collection<Categories> getCategories() {
        return categories.values();
    }

    public void handleCommand(GuildMessageReceivedEvent event, String prefix) throws IOException {
        final String[] split = event.getMessage().getContentRaw().replaceFirst(prefix, "").split("\\s+");
        final String invoke = split[0].toLowerCase();

        if (commands.containsKey(invoke)) {
            final List<String> args = Arrays.asList(split).subList(1, split.length);
            GuildManager guildManager = new GuildManager(event.getGuild());
            LanguageManager languageManager = new LanguageManager(event.getGuild());


            if (Objects.requireNonNull(event.getMember()).hasPermission(commands.get(invoke).getPermission())) {
                if (!guildManager.getMaintenance() || event.getMember().hasPermission(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)) {
                    commands.get(invoke).handle(args, event);
                } else {
                    event.getMessage().reply(languageManager.getMessage(MessageKeys.UNDERMAINTENANCEMODE)).queue();
                }
            } else {
                event.getMessage().reply(languageManager.getMessage(MessageKeys.NOPERMISSIONS)).queue();
            }

        }
    }

    public void handleSlash(SlashCommandEvent event) throws FileNotFoundException {
        final String invoke = event.getCommandString();

        if (commands.containsKey(invoke)) {
            final List<OptionMapping> args = event.getOptions();
            if (event.getMember().hasPermission(commands.get(invoke).getPermission())) {
                commands.get(invoke).handle(args, event);
            } else {
                event.reply(":x: | You don't have permission to use this command!").queue();
            }
        }
    }
}
