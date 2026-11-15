package dev.saberlabs.springcoffee.support;

import dev.saberlabs.springcoffee.SharedMySqlContainer;
import dev.saberlabs.springcoffee.repository.ChatMessageRepository;
import dev.saberlabs.springcoffee.repository.CoffeeOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Base for every test that needs the real application wired against the one shared MySQL container. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest extends SharedMySqlContainer {

    /** Plain password the barista tests log in with; its hash is injected below. */
    protected static final String BARISTA_TEST_PASSWORD = "test-password";

    @DynamicPropertySource
    static void baristaCredentials(DynamicPropertyRegistry registry) {
        registry.add("barista.password-hash",
                () -> new BCryptPasswordEncoder().encode(BARISTA_TEST_PASSWORD));
    }

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
