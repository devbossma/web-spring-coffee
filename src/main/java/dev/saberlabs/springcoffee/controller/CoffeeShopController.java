package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.config.CustomerIdentificationInterceptor;
import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.service.MenuService;
import dev.saberlabs.springcoffee.service.OrderQueryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.EnumSet;

@Controller
public class CoffeeShopController {

    private final MenuService menuService;
    private final OrderQueryService orderQueryService;
    private final CoffeeShopFacade coffeeShopFacade;

    public CoffeeShopController(MenuService menuService, OrderQueryService orderQueryService,
                                 CoffeeShopFacade coffeeShopFacade) {
        this.menuService = menuService;
        this.orderQueryService = orderQueryService;
        this.coffeeShopFacade = coffeeShopFacade;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("coffees", menuService.menu());
        model.addAttribute("extras", Extra.values());
        return "index";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam CoffeeType coffeeType,
                              @RequestParam(required = false) List<Extra> extras,
                              HttpSession session) {
        String customerId = (String) session.getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE);
        Set<Extra> extraSet = extras == null ? EnumSet.noneOf(Extra.class) : EnumSet.copyOf(extras);
        CoffeeOrder order = coffeeShopFacade.placeOrder(customerId, coffeeType, extraSet);
        return "redirect:/chat/" + order.getId();
    }

    @GetMapping("/orders")
    public String viewOrders(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE);
        model.addAttribute("customerId", customerId);
        model.addAttribute("orders", orderQueryService.forCustomer(customerId));
        return "orders";
    }

    @PostMapping("/orders/{id}/reorder")
    public String reorder(@PathVariable Long id) {
        CoffeeOrder order = coffeeShopFacade.reorder(id);
        return "redirect:/chat/" + order.getId();
    }
}
