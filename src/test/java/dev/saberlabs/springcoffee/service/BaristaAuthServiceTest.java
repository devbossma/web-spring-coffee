package dev.saberlabs.springcoffee.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BaristaAuthServiceTest {

    private static final String PASSWORD = "change-me";

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final BaristaAuthService baristaAuthService =
            new BaristaAuthService(encoder, encoder.encode(PASSWORD));

    @Test
    @DisplayName("accepts the one known credential pair")
    void acceptsTheOneKnownCredentialPair() {
        assertThat(baristaAuthService.authenticate("barista", PASSWORD)).isTrue();
    }

    @Test
    @DisplayName("rejects real user with a wrong password")
    void rejectsWrongPassword() {
        assertThat(baristaAuthService.authenticate("barista", "wrong")).isFalse();
    }

    @Test
    @DisplayName("rejects unknown username with a correct password")
    void rejectsUnknownUsername() {
        assertThat(baristaAuthService.authenticate("nobody", PASSWORD)).isFalse();
    }

    @Test
    @DisplayName("rejects wrong credential pair for an unknown user")
    void rejectsUnknownUsernameAndWrongPassword() {
        assertThat(baristaAuthService.authenticate("nobody", "wrong")).isFalse();
    }
}