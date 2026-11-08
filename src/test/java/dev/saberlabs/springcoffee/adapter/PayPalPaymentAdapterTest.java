package dev.saberlabs.springcoffee.adapter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PayPalPaymentAdapterTest {

    private final PayPalPaymentAdapter adapter = new PayPalPaymentAdapter(new PayPalClient());

    @Test
    void adaptsAPayPalResponseIntoAPaymentResult() {
        PaymentResult result = adapter.charge("ORD-20260101-002", new BigDecimal("4.00"));

        assertThat(result.successful()).isTrue();
        assertThat(result.providerReference()).isNotBlank();
    }

    @Test
    void payPalClientItselfReturnsACompletedResponse() {
        PayPalClient.PayPalResponse response = new PayPalClient().pay(4.00, "test");
        assertThat(response.state()).isEqualTo("COMPLETED");
    }
}
