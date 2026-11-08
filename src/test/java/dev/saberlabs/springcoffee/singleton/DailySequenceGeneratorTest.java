package dev.saberlabs.springcoffee.singleton;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DailySequenceGeneratorTest {

    @Test
    void getInstanceAlwaysReturnsTheSameObject() {
        assertThat(DailySequenceGenerator.getInstance()).isSameAs(DailySequenceGenerator.getInstance());
    }

    @Test
    void consecutiveOrderCodesAreUniqueAndIncreasing() {
        DailySequenceGenerator generator = DailySequenceGenerator.getInstance();

        String first = generator.nextOrderCode();
        String second = generator.nextOrderCode();

        assertThat(first).startsWith("ORD-").isNotEqualTo(second);
    }

    @Test
    void consecutiveCustomerIdsAreUniqueAndPrefixed() {
        DailySequenceGenerator generator = DailySequenceGenerator.getInstance();

        String first = generator.nextCustomerId();
        String second = generator.nextCustomerId();

        assertThat(first).startsWith("CUST-").isNotEqualTo(second);
    }
}
