package dev.saberlabs.springcoffee.controller;

import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class CoffeeShopControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeShowsTheSixItemMenu() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("coffees"))
                .andExpect(model().attributeExists("extras"));
    }

    @Test
    void placingThenViewingAnOrderWorksEndToEnd() throws Exception {
        MvcResult homeResult = mockMvc.perform(get("/")).andReturn();
        HttpSession session = homeResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        MvcResult orderResult = mockMvc.perform(post("/order")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .param("coffeeType", "ESPRESSO")
                        .param("extras", "MILK"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = orderResult.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).startsWith("/chat/");

        mockMvc.perform(get("/orders").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
