package dev.saberlabs.springcoffee.template;

public class MochaBrewTemplate extends EspressoBrewTemplate {

    @Override
    protected String extraStep() {
        return "Stir in chocolate syrup, then top with steamed milk";
    }
}
