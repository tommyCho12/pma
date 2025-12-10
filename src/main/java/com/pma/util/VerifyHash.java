package com.pma.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerifyHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hash from data.sql for 'multi' user
        String storedHash = "$2a$10$Oi0UFaP1bNZFX1QLfQGEmusWs/XxCdBLkZ/OhTgjeALNEs8AmUzhW";

        // Test various possible passwords
        System.out.println("Testing 'multi' account hash...");
        System.out.println("Stored hash: " + storedHash);
        System.out.println();

        // Test password: "multi"
        boolean multiMatches = encoder.matches("multi", storedHash);
        System.out.println("Password 'multi' matches: " + multiMatches);

        // Generate a fresh hash for "multi" to compare
        String freshHash = encoder.encode("multi");
        System.out.println("\nFresh hash for 'multi': " + freshHash);
        System.out.println("Fresh hash matches 'multi': " + encoder.matches("multi", freshHash));

        // Detailed hash analysis
        System.out.println("\n=== Hash Analysis ===");
        System.out.println("Stored hash algorithm: " + storedHash.substring(0, 4));
        System.out.println("Expected algorithm: $2a$ (BCrypt)");
        System.out.println("Hash strength: " + storedHash.substring(4, 6));
    }
}
