package dev.saberlabs.springcoffee.strategy;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;

/** Picks the strategy in effect right now: happy hour from 14:00 to 16:00, regular otherwise. */
@Component
public class PricingStrategyResolver {

    private static final LocalTime HAPPY_HOUR_START = LocalTime.of(14, 0);
    private static final LocalTime HAPPY_HOUR_END = LocalTime.of(16, 0);

    private final Clock clock;

    public PricingStrategyResolver(Clock clock) {
        this.clock = clock;
    }

    public PricingStrategy currentStrategy() {
        LocalTime now = LocalTime.now(clock);
        boolean isHappyHour = !now.isBefore(HAPPY_HOUR_START) && now.isBefore(HAPPY_HOUR_END);
        return isHappyHour ? new HappyHourPricingStrategy() : new RegularPricingStrategy();
    }
}
