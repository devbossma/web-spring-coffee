package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.decorator.CoffeeComponent;
import dev.saberlabs.springcoffee.decorator.ExtraDecorator;
import dev.saberlabs.springcoffee.decorator.SimpleCoffee;
import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import dev.saberlabs.springcoffee.singleton.DailySequenceGenerator;
import dev.saberlabs.springcoffee.strategy.PricingStrategyResolver;

import java.math.BigDecimal;
import java.util.Set;

/** Builds the decorated coffee, prices it with whatever strategy is in effect, and saves it. */
public class PlaceOrderCommand implements OrderCommand {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final CoffeeRepository coffeeRepository;
    private final PricingStrategyResolver pricingStrategyResolver;
    private final String customerId;
    private final CoffeeType coffeeType;
    private final Set<Extra> extras;

    public PlaceOrderCommand(CoffeeOrderRepository coffeeOrderRepository, CoffeeRepository coffeeRepository,
                              PricingStrategyResolver pricingStrategyResolver, String customerId,
                              CoffeeType coffeeType, Set<Extra> extras) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.coffeeRepository = coffeeRepository;
        this.pricingStrategyResolver = pricingStrategyResolver;
        this.customerId = customerId;
        this.coffeeType = coffeeType;
        this.extras = extras;
    }

    @Override
    public CoffeeOrder execute() {
        Coffee coffee = coffeeRepository.findByType(coffeeType)
                .orElseThrow(() -> new IllegalStateException("Unknown coffee type: " + coffeeType));

        CoffeeComponent decorated = new SimpleCoffee(coffee);
        for (Extra extra : extras) {
            decorated = new ExtraDecorator(decorated, extra);
        }

        BigDecimal price = pricingStrategyResolver.currentStrategy().apply(decorated.getCost());
        String orderCode = DailySequenceGenerator.getInstance().nextOrderCode();

        CoffeeOrder order = new CoffeeOrder(orderCode, customerId, coffeeType, extras, price);
        return coffeeOrderRepository.save(order);
    }
}
