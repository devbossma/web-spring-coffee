package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/** A second, independent observer: a running count of fulfilled orders, shown on the console. */
@Component
public class OrderMetricsObserver implements OrderObserver {

    private final AtomicInteger fulfilledCount = new AtomicInteger();

    @Override
    public void onStatusChanged(CoffeeOrder order) {
        if (order.getStatus() == OrderStatus.FULFILLED) {
            fulfilledCount.incrementAndGet();
        }
    }

    public int fulfilledCount() {
        return fulfilledCount.get();
    }
}
