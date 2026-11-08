package dev.saberlabs.springcoffee.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/** Deliberately minimal: one shared in-memory credential pair, no accounts, no password hashing. */
@Service
public class BaristaAuthService {

    private static final Map<String, String> CREDENTIALS = Map.of("barista", "coffee123");

    public boolean authenticate(String username, String password) {
        return CREDENTIALS.getOrDefault(username, "").equals(password);
    }
}
