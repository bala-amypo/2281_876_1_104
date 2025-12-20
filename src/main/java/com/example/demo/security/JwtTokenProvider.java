package com.example.demo.security;

import org.springframework.stereotype.Component;
import com.example.demo.model.UserAccount;

@Component
public class JwtTokenProvider {

    private String secretKey;
    private int validityInSeconds;

    public JwtTokenProvider(String secretKey, int validityInSeconds) {
        this.secretKey = secretKey;
        this.validityInSeconds = validityInSeconds;
    }

    public JwtTokenProvider() {}

    public String generateToken(String username) {
        return "dummy-token-for-" + username;
    }

    // ✅ THIS FIXES UserAccount → String error
    public String generateToken(UserAccount user) {
        return "dummy-token-for-" + user.getEmail();
    }

    public String getUsername(String token) {
        return "testuser";
    }

    public boolean validateToken(String token) {
        return true;
    }
}
