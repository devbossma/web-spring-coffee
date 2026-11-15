package dev.saberlabs.springcoffee.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BaristaAuthService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> credentials;   // username -> bcrypt hash
    private final String dummyHash;                  // used to equalize timing for unknown users

    public BaristaAuthService(PasswordEncoder passwordEncoder,
                              @Value("${barista.password-hash}") String baristaHash) {
        this.passwordEncoder = passwordEncoder;
        this.credentials = Map.of("barista", baristaHash);
        this.dummyHash = passwordEncoder.encode("not-a-real-password");
    }

    public boolean authenticate(String username, String password) {
        String hash = credentials.get(username);
        if (hash == null) {
            passwordEncoder.matches(password, dummyHash); // burn the same time, avoid username enumeration
            return false;
        }
        return passwordEncoder.matches(password, hash);
    }
}