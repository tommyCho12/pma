package com.pma.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test to verify the 'multi' account password hash
 */
public class HashVerificationTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // The hash stored in data.sql for 'multi' user
        String storedHash = "$2a$10$Oi0UFaP1bNZFX1QLfQGEmusWs/XxCdBLkZ/OhTgjeALNEs8AmUzhW";

        System.out.println("=== HASH VERIFICATION TEST ===");
        System.out.println("Account: multi@example.com");
        System.out.println("Stored Hash: " + storedHash);
        System.out.println();

        // Test if password "multi" matches the stored hash
        boolean matches = encoder.matches("multi", storedHash);

        System.out.println("Testing password: 'multi'");
        System.out.println("Result: " + (matches ? "✓ VALID - Hash is CORRECT" : "✗ INVALID - Hash is WRONG"));

        if (!matches) {
            System.out.println("\n!!! PROBLEM FOUND !!!");
            System.out.println("The stored hash does NOT match the password 'multi'");
            System.out.println("\nGenerating correct hash...");
            String correctHash = encoder.encode("multi");
            System.out.println("Correct hash for 'multi': " + correctHash);
            System.out.println("\nAction Required:");
            System.out.println("Replace the hash in data.sql (line 49) with the correct hash above");
        } else {
            System.out.println("\n✓ Hash is correct!");
            System.out.println("The authentication issue might be caused by:");
            System.out.println("1. Database not reloaded (restart app to reload data.sql)");
            System.out.println("2. Wrong email used (should use: multi@example.com)");
            System.out.println("3. Wrong password entered");
        }

        // Also verify other user hashes for comparison
        System.out.println("\n=== VERIFYING OTHER ACCOUNTS ===");

        // Admin hash
        String adminHash = "$2a$12$7Ucy9BgqTH8NoNrkvDr.dOTUfTph4Jn6Jw3FyNhjgA6pcfvJ5HjiK";
        boolean adminMatches = encoder.matches("admin", adminHash);
        System.out.println("admin@admin.adm (password: admin): " + (adminMatches ? "✓ VALID" : "✗ INVALID"));

        // Reviewer hash
        String reviewerHash = "$2a$10$s/hR5qMC8.qPG5brReJed.doBh9c1j2KU8tZP9QZ1VyCevRfjSYf6";
        boolean reviewerMatches = encoder.matches("reviewer", reviewerHash);
        System.out.println("reviewer@example.com (password: reviewer): " + (reviewerMatches ? "✓ VALID" : "✗ INVALID"));

        // User hash
        String userHash = "$2a$10$9Pdb7i4fdb1YHUByM987nOL2uqFnLV6HIMoXFhab.TB7HKStnc/c2";
        boolean userMatches = encoder.matches("user", userHash);
        System.out.println("user@example.com (password: user): " + (userMatches ? "✓ VALID" : "✗ INVALID"));
    }
}
