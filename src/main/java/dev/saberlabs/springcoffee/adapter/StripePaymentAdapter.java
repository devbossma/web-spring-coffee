package dev.saberlabs.springcoffee.adapter;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** The default gateway. {@link PayPalPaymentAdapter} exists side by side with the same interface. */
@Primary
@Component
public class StripePaymentAdapter implements PaymentGateway {

    private final StripeClient stripeClient;

    public StripePaymentAdapter(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public PaymentResult charge(String orderCode, BigDecimal amount) {
        long amountCents = amount.setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();
        StripeClient.StripeCharge charge = stripeClient.submit(amountCents, "Coffee order " + orderCode);
        return new PaymentResult(charge.captured(), charge.id());
    }
}
