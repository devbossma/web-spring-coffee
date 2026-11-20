package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * The order form's shape as Spring MVC binds it: one radio button named {@code coffeeType} and
 * zero or more checkboxes named {@code extras}, bound onto this record's constructor by
 * {@code @ModelAttribute} rather than read field-by-field with {@code @RequestParam}.
 *
 * <p>{@code coffeeType} is the one field that genuinely cannot be absent: the HTML form marks the
 * radio group {@code required}, but that is only ever a client-side nudge, and a request built by
 * hand (or a browser quirk) can still arrive without it, which would otherwise NPE several calls
 * deeper in {@code PlaceOrderCommand}.
 */
public record OrderForm(@NotNull(message = "Pick a coffee before placing your order") CoffeeType coffeeType,
                         List<Extra> extras) {
}
