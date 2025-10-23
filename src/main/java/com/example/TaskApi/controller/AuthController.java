package com.example.TaskApi.controller;

import com.example.TaskApi.dto.ApiResponse;
import com.example.TaskApi.dto.JwtAuthenticationResponse;
import com.example.TaskApi.dto.LoginRequest;
import com.example.TaskApi.dto.RegisterRequest;
import com.example.TaskApi.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponse("User registered successfully", true));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticationService.login(loginRequest);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok("User logged out successfully.");
    }
}
