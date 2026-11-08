package dev.saberlabs.springcoffee.decorator;

import java.math.BigDecimal;

/** Wraps another {@link CoffeeComponent}, adding one extra's cost and description on top. */
public abstract class CoffeeDecorator implements CoffeeComponent {

    private final CoffeeComponent wrapped;

    protected CoffeeDecorator(CoffeeComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + ", " + extraLabel();
    }

    @Override
    public BigDecimal getCost() {
        return wrapped.getCost().add(extraCost());
    }

    protected abstract String extraLabel();

    protected abstract BigDecimal extraCost();
}
