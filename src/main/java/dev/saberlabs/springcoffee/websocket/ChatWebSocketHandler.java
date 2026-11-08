package dev.saberlabs.springcoffee.websocket;

import tools.jackson.databind.ObjectMapper;
import dev.saberlabs.springcoffee.model.ChatMessage;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * One chat "room" per order id, taken from the connection's own query string
 * ({@code /ws/chat?orderId=5}), not from any per-barista routing: this app assumes a single
 * shared barista console, so every open session for an order simply gets every message.
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler implements OrderEventBroadcaster {

    private static final String ORDER_ID_ATTRIBUTE = "orderId";

    private final Map<Long, Set<WebSocketSession>> sessionsByOrder = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long orderId = orderIdFrom(session);
        session.getAttributes().put(ORDER_ID_ATTRIBUTE, orderId);
        sessionsByOrder.computeIfAbsent(orderId, id -> new CopyOnWriteArraySet<>()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long orderId = (Long) session.getAttributes().get(ORDER_ID_ATTRIBUTE);
        if (orderId != null) {
            Set<WebSocketSession> sessions = sessionsByOrder.get(orderId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Long orderId = (Long) session.getAttributes().get(ORDER_ID_ATTRIBUTE);
        ChatSocketPayload incoming = objectMapper.readValue(message.getPayload(), ChatSocketPayload.class);
        ChatMessage saved = chatService.save(orderId, incoming.sender(), incoming.text());
        broadcast(orderId, saved);
    }

    @Override
    public void broadcastSystemMessage(Long orderId, String text) {
        ChatMessage saved = chatService.save(orderId, ChatSender.SYSTEM, text);
        broadcast(orderId, saved);
    }

    private void broadcast(Long orderId, ChatMessage saved) {
        Set<WebSocketSession> sessions = sessionsByOrder.get(orderId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        ChatSocketPayload outgoing = new ChatSocketPayload(saved.getSender(), saved.getText(), saved.getSentAt());
        for (WebSocketSession session : List.copyOf(sessions)) {
            sendQuietly(session, outgoing);
        }
    }

    private void sendQuietly(WebSocketSession session, ChatSocketPayload payload) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (IOException e) {
            sessionsByOrder.getOrDefault((Long) session.getAttributes().get(ORDER_ID_ATTRIBUTE), Set.of())
                    .remove(session);
        }
    }

    private Long orderIdFrom(WebSocketSession session) {
        String raw = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst("orderId");
        return Long.valueOf(raw);
    }
}
