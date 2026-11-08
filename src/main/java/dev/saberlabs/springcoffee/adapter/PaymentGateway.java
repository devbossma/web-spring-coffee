package dev.saberlabs.springcoffee.adapter;

import java.math.BigDecimal;

/** The one shape the rest of the app expects from a payment provider, whatever its own API looks like. */
public interface PaymentGateway {
    PaymentResult charge(String orderCode, BigDecimal amount);
}
