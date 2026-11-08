package dev.saberlabs.springcoffee.template;

public class MacchiatoBrewTemplate extends EspressoBrewTemplate {

    @Override
    protected String extraStep() {
        return "Mark with a dash of steamed milk foam";
    }
}
