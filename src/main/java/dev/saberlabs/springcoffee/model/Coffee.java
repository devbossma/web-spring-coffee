package dev.saberlabs.springcoffee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/** One row per {@link CoffeeType}, seeded once at startup. The menu customers actually order from. */
@Entity
@Table(name = "coffees", uniqueConstraints = @UniqueConstraint(columnNames = "type"))
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoffeeType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal basePrice;

    protected Coffee() {
        // JPA
    }

    public Coffee(CoffeeType type, String name, BigDecimal basePrice) {
        this.type = type;
        this.name = name;
        this.basePrice = basePrice;
    }

    public Long getId() {
        return id;
    }

    public CoffeeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
