package dev.saberlabs.springcoffee.observer;

import dev.saberlabs.springcoffee.model.CoffeeOrder;

public interface OrderObserver {
    void onStatusChanged(CoffeeOrder order);
}
