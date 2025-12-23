package com.example.demo.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.demo.model.UserAccount;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY =
            "test-secret-key-test-secret-key-1234"; // 32+ chars

    private static final long VALIDITY_MS = 60 * 60 * 1000;

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ✅ generate token
    public String generateToken(UserAccount user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // MUST be email
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    // ✅ validate token (STRICT)
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false; // fixes empty-string test
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // fixes corrupted token test
        }
    }

    // ✅ get username (email)
    public String getUsername(String token) {
        if (!validateToken(token)) {
            return null;
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
