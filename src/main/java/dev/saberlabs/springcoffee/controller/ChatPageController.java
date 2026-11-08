package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.config.CustomerIdentificationInterceptor;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.service.ChatService;
import dev.saberlabs.springcoffee.service.OrderQueryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatPageController {

    private final OrderQueryService orderQueryService;
    private final ChatService chatService;

    public ChatPageController(OrderQueryService orderQueryService, ChatService chatService) {
        this.orderQueryService = orderQueryService;
        this.chatService = chatService;
    }

    @GetMapping("/chat/{orderId}")
    public String chatPage(@PathVariable Long orderId, Model model, HttpSession session) {
        var order = orderQueryService.byId(orderId);
        if (order.isEmpty()) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order.get());
        model.addAttribute("history", chatService.historyFor(orderId));
        model.addAttribute("sender", ChatSender.CUSTOMER);
        model.addAttribute("customerId", session.getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE));
        return "chat";
    }
}
