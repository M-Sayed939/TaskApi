package com.example.TaskApi.controller;

import com.example.TaskApi.dto.TaskRequest;
import com.example.TaskApi.dto.TaskStatusUpdateRequest;
import com.example.TaskApi.model.Task;
import com.example.TaskApi.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Task createdTask = taskService.createTask(taskRequest, userDetails.getUsername());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal UserDetails userDetails) {
        List<Task> tasks = taskService.getTasksForUser(userDetails.getUsername());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id,
                                                 @Valid @RequestBody TaskStatusUpdateRequest statusRequest,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        Task updatedTask = taskService.updateTaskStatus(id, statusRequest.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}


