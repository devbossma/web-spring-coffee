package dev.saberlabs.springcoffee.factory;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import org.springframework.stereotype.Component;

/**
 * Builds the {@link Coffee} menu entities from the fixed {@link CoffeeType} catalog. Centralizes
 * "how a menu item is actually constructed" in one place, instead of every caller writing
 * {@code new Coffee(type, type.displayName(), type.basePrice())} themselves.
 */
@Component
public class CoffeeFactory {

    public Coffee create(CoffeeType type) {
        return new Coffee(type, type.displayName(), type.basePrice());
    }
}
