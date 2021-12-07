package system.utilities.StringUtilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import system.Core;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public enum Placeholder {

    /* Related to user */
    USERNAME("<username>"),
    ID("<user-id>"),
    MENTION("<user-mention>"),

    /* Related to Text channel */
    T_CHANNEL_NAME("<textchannel-name>"),
    T_CHANNEL_ID("<textchannel-id>"),
    T_CHANNEL_MENTION("<textchannel-mention>"),

    /* Related to Voice channel */
    V_CHANNEL_NAME("<voicechannel-name>"),
    V_CHANNEL_ID("<voicechannel-id>"),
    V_CHANNEL_MENTION("<voicechannel-mention>"),

    /* Related to guild */
    GUILD_NAME("<guild-name>"),
    GUILD_ID("<guild-id>");

    private final String holders;

    Placeholder(String holder) {
        this.holders = holder;
    }

    public String getHolders() {
        return holders;
    }

    public String getValue(User user, VoiceChannel vchannel, TextChannel tchannel, Guild guild) {
        if (user != null) {
            switch (this) {
                case USERNAME -> user.getName();
                case ID -> user.getId();
                case MENTION -> user.getAsMention();
            }
        } else {
            return "`NONE USER`";
        }

        if (vchannel != null) {
            switch (this) {
                case V_CHANNEL_NAME -> vchannel.getName();
                case V_CHANNEL_ID -> vchannel.getId();
                case V_CHANNEL_MENTION -> vchannel.getAsMention();
            }
        } else {
            return "`NONE Voice CHANNEL`";
        }

        if (tchannel != null) {
            switch (this) {
                case T_CHANNEL_NAME -> tchannel.getName();
                case T_CHANNEL_ID -> tchannel.getId();
                case T_CHANNEL_MENTION -> tchannel.getAsMention();
            }
        } else {
            return "`NONE Text CHANNEL`";
        }

        if (guild != null) {
            switch (this) {
                case GUILD_NAME -> guild.getName();
                case GUILD_ID -> guild.getId();
            }
        } else {
            return "`NONE GUILD`";
        }

        return "`NONE`";
    }

    public static String replace(String string, User user, VoiceChannel voiceChannel ,TextChannel textChannel, Guild guild) {
        try {
            AtomicReference<String> replace = new AtomicReference<>(string);

            Arrays.stream(Placeholder.values()).forEach(value -> {
                final String replacement = replace.get();

                replace.set(replacement.replaceAll(value.holders, value.getValue(user, voiceChannel, textChannel, guild)));
            });

            return replace.get();
        } catch (Exception e) {
            Core.getLOGGER().info("None replacement");
            e.printStackTrace();
        }

        return "`NONE`";
    }
}
