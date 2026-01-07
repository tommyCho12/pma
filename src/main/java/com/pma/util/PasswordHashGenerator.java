package com.pma.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for seeding the database.
 * Run this to generate hashes for test user passwords.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate hash for "reviewer" password
        String reviewerHash = encoder.encode("reviewer");
        System.out.println("Hash for 'reviewer': " + reviewerHash);

        // Generate hash for "user" password
        String userHash = encoder.encode("user");
        System.out.println("Hash for 'user': " + userHash);

        // Generate hash for "multi" password
        String multiHash = encoder.encode("multi");
        System.out.println("Hash for 'multi': " + multiHash);

        // Verify admin hash (should already work)
        String adminHash = "$2a$12$7Ucy9BgqTH8NoNrkvDr.dOTUfTph4Jn6Jw3FyNhjgA6pcfvJ5HjiK";
        boolean adminMatches = encoder.matches("admin", adminHash);
        System.out.println("\nAdmin hash verification: " + (adminMatches ? "VALID" : "INVALID"));

        // Verify multi hash (should already work)
        boolean multiMatches = encoder.matches("multi", multiHash);
        System.out.println("\nMulti hash verification: " + (multiMatches ? "VALID" : "INVALID"));
    }
}
