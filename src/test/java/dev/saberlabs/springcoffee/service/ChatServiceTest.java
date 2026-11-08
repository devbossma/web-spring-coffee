package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.ChatMessage;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    void saveDelegatesToTheRepository() {
        ChatService chatService = new ChatService(chatMessageRepository);
        when(chatMessageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessage saved = chatService.save(1L, ChatSender.CUSTOMER, "Hi there");

        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getSender()).isEqualTo(ChatSender.CUSTOMER);
        assertThat(saved.getText()).isEqualTo("Hi there");
    }

    @Test
    void historyForDelegatesToTheRepository() {
        ChatService chatService = new ChatService(chatMessageRepository);
        when(chatMessageRepository.findByOrderIdOrderBySentAtAsc(1L)).thenReturn(List.of());

        assertThat(chatService.historyFor(1L)).isEmpty();
    }
}
