package dev.saberlabs.springcoffee.facade;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.model.OrderStatus;
import dev.saberlabs.springcoffee.observer.OrderMetricsObserver;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

class CoffeeShopFacadeTest extends AbstractIntegrationTest {

    @Autowired
    private CoffeeShopFacade coffeeShopFacade;
    @Autowired
    private OrderMetricsObserver orderMetricsObserver;

    @Test
    void fullLifecyclePlaceThenPayThenFulfill() {
        CoffeeOrder placed = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.CAPPUCCINO, EnumSet.of(Extra.MILK));
        assertThat(placed.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(placed.getPrice()).isGreaterThan(CoffeeType.CAPPUCCINO.basePrice());

        CoffeeOrder paid = coffeeShopFacade.payOrder(placed.getId());
        assertThat(paid.getStatus()).isEqualTo(OrderStatus.PAID);

        int before = orderMetricsObserver.fulfilledCount();
        CoffeeOrder fulfilled = coffeeShopFacade.fulfillOrder(placed.getId());
        assertThat(fulfilled.getStatus()).isEqualTo(OrderStatus.FULFILLED);
        assertThat(orderMetricsObserver.fulfilledCount()).isEqualTo(before + 1);
    }

    @Test
    void cancellingAnOrderMarksItCancelled() {
        CoffeeOrder placed = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class));
        CoffeeOrder cancelled = coffeeShopFacade.cancelOrder(placed.getId());
        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void reorderingClonesAPastOrderAsAFreshOne() {
        CoffeeOrder original = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.LATTE, EnumSet.of(Extra.SUGAR));
        coffeeShopFacade.fulfillOrder(original.getId());

        CoffeeOrder reordered = coffeeShopFacade.reorder(original.getId());

        assertThat(reordered.getId()).isNotEqualTo(original.getId());
        assertThat(reordered.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(reordered.getCoffeeType()).isEqualTo(CoffeeType.LATTE);
        assertThat(reordered.getExtras()).containsExactly(Extra.SUGAR);
    }
}
