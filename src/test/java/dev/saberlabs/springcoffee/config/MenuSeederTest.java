package dev.saberlabs.springcoffee.config;

import dev.saberlabs.springcoffee.factory.CoffeeFactory;
import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuSeederTest {

    @Mock
    private CoffeeRepository coffeeRepository;
    @Mock
    private CoffeeFactory coffeeFactory;

    @Test
    void skipsTypesThatAlreadyExist() {
        when(coffeeRepository.findByType(CoffeeType.ESPRESSO))
                .thenReturn(Optional.of(new Coffee(CoffeeType.ESPRESSO, "Espresso", new BigDecimal("2.50"))));
        when(coffeeRepository.findByType(org.mockito.ArgumentMatchers.argThat(type -> type != CoffeeType.ESPRESSO)))
                .thenReturn(Optional.empty());
        when(coffeeFactory.create(any())).thenAnswer(invocation -> {
            CoffeeType type = invocation.getArgument(0);
            return new Coffee(type, type.displayName(), type.basePrice());
        });

        new MenuSeeder(coffeeRepository, coffeeFactory).run(null);

        verify(coffeeFactory, never()).create(CoffeeType.ESPRESSO);
        verify(coffeeFactory, times(CoffeeType.values().length - 1)).create(any());
    }
}
