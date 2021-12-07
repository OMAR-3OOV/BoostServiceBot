package system.utilities.TicektManagerUtility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class CartUtility {

    private final User user;
    private final TextChannel textChannel;
    private Message message;
    private Bundles bundles;
    private Products products;
    private PaymentMethods payment;

    public static final HashMap<User, CartUtility> cart = new HashMap<>();

    /**
     * @param user        returns to the user who use the selectMenu
     * @param textChannel returns to the channel in which the message contained
     * @param message     returns to the message which the user selects the bundle from it
     */
    public CartUtility(User user, TextChannel textChannel, Message message) {
        this.user = user;
        this.textChannel = textChannel;
        this.message = message;

        if (!cart.containsKey(user)) {
            cart.put(this.user, this);
        }
    }

    public CartUtility(User user, TextChannel textChannel) {
        this.user = user;
        this.textChannel = textChannel;

        if (!cart.containsKey(user)) {
            cart.put(this.user, this);
        }
    }

    public User getUser() {
        return user;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Message getMessage() {
        return message;
    }

    public Bundles getBundles() {
        return bundles;
    }

    public Products getProducts() {
        return products;
    }

    public PaymentMethods getPayment() {
        return payment;
    }

    public HashMap<User, CartUtility> getCart() {
        return cart;
    }

    public void setBundles(Bundles bundles) {
        this.bundles = bundles;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public void setPayment(PaymentMethods payment) {
        this.payment = payment;
    }
}
