package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.config.BaristaAuthInterceptor;
import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.observer.OrderMetricsObserver;
import dev.saberlabs.springcoffee.service.BaristaAuthService;
import dev.saberlabs.springcoffee.service.BrewService;
import dev.saberlabs.springcoffee.service.ChatService;
import dev.saberlabs.springcoffee.service.OrderQueryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BaristaController {

    private final BaristaAuthService baristaAuthService;
    private final OrderQueryService orderQueryService;
    private final ChatService chatService;
    private final BrewService brewService;
    private final CoffeeShopFacade coffeeShopFacade;
    private final OrderMetricsObserver orderMetricsObserver;

    public BaristaController(BaristaAuthService baristaAuthService, OrderQueryService orderQueryService,
                              ChatService chatService, BrewService brewService, CoffeeShopFacade coffeeShopFacade,
                              OrderMetricsObserver orderMetricsObserver) {
        this.baristaAuthService = baristaAuthService;
        this.orderQueryService = orderQueryService;
        this.chatService = chatService;
        this.brewService = brewService;
        this.coffeeShopFacade = coffeeShopFacade;
        this.orderMetricsObserver = orderMetricsObserver;
    }

    @GetMapping("/barista")
    public String root(HttpSession session) {
        boolean loggedIn = session.getAttribute(BaristaAuthInterceptor.SESSION_ATTRIBUTE) != null;
        return loggedIn ? "redirect:/barista/console" : "redirect:/barista/login";
    }

    @GetMapping("/barista/login")
    public String loginForm() {
        return "barista-login";
    }

    @PostMapping("/barista/login")
    public String login(@RequestParam String username, @RequestParam String password,
                         Model model, HttpSession session) {
        if (baristaAuthService.authenticate(username, password)) {
            session.setAttribute(BaristaAuthInterceptor.SESSION_ATTRIBUTE, username);
            return "redirect:/barista/console";
        }
        model.addAttribute("error", "Wrong username or password");
        return "barista-login";
    }

    @GetMapping("/barista/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(BaristaAuthInterceptor.SESSION_ATTRIBUTE);
        return "redirect:/barista/login";
    }

    @GetMapping("/barista/console")
    public String console(@RequestParam(required = false) Long orderId, Model model) {
        model.addAttribute("orders", orderQueryService.activeOrders());
        model.addAttribute("fulfilledCount", orderMetricsObserver.fulfilledCount());
        if (orderId != null) {
            orderQueryService.byId(orderId).ifPresent(order -> {
                model.addAttribute("selectedOrder", order);
                model.addAttribute("history", chatService.historyFor(orderId));
                model.addAttribute("brewSteps", brewService.stepsFor(order.getCoffeeType()));
            });
        }
        return "barista-console";
    }

    @PostMapping("/barista/orders/{id}/pay")
    public String pay(@PathVariable Long id) {
        coffeeShopFacade.payOrder(id);
        return "redirect:/barista/console?orderId=" + id;
    }

    @PostMapping("/barista/orders/{id}/fulfill")
    public String fulfill(@PathVariable Long id) {
        coffeeShopFacade.fulfillOrder(id);
        return "redirect:/barista/console";
    }

    @PostMapping("/barista/orders/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        coffeeShopFacade.cancelOrder(id);
        return "redirect:/barista/console";
    }
}
