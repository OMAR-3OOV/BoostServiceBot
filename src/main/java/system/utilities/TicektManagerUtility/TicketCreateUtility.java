package system.utilities.TicektManagerUtility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
import system.Core;
import system.utilities.StringUtilities.GenerateStringAPI;
import system.utilities.languageManager.Languages;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class TicketCreateUtility {

    private File file;
    private File folder;
    private Properties properties;

    private Guild guild;
    private User creator;
    private TextChannel textChannel;
    private Message message;

    private String id;

    public TicketCreateUtility() {
        this.folder = new File("Database/Guilds/Tickets/");
    }

    public TicketCreateUtility(Guild guild, User creator, TextChannel textChannel, Message message) throws IOException {
        this.guild = guild;
        this.creator = creator;
        this.textChannel = textChannel;
        this.message = message;
        this.folder = new File("Database/Guilds/Tickets/");
        this.properties = new Properties();
        this.id = generateNewCode();

        createTicket();
    }

    public TicketCreateUtility(TextChannel textChannel, Guild guild) {
        this.folder = new File("Database/Guilds/Tickets/");
        this.textChannel = textChannel;
        this.properties = new Properties();
        this.guild = textChannel.getGuild();

        if (this.folder.listFiles() != null) {

            for (File file : Objects.requireNonNull(this.folder.listFiles())) {
                try {
                    if (textChannel.getId().equalsIgnoreCase(file.getName().replaceAll(".properties", ""))) {
                        try (InputStream stream = new FileInputStream(file)) {
                            this.properties.load(stream);
                            this.file = file;
                            this.textChannel = guild.getTextChannelById(properties.getProperty("channel-id"));

                            RestAction<Message> message = textChannel.retrieveMessageById(properties.getProperty("message-id"));

                            message.submit().whenComplete((msg, error) -> {
                                if (error==null) {
                                    this.message = msg;
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return folder;
    }

    public Properties getProperties() {
        return properties;
    }

    public Guild getGuild() {
        return guild;
    }

    public User getCreator() {
        return creator;
    }

    public User getCreatorByProperties() {
        return this.guild.getMemberById(properties.getProperty("creator")).getUser();
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void createTicket() throws IOException {
        this.file = new File("Database/Guilds/Tickets/" + this.textChannel.getId() + ".properties");

        if (!this.folder.exists() || !this.file.getParentFile().mkdirs()) {
            if (folder.mkdirs()) {
                Core.getLOGGER().info("Tickets folder has been created for " + this.guild.getName());
            }
        }

        if (!this.file.exists()) {
            if (this.file.createNewFile()) {

                properties.put("ticket-id", this.id);
                properties.put("guild-id", this.guild.getId());
                properties.put("channel-id", textChannel.getId());
                properties.put("creator", this.creator.getId());
                properties.put("bundle", "none");
                properties.put("product", "none");
                properties.put("responsible", "none");
                properties.put("created", new SimpleDateFormat("HH:mm:ss dd - MMM - yyyy").format(new Date()));
                properties.put("message-id", this.message.getId());

                save(this.file);
            }
        } else {
            try (InputStream stream = new FileInputStream(this.file)) {
                properties.load(stream);
            }
        }

    }

    @Contract(pure = true)
    public String getTicketId() {
        return properties.getProperty("ticket-id");
    }

    @Contract(pure = true)
    public User getResponsible() {
        if (properties.getProperty("responsible").equalsIgnoreCase("none") || properties.getProperty("responsible").isEmpty() || properties.getProperty("responsible").isBlank())
            return null;
        return User.fromId(properties.getProperty("responsible"));
    }

    @Contract(pure = true)
    public void setResponsible(User user) throws IOException {
        properties.setProperty("resonpsible", user.getId());
        save(this.file);
    }

    @Contract(pure = true)
    public Bundles getBundle() {
        if (properties.getProperty("bundle").equalsIgnoreCase("none") || properties.getProperty("bundle").isEmpty() || properties.getProperty("bundle").isBlank())
            return null;
        return Bundles.getBundleByKey(properties.getProperty("bundle"));
    }

    @Contract(pure = true)
    public void setBundle(Bundles bundle) throws IOException {
        properties.setProperty("bundle", bundle.getKey());
        save(this.file);
    }

    @Contract(pure = true)
    public Products getProduct() {
        if (properties.getProperty("product").equalsIgnoreCase("none") || properties.getProperty("product").isEmpty() || properties.getProperty("product").isBlank())
            return null;
        return Products.getProductByKey(properties.getProperty("product"));
    }

    @Contract(pure = true)
    public void setProduct(Products product) throws IOException {
        properties.setProperty("product", product.getKey());
        save(this.file);
    }

    public Date getTimeCreated() {
        return new Date(properties.getProperty("created"));
    }

    public void save(File file) throws IOException {
        try (OutputStream stream = new FileOutputStream(file)) {
            this.properties.store(stream, "Last update " + new SimpleDateFormat("HH:mm:ss dd - MMM - yyy").format(new Date()));
        }
    }

    public String generateNewCode() {
        AtomicReference<String> generator = new AtomicReference<>();

        GenerateStringAPI api = new GenerateStringAPI(this.guild, 6);

        generator.set(api.getKey());

        if (this.folder.listFiles() != null)
            Arrays.stream(Objects.requireNonNull(this.folder.listFiles())).forEach(file -> {
                if (api.getKey().equalsIgnoreCase(file.getName())) {
                    generator.set(api.regenerate());
                }
            });

        return generator.get();
    }
}
