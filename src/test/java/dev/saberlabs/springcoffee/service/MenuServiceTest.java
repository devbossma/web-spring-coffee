package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private CoffeeRepository coffeeRepository;

    @Test
    void menuIsSortedCheapestFirst() {
        Coffee mocha = new Coffee(CoffeeType.MOCHA, "Mocha", new BigDecimal("4.00"));
        Coffee espresso = new Coffee(CoffeeType.ESPRESSO, "Espresso", new BigDecimal("2.50"));
        when(coffeeRepository.findAll()).thenReturn(List.of(mocha, espresso));

        List<Coffee> menu = new MenuService(coffeeRepository).menu();

        assertThat(menu).extracting(Coffee::getType).containsExactly(CoffeeType.ESPRESSO, CoffeeType.MOCHA);
    }
}
