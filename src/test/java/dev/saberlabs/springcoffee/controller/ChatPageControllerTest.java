package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ChatPageControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CoffeeShopFacade coffeeShopFacade;

    @Test
    void showsTheChatPageForAnExistingOrder() throws Exception {
        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.LATTE, EnumSet.noneOf(Extra.class));

        mockMvc.perform(get("/chat/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attribute("order", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.equalTo(order.getId()))))
                .andExpect(model().attributeExists("history"));
    }

    @Test
    void redirectsToOrdersForAnUnknownOrder() throws Exception {
        mockMvc.perform(get("/chat/{id}", 999_999L))
                .andExpect(redirectedUrl("/orders"));
    }
}
