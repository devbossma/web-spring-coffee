package dev.saberlabs.springcoffee.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeOrderTest {

    private CoffeeOrder newOrder() {
        return new CoffeeOrder("ORD-20260101-001", "CUST-20260101-001", CoffeeType.LATTE,
                EnumSet.of(Extra.MILK), new BigDecimal("4.15"));
    }

    @Test
    void startsOutPlaced() {
        assertThat(newOrder().getStatus()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void statusTransitionsAreTracked() {
        CoffeeOrder order = newOrder();

        order.markPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        order.markFulfilled();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FULFILLED);
    }

    @Test
    void cancelSetsCancelledStatus() {
        CoffeeOrder order = newOrder();
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void copyKeepsContentButResetsIdentityAndStatus() {
        CoffeeOrder original = newOrder();
        ReflectionTestUtils.setField(original, "id", 42L);
        original.markFulfilled();

        CoffeeOrder copy = original.copy();

        assertThat(copy.getId()).isNull();
        assertThat(copy.getOrderCode()).isNull();
        assertThat(copy.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(copy.getCustomerId()).isEqualTo(original.getCustomerId());
        assertThat(copy.getCoffeeType()).isEqualTo(original.getCoffeeType());
        assertThat(copy.getExtras()).isEqualTo(original.getExtras());
        assertThat(copy.getPrice()).isEqualTo(original.getPrice());
    }

    @Test
    void assignOrderCodeSetsItAfterTheFact() {
        CoffeeOrder order = newOrder().copy();
        order.assignOrderCode("ORD-20260101-002");
        assertThat(order.getOrderCode()).isEqualTo("ORD-20260101-002");
    }
}
