package dev.saberlabs.springcoffee.adapter;

import org.springframework.stereotype.Component;

import java.util.UUID;

/** Stands in for the real PayPal SDK: a {@code double} amount and a free-text note, its own shape. */
@Component
public class PayPalClient {

    public PayPalResponse pay(double amount, String note) {
        return new PayPalResponse(UUID.randomUUID().toString(), "COMPLETED");
    }

    public record PayPalResponse(String transactionId, String state) {
    }
}
