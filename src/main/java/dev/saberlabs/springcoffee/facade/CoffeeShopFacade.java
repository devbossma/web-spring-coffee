package dev.saberlabs.springcoffee.facade;

import dev.saberlabs.springcoffee.adapter.PaymentGateway;
import dev.saberlabs.springcoffee.command.CancelOrderCommand;
import dev.saberlabs.springcoffee.command.FulfillOrderCommand;
import dev.saberlabs.springcoffee.command.PayOrderCommand;
import dev.saberlabs.springcoffee.command.PlaceOrderCommand;
import dev.saberlabs.springcoffee.command.ReorderCommand;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.observer.OrderNotifier;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import dev.saberlabs.springcoffee.strategy.PricingStrategyResolver;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The single entry point controllers and the WebSocket layer actually call. Hides which pattern
 * object does what: a command gets built and executed, an observer gets notified, a strategy
 * gets picked, all behind one small method each.
 */
@Component
public class CoffeeShopFacade {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final CoffeeRepository coffeeRepository;
    private final PricingStrategyResolver pricingStrategyResolver;
    private final PaymentGateway paymentGateway;
    private final OrderNotifier orderNotifier;

    public CoffeeShopFacade(CoffeeOrderRepository coffeeOrderRepository, CoffeeRepository coffeeRepository,
                             PricingStrategyResolver pricingStrategyResolver, PaymentGateway paymentGateway,
                             OrderNotifier orderNotifier) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.coffeeRepository = coffeeRepository;
        this.pricingStrategyResolver = pricingStrategyResolver;
        this.paymentGateway = paymentGateway;
        this.orderNotifier = orderNotifier;
    }

    public CoffeeOrder placeOrder(String customerId, CoffeeType coffeeType, Set<Extra> extras) {
        return new PlaceOrderCommand(coffeeOrderRepository, coffeeRepository, pricingStrategyResolver,
                customerId, coffeeType, extras).execute();
    }

    public CoffeeOrder payOrder(Long orderId) {
        return new PayOrderCommand(coffeeOrderRepository, paymentGateway, orderNotifier, orderId).execute();
    }

    public CoffeeOrder fulfillOrder(Long orderId) {
        return new FulfillOrderCommand(coffeeOrderRepository, orderNotifier, orderId).execute();
    }

    public CoffeeOrder cancelOrder(Long orderId) {
        return new CancelOrderCommand(coffeeOrderRepository, orderNotifier, orderId).execute();
    }

    public CoffeeOrder reorder(Long originalOrderId) {
        return new ReorderCommand(coffeeOrderRepository, originalOrderId).execute();
    }
}
