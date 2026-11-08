package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.websocket.OrderEventBroadcaster;
import org.springframework.stereotype.Component;

/** Pushes a system message into the order's chat every time its status changes. */
@Component
public class ChatOrderStatusObserver implements OrderObserver {

    private final OrderEventBroadcaster broadcaster;

    public ChatOrderStatusObserver(OrderEventBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void onStatusChanged(CoffeeOrder order) {
        String text = switch (order.getStatus()) {
            case PAID -> "Payment received, your order is being prepared.";
            case FULFILLED -> "Your order is ready. Enjoy!";
            case CANCELLED -> "This order was cancelled.";
            case PLACED -> "Order received.";
        };
        broadcaster.broadcastSystemMessage(order.getId(), text);
    }
}
