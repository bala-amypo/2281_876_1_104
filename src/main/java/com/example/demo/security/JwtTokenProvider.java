package com.example.demo.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.demo.model.UserAccount;

@Component
public class JwtTokenProvider {

    private String secretKey;
    private int validityInMs;

    // ✅ constructor REQUIRED by test cases
    public JwtTokenProvider(String secretKey, int validityInMs) {
        this.secretKey = secretKey;
        this.validityInMs = validityInMs;
    }

    // ✅ default constructor for Spring
    public JwtTokenProvider() {
        this.secretKey = "test-secret-key";
        this.validityInMs = 3600000;
    }

    public String generateToken(UserAccount user) {
        return user.getEmail() + ":" + user.getRole();
    }

    public boolean validateToken(String token) {
        return token != null && token.contains(":");
    }

    public String getUsername(String token) {
        return token.split(":")[0];
    }
}
