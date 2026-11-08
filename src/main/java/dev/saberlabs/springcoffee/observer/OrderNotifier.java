package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Subject: every {@link OrderObserver} bean in the context is injected here automatically by
 * Spring (a {@code List<OrderObserver>} constructor parameter collects them all), so adding a new
 * observer is just writing the class, never editing this one.
 */
@Component
public class OrderNotifier {

    private final List<OrderObserver> observers;

    public OrderNotifier(List<OrderObserver> observers) {
        this.observers = observers;
    }

    public void notifyStatusChanged(CoffeeOrder order) {
        for (OrderObserver observer : observers) {
            observer.onStatusChanged(order);
        }
    }
}
