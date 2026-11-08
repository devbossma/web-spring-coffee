package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {
    List<CoffeeOrder> findByStatus(OrderStatus status);

    List<CoffeeOrder> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    Optional<CoffeeOrder> findByOrderCode(String orderCode);

    List<CoffeeOrder> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);
}
