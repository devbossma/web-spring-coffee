package dev.saberlabs.springcoffee.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * STOMP messaging over the same {@code /ws/chat} endpoint the raw handler used to own: a message
 * sent to {@code /app/chat/{orderId}} is handled by {@link ChatStompController}'s
 * {@code @MessageMapping} method, and broadcast back out to every subscriber of
 * {@code /topic/chat/{orderId}} through the simple in-memory broker below.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registered explicitly, rather than relying on Spring's own Jackson-on-the-classpath
     * auto-detection, so the converter actually in use here is never a surprise.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new JacksonJsonMessageConverter());
        return false;
    }
}
