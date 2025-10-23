package com.example.TaskApi.services;

import com.example.TaskApi.dto.LoginRequest;
import com.example.TaskApi.dto.RegisterRequest;
import com.example.TaskApi.exception.EmailAlreadyExistsException;
import com.example.TaskApi.model.Role;
import com.example.TaskApi.model.User;
import com.example.TaskApi.repository.UserRepository;
import com.example.TaskApi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Error: Email is already in use!");
        }
        User user = User.builder().
                name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        return jwtTokenProvider.generateToken(authentication);
    }
}
