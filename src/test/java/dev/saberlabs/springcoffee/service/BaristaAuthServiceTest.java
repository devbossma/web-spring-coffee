package dev.saberlabs.springcoffee.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaristaAuthServiceTest {

    private final BaristaAuthService baristaAuthService = new BaristaAuthService();

    @Test
    void acceptsTheOneKnownCredentialPair() {
        assertThat(baristaAuthService.authenticate("barista", "coffee123")).isTrue();
    }

    @Test
    void rejectsAnyOtherUsernameOrPassword() {
        assertThat(baristaAuthService.authenticate("barista", "wrong")).isFalse();
        assertThat(baristaAuthService.authenticate("nobody", "coffee123")).isFalse();
    }
}
