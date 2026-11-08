package dev.saberlabs.springcoffee.websocket;

import dev.saberlabs.springcoffee.model.ChatSender;

import java.time.Instant;

/** The JSON shape exchanged over the socket in both directions. */
public record ChatSocketPayload(ChatSender sender, String text, Instant sentAt) {
}
