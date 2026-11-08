package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMetricsObserverTest {

    private final OrderMetricsObserver observer = new OrderMetricsObserver();

    private CoffeeOrder newOrder() {
        return new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class),
                new BigDecimal("2.50"));
    }

    @Test
    void onlyFulfilledOrdersIncrementTheCount() {
        CoffeeOrder placed = newOrder();
        observer.onStatusChanged(placed);
        assertThat(observer.fulfilledCount()).isZero();

        CoffeeOrder fulfilled = newOrder();
        fulfilled.markFulfilled();
        observer.onStatusChanged(fulfilled);
        assertThat(observer.fulfilledCount()).isEqualTo(1);

        observer.onStatusChanged(fulfilled);
        assertThat(observer.fulfilledCount()).isEqualTo(2);
    }
}
