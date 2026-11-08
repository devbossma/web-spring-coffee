package dev.saberlabs.springcoffee.decorator;

import java.math.BigDecimal;

public interface CoffeeComponent {
    String getDescription();

    BigDecimal getCost();
}
