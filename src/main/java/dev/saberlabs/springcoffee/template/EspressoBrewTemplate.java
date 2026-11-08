package dev.saberlabs.springcoffee.template;

public class EspressoBrewTemplate extends CoffeeBrewTemplate {

    @Override
    protected String brewBase() {
        return "Pull a double espresso shot";
    }
}
