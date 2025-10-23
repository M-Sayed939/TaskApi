package com.example.TaskApi.dto;

import com.example.TaskApi.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank(message = "Task title is required")
    private String title;

    private String description;
    private TaskStatus status = TaskStatus.OPEN;

}
