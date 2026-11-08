package dev.saberlabs.springcoffee.strategy;

import java.math.BigDecimal;

/** Turns a decorated coffee's raw cost into the price the customer actually pays. */
public interface PricingStrategy {
    BigDecimal apply(BigDecimal rawCost);
}
