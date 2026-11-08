package dev.saberlabs.springcoffee.adapter;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stands in for the real Stripe SDK: its own shape (cents as a {@code long}, a free-text
 * description), nothing like {@link PaymentGateway}. No real charge happens, there is no payment
 * processor behind this project, {@link StripePaymentAdapter} is what the rest of the app
 * actually talks to.
 */
@Component
public class StripeClient {

    public StripeCharge submit(long amountCents, String description) {
        return new StripeCharge("ch_" + UUID.randomUUID(), true);
    }

    public record StripeCharge(String id, boolean captured) {
    }
}
