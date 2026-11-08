package dev.saberlabs.springcoffee.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** 10% off, applied only while {@link PricingStrategyResolver} decides it is happy hour. */
public class HappyHourPricingStrategy implements PricingStrategy {

    private static final BigDecimal DISCOUNT_MULTIPLIER = new BigDecimal("0.90");

    @Override
    public BigDecimal apply(BigDecimal rawCost) {
        return rawCost.multiply(DISCOUNT_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }
}
