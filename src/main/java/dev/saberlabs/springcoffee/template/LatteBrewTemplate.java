package dev.saberlabs.springcoffee.template;

public class LatteBrewTemplate extends EspressoBrewTemplate {

    @Override
    protected String extraStep() {
        return "Add steamed milk with a thin layer of foam";
    }
}
