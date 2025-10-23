package com.example.TaskApi.controller;

import com.example.TaskApi.dto.TaskRequest;
import com.example.TaskApi.model.Role;
import com.example.TaskApi.model.Task;
import com.example.TaskApi.model.TaskStatus;
import com.example.TaskApi.model.User;
import com.example.TaskApi.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.TaskApi.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public TaskService taskService() {
            return mock(TaskService.class);
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return mock(JwtTokenProvider.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    private User testUser;
    private Task testTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.ROLE_USER)
                .build();

        testTask = Task.builder()
                .id(100L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .user(testUser)
                .build();

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createTask_Success_ShouldReturn201Created() throws Exception {
        given(taskService.createTask(any(TaskRequest.class), eq("test@example.com"))).willReturn(testTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createTask_ValidationFails_ShouldReturn400BadRequest() throws Exception {
        TaskRequest badRequest = new TaskRequest();
        badRequest.setTitle("");
        badRequest.setDescription("No title");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getTasks_Success_ShouldReturnListOfTasks() throws Exception {
        given(taskService.getTasksForUser("test@example.com")).willReturn(List.of(testTask));

        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTasks_NoToken_ShouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateTaskStatus_Success_ShouldReturn200OK() throws Exception {
        testTask.setStatus(TaskStatus.COMPLETED);
        given(taskService.updateTaskStatus(eq(100L), eq(TaskStatus.COMPLETED), eq("test@example.com")))
                .willReturn(testTask);

        mockMvc.perform(put("/tasks/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TaskStatus.COMPLETED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "attacker@example.com", roles = "USER")
    void deleteTask_NotOwner_ShouldReturn403Forbidden() throws Exception {
        String attackerEmail = "attacker@example.com";
        willThrow(new RuntimeException("Access Denied"))
                .given(taskService)
                .deleteTask(eq(100L), eq(attackerEmail));

        mockMvc.perform(delete("/tasks/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void deleteTask_Success_ShouldReturn204NoContent() throws Exception {
        willDoNothing()
                .given(taskService)
                .deleteTask(eq(100L), eq("test@example.com"));

        mockMvc.perform(delete("/tasks/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}