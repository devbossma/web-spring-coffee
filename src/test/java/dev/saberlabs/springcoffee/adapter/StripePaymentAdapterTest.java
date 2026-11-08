package dev.saberlabs.springcoffee.adapter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StripePaymentAdapterTest {

    private final StripePaymentAdapter adapter = new StripePaymentAdapter(new StripeClient());

    @Test
    void adaptsAStripeChargeIntoAPaymentResult() {
        PaymentResult result = adapter.charge("ORD-20260101-001", new BigDecimal("3.75"));

        assertThat(result.successful()).isTrue();
        assertThat(result.providerReference()).startsWith("ch_");
    }

    @Test
    void stripeClientItselfReturnsACapturedCharge() {
        StripeClient.StripeCharge charge = new StripeClient().submit(375, "test");
        assertThat(charge.captured()).isTrue();
        assertThat(charge.id()).startsWith("ch_");
    }
}
