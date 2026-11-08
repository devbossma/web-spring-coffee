package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.websocket.OrderEventBroadcaster;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatOrderStatusObserverTest {

    private final List<String> broadcasts = new ArrayList<>();
    private final OrderEventBroadcaster broadcaster = (orderId, text) -> broadcasts.add(orderId + ":" + text);
    private final ChatOrderStatusObserver observer = new ChatOrderStatusObserver(broadcaster);

    private CoffeeOrder orderWithId(long id) {
        CoffeeOrder order = new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.MOCHA,
                EnumSet.noneOf(Extra.class), new BigDecimal("4.00"));
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }

    @Test
    void paidBroadcastsAPreparationMessage() {
        CoffeeOrder order = orderWithId(1L);
        order.markPaid();

        observer.onStatusChanged(order);

        assertThat(broadcasts).containsExactly("1:Payment received, your order is being prepared.");
    }

    @Test
    void fulfilledBroadcastsAReadyMessage() {
        CoffeeOrder order = orderWithId(2L);
        order.markFulfilled();

        observer.onStatusChanged(order);

        assertThat(broadcasts).containsExactly("2:Your order is ready. Enjoy!");
    }

    @Test
    void cancelledBroadcastsACancellationMessage() {
        CoffeeOrder order = orderWithId(3L);
        order.cancel();

        observer.onStatusChanged(order);

        assertThat(broadcasts).containsExactly("3:This order was cancelled.");
    }
}
