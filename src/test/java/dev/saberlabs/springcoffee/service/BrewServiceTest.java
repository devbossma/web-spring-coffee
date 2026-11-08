package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.CoffeeType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrewServiceTest {

    private final BrewService brewService = new BrewService();

    @Test
    void everyMenuItemHasPrepSteps() {
        for (CoffeeType type : CoffeeType.values()) {
            assertThat(brewService.stepsFor(type)).isNotEmpty();
        }
    }

    @Test
    void americanoStepsMentionAddingWater() {
        assertThat(brewService.stepsFor(CoffeeType.AMERICANO)).anyMatch(step -> step.contains("water"));
    }
}
