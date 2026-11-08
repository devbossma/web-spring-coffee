package dev.saberlabs.springcoffee;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

/**
 * One MySQL container for the entire test JVM, shared by every Testcontainers-backed test class,
 * started manually in a static initializer (the "singleton container" pattern from Testcontainers'
 * own docs) rather than via {@code @Testcontainers}/{@code @Container}, whose lifecycle management
 * only reliably covers a single test class. Ryuk reaps it when the JVM exits.
 */
public abstract class SharedMySqlContainer {

    @ServiceConnection
    protected static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    static {
        MYSQL.start();
    }
}
