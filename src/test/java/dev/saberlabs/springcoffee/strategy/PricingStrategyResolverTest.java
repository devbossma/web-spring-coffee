package dev.saberlabs.springcoffee.strategy;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyResolverTest {

    @Test
    void duringHappyHourResolvesTheDiscountStrategy() {
        Clock fifteenOClock = Clock.fixed(
                Instant.parse("2026-01-01T15:00:00Z"), ZoneId.of("UTC"));
        PricingStrategyResolver resolver = new PricingStrategyResolver(fifteenOClock);

        assertThat(resolver.currentStrategy()).isInstanceOf(HappyHourPricingStrategy.class);
    }

    @Test
    void outsideHappyHourResolvesTheRegularStrategy() {
        Clock tenOClock = Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));
        PricingStrategyResolver resolver = new PricingStrategyResolver(tenOClock);

        assertThat(resolver.currentStrategy()).isInstanceOf(RegularPricingStrategy.class);
    }
}
