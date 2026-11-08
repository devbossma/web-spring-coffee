package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.OrderStatus;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Read-only order lookups, kept separate from the mutating {@code command}/{@code facade} path. */
@Service
public class OrderQueryService {

    private final CoffeeOrderRepository coffeeOrderRepository;

    public OrderQueryService(CoffeeOrderRepository coffeeOrderRepository) {
        this.coffeeOrderRepository = coffeeOrderRepository;
    }

    public List<CoffeeOrder> forCustomer(String customerId) {
        return coffeeOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<CoffeeOrder> activeOrders() {
        return coffeeOrderRepository.findByStatusInOrderByCreatedAtAsc(List.of(OrderStatus.PLACED, OrderStatus.PAID));
    }

    public Optional<CoffeeOrder> byId(Long id) {
        return coffeeOrderRepository.findById(id);
    }
}
