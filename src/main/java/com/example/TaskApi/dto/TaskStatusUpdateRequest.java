package com.example.TaskApi.dto;

import com.example.TaskApi.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private TaskStatus status;
}
