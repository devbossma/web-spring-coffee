package dev.saberlabs.springcoffee.strategy;

import java.math.BigDecimal;

public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal apply(BigDecimal rawCost) {
        return rawCost;
    }
}
