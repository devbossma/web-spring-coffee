package dev.saberlabs.springcoffee.singleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classic GoF singleton, deliberately not a Spring bean: exactly one instance exists for the
 * whole JVM, reached through {@link #getInstance()}, not through dependency injection. It hands
 * out human-readable, date-plus-daily-counter codes for both first-time customer ids and order
 * codes, each counter resetting at midnight.
 *
 * <p>Two independent counters share one {@code synchronized} method each because handing out the
 * next code is a check-the-date-then-increment-the-counter operation on two fields together,
 * which a bare {@code AtomicInteger} cannot express by itself.
 */
public final class DailySequenceGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DailySequenceGenerator INSTANCE = new DailySequenceGenerator();

    private LocalDate customerLastDate;
    private int customerCounter;
    private LocalDate orderLastDate;
    private int orderCounter;

    private DailySequenceGenerator() {
        // single instance, see getInstance()
    }

    public static DailySequenceGenerator getInstance() {
        return INSTANCE;
    }

    public synchronized String nextCustomerId() {
        LocalDate today = LocalDate.now();
        if (!today.equals(customerLastDate)) {
            customerLastDate = today;
            customerCounter = 0;
        }
        customerCounter++;
        return "CUST-" + today.format(DATE_FORMAT) + "-" + String.format("%03d", customerCounter);
    }

    public synchronized String nextOrderCode() {
        LocalDate today = LocalDate.now();
        if (!today.equals(orderLastDate)) {
            orderLastDate = today;
            orderCounter = 0;
        }
        orderCounter++;
        return "ORD-" + today.format(DATE_FORMAT) + "-" + String.format("%03d", orderCounter);
    }
}
