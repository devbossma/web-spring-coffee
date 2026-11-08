package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.repository.CoffeeRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MenuService {

    private final CoffeeRepository coffeeRepository;

    public MenuService(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    public List<Coffee> menu() {
        return coffeeRepository.findAll().stream()
                .sorted(Comparator.comparing(Coffee::getBasePrice))
                .toList();
    }
}
