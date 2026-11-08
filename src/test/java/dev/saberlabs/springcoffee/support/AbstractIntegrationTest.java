package dev.saberlabs.springcoffee.support;

import dev.saberlabs.springcoffee.SharedMySqlContainer;
import dev.saberlabs.springcoffee.repository.ChatMessageRepository;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

/** Base for every test that needs the real application wired against the one shared MySQL container. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest extends SharedMySqlContainer {

    @Autowired
    protected CoffeeOrderRepository coffeeOrderRepository;
    @Autowired
    protected ChatMessageRepository chatMessageRepository;

    /** Orders and chat history committed by one test must never leak into the next. */
    @AfterEach
    void cleanUpOrdersAndChat() {
        chatMessageRepository.deleteAllInBatch();
        coffeeOrderRepository.deleteAllInBatch();
    }
}
