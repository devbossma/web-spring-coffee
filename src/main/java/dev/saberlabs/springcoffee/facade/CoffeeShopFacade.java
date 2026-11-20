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
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * The single entry point controllers and the WebSocket layer actually call. Hides which pattern
 * object does what: a command gets built and executed, an observer gets notified, a strategy
 * gets picked, all behind one small method each.
 *
 * <p>Each method is one {@code @Transactional} boundary: the command's repository writes and the
 * observer notification it triggers either all commit together or none of them do. Without this,
 * {@link #payOrder} or {@link #fulfillOrder} could save the order's new status, then blow up
 * inside {@code orderNotifier.notifyStatusChanged(...)}, leaving the status change committed with
 * no notification ever having gone out.
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

    @Transactional
    public CoffeeOrder placeOrder(String customerId, CoffeeType coffeeType, Set<Extra> extras) {
        return new PlaceOrderCommand(coffeeOrderRepository, coffeeRepository, pricingStrategyResolver,
                customerId, coffeeType, extras).execute();
    }

    @Transactional
    public CoffeeOrder payOrder(Long orderId) {
        return new PayOrderCommand(coffeeOrderRepository, paymentGateway, orderNotifier, orderId).execute();
    }

    @Transactional
    public CoffeeOrder fulfillOrder(Long orderId) {
        return new FulfillOrderCommand(coffeeOrderRepository, orderNotifier, orderId).execute();
    }

    @Transactional
    public CoffeeOrder cancelOrder(Long orderId) {
        return new CancelOrderCommand(coffeeOrderRepository, orderNotifier, orderId).execute();
    }

    @Transactional
    public CoffeeOrder reorder(Long originalOrderId) {
        return new ReorderCommand(coffeeOrderRepository, originalOrderId).execute();
    }
}
