package dev.saberlabs.springcoffee.model;

import java.math.BigDecimal;

/** The fixed menu. Each type carries its own base price, read by {@code CoffeeFactory}. */
public enum CoffeeType {
    ESPRESSO("Espresso", new BigDecimal("2.50")),
    AMERICANO("Americano", new BigDecimal("2.75")),
    MACCHIATO("Macchiato", new BigDecimal("3.25")),
    CAPPUCCINO("Cappuccino", new BigDecimal("3.50")),
    LATTE("Latte", new BigDecimal("3.75")),
    MOCHA("Mocha", new BigDecimal("4.00"));

    private final String displayName;
    private final BigDecimal basePrice;

    CoffeeType(String displayName, BigDecimal basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String displayName() {
        return displayName;
    }

    public BigDecimal basePrice() {
        return basePrice;
    }
}
