package dev.saberlabs.springcoffee.tools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HashGeneratorTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void captureStdout() {
        originalOut = System.out;
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    private String runWith(String... args) {
        captured.reset();
        HashGenerator.main(args);
        return captured.toString(StandardCharsets.UTF_8).trim();
    }

    @Test
    void printsAValidBcryptHash() {
        String output = runWith("my-password");

        assertThat(output).matches("\\$2[aby]\\$10\\$[./A-Za-z0-9]{53}");
        assertThat(output).hasSize(60);
    }

    @Test
    void hashMatchesThePasswordGivenAsArgument() {
        String output = runWith("my-password");

        assertThat(encoder.matches("my-password", output)).isTrue();
        assertThat(encoder.matches("other-password", output)).isFalse();
    }

    @Test
    void usesTheDefaultPasswordWhenNoArgumentIsGiven() {
        String output = runWith();

        assertThat(encoder.matches("change-me", output)).isTrue();
    }

    @Test
    void producesADifferentHashEachRunBecauseOfTheRandomSalt() {
        String first = runWith("my-password");
        String second = runWith("my-password");

        assertThat(first).isNotEqualTo(second);
        assertThat(encoder.matches("my-password", first)).isTrue();
        assertThat(encoder.matches("my-password", second)).isTrue();
    }

    @Test
    void printsOnlyTheHashOnASingleLine() {
        runWith("my-password");

        String raw = captured.toString(StandardCharsets.UTF_8);
        assertThat(raw.trim().lines().count()).isEqualTo(1);
    }

    @Test
    void ignoresExtraArguments() {
        String output = runWith("first", "second");

        assertThat(encoder.matches("first", output)).isTrue();
        assertThat(encoder.matches("second", output)).isFalse();
    }
}