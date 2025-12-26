package com.example.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import com.example.demo.security.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserAccountRepository userRepo,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        System.out.println("🔥 REGISTER CONTROLLER HIT 🔥");

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
        throw new BadRequestException("Email already exists");
        }

        UserAccount user = new UserAccount();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        // default role as per question
        user.setRole(
                request.getRole() != null ? request.getRole() : "IT_OPERATOR"
        );

        user.setActive(true);

        userRepo.save(user);

        return "User registered successfully";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        UserAccount user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("Invalid credentials"));

        if (!user.getActive()) {
            throw new BadRequestException("User inactive");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
