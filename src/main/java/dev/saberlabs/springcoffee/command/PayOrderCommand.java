package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.adapter.PaymentGateway;
import dev.saberlabs.springcoffee.adapter.PaymentResult;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.observer.OrderNotifier;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;

public class PayOrderCommand implements OrderCommand {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final PaymentGateway paymentGateway;
    private final OrderNotifier orderNotifier;
    private final Long orderId;

    public PayOrderCommand(CoffeeOrderRepository coffeeOrderRepository, PaymentGateway paymentGateway,
                            OrderNotifier orderNotifier, Long orderId) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.paymentGateway = paymentGateway;
        this.orderNotifier = orderNotifier;
        this.orderId = orderId;
    }

    @Override
    public CoffeeOrder execute() {
        CoffeeOrder order = coffeeOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No such order: " + orderId));

        PaymentResult result = paymentGateway.charge(order.getOrderCode(), order.getPrice());
        if (!result.successful()) {
            throw new IllegalStateException("Payment declined for order " + order.getOrderCode());
        }

        order.markPaid();
        CoffeeOrder saved = coffeeOrderRepository.save(order);
        orderNotifier.notifyStatusChanged(saved);
        return saved;
    }
}
