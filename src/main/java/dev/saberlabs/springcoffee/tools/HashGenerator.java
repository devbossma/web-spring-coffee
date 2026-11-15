package dev.saberlabs.springcoffee.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {

    public static void main(String[] args) {
        String raw = args.length > 0 ? args[0] : "change-me";
        System.out.println(new BCryptPasswordEncoder().encode(raw));
    }
}