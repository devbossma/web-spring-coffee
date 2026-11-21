package dev.saberlabs.springcoffee.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void anUnknownOrderIdRendersTheErrorViewWithItsMessage() {
        Model model = new ExtendedModelMap();

        String view = handler.handleNotFound(new IllegalArgumentException("No such order: 999"), model);

        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("message")).isEqualTo("No such order: 999");
    }

    @Test
    void aConflictingRequestRendersTheErrorViewWithItsMessage() {
        Model model = new ExtendedModelMap();

        String view = handler.handleConflict(new IllegalStateException("Payment declined for order X-1"), model);

        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("message")).isEqualTo("Payment declined for order X-1");
    }
}
