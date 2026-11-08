package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReorderCommandTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;

    @Test
    void clonesThePastOrderAsAFreshPendingOne() {
        CoffeeOrder original = new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.MACCHIATO,
                EnumSet.of(Extra.SUGAR), new BigDecimal("3.35"));
        original.markFulfilled();
        when(coffeeOrderRepository.findById(5L)).thenReturn(Optional.of(original));
        when(coffeeOrderRepository.save(any(CoffeeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CoffeeOrder clone = new ReorderCommand(coffeeOrderRepository, 5L).execute();

        assertThat(clone.getStatus().name()).isEqualTo("PLACED");
        assertThat(clone.getOrderCode()).startsWith("ORD-");
        assertThat(clone.getCoffeeType()).isEqualTo(CoffeeType.MACCHIATO);
        assertThat(clone.getExtras()).containsExactly(Extra.SUGAR);
    }

    @Test
    void missingOriginalOrderThrows() {
        when(coffeeOrderRepository.findById(404L)).thenReturn(Optional.empty());

        assertThat(org.assertj.core.api.Assertions.catchThrowable(
                () -> new ReorderCommand(coffeeOrderRepository, 404L).execute()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
