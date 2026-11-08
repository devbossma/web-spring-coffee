package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.OrderStatus;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;

    private OrderQueryService orderQueryService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        orderQueryService = new OrderQueryService(coffeeOrderRepository);
    }

    @Test
    void delegatesCustomerLookupToTheRepository() {
        when(coffeeOrderRepository.findByCustomerIdOrderByCreatedAtDesc("CUST-1")).thenReturn(List.of());
        assertThat(orderQueryService.forCustomer("CUST-1")).isEmpty();
    }

    @Test
    void activeOrdersAreOnlyPlacedOrPaid() {
        orderQueryService.activeOrders();
        verify(coffeeOrderRepository)
                .findByStatusInOrderByCreatedAtAsc(List.of(OrderStatus.PLACED, OrderStatus.PAID));
    }

    @Test
    void byIdDelegatesToTheRepository() {
        when(coffeeOrderRepository.findById(5L)).thenReturn(Optional.empty());
        assertThat(orderQueryService.byId(5L)).isEmpty();
    }
}
