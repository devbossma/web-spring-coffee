package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.adapter.PaymentGateway;
import dev.saberlabs.springcoffee.adapter.PaymentResult;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.observer.OrderNotifier;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayOrderCommandTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;
    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private OrderNotifier orderNotifier;

    private CoffeeOrder order() {
        return new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class),
                new BigDecimal("2.50"));
    }

    @Test
    void successfulChargeMarksTheOrderPaidAndNotifies() {
        CoffeeOrder order = order();
        when(coffeeOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.charge(any(), any())).thenReturn(new PaymentResult(true, "ref"));
        when(coffeeOrderRepository.save(order)).thenReturn(order);

        CoffeeOrder result = new PayOrderCommand(coffeeOrderRepository, paymentGateway, orderNotifier, 1L).execute();

        assertThat(result.getStatus().name()).isEqualTo("PAID");
        verify(orderNotifier).notifyStatusChanged(order);
    }

    @Test
    void declinedChargeThrowsAndNeverMarksPaid() {
        CoffeeOrder order = order();
        when(coffeeOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.charge(any(), any())).thenReturn(new PaymentResult(false, "ref"));

        Throwable thrown = catchThrowable(
                () -> new PayOrderCommand(coffeeOrderRepository, paymentGateway, orderNotifier, 1L).execute());

        assertThat(thrown).isInstanceOf(IllegalStateException.class);
        assertThat(order.getStatus().name()).isEqualTo("PLACED");
    }

    @Test
    void missingOrderThrows() {
        when(coffeeOrderRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(
                () -> new PayOrderCommand(coffeeOrderRepository, paymentGateway, orderNotifier, 99L).execute());

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
}
