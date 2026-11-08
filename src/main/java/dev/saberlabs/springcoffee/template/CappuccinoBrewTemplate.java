package dev.saberlabs.springcoffee.template;

public class CappuccinoBrewTemplate extends EspressoBrewTemplate {

    @Override
    protected String extraStep() {
        return "Add equal parts steamed milk and milk foam";
    }
}
