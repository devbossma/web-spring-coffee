package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotifierTest {

    @Test
    void everyRegisteredObserverIsNotified() {
        List<CoffeeOrder> seenByFirst = new ArrayList<>();
        List<CoffeeOrder> seenBySecond = new ArrayList<>();
        OrderNotifier notifier = new OrderNotifier(List.of(seenByFirst::add, seenBySecond::add));

        CoffeeOrder order = new CoffeeOrder("ORD-1", "CUST-1", CoffeeType.ESPRESSO,
                EnumSet.noneOf(Extra.class), new BigDecimal("2.50"));
        notifier.notifyStatusChanged(order);

        assertThat(seenByFirst).containsExactly(order);
        assertThat(seenBySecond).containsExactly(order);
    }
}
