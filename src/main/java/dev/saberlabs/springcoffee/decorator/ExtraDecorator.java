package dev.saberlabs.springcoffee.decorator;

import dev.saberlabs.springcoffee.model.Extra;

import java.math.BigDecimal;

/**
 * One decorator class parameterized by {@link Extra} rather than one subclass per extra: each
 * {@link Extra} already carries its own label and cost, so a {@code MilkDecorator},
 * {@code SugarDecorator}, etc. would just repeat the same two lines four times.
 */
public class ExtraDecorator extends CoffeeDecorator {

    private final Extra extra;

    public ExtraDecorator(CoffeeComponent wrapped, Extra extra) {
        super(wrapped);
        this.extra = extra;
    }

    @Override
    protected String extraLabel() {
        return extra.displayName();
    }

    @Override
    protected BigDecimal extraCost() {
        return extra.cost();
    }
}
