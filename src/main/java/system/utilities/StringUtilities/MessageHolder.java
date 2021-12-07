package system.utilities.StringUtilities;

import net.dv8tion.jda.api.entities.*;

public class MessageHolder {

    private final String message;

    /**
     *
     * @param message to get the message from MessageKeys, related from languages folder
     */
    public MessageHolder(String message) {
        this.message = message;
    }

    /**
     * Related to the message in the language folder
     * @return to message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return to message
     */
    public String toHolder() {
        return Placeholder.replace(this.message, null, null, null, null);
    }

    /**
     *
     * @param user to get user information
     * @return to message
     */
    public String toHolder(User user) {
        return Placeholder.replace(this.message, user, null, null, null);
    }

    public String toHolder(User user, Guild guild) {
        return Placeholder.replace(this.message, user, null, null, guild);
    }

    /**
     *
     * @param user to get user information
     * @param voiceChannel to get voice channel information
     * @return to message
     */
    public String toHolder(User user, VoiceChannel voiceChannel) {
        return Placeholder.replace(this.message, user, voiceChannel, null, null);
    }

    /**
     *
     * @param user to get user information
     * @param voiceChannel to get voice channel information
     * @param textChannel to get text channel information
     * @return to message
     */
    public String toHolder(User user, VoiceChannel voiceChannel, TextChannel textChannel) {
        return Placeholder.replace(this.message, user, voiceChannel, textChannel, null);
    }

    /**
     *
     * @param user to get user information
     * @param voiceChannel to get voice channel information
     * @param textChannel to get text channel information
     * @param guild to get guild information
     * @return to message
     */
    public String toHolder(User user, VoiceChannel voiceChannel, TextChannel textChannel, Guild guild) {
        return Placeholder.replace(this.message, user, voiceChannel, textChannel, guild);
    }
}
