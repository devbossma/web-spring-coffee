package dev.saberlabs.springcoffee.decorator;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeDecoratorTest {

    private final Coffee latte = new Coffee(CoffeeType.LATTE, "Latte", new BigDecimal("3.75"));

    @Test
    void simpleCoffeeCostsJustItsBasePrice() {
        CoffeeComponent component = new SimpleCoffee(latte);

        assertThat(component.getDescription()).isEqualTo("Latte");
        assertThat(component.getCost()).isEqualTo(new BigDecimal("3.75"));
    }

    @Test
    void eachDecoratorAddsItsOwnCostAndLabel() {
        CoffeeComponent component = new SimpleCoffee(latte);
        component = new ExtraDecorator(component, Extra.MILK);
        component = new ExtraDecorator(component, Extra.EXTRA_SHOT);

        assertThat(component.getDescription()).isEqualTo("Latte, Milk, Extra shot");
        assertThat(component.getCost()).isEqualTo(new BigDecimal("3.75").add(Extra.MILK.cost()).add(Extra.EXTRA_SHOT.cost()));
    }
}
