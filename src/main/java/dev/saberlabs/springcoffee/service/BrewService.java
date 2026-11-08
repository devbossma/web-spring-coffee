package dev.saberlabs.springcoffee.service;

import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.template.AmericanoBrewTemplate;
import dev.saberlabs.springcoffee.template.CappuccinoBrewTemplate;
import dev.saberlabs.springcoffee.template.CoffeeBrewTemplate;
import dev.saberlabs.springcoffee.template.EspressoBrewTemplate;
import dev.saberlabs.springcoffee.template.LatteBrewTemplate;
import dev.saberlabs.springcoffee.template.MacchiatoBrewTemplate;
import dev.saberlabs.springcoffee.template.MochaBrewTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/** Picks the right {@link CoffeeBrewTemplate} for a menu item and hands back its prep steps. */
@Service
public class BrewService {

    public List<String> stepsFor(CoffeeType type) {
        CoffeeBrewTemplate template = switch (type) {
            case ESPRESSO -> new EspressoBrewTemplate();
            case AMERICANO -> new AmericanoBrewTemplate();
            case MACCHIATO -> new MacchiatoBrewTemplate();
            case CAPPUCCINO -> new CappuccinoBrewTemplate();
            case LATTE -> new LatteBrewTemplate();
            case MOCHA -> new MochaBrewTemplate();
        };
        return template.steps();
    }
}
