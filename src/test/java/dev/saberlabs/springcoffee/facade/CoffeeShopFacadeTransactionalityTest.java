package dev.saberlabs.springcoffee.facade;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.model.OrderStatus;
import dev.saberlabs.springcoffee.observer.OrderNotifier;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * Proves {@code @Transactional} on {@link CoffeeShopFacade} actually does something: if the
 * observer notification that follows a status change blows up, the status change itself must
 * roll back too, rather than being left committed with no notification ever having gone out.
 */
class CoffeeShopFacadeTransactionalityTest extends AbstractIntegrationTest {

    @Autowired
    private CoffeeShopFacade coffeeShopFacade;
    @MockitoBean
    private OrderNotifier orderNotifier;

    @Test
    void aFailedNotificationRollsBackTheStatusChangeThatTriggeredIt() {
        CoffeeOrder placed = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class));
        doThrow(new RuntimeException("broadcast failed")).when(orderNotifier).notifyStatusChanged(any());

        assertThatThrownBy(() -> coffeeShopFacade.payOrder(placed.getId()))
                .isInstanceOf(RuntimeException.class);

        CoffeeOrder reloaded = coffeeOrderRepository.findById(placed.getId()).orElseThrow();
        assertThat(reloaded.getStatus())
                .as("the payment status change must roll back along with the failed notification")
                .isEqualTo(OrderStatus.PLACED);
    }
}
