package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.config.CustomerIdentificationInterceptor;
import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.service.MenuService;
import dev.saberlabs.springcoffee.service.OrderQueryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        addMenuAttributes(model);
        return "index";
    }

    @PostMapping("/order")
    public String placeOrder(@Valid @ModelAttribute OrderForm orderForm, BindingResult bindingResult,
                              HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            addMenuAttributes(model);
            return "index";
        }
        String customerId = (String) session.getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE);
        List<Extra> extras = orderForm.extras();
        Set<Extra> extraSet = extras == null ? EnumSet.noneOf(Extra.class) : EnumSet.copyOf(extras);
        CoffeeOrder order = coffeeShopFacade.placeOrder(customerId, orderForm.coffeeType(), extraSet);
        return "redirect:/chat/" + order.getId();
    }

    private void addMenuAttributes(Model model) {
        model.addAttribute("coffees", menuService.menu());
        model.addAttribute("extras", Extra.values());
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
