package dev.saberlabs.springcoffee.config;

import dev.saberlabs.springcoffee.factory.CoffeeFactory;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Makes sure every {@link CoffeeType} has a matching {@code coffees} row, once, at startup. */
@Component
public class MenuSeeder implements ApplicationRunner {

    private final CoffeeRepository coffeeRepository;
    private final CoffeeFactory coffeeFactory;

    public MenuSeeder(CoffeeRepository coffeeRepository, CoffeeFactory coffeeFactory) {
        this.coffeeRepository = coffeeRepository;
        this.coffeeFactory = coffeeFactory;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (CoffeeType type : CoffeeType.values()) {
            coffeeRepository.findByType(type)
                    .orElseGet(() -> coffeeRepository.save(coffeeFactory.create(type)));
        }
    }
}
