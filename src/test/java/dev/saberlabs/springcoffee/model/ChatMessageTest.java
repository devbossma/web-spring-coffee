package dev.saberlabs.springcoffee.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void capturesSenderTextAndOrderId() {
        ChatMessage message = new ChatMessage(7L, ChatSender.BARISTA, "Your order is ready");

        assertThat(message.getOrderId()).isEqualTo(7L);
        assertThat(message.getSender()).isEqualTo(ChatSender.BARISTA);
        assertThat(message.getText()).isEqualTo("Your order is ready");
        assertThat(message.getSentAt()).isNotNull();
    }
}
