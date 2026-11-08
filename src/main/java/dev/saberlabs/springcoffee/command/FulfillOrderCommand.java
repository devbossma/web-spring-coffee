package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.observer.OrderNotifier;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;

public class FulfillOrderCommand implements OrderCommand {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final OrderNotifier orderNotifier;
    private final Long orderId;

    public FulfillOrderCommand(CoffeeOrderRepository coffeeOrderRepository, OrderNotifier orderNotifier,
                                Long orderId) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.orderNotifier = orderNotifier;
        this.orderId = orderId;
    }

    @Override
    public CoffeeOrder execute() {
        CoffeeOrder order = coffeeOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No such order: " + orderId));
        order.markFulfilled();
        CoffeeOrder saved = coffeeOrderRepository.save(order);
        orderNotifier.notifyStatusChanged(saved);
        return saved;
    }
}
