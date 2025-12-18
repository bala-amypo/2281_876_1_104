package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;

import io.swagger.v3.oas.annotations.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Endpoints")
public class AuthController {

    @Autowired
    private UserAccountRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<UserAccount> register(@RequestBody UserAccount user) {
        UserAccount saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // JWT logic can be added later
        return ResponseEntity.ok("JWT_TOKEN");
    }
}
