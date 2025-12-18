package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Endpoints")
public class AuthController {

    private final UserAccountRepository userRepo;

    // ✅ Constructor Injection (preferred)
    public AuthController(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<UserAccount> register(@RequestBody UserAccount user) {
        UserAccount saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Login user and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // JWT logic will be added later
        return ResponseEntity.ok("JWT_TOKEN");
    }
}
