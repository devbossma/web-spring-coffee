package dev.saberlabs.springcoffee.factory;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeFactoryTest {

    private final CoffeeFactory factory = new CoffeeFactory();

    @Test
    void createsACoffeeMatchingItsType() {
        Coffee coffee = factory.create(CoffeeType.LATTE);

        assertThat(coffee.getType()).isEqualTo(CoffeeType.LATTE);
        assertThat(coffee.getName()).isEqualTo("Latte");
        assertThat(coffee.getBasePrice()).isEqualTo(CoffeeType.LATTE.basePrice());
    }

    @Test
    void everyMenuTypeProducesADistinctCoffee() {
        for (CoffeeType type : CoffeeType.values()) {
            Coffee coffee = factory.create(type);
            assertThat(coffee.getType()).isEqualTo(type);
            assertThat(coffee.getName()).isEqualTo(type.displayName());
            assertThat(coffee.getBasePrice()).isEqualTo(type.basePrice());
        }
    }
}
