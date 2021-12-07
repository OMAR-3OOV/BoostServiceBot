package system.utilities.TicektManagerUtility;

import system.utilities.languageManager.MessageKeys;

import java.util.Arrays;

public enum PaymentMethods {

    PAYPAL(0, "paypal", MessageKeys.TICKET_CHECKOUT_SELECTION_PAYPAL),
    BITOIN(1, "bitcoin", MessageKeys.TICKET_CHECKOUT_SELECTION_BITCOINS),
    CASHAPP(2, "cashapp", MessageKeys.TICKET_CHECKOUT_SELECTION_CASHAPP),

    ;

    private final int id;
    private final String key;
    private final MessageKeys displayName;

    PaymentMethods(int id, String key, MessageKeys displayName) {
        this.id = id;
        this.key = key;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public MessageKeys getDisplayName() {
        return displayName;
    }

    public static PaymentMethods getPaymentByKey(String key) {
        return Arrays.stream(PaymentMethods.values()).filter(f -> f.getKey().equalsIgnoreCase(key)).findFirst().get();
    }
}
