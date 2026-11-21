package dev.saberlabs.springcoffee.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Catches the order-lifecycle commands' own exceptions in one place, across every controller,
 * so a bad order id or a declined payment shows a plain error page instead of a raw stack trace.
 * The commands never throw anything more specific than these two built-in exception types, so
 * there is no custom exception hierarchy to maintain here.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Commands throw this for an order id that does not exist: a 404, not a server error. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(IllegalArgumentException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    /** Commands throw this for a request that is well-formed but cannot be carried out right
     *  now (an unknown coffee type, a declined payment): a 409, not a server error. */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(IllegalStateException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
