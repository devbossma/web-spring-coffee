package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.SharedMySqlContainer;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.model.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoffeeOrderRepositoryTest extends SharedMySqlContainer {

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    @AfterEach
    void cleanUp() {
        coffeeOrderRepository.deleteAllInBatch();
    }

    private CoffeeOrder save(String code, String customerId, OrderStatus status) {
        CoffeeOrder order = new CoffeeOrder(code, customerId, CoffeeType.ESPRESSO,
                EnumSet.noneOf(Extra.class), new BigDecimal("2.50"));
        if (status == OrderStatus.PAID) {
            order.markPaid();
        } else if (status == OrderStatus.FULFILLED) {
            order.markFulfilled();
        }
        return coffeeOrderRepository.save(order);
    }

    @Test
    void findsByOrderCode() {
        save("ORD-1", "CUST-1", OrderStatus.PLACED);

        assertThat(coffeeOrderRepository.findByOrderCode("ORD-1")).isPresent();
        assertThat(coffeeOrderRepository.findByOrderCode("missing")).isEmpty();
    }

    @Test
    void findsByCustomerIdNewestFirst() {
        save("ORD-1", "CUST-1", OrderStatus.PLACED);
        save("ORD-2", "CUST-1", OrderStatus.PAID);
        save("ORD-3", "CUST-2", OrderStatus.PLACED);

        List<CoffeeOrder> mine = coffeeOrderRepository.findByCustomerIdOrderByCreatedAtDesc("CUST-1");

        assertThat(mine).extracting(CoffeeOrder::getOrderCode).containsExactly("ORD-2", "ORD-1");
    }

    @Test
    void findsActiveOrdersByStatusList() {
        save("ORD-1", "CUST-1", OrderStatus.PLACED);
        save("ORD-2", "CUST-1", OrderStatus.PAID);
        save("ORD-3", "CUST-1", OrderStatus.FULFILLED);

        List<CoffeeOrder> active = coffeeOrderRepository.findByStatusInOrderByCreatedAtAsc(
                List.of(OrderStatus.PLACED, OrderStatus.PAID));

        assertThat(active).extracting(CoffeeOrder::getOrderCode).containsExactly("ORD-1", "ORD-2");
    }

    @Test
    void findsByStatus() {
        save("ORD-1", "CUST-1", OrderStatus.FULFILLED);
        save("ORD-2", "CUST-1", OrderStatus.PLACED);

        assertThat(coffeeOrderRepository.findByStatus(OrderStatus.FULFILLED))
                .extracting(CoffeeOrder::getOrderCode).containsExactly("ORD-1");
    }
}
