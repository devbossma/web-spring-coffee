package dev.saberlabs.springcoffee.adapter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PayPalPaymentAdapter implements PaymentGateway {

    private final PayPalClient payPalClient;

    public PayPalPaymentAdapter(PayPalClient payPalClient) {
        this.payPalClient = payPalClient;
    }

    @Override
    public PaymentResult charge(String orderCode, BigDecimal amount) {
        PayPalClient.PayPalResponse response = payPalClient.pay(amount.doubleValue(), "Coffee order " + orderCode);
        return new PaymentResult("COMPLETED".equals(response.state()), response.transactionId());
    }
}
