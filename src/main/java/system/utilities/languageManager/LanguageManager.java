package system.utilities.languageManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.Contract;
import system.Core;
import system.utilities.DataBase.GuildUtility.GuildManager;
import system.utilities.StringUtilities.MessageHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class LanguageManager {

    private File file;
    private Properties properties;
    private Languages languages;

    private GuildManager guildManager;

    public User user;

    public Languages defaultLanguage = Languages.ENGLISH;

    public LanguageManager() {
    }

    public LanguageManager(Guild guild) throws IOException {
        this.guildManager = new GuildManager(guild);
    }

    public File getFile() {
        return file;
    }

    public Properties getProperties() {
        return properties;
    }

    public Languages getLanguages() {
        return languages;
    }

    public Languages getDefaultLanguage() {
        return defaultLanguage;
    }

    public User getUser() {
        return user;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    @Contract(pure = false)
    public String getMessage(MessageKeys key) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+this.guildManager.getLanguage().getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder().getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix));

        return message.get();
    }


    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+languages.getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder().getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(MessageKeys key, User user) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+this.guildManager.getLanguage().getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user).getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix).replaceAll("<user>", user.getName()));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key, User user) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+languages.getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user).getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix).replaceAll("<user>", user.getName()));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(MessageKeys key, User user, Guild guild) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+this.guildManager.getLanguage().getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user, guild).getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix).replaceAll("<user>", user.getName()).replaceAll("<guild>", guild.getName()));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key, User user, Guild guild) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+languages.getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user, guild).getBytes(fromCharset), toCharset).replaceAll("<prefix>", Core.prefix).replaceAll("<user>", user.getName()).replaceAll("<guild>", guild.getName()));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key, User user, VoiceChannel voiceChannel) {
        AtomicReference<String> message = new AtomicReference<>();

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user, voiceChannel).getBytes(fromCharset), toCharset));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key, User user, VoiceChannel voiceChannel, TextChannel textChannel) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+languages.getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user, voiceChannel, textChannel).getBytes(fromCharset), toCharset));

        return message.get();
    }

    @Contract(pure = false)
    public String getMessage(Languages languages, MessageKeys key, User user, VoiceChannel voiceChannel, TextChannel textChannel, Guild guild) throws IOException {
        AtomicReference<String> message = new AtomicReference<>();

        this.file = new File("Database/Languages/"+languages.getCode()+".properties");
        this.properties = new Properties();

        if (this.file.exists()) {
            properties.load(new FileInputStream(this.file));
        }

        final Charset fromCharset = StandardCharsets.ISO_8859_1;
        final Charset toCharset = StandardCharsets.UTF_8;

        message.set(new String(new MessageHolder(this.properties.getProperty(key.getKey())).toHolder(user, voiceChannel, textChannel, guild).getBytes(fromCharset), toCharset));

        return message.get();
    }
}
