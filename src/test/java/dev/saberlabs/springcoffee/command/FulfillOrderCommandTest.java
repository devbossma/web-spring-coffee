package dev.saberlabs.springcoffee.command;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FulfillOrderCommandTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;
    @Mock
    private OrderNotifier orderNotifier;

    @Test
    void marksTheOrderFulfilledAndNotifies() {
        CoffeeOrder order = new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.ESPRESSO,
                EnumSet.noneOf(Extra.class), new BigDecimal("2.50"));
        when(coffeeOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(coffeeOrderRepository.save(order)).thenReturn(order);

        CoffeeOrder result = new FulfillOrderCommand(coffeeOrderRepository, orderNotifier, 1L).execute();

        assertThat(result.getStatus().name()).isEqualTo("FULFILLED");
        verify(orderNotifier).notifyStatusChanged(order);
    }
}
