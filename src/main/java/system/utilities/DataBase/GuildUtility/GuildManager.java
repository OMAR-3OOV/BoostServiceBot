package system.utilities.DataBase.GuildUtility;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
import system.Core;
import system.utilities.TicektManagerUtility.PaymentMethods;
import system.utilities.languageManager.LanguageManager;
import system.utilities.languageManager.Languages;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

public class GuildManager {

    private final File file;
    private final Properties properties;
    private final Guild guild;

    public GuildManager(Guild guild) throws IOException {
        this.file = new File("Database/Guilds/" + guild.getId() + ".properties");
        this.properties = new Properties();
        this.guild = guild;

        loadProperties();
    }

    @Contract(pure = true)
    public File getFile() {
        return file;
    }

    @Contract(pure = true)
    public Properties getProperties() {
        return properties;
    }

    @Contract(pure = true)
    public Guild getGuild() {
        return guild;
    }

    public void loadProperties() throws IOException {
        if (this.file.exists()) {
            try (InputStream stream = new FileInputStream(this.file)) {
                properties.load(stream);
            }
        }
    }

    @Contract(pure = false)
    public void createNewGuildData() throws IOException {
        if (!this.file.getParentFile().exists()) {
            if (this.file.getParentFile().mkdirs()) {
                Core.getLOGGER().info(this.guild.getName() + " Data properties is loaded successfully!");
            }
        }

        if (!this.file.exists()) {
            try {
                if (this.file.createNewFile()) {
                    LanguageManager languageManager = new LanguageManager();

                    properties.setProperty("name", this.guild.getName());
                    properties.setProperty("id", this.guild.getId());
                    properties.setProperty("ownership", this.guild.getOwnerId());
                    properties.setProperty("ticket-channel-id", "none");
                    properties.setProperty("ticket-category-id", "none");
                    properties.setProperty("ticket-message-id", "none");
                    properties.setProperty("tickets-category", "none");
                    properties.setProperty("setup", String.valueOf(false));
                    properties.setProperty("maintenance", String.valueOf(false));
                    properties.setProperty("language", String.valueOf(languageManager.getDefaultLanguage().getId()));
                    properties.setProperty("paypal", "-");
                    properties.setProperty("bitcoin", "-");
                    properties.setProperty("cashapp", "-");
                    properties.setProperty("total-tickets", "0");
                    properties.setProperty("total-tickets-onhold", "0");

                    fileSave();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Contract(pure = false)
    public void fileSave() throws IOException {
        try (OutputStream stream = new FileOutputStream(this.file)) {
            properties.store(stream, new SimpleDateFormat("HH:mm:ss MMM - dd - yyyy").format(new Date()) + " Last update!");
        }
    }

    public final Languages getLanguage() {
        if (properties.getProperty("language").isEmpty() || properties.getProperty("language").equalsIgnoreCase("none"))
            return null;
        return Languages.getLanguage(Integer.parseInt(properties.getProperty("language")));
    }

    public final Category getTicketCategory() {
        if (properties.getProperty("ticket-category-id").isEmpty() || properties.getProperty("ticket-category-id").equalsIgnoreCase("none"))
            return null;
        return this.guild.getCategoryById(properties.getProperty("ticket-category-id"));
    }

    public final Category getTicketsCategory() {
        if (properties.getProperty("tickets-category").isEmpty() || properties.getProperty("tickets-category").equalsIgnoreCase("none"))
            return null;
        return this.guild.getCategoryById(properties.getProperty("tickets-category"));
    }

    public final String getTicketChannelId() {
        try {
            return properties.getProperty("ticket-channel-id");
        } catch (NullPointerException e) {
            return null;
        }
    }

    public final TextChannel getTicketChannel() {
        try {
            if (properties.getProperty("ticket-channel-id").isEmpty() || properties.getProperty("ticket-channel-id").equalsIgnoreCase("none"))
                return null;
            return this.guild.getTextChannelById(properties.getProperty("ticket-channel-id"));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public final Message getMessageChannel() {
        try {
            RestAction<Message> retrivemessage = Objects.requireNonNull(getTicketChannel()).retrieveMessageById(properties.getProperty("ticket-message-id"));

            if (properties.getProperty("ticket-message-id").isEmpty() || properties.getProperty("ticket-message-id").equalsIgnoreCase("none") || getTicketChannel() == null)
                return null;
            return retrivemessage.complete();
        } catch (ErrorResponseException | NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean getSetup() {
        return Boolean.parseBoolean(properties.getProperty("setup"));
    }

    public void setSetup(boolean bool) {
        try {
            properties.setProperty("setup", String.valueOf(bool));
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getMaintenance() {
        return Boolean.parseBoolean(properties.getProperty("maintenance"));
    }

    public void setMaintenance(boolean bool) {
        try {
            properties.put("maintenance", String.valueOf(bool));
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLanguage(String key) {
        try {
            properties.put("language", Languages.getLanguageFromKey(key).getId());
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCategoryTicket(String id) {
        try {
            properties.put("ticket-category-id", id);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCategoryTickets(String id) {
        try {
            properties.setProperty("tickets-category", id);
            fileSave();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setChannelTicket(String id) {
        try {
            properties.put("ticket-channel-id", id);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessageTicket(String id) {
        try {
            properties.put("ticket-message-id", id);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPaymentLink(PaymentMethods method) {
        return properties.getProperty(method.getKey());
    }

    public String getPaypal() {
        return properties.getProperty("paypal");
    }

    public String getBitcoins() {
        return properties.getProperty("bitcoin");
    }

    public String getCashApp() {
        return properties.getProperty("cashapp");
    }

    public void setPaypal(String link) {
        try {
            properties.put("paypal", link);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBitcoins(String code) {
        try {
            properties.put("bitcoin", code);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCashApp(String code) {
        try {
            properties.put("cashapp", code);
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalTickets() {
        return Integer.parseInt(properties.getProperty("total-tickets"));
    }

    public void addTotalTicket(int amount) {
        try {
            properties.setProperty("total-tickets", String.valueOf((getTotalTickets()) + amount));
            fileSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
