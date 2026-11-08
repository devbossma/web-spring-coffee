package dev.saberlabs.springcoffee.websocket;

/**
 * What the rest of the app (the observer package, specifically) needs from the WebSocket layer:
 * a way to push a system message into an order's chat without knowing anything about sessions,
 * handlers, or the wire format underneath.
 */
public interface OrderEventBroadcaster {
    void broadcastSystemMessage(Long orderId, String text);
}
