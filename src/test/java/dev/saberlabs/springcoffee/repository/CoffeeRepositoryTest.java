package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.SharedMySqlContainer;
import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoffeeRepositoryTest extends SharedMySqlContainer {

    @Autowired
    private CoffeeRepository coffeeRepository;

    /**
     * {@code @DataJpaTest} rolls this method's transaction back when it ends, so touching rows
     * here is safe even though a {@code MOCHA} row may already exist, committed for real by an
     * unrelated {@code @SpringBootTest} class's {@code MenuSeeder} run against this same shared
     * container.
     */
    @Test
    void findsByType() {
        coffeeRepository.findByType(CoffeeType.LATTE).ifPresent(coffeeRepository::delete);
        coffeeRepository.findByType(CoffeeType.MOCHA)
                .orElseGet(() -> coffeeRepository.save(new Coffee(CoffeeType.MOCHA, "Mocha", new BigDecimal("4.00"))));

        assertThat(coffeeRepository.findByType(CoffeeType.MOCHA)).isPresent();
        assertThat(coffeeRepository.findByType(CoffeeType.LATTE)).isEmpty();
    }
}
