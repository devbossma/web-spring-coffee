package dev.saberlabs.springcoffee.template;

public class AmericanoBrewTemplate extends CoffeeBrewTemplate {

    @Override
    protected String brewBase() {
        return "Pull a single espresso shot";
    }

    @Override
    protected String extraStep() {
        return "Add hot water to lengthen the shot";
    }
}
