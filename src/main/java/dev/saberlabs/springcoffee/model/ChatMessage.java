package dev.saberlabs.springcoffee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatSender sender;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    private Instant sentAt;

    protected ChatMessage() {
        // JPA
    }

    public ChatMessage(Long orderId, ChatSender sender, String text) {
        this.orderId = orderId;
        this.sender = sender;
        this.text = text;
        this.sentAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public ChatSender getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
