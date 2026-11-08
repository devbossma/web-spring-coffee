package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByOrderIdOrderBySentAtAsc(Long orderId);
}
