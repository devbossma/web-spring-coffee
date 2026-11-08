package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import dev.saberlabs.springcoffee.strategy.PricingStrategyResolver;
import dev.saberlabs.springcoffee.strategy.RegularPricingStrategy;
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
class PlaceOrderCommandTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;
    @Mock
    private CoffeeRepository coffeeRepository;
    @Mock
    private PricingStrategyResolver pricingStrategyResolver;

    @Test
    void buildsAPricedOrderFromTheMenuEntryAndExtras() {
        Coffee latte = new Coffee(CoffeeType.LATTE, "Latte", new BigDecimal("3.75"));
        when(coffeeRepository.findByType(CoffeeType.LATTE)).thenReturn(Optional.of(latte));
        when(pricingStrategyResolver.currentStrategy()).thenReturn(new RegularPricingStrategy());
        when(coffeeOrderRepository.save(any(CoffeeOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlaceOrderCommand command = new PlaceOrderCommand(coffeeOrderRepository, coffeeRepository,
                pricingStrategyResolver, "CUST-1", CoffeeType.LATTE, EnumSet.of(Extra.MILK));

        CoffeeOrder order = command.execute();

        assertThat(order.getCoffeeType()).isEqualTo(CoffeeType.LATTE);
        assertThat(order.getCustomerId()).isEqualTo("CUST-1");
        assertThat(order.getExtras()).containsExactly(Extra.MILK);
        assertThat(order.getPrice()).isEqualTo(new BigDecimal("3.75").add(Extra.MILK.cost()));
        assertThat(order.getOrderCode()).startsWith("ORD-");
    }

    @Test
    void rejectsAnUnknownCoffeeType() {
        when(coffeeRepository.findByType(CoffeeType.MOCHA)).thenReturn(Optional.empty());

        PlaceOrderCommand command = new PlaceOrderCommand(coffeeOrderRepository, coffeeRepository,
                pricingStrategyResolver, "CUST-1", CoffeeType.MOCHA, EnumSet.noneOf(Extra.class));

        assertThat(org.assertj.core.api.Assertions.catchThrowable(command::execute))
                .isInstanceOf(IllegalStateException.class);
    }
}
