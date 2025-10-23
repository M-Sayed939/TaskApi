package com.example.TaskApi.controller;

import com.example.TaskApi.dto.RegisterRequest;
import com.example.TaskApi.model.Role;
import com.example.TaskApi.model.User;
import com.example.TaskApi.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuthControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public AuthenticationService authenticationService() {
            return mock(AuthenticationService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;


    @Test
    public void registerUser_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        User registeredUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.ROLE_USER)
                .build();

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    public void registerUser_ValidationFails_BlankEmail() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("")
                .password("password123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
}