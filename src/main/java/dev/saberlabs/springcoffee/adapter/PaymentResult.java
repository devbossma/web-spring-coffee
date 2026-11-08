package dev.saberlabs.springcoffee.adapter;

public record PaymentResult(boolean successful, String providerReference) {
}
