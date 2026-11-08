package dev.saberlabs.springcoffee.command;

import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import dev.saberlabs.springcoffee.singleton.DailySequenceGenerator;

/** "Order it again": clones a past order via {@link CoffeeOrder#copy()} into a fresh pending one. */
public class ReorderCommand implements OrderCommand {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final Long originalOrderId;

    public ReorderCommand(CoffeeOrderRepository coffeeOrderRepository, Long originalOrderId) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.originalOrderId = originalOrderId;
    }

    @Override
    public CoffeeOrder execute() {
        CoffeeOrder original = coffeeOrderRepository.findById(originalOrderId)
                .orElseThrow(() -> new IllegalArgumentException("No such order: " + originalOrderId));

        CoffeeOrder clone = original.copy();
        clone.assignOrderCode(DailySequenceGenerator.getInstance().nextOrderCode());
        return coffeeOrderRepository.save(clone);
    }
}
