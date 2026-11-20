package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;

import java.util.List;

/**
 * The order form's shape as Spring MVC binds it: one radio button named {@code coffeeType} and
 * zero or more checkboxes named {@code extras}, bound onto this record's constructor by
 * {@code @ModelAttribute} rather than read field-by-field with {@code @RequestParam}.
 */
public record OrderForm(CoffeeType coffeeType, List<Extra> extras) {
}
