package dev.saberlabs.springcoffee.repository;

import dev.saberlabs.springcoffee.model.Coffee;
import dev.saberlabs.springcoffee.model.CoffeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Optional<Coffee> findByType(CoffeeType type);
}
