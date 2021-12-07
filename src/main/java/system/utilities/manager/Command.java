package system.utilities.manager;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.*;
import java.util.*;

public interface Command {

    void handle(List<String> args, GuildMessageReceivedEvent event) throws IOException;

    void handle(List<OptionMapping> args, SlashCommandEvent event) throws FileNotFoundException;
    String getHelp();
    String getInVoke();
    Categories getCategory();
    String getDescription();
    Permission getPermission();
}
