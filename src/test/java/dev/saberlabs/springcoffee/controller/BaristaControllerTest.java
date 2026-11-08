package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.model.OrderStatus;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class BaristaControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CoffeeShopFacade coffeeShopFacade;

    @Test
    void rootRedirectsToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/barista"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/barista/login"));
    }

    @Test
    void consoleRedirectsToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/barista/console"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/barista/login"));
    }

    @Test
    void wrongCredentialsReshowTheFormWithAnError() throws Exception {
        mockMvc.perform(post("/barista/login").param("username", "barista").param("password", "nope"))
                .andExpect(status().isOk())
                .andExpect(view().name("barista-login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void correctCredentialsLogInAndUnlockTheConsole() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/barista/login").session(session)
                        .param("username", "barista").param("password", "coffee123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/barista/console"));

        mockMvc.perform(get("/barista").session(session))
                .andExpect(redirectedUrl("/barista/console"));

        mockMvc.perform(get("/barista/console").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("barista-console"));
    }

    @Test
    void logoutClearsTheSessionFlag() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/barista/login").session(session)
                .param("username", "barista").param("password", "coffee123"));

        mockMvc.perform(get("/barista/logout").session(session))
                .andExpect(redirectedUrl("/barista/login"));

        mockMvc.perform(get("/barista/console").session(session))
                .andExpect(redirectedUrl("/barista/login"));
    }

    @Test
    void payFulfillAndCancelActionsDriveTheOrderThroughTheFacade() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/barista/login").session(session)
                .param("username", "barista").param("password", "coffee123"));

        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.AMERICANO, EnumSet.noneOf(Extra.class));

        mockMvc.perform(post("/barista/orders/{id}/pay", order.getId()).session(session))
                .andExpect(status().is3xxRedirection());
        assertStatus(order.getId(), OrderStatus.PAID);

        mockMvc.perform(post("/barista/orders/{id}/fulfill", order.getId()).session(session))
                .andExpect(status().is3xxRedirection());
        assertStatus(order.getId(), OrderStatus.FULFILLED);
    }

    @Test
    void cancelActionMarksTheOrderCancelled() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/barista/login").session(session)
                .param("username", "barista").param("password", "coffee123"));

        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.MOCHA, EnumSet.noneOf(Extra.class));

        mockMvc.perform(post("/barista/orders/{id}/cancel", order.getId()).session(session))
                .andExpect(status().is3xxRedirection());
        assertStatus(order.getId(), OrderStatus.CANCELLED);
    }

    private void assertStatus(Long orderId, OrderStatus expected) {
        org.assertj.core.api.Assertions.assertThat(coffeeOrderRepository.findById(orderId).orElseThrow().getStatus())
                .isEqualTo(expected);
    }
}
