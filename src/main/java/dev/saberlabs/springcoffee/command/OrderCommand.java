package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.model.CoffeeOrder;

/** One order-mutating action, fully parameterized at construction time, run via {@link #execute()}. */
public interface OrderCommand {
    CoffeeOrder execute();
}
