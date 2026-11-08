package dev.saberlabs.springcoffee.decorator;

import dev.saberlabs.springcoffee.model.Coffee;

import java.math.BigDecimal;

/** The undecorated base: just whatever the menu's base price for this coffee is. */
public class SimpleCoffee implements CoffeeComponent {

    private final Coffee coffee;

    public SimpleCoffee(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return coffee.getName();
    }

    @Override
    public BigDecimal getCost() {
        return coffee.getBasePrice();
    }
}
