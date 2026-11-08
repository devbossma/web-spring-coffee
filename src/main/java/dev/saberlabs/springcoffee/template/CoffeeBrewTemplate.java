package dev.saberlabs.springcoffee.template;

import java.util.ArrayList;
import java.util.List;

/**
 * The skeleton every drink follows: boil water, brew the base, pour, then whatever that specific
 * drink adds on top. Subclasses fill in {@link #brewBase()} and may override the
 * {@link #extraStep()} hook; they cannot change the order or skip a step.
 */
public abstract class CoffeeBrewTemplate {

    public final List<String> steps() {
        List<String> steps = new ArrayList<>();
        steps.add(boilWater());
        steps.add(brewBase());
        steps.add(pourInCup());
        String extra = extraStep();
        if (extra != null && !extra.isBlank()) {
            steps.add(extra);
        }
        return steps;
    }

    private String boilWater() {
        return "Boil water to 93C";
    }

    private String pourInCup() {
        return "Pour into a cup";
    }

    protected abstract String brewBase();

    /** Hook: most drinks need nothing extra, so the default is "no extra step". */
    protected String extraStep() {
        return null;
    }
}
