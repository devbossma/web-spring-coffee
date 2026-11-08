package dev.saberlabs.springcoffee.config;

import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class ClockConfigTest {

    @Test
    void exposesASystemClock() {
        Clock clock = new ClockConfig().clock();
        assertThat(clock).isNotNull();
        assertThat(clock.instant()).isNotNull();
    }
}
