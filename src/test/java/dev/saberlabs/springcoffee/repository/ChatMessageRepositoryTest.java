package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.SharedMySqlContainer;
import dev.saberlabs.springcoffee.model.ChatMessage;
import dev.saberlabs.springcoffee.model.ChatSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatMessageRepositoryTest extends SharedMySqlContainer {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @AfterEach
    void cleanUp() {
        chatMessageRepository.deleteAllInBatch();
    }

    @Test
    void returnsHistoryForAnOrderInSentOrder() throws InterruptedException {
        chatMessageRepository.save(new ChatMessage(1L, ChatSender.CUSTOMER, "Hi"));
        Thread.sleep(5);
        chatMessageRepository.save(new ChatMessage(1L, ChatSender.BARISTA, "Hello"));
        chatMessageRepository.save(new ChatMessage(2L, ChatSender.CUSTOMER, "Unrelated order"));

        List<ChatMessage> history = chatMessageRepository.findByOrderIdOrderBySentAtAsc(1L);

        assertThat(history).extracting(ChatMessage::getText).containsExactly("Hi", "Hello");
    }
}
