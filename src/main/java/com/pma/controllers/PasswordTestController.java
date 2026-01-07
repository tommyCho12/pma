package com.pma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PasswordTestController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/api/test/hash-password")
    public Map<String, String> hashPassword(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        Map<String, String> result = new HashMap<>();
        result.put("plaintext", password);
        result.put("hash", hash);
        return result;
    }

    @GetMapping("/api/test/verify-password")
    public Map<String, Object> verifyPassword(
            @RequestParam String password,
            @RequestParam String hash) {
        boolean matches = passwordEncoder.matches(password, hash);
        Map<String, Object> result = new HashMap<>();
        result.put("plaintext", password);
        result.put("hash", hash);
        result.put("matches", matches);
        return result;
    }
}
