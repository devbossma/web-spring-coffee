package dev.saberlabs.springcoffee.model;

import dev.saberlabs.springcoffee.prototype.Prototype;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

/**
 * A single customer order. Named {@code CoffeeOrder}, not {@code Order}, because {@code ORDER} is
 * a reserved word in SQL and this avoids quoting it everywhere.
 */
@Entity
@Table(name = "coffee_orders")
public class CoffeeOrder implements Prototype<CoffeeOrder> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoffeeType coffeeType;

    // EAGER, deliberately: at most 4 enum values, and commands like ReorderCommand read this
    // right after loading the order, often outside any transaction that would keep a LAZY
    // collection's session open.
    @ElementCollection(targetClass = Extra.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "order_extras", joinColumns = @JoinColumn(name = "order_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "extra")
    private Set<Extra> extras = EnumSet.noneOf(Extra.class);

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected CoffeeOrder() {
        // JPA
    }

    public CoffeeOrder(String orderCode, String customerId, CoffeeType coffeeType, Set<Extra> extras,
                        BigDecimal price) {
        this.orderCode = orderCode;
        this.customerId = customerId;
        this.coffeeType = coffeeType;
        this.extras = EnumSet.copyOf(extras.isEmpty() ? EnumSet.noneOf(Extra.class) : extras);
        this.price = price;
        this.status = OrderStatus.PLACED;
        this.createdAt = Instant.now();
    }

    /**
     * An independent, still-unsaved copy for the "order it again" flow: same coffee, same extras,
     * same price, same customer, but its own fresh code, id, status, and timestamp. The caller is
     * responsible for giving it a fresh {@code orderCode} before persisting, the same way a brand
     * new order would get one.
     */
    @Override
    public CoffeeOrder copy() {
        CoffeeOrder clone = new CoffeeOrder(orderCode, customerId, coffeeType, extras, price);
        clone.orderCode = null;
        return clone;
    }

    public void assignOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markFulfilled() {
        this.status = OrderStatus.FULFILLED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CoffeeType getCoffeeType() {
        return coffeeType;
    }

    public Set<Extra> getExtras() {
        return extras;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
