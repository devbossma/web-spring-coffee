package dev.saberlabs.springcoffee.model;

import java.math.BigDecimal;

/** An add-on a customer can pile onto their base coffee, each wrapped on by its own decorator. */
public enum Extra {
    MILK("Milk", new BigDecimal("0.40")),
    SUGAR("Sugar", new BigDecimal("0.10")),
    EXTRA_SHOT("Extra shot", new BigDecimal("0.80")),
    WHIPPED_CREAM("Whipped cream", new BigDecimal("0.60"));

    private final String displayName;
    private final BigDecimal cost;

    Extra(String displayName, BigDecimal cost) {
        this.displayName = displayName;
        this.cost = cost;
    }

    public String displayName() {
        return displayName;
    }

    public BigDecimal cost() {
        return cost;
    }
}
