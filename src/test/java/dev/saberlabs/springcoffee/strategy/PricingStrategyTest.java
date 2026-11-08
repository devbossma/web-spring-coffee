package dev.saberlabs.springcoffee.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyTest {

    @Test
    void regularPricingLeavesTheCostUnchanged() {
        PricingStrategy regular = new RegularPricingStrategy();
        assertThat(regular.apply(new BigDecimal("3.50"))).isEqualTo(new BigDecimal("3.50"));
    }

    @Test
    void happyHourTakesTenPercentOff() {
        PricingStrategy happyHour = new HappyHourPricingStrategy();
        assertThat(happyHour.apply(new BigDecimal("4.00"))).isEqualTo(new BigDecimal("3.60"));
    }
}
