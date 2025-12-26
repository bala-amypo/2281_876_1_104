package com.example.demo.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.demo.model.UserAccount;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMs;

    // ✅ REQUIRED BY TESTS
    public JwtTokenProvider(String secret, long validityInMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMs = validityInMs;
    }

    // ✅ REQUIRED BY SPRING (default bean)
    public JwtTokenProvider() {
        String secret =
            "sdjhgbwubwwbgwiub8QFQ8qg87G1bfewifbiuwg7iu8wefqhjk";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMs = 10 * 60 * 1000; // 10 minutes
    }

    // ✅ REQUIRED BY TESTS
    public String generateToken(UserAccount user) {
        return generateToken(user.getEmail(), user.getRole());
    }

    // Internal use
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ REQUIRED BY TESTS
    public String getUsername(String token) {
        return getEmailFromToken(token);
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
