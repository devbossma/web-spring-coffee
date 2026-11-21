package dev.saberlabs.springcoffee.websocket;

import dev.saberlabs.springcoffee.model.ChatMessage;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * One chat "room" per order id, taken from the destination path rather than any per-barista
 * routing: this app assumes a single shared barista console, so every subscriber to an order's
 * topic gets every message, the same broadcast shape the previous raw {@code TextWebSocketHandler}
 * had, just expressed through STOMP destinations instead of a hand-kept session map.
 */
@Controller
public class ChatStompController implements OrderEventBroadcaster {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{orderId}")
    public void handleMessage(@DestinationVariable Long orderId, ChatSocketPayload incoming) {
        ChatMessage saved = chatService.save(orderId, incoming.sender(), incoming.text());
        broadcast(orderId, saved);
    }

    @Override
    public void broadcastSystemMessage(Long orderId, String text) {
        ChatMessage saved = chatService.save(orderId, ChatSender.SYSTEM, text);
        broadcast(orderId, saved);
    }

    private void broadcast(Long orderId, ChatMessage saved) {
        ChatSocketPayload outgoing = new ChatSocketPayload(saved.getSender(), saved.getText(), saved.getSentAt());
        messagingTemplate.convertAndSend("/topic/chat/" + orderId, outgoing);
    }
}
