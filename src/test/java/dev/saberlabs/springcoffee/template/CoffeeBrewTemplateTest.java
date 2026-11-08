package dev.saberlabs.springcoffee.template;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeBrewTemplateTest {

    @Test
    void espressoHasNoExtraStep() {
        List<String> steps = new EspressoBrewTemplate().steps();
        assertThat(steps).containsExactly(
                "Boil water to 93C",
                "Pull a double espresso shot",
                "Pour into a cup");
    }

    @Test
    void americanoAddsHotWaterAsItsExtraStep() {
        List<String> steps = new AmericanoBrewTemplate().steps();
        assertThat(steps).containsExactly(
                "Boil water to 93C",
                "Pull a single espresso shot",
                "Pour into a cup",
                "Add hot water to lengthen the shot");
    }

    @Test
    void macchiatoAddsADashOfMilkFoam() {
        assertThat(new MacchiatoBrewTemplate().steps())
                .last().isEqualTo("Mark with a dash of steamed milk foam");
    }

    @Test
    void cappuccinoAddsEqualPartsMilkAndFoam() {
        assertThat(new CappuccinoBrewTemplate().steps())
                .last().isEqualTo("Add equal parts steamed milk and milk foam");
    }

    @Test
    void latteAddsSteamedMilkWithLightFoam() {
        assertThat(new LatteBrewTemplate().steps())
                .last().isEqualTo("Add steamed milk with a thin layer of foam");
    }

    @Test
    void mochaAddsChocolateAndSteamedMilk() {
        assertThat(new MochaBrewTemplate().steps())
                .last().isEqualTo("Stir in chocolate syrup, then top with steamed milk");
    }

    @Test
    void everyVariantSharesTheSameBoilAndPourSteps() {
        for (CoffeeBrewTemplate template : List.of(
                new EspressoBrewTemplate(), new AmericanoBrewTemplate(), new MacchiatoBrewTemplate(),
                new CappuccinoBrewTemplate(), new LatteBrewTemplate(), new MochaBrewTemplate())) {
            List<String> steps = template.steps();
            assertThat(steps.get(0)).isEqualTo("Boil water to 93C");
            assertThat(steps).contains("Pour into a cup");
        }
    }
}
