package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.ChatMessage;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage save(Long orderId, ChatSender sender, String text) {
        return chatMessageRepository.save(new ChatMessage(orderId, sender, text));
    }

    public List<ChatMessage> historyFor(Long orderId) {
        return chatMessageRepository.findByOrderIdOrderBySentAtAsc(orderId);
    }
}
